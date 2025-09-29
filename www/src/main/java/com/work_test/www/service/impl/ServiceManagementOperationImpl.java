package com.work_test.www.service.impl;

import com.work_test.www.model.Card;
import com.work_test.www.model.StatusCard;
import com.work_test.www.model.User;
import com.work_test.www.repo.CardRepository;
import com.work_test.www.repo.UserRepository;
import com.work_test.www.service.ServiceManagementOperation;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Service
@Transactional
public class ServiceManagementOperationImpl implements ServiceManagementOperation {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    public ServiceManagementOperationImpl(CardRepository cardRepository, UserRepository userRepository, EntityManager entityManager) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    /**
     *
     * @param numberCard
     * @return
     */
    @Override
    public Card getAddCard(Long numberCard) {
        Card card = Card.builder()
                .cardNumber(numberCard)
                .build();
        cardRepository.save(card);
        return card;
    }

    @Override
    public String getBlockCard(Long cardId) {
        Card card = entityManager.find(Card.class, cardId);
        if (!card.getStatusCard().equals(StatusCard.BLOCK)) {
            card.setStatusCard(StatusCard.BLOCK);
        }
        return "Card with number = " + card.getCardNumber() + " is blocked";
    }

    @Override
    public String getActivateCard(Long cardId) {
        Card card = entityManager.find(Card.class, cardId);
        if (card.getStatusCard() == null) {
            card.setStatusCard(StatusCard.ACTIVE);
            card.setDateActive(LocalDateTime.now());
        }
        return "Card with number = " + card.getCardNumber() + " is activated";
    }

    @Override
    public void getDeleteCard(Long cardId) {
        Card card = entityManager.find(Card.class, cardId);
        if (card != null) {
            entityManager.remove(card);
        } else {
            throw new RuntimeException("Card not found " + card);
        }
    }

    @Override
    public Collection<Card> getAllCard() {
        return cardRepository.findAll();
    }

    @Override
    public User getAddCardToClient(Long userId, Long cardId) {
        User user = entityManager.find(User.class, userId);
        Card card = entityManager.find(Card.class, cardId);
        user.addCard(card);
        return user;
    }

    @Override
    public User getDeleteCardToClient(Long userId, Long cardId) {
        Card card = entityManager.find(Card.class, cardId);
        User user = entityManager.find(User.class, userId);
        user.getCards().remove(card);
        return user;
    }

    @Override
    public void getTrafficCash(Long cardIdOutput, Long cardIdInput, double sum) {
        Card cardOut = entityManager.find(Card.class, cardIdOutput);
        Card cardIn = entityManager.find(Card.class, cardIdInput);
        cardOut.setBalanceCard(cardOut.getBalanceCard() - sum);
        cardIn.setBalanceCard(cardIn.getBalanceCard() + sum);
    }

    @Override
    public Double getBalance(Long cardId) {
        return entityManager.find(Card.class, cardId).getBalanceCard();
    }

    @Override
    public Page<Card> getAllCardsToUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findById(userId, pageable);
    }

    private Set<Card> deleteCard(Set<Card> cards, Card card) {
        cards.remove(card);
        return cards;
    }
}
