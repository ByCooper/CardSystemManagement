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
     * Метод создает новую банковскую карту
     * @param numberCard
     * @return Card (возвращает созданную карту с номером)
     */
    @Override
    public Card getAddCard(Long numberCard) {
        Card card = Card.builder()
                .cardNumber(numberCard)
                .build();
        cardRepository.save(card);
        return card;
    }

    /**
     * Метод блокирует карту
     * @param cardId
     * @return Информационное сообщение о блокировке карты с отображением номера карты
     */
    @Override
    public String getBlockCard(Long cardId) {
        Card card = entityManager.find(Card.class, cardId);
        if (!card.getStatusCard().equals(StatusCard.BLOCK)) {
            card.setStatusCard(StatusCard.BLOCK);
        }
        return "Card with number = " + card.getCardNumber() + " is blocked";
    }


    /**
     * Метод активирует карту
     * @param cardId
     * @return Информационное сообщение об активации карты с отображением номера карты
     */
    @Override
    public String getActivateCard(Long cardId) {
        Card card = entityManager.find(Card.class, cardId);
        if (card.getStatusCard() == null) {
            card.setStatusCard(StatusCard.ACTIVE);
            card.setDateActive(LocalDateTime.now());
        }
        return "Card with number = " + card.getCardNumber() + " is activated";
    }

    /**
     * Метод удаляет карту из базы данных
     * @param cardId
     */
    @Override
    public void getDeleteCard(Long cardId) {
        Card card = entityManager.find(Card.class, cardId);
        if (card != null) {
            entityManager.remove(card);
        } else {
            throw new RuntimeException("Card not found " + card);
        }
    }

    /**
     * Метод отображает все карты из базы данных
     * @return
     */
    @Override
    public Collection<Card> getAllCard() {
        return cardRepository.findAll();
    }

    /**
     * Метод присваивает карту пользователю
     * @param userId
     * @param cardId
     * @return Возвращает пользователя. Отображает информацию о пользователе и актуальный список привязанных карт
     */
    @Override
    public User getAddCardToClient(Long userId, Long cardId) {
        User user = entityManager.find(User.class, userId);
        Card card = entityManager.find(Card.class, cardId);
        user.addCard(card);
        return user;
    }

    /**
     * Метод отвязывает карту от пользователя
     * @param userId
     * @param cardId
     * @return Возвращает пользователя. Отображает информацию о пользователе и актуальный список привязанных карт
     */
    @Override
    public User getDeleteCardToClient(Long userId, Long cardId) {
        Card card = entityManager.find(Card.class, cardId);
        User user = entityManager.find(User.class, userId);
        user.getCards().remove(card);
        return user;
    }

    /**
     * Метод осуществляет перевод денежных средств между картами пользователя
     * @param cardIdOutput
     * @param cardIdInput
     * @param sum
     */
    @Override
    public void getTrafficCash(Long cardIdOutput, Long cardIdInput, double sum) {
        Card cardOut = entityManager.find(Card.class, cardIdOutput);
        Card cardIn = entityManager.find(Card.class, cardIdInput);
        cardOut.setBalanceCard(cardOut.getBalanceCard() - sum);
        cardIn.setBalanceCard(cardIn.getBalanceCard() + sum);
    }

    /**
     * Метод отображает состояние баланса карты
     * @param cardId
     * @return Возвращает сумму денежных средств
     */
    @Override
    public Double getBalance(Long cardId) {
        return entityManager.find(Card.class, cardId).getBalanceCard();
    }

    /**
     * Метод возвращает список карт пользователя, которые привязаны к пользователю
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Card> getAllCardsToUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findById(userId, pageable);
    }

    /**
     * Приватный метод, используемый в переопределенном методе для удаления карты
     * @param cards
     * @param card
     * @return
     */
    private Set<Card> deleteCard(Set<Card> cards, Card card) {
        cards.remove(card);
        return cards;
    }
}
