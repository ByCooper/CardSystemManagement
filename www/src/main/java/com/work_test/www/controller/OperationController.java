package com.work_test.www.controller;

import com.work_test.www.model.Card;
import com.work_test.www.model.User;
import com.work_test.www.repo.CardRepository;
import com.work_test.www.repo.UserRepository;
import com.work_test.www.service.ServiceManagementOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bank")
public class OperationController {


    @GetMapping("/hello")
    @Operation(summary = "Приветствие")
    public String getHi(){
        return "Hello Bitch!";
    }

    @PostMapping("/gou")
    @Operation(summary = "Просмотреть карту по id")
    public Optional<Card> getGou(@RequestParam Long id){
        return cardRepository.findById(id);
    }

    @PostMapping("/getUser")
    @Operation(summary = "Получение пользователя")
    public Optional<User> getUser(@RequestParam Long id){
        return userRepository.findById(id);
    }

    private final ServiceManagementOperation service;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    public OperationController(ServiceManagementOperation service, UserRepository userRepository, CardRepository cardRepository) {
        this.service = service;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    @PostMapping("/create-card")
    @Operation(summary = "Создание карты")
    public ResponseEntity<Card> getAddCard(@RequestBody Long numberCard) {
        if (cardRepository.findById(numberCard).isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(service.getAddCard(numberCard));
    }

    @PostMapping("/block-card")
    @Operation(summary = "Блокирование карты")
    public ResponseEntity<String> getBlockCard(@RequestBody Long numberCard) {
        if (cardRepository.findById(numberCard).isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(service.getBlockCard(numberCard));
    }

    @PostMapping("/activate-card")
    @Operation(summary = "Активация карты")
    public ResponseEntity<String> getActivateCard(@RequestBody Long numberCard) {
        if (cardRepository.findById(numberCard).isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(service.getActivateCard(numberCard));
    }

    @PostMapping("/delete-card")
    @Operation(summary = "Удаление карты")
    public ResponseEntity<?> getDeleteCard(@RequestBody Long numberCard) {
        if (cardRepository.findById(numberCard).isEmpty()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            service.getDeleteCard(numberCard);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-to-user")
    @Operation(summary = "Присваивание карты клиенту")
    public ResponseEntity<User> getAddCardToClient(@RequestParam("userId") Long userId,
                                                   @RequestParam("cardId") Long cardId) {
        if (userRepository.findById(userId).isPresent() || cardRepository.findById(cardId).isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(service.getAddCardToClient(userId, cardId));
    }

    @PostMapping("/delete-to-user")
    @Operation(summary = "Удаление карты из списка карт клиента")
    public ResponseEntity<?> getDeleteCardToClient(@RequestParam("userId") Long userId,
                                                   @RequestParam("cardId") Long cardId) {
        if (userRepository.findById(userId).isPresent() || cardRepository.findById(cardId).isPresent()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(service.getDeleteCardToClient(userId, cardId));
    }

    @GetMapping("/all-card")
    @Operation(summary = "Получение всех имеющихся карт")
    public ResponseEntity<List<Card>> getAllCard(){
        List<Card> list = service.getAllCard().stream().toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/page")
    public Page<Card> getAllCardsToUser(@RequestParam int page,
                                        @RequestParam int size,
                                        @RequestParam Long userId) {
        return service.getAllCardsToUser(userId, page, size);
    }

//    @GetMapping("/user/card/all")
//    public ResponseEntity<List<Card>> getUserCard(Authentication authentication) {
//        return ResponseEntity.ok(service.getUserCard(authentication).stream().toList());
//    }
//
//    @PostMapping("/block/card")
//    public ResponseEntity<?> getBlockCard(@RequestParam String numberCard,
//                                          Authentication authentication) {
//        service.getBlockCard(numberCard, authentication);
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/card/traffic")
    @Operation(summary = "Перевод средств между своими счетами карт")
    public ResponseEntity<?> getTrafficCash(@RequestParam Long numberOutput,
                                            @RequestParam Long numberInput,
                                            @RequestParam Double sum) {
        service.getTrafficCash(numberOutput, numberInput, sum);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/card/balance/{numberCard}")
    @Operation(summary = "Получение баланса карты по номеру карты")
    public ResponseEntity<Double> getBalance(@PathVariable("numberCard") Long numberCard){
        return ResponseEntity.ok(service.getBalance(numberCard));
    }

}
