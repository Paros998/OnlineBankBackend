package com.OBS.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransferCategory {
    SALARY("Wynagrodzenie"),
    BILLS("Rachunki"),
    COMMON_EXPENSES("Wydatki Bieżące"),
    ENTERTAINMENT("Rozrywka"),
    HEALTHCARE("Zdrowie"),
    OTHERS("Inne");

    private final String category;
}
