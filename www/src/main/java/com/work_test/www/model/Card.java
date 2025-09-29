package com.work_test.www.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cardNumber;
    @ManyToOne
    @JoinColumn(name = "cardholder_id")
    @JsonIgnore
    private User cardHolder;
    private LocalDateTime dateActive;
    @Enumerated(EnumType.STRING)
    private StatusCard statusCard;
    private Double balanceCard;
}
