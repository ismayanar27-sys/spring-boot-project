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

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final EmailService emailService;
    private final ModelMapper modelMapper; // DTO-dan Entity-ə çevirmək üçün

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
        }}}
