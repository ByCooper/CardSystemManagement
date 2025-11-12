package com.work_test.www.service.impl;

import com.work_test.www.model.Card;
import com.work_test.www.model.StatusCard;
import com.work_test.www.model.User;
import com.work_test.www.repo.CardRepository;
import com.work_test.www.repo.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceManagementOperationImplTest {

    @InjectMocks
    ServiceManagementOperationImpl serviceManagementOperation;

    @Mock
    CardRepository cardRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    EntityManager entityManager;

    @Test
    void TestGetAddCard() {
        //Подготовка ввода данных
        Card expectedCard = Card.builder()
                .cardNumber(1111222233334444L)
                .statusCard(StatusCard.ACTIVE)
                .build();

        //Подготовка ожидаемого результата
        when(cardRepository.findByCardnumber(anyLong())).thenReturn(Optional.ofNullable(expectedCard));
        Card actualCard = serviceManagementOperation.getAddCard(1111222233334444L);

        //Запуск теста
        assertEquals(actualCard, expectedCard);
    }

    @Test
    void TestGetBlockCard() {
        //Подготовка ввода данных
        Card card = Card.builder()
                .id(1L)
                .cardNumber(1111222233334444L)
                .statusCard(StatusCard.ACTIVE)
                .build();
        String expectedCard = "Card with number = " + 1111222233334444L + " is blocked";

        //Подготовка ожидаемого результата
        when(entityManager.find(Card.class, card.getId())).thenReturn(card);
        String actualCard = serviceManagementOperation.getBlockCard(1L);

        //Запуск теста
        assertEquals(expectedCard, actualCard);
    }

    @Test
    void TestGetActivateCard() {
        //Подготовка ввода данных
        Card card = Card.builder()
                .id(1L)
                .cardNumber(1111222233334444L)
                .statusCard(StatusCard.ACTIVE)
                .build();
        String expectedCard = "Card with number = " + 1111222233334444L + " is activated";

        //Подготовка ожидаемого результата
        when(entityManager.find(Card.class, card.getId())).thenReturn(card);
        String actualCard = serviceManagementOperation.getActivateCard(1L);

        //Запуск теста
        assertEquals(expectedCard, actualCard);
    }

    @Test
    void TestGetDeleteCard() {
        //Подготовка ввода данных
        Card card = Card.builder()
                .id(1L)
                .cardNumber(1111222233334444L)
                .statusCard(StatusCard.ACTIVE)
                .build();
        String expectedCard = "Удалена карта " + 1111222233334444L;

        //Подготовка ожидаемого результата
        when(entityManager.find(Card.class, card.getId())).thenReturn(card);
        String actualCard = serviceManagementOperation.getDeleteCard(1L);

        //Запуск теста
        assertEquals(expectedCard, actualCard);
    }

    @Test
    void TestGetAllCard() {
        //Подготовка ввода данных
        Card card = Card.builder()
                .id(1L)
                .cardNumber(1111222233334444L)
                .statusCard(StatusCard.ACTIVE)
                .build();
        List<Card> expected = new ArrayList<>();
        expected.add(card);

        //Подготовка ожидаемого результата
        when(cardRepository.findAll()).thenReturn(expected);
        Collection<Card> actual = serviceManagementOperation.getAllCard();

        //Запуск теста
        assertEquals(expected, actual);
    }

    @Test
    void TestGetAddCardToClient() {
        //Подготовка ввода данных
        Card card = Card.builder()
                .id(1L)
                .cardNumber(1111222233334444L)
                .statusCard(StatusCard.ACTIVE)
                .build();
        User user = User.builder()
                .id(1L)
                .name("Igor")
                .cards(new HashSet<>())
                .build();

        //Подготовка ожидаемого результата
        when(entityManager.find(User.class, user.getId())).thenReturn(user);
        when(entityManager.find(Card.class, card.getId())).thenReturn(card);
        User actual = serviceManagementOperation.getAddCardToClient(1L, 1L);
        user.addCard(card);

        //Запуск теста
        assertEquals(user, actual);

    }

    @Test
    void TestGetDeleteCardToClient() {
        //Подготовка ввода данных
        Card card = Card.builder()
                .id(1L)
                .cardNumber(1111222233334444L)
                .statusCard(StatusCard.ACTIVE)
                .build();
        User user = User.builder()
                .id(1L)
                .name("Igor")
                .cards(new HashSet<>())
                .build();
        user.addCard(card);

        //Подготовка ожидаемого результата
        when(entityManager.find(User.class, user.getId())).thenReturn(user);
        when(entityManager.find(Card.class, card.getId())).thenReturn(card);
        User actual = serviceManagementOperation.getDeleteCardToClient(1L, 1L);
        user.getCards().remove(card);

        //Запуск теста
        assertEquals(user, actual);
    }

    @Test
    void TestGetTrafficCash() {
        //Подготовка ввода данных
        Card cardOutput = Card.builder()
                .id(1L)
                .cardNumber(1111L)
                .statusCard(StatusCard.ACTIVE)
                .balanceCard(1000.0)
                .build();
        Card cardInput = Card.builder()
                .id(2L)
                .cardNumber(2222L)
                .statusCard(StatusCard.ACTIVE)
                .balanceCard(1000.0)
                .build();

        //Подготовка ожидаемого результата
        when(entityManager.find(Card.class, 1L)).thenReturn(cardOutput);
        when(entityManager.find(Card.class, 2L)).thenReturn(cardInput);
        String actual = serviceManagementOperation.getTrafficCash(1L, 2L, 500.0);
        String expected = "Перевод между счетами " + '\n' + "Пополнение карты " + cardInput.getCardNumber() + " на сумму: " + 500.0 + '\n' + "Списание с карты " + cardOutput.getCardNumber() + " на сумму: " + 500.0;

        //Запуск теста
        assertEquals(expected, actual);
    }

    @Test
    void TestGetBalance() {
        //Подготовка ввода данных
        Card card = Card.builder()
                .id(1L)
                .cardNumber(1111222233334444L)
                .statusCard(StatusCard.ACTIVE)
                .balanceCard(1000.0)
                .build();
        Double expected = card.getBalanceCard();

        //Подготовка ожидаемого результата
        when(entityManager.find(Card.class, 1L)).thenReturn(card);
        Double actual = serviceManagementOperation.getBalance(1L);

        //Запуск теста
        assertEquals(expected, actual);
    }

    @Test
    void TestGetAllCardsToUser() {
        //Подготовка ввода данных

        //Подготовка ожидаемого результата

        //Запуск теста
    }
}