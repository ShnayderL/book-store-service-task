package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // 1) Отримати список усіх книг
    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    // 2) Отримати книгу за назвою
    @GetMapping("/{name}")
    public BookDTO getBookByName(@PathVariable String name) {
        return bookService.getBookByName(name);
    }

    // 3) Додати нову книгу
    @PostMapping
    public BookDTO addBook(@RequestBody BookDTO bookDTO) {
        return bookService.addBook(bookDTO);
    }

    // 4) Оновити книгу за назвою
    @PutMapping("/{name}")
    public BookDTO updateBookByName(@PathVariable String name, @RequestBody BookDTO bookDTO) {
        return bookService.updateBookByName(name, bookDTO);
    }

    // 5) Видалити книгу за назвою
    @DeleteMapping("/{name}")
    public void deleteBookByName(@PathVariable String name) {
        bookService.deleteBookByName(name);
    }
}
