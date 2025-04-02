package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, ModelMapper modelMapper) {
        this.clientRepository = clientRepository;
        this.modelMapper = modelMapper;
        logger.info("ClientService initialized with dependencies");
    }

    @Override
    public List<ClientDTO> getAllClients() {
        logger.debug("Fetching all clients");
        try {
            List<ClientDTO> clients = clientRepository.findAll()
                    .stream()
                    .map(client -> {
                        logger.trace("Mapping client entity to DTO: {}", client.getEmail());
                        return modelMapper.map(client, ClientDTO.class);
                    })
                    .collect(Collectors.toList());
            logger.info("Successfully retrieved {} clients", clients.size());
            return clients;
        } catch (Exception e) {
            logger.error("Failed to fetch clients", e);
            throw e;
        }
    }

    @Override
    public ClientDTO getClientByEmail(String email) {
        logger.debug("Fetching client by email: {}", email);
        try {
            Client client = clientRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Client not found with email: {}", email);
                        return new NotFoundException("Client not found: " + email);
                    });
            logger.info("Successfully retrieved client: {}", email);
            return modelMapper.map(client, ClientDTO.class);
        } catch (Exception e) {
            logger.error("Error retrieving client with email: {}", email, e);
            throw e;
        }
    }

    @Override
    public ClientDTO updateClientByEmail(String email, ClientDTO clientDTO) {
        logger.debug("Updating client with email: {}", email);
        try {
            Client client = clientRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Client to update not found: {}", email);
                        return new NotFoundException("Client not found: " + email);
                    });

            logger.debug("Original client data - Name: {}, Balance: {}",
                    client.getName(), client.getBalance());

            client.setName(clientDTO.getName());
            // Password updates should be logged at DEBUG level only
            logger.debug("Updating client password (hashed)");
            client.setPassword(clientDTO.getPassword());
            client.setBalance(clientDTO.getBalance());

            Client updatedClient = clientRepository.save(client);
            logger.info("Successfully updated client: {}", email);
            return modelMapper.map(updatedClient, ClientDTO.class);
        } catch (Exception e) {
            logger.error("Failed to update client: {}", email, e);
            throw e;
        }
    }

    @Override
    public void deleteClientByEmail(String email) {
        logger.debug("Attempting to delete client: {}", email);
        try {
            Client client = clientRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Client to delete not found: {}", email);
                        return new NotFoundException("Client not found: " + email);
                    });
            clientRepository.delete(client);
            logger.info("Successfully deleted client: {}", email);
        } catch (Exception e) {
            logger.error("Failed to delete client: {}", email, e);
            throw e;
        }
    }

    @Override
    public ClientDTO addClient(ClientDTO clientDTO) {
        logger.debug("Attempting to add new client with email: {}", clientDTO.getEmail());
        try {
            // Check for existing client
            clientRepository.findByEmail(clientDTO.getEmail())
                    .ifPresent(existing -> {
                        logger.warn("Duplicate client creation attempt: {}", clientDTO.getEmail());
                        throw new AlreadyExistException("Client already exists with email: " + clientDTO.getEmail());
                    });

            Client client = modelMapper.map(clientDTO, Client.class);
            Client savedClient = clientRepository.save(client);
            logger.info("Successfully created new client: {}", clientDTO.getEmail());
            return modelMapper.map(savedClient, ClientDTO.class);
        } catch (Exception e) {
            logger.error("Failed to create new client: {}", clientDTO.getEmail(), e);
            throw e;
        }
    }
}