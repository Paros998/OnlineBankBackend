package com.OBS.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransferType {
    INCOMING("Przychody"),
    OUTGOING("Wydatki");

    private final String typ;
}
