package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.Book;

import java.math.BigDecimal;

public class CartItemDTO {
    private Book book;
    private int quantity;
    private BigDecimal subtotal;

    public CartItemDTO(Book book, int quantity, BigDecimal subtotal) {
        this.book = book;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Book getBook() {
        return book;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}