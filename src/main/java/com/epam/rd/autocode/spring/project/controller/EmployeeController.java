package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final ClientRepository clientRepository;
    private final BookRepository bookRepository;

    public EmployeeController(ClientRepository clientRepository, BookRepository bookRepository) {
        this.clientRepository = clientRepository;
        this.bookRepository = bookRepository;
        logger.info("EmployeeController initialized with repositories");
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping
    public String getEmployeeDashboard(Model model) {
        logger.debug("Loading employee dashboard");
        try {
            model.addAttribute("clients", clientRepository.findAll());
            model.addAttribute("books", bookRepository.findAll());

            int clientCount = clientRepository.findAll().size();
            int bookCount = bookRepository.findAll().size();
            logger.info("Dashboard loaded with {} clients and {} books", clientCount, bookCount);

            return "employee";
        } catch (Exception e) {
            logger.error("Failed to load employee dashboard", e);
            throw e;
        }
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping("/client/{email}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteClientByEmail(@PathVariable String email) {
        logger.info("Attempting to delete client with email: {}", email);

        return clientRepository.findByEmail(email)
                .map(client -> {
                    clientRepository.delete(client);
                    logger.warn("Client deleted successfully: {}", email);
                    return ResponseEntity.ok(Collections.singletonMap("message", "Клієнта видалено"));
                })
                .orElseGet(() -> {
                    logger.error("Client not found for deletion: {}", email);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Collections.singletonMap("error", "Клієнта не знайдено"));
                });
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/book")
    @ResponseBody
    public ResponseEntity<Map<String, String>> addBook(@RequestBody Book book) {
        logger.debug("Attempting to add new book: {}", book.getName());

        try {
            bookRepository.save(book);
            logger.info("Book added successfully: {} (ID: {})", book.getName(), book.getId());
            return ResponseEntity.ok(Collections.singletonMap("message", "Книгу додано"));
        } catch (Exception e) {
            logger.error("Failed to add book: {}", book.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Помилка при додаванні книги"));
        }
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/book/{id}")
    @ResponseBody
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        logger.debug("Fetching book with ID: {}", id);

        return bookRepository.findById(id)
                .map(book -> {
                    logger.debug("Book found: {} (ID: {})", book.getName(), id);
                    return ResponseEntity.ok(book);
                })
                .orElseGet(() -> {
                    logger.warn("Book not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/book/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateBook(
            @PathVariable Long id,
            @RequestBody Book updatedBook
    ) {
        logger.info("Attempting to update book with ID: {}", id);

        return bookRepository.findById(id)
                .map(book -> {
                    updateBookFields(book, updatedBook);
                    bookRepository.save(book);
                    logger.info("Book updated successfully: {} (ID: {})", book.getName(), id);
                    return ResponseEntity.ok(Collections.singletonMap("message", "Книгу оновлено"));
                })
                .orElseGet(() -> {
                    logger.error("Book not found for update with ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Collections.singletonMap("error", "Книгу не знайдено"));
                });
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping("/book/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long id) {
        logger.info("Attempting to delete book with ID: {}", id);

        return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.delete(book);
                    logger.warn("Book deleted successfully: {} (ID: {})", book.getName(), id);
                    return ResponseEntity.ok(Collections.singletonMap("message", "Книгу видалено"));
                })
                .orElseGet(() -> {
                    logger.error("Book not found for deletion with ID: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Collections.singletonMap("error", "Книгу не знайдено"));
                });
    }

    private void updateBookFields(Book target, Book source) {
        logger.trace("Updating book fields for ID: {}", target.getId());
        target.setName(source.getName());
        target.setAuthor(source.getAuthor());
        target.setGenre(source.getGenre());
        target.setAgeGroup(source.getAgeGroup());
        target.setPrice(source.getPrice());
        target.setDescription(source.getDescription());
        target.setLanguage(source.getLanguage());
        target.setPages(source.getPages());
        target.setPublicationDate(source.getPublicationDate());
        target.setCharacteristics(source.getCharacteristics());
    }
}