package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository,
                           ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO getBookByName(String name) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found: " + name));
        return modelMapper.map(book, BookDTO.class);
    }

    @Override
    public BookDTO updateBookByName(String name, BookDTO bookDTO) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found: " + name));

                        // Оновлюємо поля. Переконайтеся, що в моделі Book є відповідні поля та сетери.
                        book.setName(bookDTO.getName());
        book.setGenre(bookDTO.getGenre());
        // Якщо ageGroup у DTO це String, а в Book – enum, треба додатково конвертувати:
        book.setAgeGroup(bookDTO.getAgeGroup());
        book.setPrice(bookDTO.getPrice());
        book.setPublicationDate(bookDTO.getPublicationDate());
        book.setAuthor(bookDTO.getAuthor());
        book.setPages(bookDTO.getPages());
        book.setCharacteristics(bookDTO.getCharacteristics());
        book.setDescription(bookDTO.getDescription());
        book.setLanguage(bookDTO.getLanguage());

        Book updatedBook = bookRepository.save(book);
        return modelMapper.map(updatedBook, BookDTO.class);
    }

    @Override
    public void deleteBookByName(String name) {
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found: " + name));
                        bookRepository.delete(book);
    }

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        // Перевіряємо, чи немає вже книги з такою назвою
        bookRepository.findByName(bookDTO.getName())
                .ifPresent(existing -> {
                    throw new AlreadyExistException("Book already exists with name: " + bookDTO.getName());
                });

        // Мапимо DTO -> Entity
        Book book = modelMapper.map(bookDTO, Book.class);
        Book savedBook = bookRepository.save(book);

        // Повертаємо DTO
        return modelMapper.map(savedBook, BookDTO.class);
    }
}
