package com.myapp.myapp.services.impl;

import com.myapp.myapp.dtos.ContactDTO;
import com.myapp.myapp.models.Contact;
import com.myapp.myapp.repositories.ContactRepository;
import com.myapp.myapp.services.ContactService;
import com.myapp.myapp.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final EmailService emailService;
    private final ModelMapper modelMapper; //DTO-dan Entity-ə çevirmək üçün

    // application.properties faylından admin mail ünvanını oxuyuruq
    @Value("${admin.email.recipient:ismayanar27@gmail.com}")
    private String adminEmailRecipient;

    /**
     * Kontakt mesajını bazaya yazır və adminə mail göndərir.
     * @param contactDTO - Formadan gələn məlumatlar.
     * @return Yaddaşa yazılmış Contact obyekti.
     */
    @Override
    @Transactional
    public Contact saveAndSendContactMessage(ContactDTO contactDTO) {

        // 1. DTO-nu Contact obyektinə çeviririk
        Contact contactMessage = modelMapper.map(contactDTO, Contact.class);
        contactMessage.setReceivedAt(LocalDateTime.now());

        // 2. Məlumatı bazaya yazdır
        Contact savedMessage = contactRepository.save(contactMessage);

        // 3. Adminə bildiriş maili göndər
        sendAdminNotification(savedMessage);

        return savedMessage;
    }

    /**
     * DÜZƏLDİLDİ: Bu metod "return List.of();" edən boş stub idi - yəni
     * admin panelində bazada real mesajlar olsa belə, həmişə boş siyahı
     * göstərilirdi ("Hələ heç bir əlaqə mesajı yoxdur" mesajı yalan idi).
     *
     * SİLİNDİ: public List<Contact> getAllMessages() { return List.of(); }
     *
     * ƏVƏZİNƏ: bazadan real mesajlar, ən yenidən köhnəyə sıralı gətirilir.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Contact> getAllMessages() {
        return contactRepository.findAllByOrderByReceivedAtDesc();
    }

    /**
     * DÜZƏLDİLDİ: Bu metod "return false;" edən boş stub idi - silmə
     * əməliyyatı heç vaxt baş vermirdi, admin panelində "Sil" düyməsi
     * basılanda mesaj bazada qalırdı, amma "uğursuz" mesajı göstərilirdi.
     *
     * SİLİNDİ: public boolean deleteMessage(Long id) { return false; }
     *
     * ƏVƏZİNƏ: mesajın mövcudluğu yoxlanılır, varsa silinir.
     */
    @Override
    @Transactional
    public boolean deleteMessage(Long id) {
        if (contactRepository.existsById(id)) {
            contactRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * DÜZƏLDİLDİ: Bu metod "return 0;" edən boş stub idi.
     *
     * SİLİNDİ: public long countMessages() { return 0; }
     *
     * ƏVƏZİNƏ: real say bazadan gətirilir.
     */
    @Override
    @Transactional(readOnly = true)
    public long countMessages() {
        return contactRepository.count();
    }

    private void sendAdminNotification(Contact contact) {
        try {
            final String subject = "YENİ ƏLAQƏ MESAJI: " + contact.getSubject();
            final String body = String.format(
                    "<b>Kimdən:</b> %s<br><b>Email:</b> %s<br><b>Mövzu:</b> %s<br><b>Göndərilmə Tarixi:</b> %s<br>" +
                            "<h3>Mesaj:</h3>%s",
                    contact.getName(),
                    contact.getEmail(),
                    contact.getSubject(),
                    contact.getReceivedAt().toString(),
                    contact.getMessage()
            );
            // Admin mail ünvanına göndəririk
            emailService.sendEmail(adminEmailRecipient, subject, body);
        } catch (Exception e) {
            // Mail göndərilməsində xəta olsa da, tranzaksiya tamamlanır (bazaya yazılır).
            System.err.println("XƏTA: Adminə kontakt maili göndərilərkən xəta: " + e.getMessage());
        }
    }
}