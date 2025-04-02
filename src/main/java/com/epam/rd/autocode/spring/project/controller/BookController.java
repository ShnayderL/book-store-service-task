package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
        logger.info("BookController initialized with BookService");
    }

    @GetMapping
    public String showBooksPage(Model model, HttpServletRequest request) {
        logger.info("GET /books | Client IP: {}", request.getRemoteAddr());

        try {
            List<BookDTO> books = bookService.getAllBooks();
            model.addAttribute("books", books);
            logger.debug("Loaded {} books for listing page", books.size());
            return "books";
        } catch (Exception e) {
            logger.error("Failed to load books page: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{name}")
    @ResponseBody
    public BookDTO getBookByName(@PathVariable String name, HttpServletRequest request) {
        logger.info("GET /books/{} | Client: {}", name, request.getRemoteAddr());

        try {
            BookDTO book = bookService.getBookByName(name);
            logger.debug("Retrieved book details for: {}", name);
            return book;
        } catch (NotFoundException e) {
            logger.warn("Book not found: {} | Client: {}", name, request.getRemoteAddr());
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching book {}: {}", name, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping
    @ResponseBody
    public BookDTO addBook(@RequestBody @Valid BookDTO bookDTO, HttpServletRequest request) {
        logger.info("POST /books | Client: {} | Book: {}",
                request.getRemoteAddr(),
                bookDTO.getName());
        logger.debug("Book details: Author={}, Genre={}",
                bookDTO.getAuthor(), bookDTO.getGenre());

        try {
            BookDTO createdBook = bookService.addBook(bookDTO);
            logger.info("Successfully added book: {}", createdBook.getName());
            return createdBook;
        } catch (AlreadyExistException e) {
            logger.warn("Duplicate book attempt: {} | Client: {}",
                    bookDTO.getName(),
                    request.getRemoteAddr());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to add book {}: {}", bookDTO.getName(), e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{name}")
    @ResponseBody
    public BookDTO updateBookByName(
            @PathVariable String name,
            @RequestBody BookDTO bookDTO,
            HttpServletRequest request) {
        logger.info("PUT /books/{} | Client: {}", name, request.getRemoteAddr());
        logger.debug("Update details: NewName={}, Author={}",
                bookDTO.getName(), bookDTO.getAuthor());

        try {
            BookDTO updatedBook = bookService.updateBookByName(name, bookDTO);
            logger.info("Successfully updated book: {} → {}", name, updatedBook.getName());
            return updatedBook;
        } catch (NotFoundException e) {
            logger.warn("Book to update not found: {} | Client: {}",
                    name,
                    request.getRemoteAddr());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to update book {}: {}", name, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{name}")
    @ResponseBody
    public ResponseEntity<Void> deleteBookByName(
            @PathVariable String name,
            HttpServletRequest request) {
        logger.info("DELETE /books/{} | Client: {}", name, request.getRemoteAddr());

        try {
            bookService.deleteBookByName(name);
            logger.info("Successfully deleted book: {}", name);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            logger.warn("Book to delete not found: {} | Client: {}",
                    name,
                    request.getRemoteAddr());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to delete book {}: {}", name, e.getMessage(), e);
            throw e;
        }
    }
}