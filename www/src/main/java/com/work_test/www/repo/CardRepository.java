package com.work_test.www.repo;

import com.work_test.www.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findById(Long cardId);
    List<Card> findAll();
    void deleteById(Long id);
}
