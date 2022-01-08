package com.OBS.alternativeBodies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientCreditWorthiness {
    Float sumOfIncoming;
    Float sumOfOutgoing;
    Float monthlyBalance;
    Float sumOfBalance;
}
