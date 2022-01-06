package com.OBS.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "credit_cards")
public class CreditCard {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            nullable = false,
            updatable = false
    )
    private Long cardId;

    private Boolean isActive;
    private String cardNumber;
    private LocalDate expireDate;
    private int cvvNumber;
    private String pinNumber;

    private String cardImage;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public CreditCard() {

    }
}
