package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setEmail("test@example.com");
        client.setName("John Doe");

        clientDTO = new ClientDTO();
        clientDTO.setEmail("test@example.com");
        clientDTO.setName("John Doe");

        clientService = new ClientServiceImpl(clientRepository, modelMapper);
    }



    @Test
    void getClientByEmail_WhenClientExists_ShouldReturnClientDTO() {
        // Arrange
        when(clientRepository.findByEmail("test@example.com")).thenReturn(Optional.of(client));

        // Act
        ClientDTO result = clientService.getClientByEmail("test@example.com");

        // Assert
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void updateClientByEmail_ShouldUpdateClient() {
        // Arrange
        when(clientRepository.findByEmail("test@example.com")).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        clientDTO.setName("Updated Name");

        // Act
        ClientDTO result = clientService.updateClientByEmail("test@example.com", clientDTO);

        // Assert
        assertEquals("Updated Name", result.getName());
        verify(clientRepository).save(client);
    }
}