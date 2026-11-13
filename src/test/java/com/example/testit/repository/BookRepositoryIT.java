package com.example.testit.repository;

import com.example.testit.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryIT {
    @Autowired BookRepository bookRepository;
    @Autowired MemberRepository memberRepository;

    private Book livre(String titre, Status s, Member m) {
        Book b = new Book(titre, "zahid"); b.setStatus(s); b.setBorrower(m);
        return bookRepository.save(b);
    }

    @Test
    void findByMemberAndStatusAvecDeuxMembres() {
        Member abdel = memberRepository.save(new Member("abdel"));
        Member alexi = memberRepository.save(new Member("alexi"));
        Book saison1Abdel = livre("Harry Potter saison 1", Status.EMPRUNTE, abdel);
        livre("Harry Potter saison 2", Status.RENDU, abdel);
        livre("Harry Potter saison 1", Status.EMPRUNTE, alexi);
        livre("Harry Potter saison 2", Status.DISPONIBLE, null);

        assertThat(bookRepository.findByMemberAndStatus(abdel.getId(), Status.EMPRUNTE))
            .map(Book::getId)
            .containsExactly(saison1Abdel.getId());
    }
}