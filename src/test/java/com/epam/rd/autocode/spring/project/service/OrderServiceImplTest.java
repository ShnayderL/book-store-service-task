package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.*;
import com.epam.rd.autocode.spring.project.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private BookRepository bookRepository;

    private ModelMapper modelMapper;
    private OrderServiceImpl orderService;

    private OrderDTO orderDTO;
    private Client client;
    private Employee employee;
    private Book book;

    @BeforeEach
    void setUp() {
        // Ініціалізація реального ModelMapper
        modelMapper = new ModelMapper();

        // Ручне створення сервісу з усіма залежностями
        orderService = new OrderServiceImpl(
                orderRepository,
                clientRepository,
                employeeRepository,
                bookRepository,
                modelMapper
        );

        // Ініціалізація тестових даних
        client = new Client();
        client.setEmail("client@test.com");

        employee = new Employee();
        employee.setEmail("employee@company.com");

        book = new Book();
        book.setName("Design Patterns");

        BookItemDTO bookItemDTO = new BookItemDTO();
        bookItemDTO.setBookName("Design Patterns");
        bookItemDTO.setQuantity(2);

        orderDTO = new OrderDTO();
        orderDTO.setClientEmail("client@test.com");
        orderDTO.setEmployeeEmail("employee@company.com");
        orderDTO.setBookItems(List.of(bookItemDTO));
    }

    @Test
    void addOrder_WithValidData_ShouldCreateOrder() {
        // Arrange
        when(clientRepository.findByEmail("client@test.com")).thenReturn(Optional.of(client));
        when(employeeRepository.findByEmail("employee@company.com")).thenReturn(Optional.of(employee));
        when(bookRepository.findByName("Design Patterns")).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderDTO result = orderService.addOrder(orderDTO);

        // Assert
        assertNotNull(result);
        assertEquals("client@test.com", result.getClientEmail());
        assertEquals("employee@company.com", result.getEmployeeEmail());
        assertEquals(1, result.getBookItems().size());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void addOrder_WhenClientNotFound_ShouldThrowException() {
        // Arrange
        when(clientRepository.findByEmail("client@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> orderService.addOrder(orderDTO));
        verify(clientRepository).findByEmail("client@test.com");
    }

    @Test
    void addOrder_WhenBookNotFound_ShouldThrowException() {
        // Arrange
        when(clientRepository.findByEmail("client@test.com")).thenReturn(Optional.of(client));
        when(employeeRepository.findByEmail("employee@company.com")).thenReturn(Optional.of(employee)); // Додано заглушку для працівника
        when(bookRepository.findByName("Design Patterns")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> orderService.addOrder(orderDTO));

        // Перевірка, що метод findByName викликався з правильною назвою
        verify(bookRepository).findByName("Design Patterns");
    }
}