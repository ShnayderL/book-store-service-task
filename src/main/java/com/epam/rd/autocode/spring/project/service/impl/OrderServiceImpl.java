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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

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
        logger.info("OrderService initialized with all dependencies");
    }

    @Override
    public List<OrderDTO> getOrdersByClient(String clientEmail) {
        logger.debug("Fetching orders for client: {}", clientEmail);
        try {
            List<Order> orders = orderRepository.findAllByClient_Email(clientEmail);
            logger.info("Found {} orders for client: {}", orders.size(), clientEmail);
            return orders.stream()
                    .peek(order -> logger.trace("Mapping order ID: {}", order.getId()))
                    .map(order -> modelMapper.map(order, OrderDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to fetch orders for client: {}", clientEmail, e);
            throw e;
        }
    }

    @Override
    public List<OrderDTO> getOrdersByEmployee(String employeeEmail) {
        logger.debug("Fetching orders handled by employee: {}", employeeEmail);
        try {
            List<Order> orders = orderRepository.findAllByEmployee_Email(employeeEmail);
            logger.info("Found {} orders handled by employee: {}", orders.size(), employeeEmail);
            return orders.stream()
                    .peek(order -> logger.trace("Processing order ID: {}", order.getId()))
                    .map(order -> modelMapper.map(order, OrderDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to fetch orders for employee: {}", employeeEmail, e);
            throw e;
        }
    }

    @Override
    public OrderDTO addOrder(OrderDTO orderDTO) {
        logger.debug("Creating new order for client: {}", orderDTO.getClientEmail());
        try {
            // Validate and log client
            Client client = clientRepository.findByEmail(orderDTO.getClientEmail())
                    .orElseThrow(() -> {
                        logger.warn("Client not found: {}", orderDTO.getClientEmail());
                        return new NotFoundException("Client not found: " + orderDTO.getClientEmail());
                    });
            logger.debug("Found client ID: {} for order", client.getId());

            // Validate and log employee
            Employee employee = employeeRepository.findByEmail(orderDTO.getEmployeeEmail())
                    .orElseThrow(() -> {
                        logger.warn("Employee not found: {}", orderDTO.getEmployeeEmail());
                        return new NotFoundException("Employee not found: " + orderDTO.getEmployeeEmail());
                    });
            logger.debug("Found employee ID: {} for order", employee.getId());

            Order order = new Order();
            order.setClient(client);
            order.setEmployee(employee);
            order.setOrderDate(orderDTO.getOrderDate());
            order.setPrice(orderDTO.getPrice());
            logger.debug("Created order skeleton with price: {}", orderDTO.getPrice());

            // Process book items
            List<BookItem> bookItems = new ArrayList<>();
            if (orderDTO.getBookItems() != null) {
                logger.debug("Processing {} book items", orderDTO.getBookItems().size());
                for (BookItemDTO bookItemDTO : orderDTO.getBookItems()) {
                    Book book = bookRepository.findByName(bookItemDTO.getBookName())
                            .orElseThrow(() -> {
                                logger.warn("Book not found: {}", bookItemDTO.getBookName());
                                return new NotFoundException("Book not found: " + bookItemDTO.getBookName());
                            });

                    logger.trace("Adding book: {} (Qty: {}) to order",
                            bookItemDTO.getBookName(), bookItemDTO.getQuantity());

                    BookItem bookItem = new BookItem();
                    bookItem.setBook(book);
                    bookItem.setQuantity(bookItemDTO.getQuantity());
                    bookItem.setOrder(order);
                    bookItems.add(bookItem);
                }
            } else {
                logger.warn("No book items provided in order");
            }
            order.setBookItems(bookItems);

            Order savedOrder = orderRepository.save(order);
            logger.info("Successfully created order ID: {} for client: {}",
                    savedOrder.getId(), orderDTO.getClientEmail());

            return modelMapper.map(savedOrder, OrderDTO.class);
        } catch (NotFoundException e) {
            logger.error("Validation failed for order creation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to create order for client: {}", orderDTO.getClientEmail(), e);
            throw e;
        }
    }
}