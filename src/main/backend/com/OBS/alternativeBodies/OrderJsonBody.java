package com.OBS.alternativeBodies;

import com.OBS.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderJsonBody {
    private Order order;
    private String requestBody;
}
