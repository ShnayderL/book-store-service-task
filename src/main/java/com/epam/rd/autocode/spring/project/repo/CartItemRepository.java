package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.CartItem;
import com.epam.rd.autocode.spring.project.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByClient(Client client);

    // Доданий метод для пошуку запису кошика за клієнтом і книгою
    Optional<CartItem> findByClientAndBook(Client client, Book book);

    void deleteByClient(Client client);
}