package com.OBS.alternativeBodies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FilterTransferFromClient {
    LocalDateTime dateFrom,dateTo;
    String transferType,transferCategory;
}
