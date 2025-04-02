package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository,
                           ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
        logger.info("BookServiceImpl initialized with dependencies");
    }

    @Override
    public List<BookDTO> getAllBooks() {
        logger.debug("Fetching all books");
        try {
            List<BookDTO> books = bookRepository.findAll()
                    .stream()
                    .map(book -> modelMapper.map(book, BookDTO.class))
                    .collect(Collectors.toList());
            logger.info("Successfully retrieved {} books", books.size());
            return books;
        } catch (Exception e) {
            logger.error("Error occurred while fetching all books", e);
            throw e;
        }
    }

    @Override
    public BookDTO getBookByName(String name) {
        logger.debug("Fetching book by name: {}", name);
        try {
            Book book = bookRepository.findByName(name)
                    .orElseThrow(() -> {
                        logger.warn("Book not found: {}", name);
                        return new NotFoundException("Book not found: " + name);
                    });
            logger.info("Successfully retrieved book: {}", name);
            return modelMapper.map(book, BookDTO.class);
        } catch (Exception e) {
            logger.error("Error occurred while fetching book by name: {}", name, e);
            throw e;
        }
    }

    @Override
    public BookDTO updateBookByName(String name, BookDTO bookDTO) {
        logger.debug("Updating book: {} with new data: {}", name, bookDTO);
        try {
            Book book = bookRepository.findByName(name)
                    .orElseThrow(() -> {
                        logger.warn("Book to update not found: {}", name);
                        return new NotFoundException("Book not found: " + name);
                    });

            logger.debug("Original book data: {}", book);
            book.setName(bookDTO.getName());
            book.setGenre(bookDTO.getGenre());
            book.setAgeGroup(bookDTO.getAgeGroup());
            book.setPrice(bookDTO.getPrice());
            book.setPublicationDate(bookDTO.getPublicationDate());
            book.setAuthor(bookDTO.getAuthor());
            book.setPages(bookDTO.getPages());
            book.setCharacteristics(bookDTO.getCharacteristics());
            book.setDescription(bookDTO.getDescription());
            book.setLanguage(bookDTO.getLanguage());

            Book updatedBook = bookRepository.save(book);
            logger.info("Successfully updated book: {}", name);
            return modelMapper.map(updatedBook, BookDTO.class);
        } catch (Exception e) {
            logger.error("Error occurred while updating book: {}", name, e);
            throw e;
        }
    }

    @Override
    public void deleteBookByName(String name) {
        logger.debug("Attempting to delete book: {}", name);
        try {
            Book book = bookRepository.findByName(name)
                    .orElseThrow(() -> {
                        logger.warn("Book to delete not found: {}", name);
                        return new NotFoundException("Book not found: " + name);
                    });
            bookRepository.delete(book);
            logger.info("Successfully deleted book: {}", name);
        } catch (Exception e) {
            logger.error("Error occurred while deleting book: {}", name, e);
            throw e;
        }
    }

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        logger.debug("Attempting to add new book: {}", bookDTO.getName());
        try {
            bookRepository.findByName(bookDTO.getName())
                    .ifPresent(existing -> {
                        logger.warn("Attempt to add duplicate book: {}", bookDTO.getName());
                        throw new AlreadyExistException("Book already exists with name: " + bookDTO.getName());
                    });

            Book book = modelMapper.map(bookDTO, Book.class);
            Book savedBook = bookRepository.save(book);
            logger.info("Successfully added new book: {}", bookDTO.getName());
            return modelMapper.map(savedBook, BookDTO.class);
        } catch (Exception e) {
            logger.error("Error occurred while adding new book: {}", bookDTO.getName(), e);
            throw e;
        }
    }
}