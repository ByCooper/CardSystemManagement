package com.work_test.www.repo;

import com.work_test.www.model.Card;
import com.work_test.www.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long userId);
    Page<Card> findById(Long userId, Pageable pageable);
}
