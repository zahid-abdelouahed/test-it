package com.example.testit.service;

import com.example.testit.adapter.mail.MailService;
import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.model.Status;
import com.example.testit.repository.BookRepository;
import com.example.testit.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class BookServiceTest {
    private MemberRepository memberRepository;
    private BookRepository bookRepository;
    private MailService mailService;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        memberRepository = Mockito.mock(MemberRepository.class);
        bookRepository = Mockito.mock(BookRepository.class);
        mailService = Mockito.mock(MailService.class);
        bookService = new BookService(bookRepository, memberRepository, mailService);
    }

    @Test
    void borrowBook() {
        
        // Given

        Member member = new Member("Abdel");
        member.setId(1L);

        Book book = new Book("Harry Potter", "Zahid");
        book.setId(12L);

        Mockito.when(bookRepository.findById(12L)).thenReturn(Optional.of(book));
        Mockito.when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        Mockito.when(bookRepository.findByMemberAndStatus(1L, Status.EMPRUNTE)).thenReturn(List.of());
        Mockito.when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When

        Book result = bookService.borrowBook(12L, 1L);

        // Then

        assertThat(result.getStatus()).isEqualTo(Status.EMPRUNTE);
        assertThat(result.getBorrower()).isEqualTo(member);
        verify(mailService).sendMail(eq(member), anyString(), anyString());
    }
}
