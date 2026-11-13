package com.example.testit.service;

import com.example.testit.model.*;
import com.example.testit.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookServiceIT {
    @Autowired BookService bookService;
    @Autowired BookRepository bookRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    void emprunterPuisRendreUnLivre() {
        Member member = memberRepository.save(new Member("Abdel"));
        Book book = bookRepository.save(new Book("Harry Potter", "zahid"));

        bookService.borrowBook(book.getId(), member.getId());
        bookService.returnBook(book.getId(), member.getId());

        Book relu = bookRepository.findById(book.getId()).orElseThrow();
        assertThat(relu.getStatus()).isEqualTo(Status.RENDU);
        assertThat(relu.getBorrower().getId()).isEqualTo(member.getId());
    }
}