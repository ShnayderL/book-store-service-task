package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Пошук клієнта за email
    Optional<Client> findByEmail(String email);

    // Видалення клієнта за email
    void deleteByEmail(String email);
}
