package com.OBS.entity;

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
    private String accountNumber;
    private Boolean isActive;
    private String number;
    private LocalDate expireDate;
    private int cvvNumber;
    private int pinNumber;
    private String cardImage;

    public Client getManyToOne() {
        return manyToOne;
    }

    public void setManyToOne(Client manyToOne) {
        this.manyToOne = manyToOne;
    }

    @ManyToOne
    private Client manyToOne;
}
