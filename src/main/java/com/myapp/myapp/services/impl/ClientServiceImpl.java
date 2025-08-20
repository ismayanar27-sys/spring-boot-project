package com.myapp.myapp.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import com.myapp.myapp.dtos.ClientDtos.ClientCreateDto;
import com.myapp.myapp.dtos.ClientDtos.ClientDto;
import com.myapp.myapp.dtos.ClientDtos.ClientUpDateDto;
import com.myapp.myapp.models.Client;
import com.myapp.myapp.repositstories.ClientRepository;
import com.myapp.myapp.services.ClientService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service // Spring'ə bu sinfin biznes məntiqi daşıyan bir xidmət (service) sinfi olduğunu bildirir.
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;
    private final Cloudinary cloudinary; // Cloudinary obyektini daxil edirik.

    public ClientServiceImpl(ClientRepository clientRepository, ModelMapper modelMapper, Cloudinary cloudinary) {
        this.clientRepository = clientRepository;
        this.modelMapper = modelMapper;
        this.cloudinary = cloudinary;
    }

    @Override
    public List<ClientDto> getAllClients() {
        List<Client>clients = clientRepository.findAll();
        List<ClientDto> clientDtos = clients.stream().map(x->modelMapper.map(x,ClientDto.class)).toList();
        return clientDtos;
    }

    @Override
    public ClientDto getClientsId(Long id) {
        Client client = clientRepository.findById(id).orElseThrow();
        return modelMapper.map(client,ClientDto.class);
    }

    // Yeni müştəri əlavə edir. Şəkil yüklənməsi ilə birlikdə.
    @Override
    public boolean createClients(ClientCreateDto clientCreateDto, MultipartFile image) {
        try {
            Client client =new Client();
            client.setName(clientCreateDto.getName());
            client.setDescription(clientCreateDto.getDescription());
            client.setInformation(clientCreateDto.getInformation());
            client.setEmail(clientCreateDto.getEmail());

            // Şəkil yüklənməsi əlavə olunub
            Map upLoadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            String photoUrl = (String) upLoadResult.get("url");
            client.setPhotoUrl(photoUrl);

            clientRepository.save(client);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Müştəri məlumatlarını yeniləyir.
    @Override
    public boolean updateClients(ClientUpDateDto clientUpDateDto, Long id, MultipartFile image) {
        Optional<Client> optionalClient = clientRepository.findById(id);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            client.setName(clientUpDateDto.getName());
            client.setDescription(clientUpDateDto.getDescription());
            client.setInformation(clientUpDateDto.getInformation());
            client.setEmail(clientUpDateDto.getEmail());

            if (image != null && !image.isEmpty()) {
                try {
                    Map upLoadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                    String photoUrl = (String) upLoadResult.get("url");
                    client.setPhotoUrl(photoUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            clientRepository.save(client);
            return true;
        }
        return false;
    }


    @Override
    public boolean deleteClients(Long id) {
        clientRepository.deleteById(id);
        return true;
    }

    @Override
    public ClientUpDateDto findClientById(Long id) {
        Client client= clientRepository.findById(id).orElseThrow();
        return modelMapper.map(client,ClientUpDateDto.class);
    }
}
