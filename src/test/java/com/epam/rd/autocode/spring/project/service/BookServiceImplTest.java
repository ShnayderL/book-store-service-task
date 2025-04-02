package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setName("Clean Code");
        book.setAuthor("Robert Martin");

        bookDTO = new BookDTO();
        bookDTO.setName("Clean Code");
        bookDTO.setAuthor("Robert Martin");

        bookService = new BookServiceImpl(bookRepository, modelMapper);
    }

    @Test
    void getAllBooks_ShouldReturnAllBooks() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(List.of(book));

        // Act
        List<BookDTO> result = bookService.getAllBooks();

        // Assert
        assertEquals(1, result.size());
        verify(bookRepository).findAll();
    }

    @Test
    void getBookByName_WhenBookExists_ShouldReturnBookDTO() {
        // Arrange
        when(bookRepository.findByName("Clean Code")).thenReturn(Optional.of(book));

        // Act
        BookDTO result = bookService.getBookByName("Clean Code");

        // Assert
        assertNotNull(result);
        assertEquals("Clean Code", result.getName());
    }

    @Test
    void getBookByName_WhenBookNotExists_ShouldThrowException() {
        // Arrange
        when(bookRepository.findByName("Unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                bookService.getBookByName("Unknown"));
    }

    @Test
    void addBook_WhenBookDoesNotExist_ShouldSaveBook() {
        // Arrange
        when(bookRepository.findByName("Clean Code")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        BookDTO result = bookService.addBook(bookDTO);

        // Assert
        assertNotNull(result);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void addBook_WhenBookExists_ShouldThrowException() {
        // Arrange
        when(bookRepository.findByName("Clean Code")).thenReturn(Optional.of(book));

        // Act & Assert
        assertThrows(AlreadyExistException.class, () ->
                bookService.addBook(bookDTO));
    }
}