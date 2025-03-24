package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.BookItem;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            ClientRepository clientRepository,
                            EmployeeRepository employeeRepository,
                            BookRepository bookRepository,
                            ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<OrderDTO> getOrdersByClient(String clientEmail) {
        List<Order> orders = orderRepository.findAllByClient_Email(clientEmail);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByEmployee(String employeeEmail) {
        List<Order> orders = orderRepository.findAllByEmployee_Email(employeeEmail);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO addOrder(OrderDTO orderDTO) {
        // Знаходимо клієнта за email
        Client client = clientRepository.findByEmail(orderDTO.getClientEmail())
                .orElseThrow(() -> new NotFoundException("Client not found: " + orderDTO.getClientEmail()));
        // Знаходимо працівника за email
        Employee employee = employeeRepository.findByEmail(orderDTO.getEmployeeEmail())
                .orElseThrow(() -> new NotFoundException("Employee not found: " + orderDTO.getEmployeeEmail()));

        // Створюємо нове замовлення
        Order order = new Order();
        order.setClient(client);
        order.setEmployee(employee);
        order.setOrderDate(orderDTO.getOrderDate());
        order.setPrice(orderDTO.getPrice());

        // Обробка списку BookItem з DTO
        List<BookItem> bookItems = new ArrayList<>();
        if (orderDTO.getBookItems() != null) {
            for (BookItemDTO bookItemDTO : orderDTO.getBookItems()) {
                // Знаходимо книгу за назвою, використовуючи нове поле bookName
                Book book = bookRepository.findByName(bookItemDTO.getBookName())
                        .orElseThrow(() -> new NotFoundException("Book not found: " + bookItemDTO.getBookName()));
                BookItem bookItem = new BookItem();
                bookItem.setBook(book);
                bookItem.setQuantity(bookItemDTO.getQuantity());
                // Встановлюємо зв'язок з замовленням
                bookItem.setOrder(order);
                bookItems.add(bookItem);
            }
        }
        order.setBookItems(bookItems);

        // Зберігаємо замовлення в базі даних
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDTO.class);
    }
}
