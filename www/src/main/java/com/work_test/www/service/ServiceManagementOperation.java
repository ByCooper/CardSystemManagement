package com.work_test.www.service;

import com.work_test.www.model.Card;
import com.work_test.www.model.User;
import org.springframework.data.domain.Page;

import java.util.Collection;

public interface ServiceManagementOperation {
    Card getAddCard(Long cardId);
    String getBlockCard(Long cardId);
    String getActivateCard(Long cardId);
    String getDeleteCard(Long cardId);
    Collection<Card> getAllCard();

    User getAddCardToClient(Long userId, Long cardId);
    User getDeleteCardToClient(Long userId, Long cardId);
    //    Collection<Card> getUserCard(Authentication authentication);
//    void getBlockCard(String numberCard, Authentication authentication);
    String getTrafficCash(Long cardIdOutput, Long cardIdInput, double sum);
    Double getBalance(Long numberCard);
    Page<Card> getAllCardsToUser(Long userId, int page, int size);
}
