package com.example.testit.controller;

import com.example.testit.adapter.member.CurrentMemberService;
import com.example.testit.model.Book;
import com.example.testit.model.Status;
import com.example.testit.service.BookService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private BookService bookService;
    @MockitoBean
    private CurrentMemberService currentMemberService;

    @Test
    void itShouldCreateBook()
            throws Exception {
        Book book = new Book("Harry Potter", "zahid");
        book.setId(1L);
        Mockito.when(bookService.createBook("Harry Potter", "zahid")).thenReturn(book);

        mockMvc.perform(MockMvcRequestBuilders.post("/books").content("""
                {
                    "title":"Harry Potter",
                    "author":"zahid"
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        Mockito.verify(bookService).createBook("Harry Potter", "zahid");
    }

    @Test
    void itShouldBorrowBook()
            throws Exception {
        Book book = new Book("Harry Potter", "zahid");
        book.setId(1L);
        book.setStatus(Status.EMPRUNTE);
        Mockito.when(currentMemberService.getCurrentMemberId()).thenReturn(Optional.of(2L));
        Mockito.when(bookService.borrowBook(1L, 2L)).thenReturn(book);

        mockMvc.perform(MockMvcRequestBuilders.post("/books/1/borrow"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("EMPRUNTE"));
        Mockito.verify(bookService).borrowBook(1L, 2L);
    }
}