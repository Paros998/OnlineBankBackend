package com.OBS.alternativeBodies;

import com.OBS.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCreditCardModel {
    private Boolean isActive;
    private String cardNumber;
    private LocalDate expireDate;
    private int cvvNumber;
    private String pinNumber;
    private String cardImage;
    private Client client;
}
