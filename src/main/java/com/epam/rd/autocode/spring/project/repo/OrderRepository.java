package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Отримати всі замовлення за email клієнта
    // (Order містить поле 'client', а у Client є поле 'email')
    List<Order> findAllByClient_Email(String email);

    // Отримати всі замовлення за email працівника
    // (Order містить поле 'employee', а у Employee є поле 'email')
    List<Order> findAllByEmployee_Email(String email);
}
