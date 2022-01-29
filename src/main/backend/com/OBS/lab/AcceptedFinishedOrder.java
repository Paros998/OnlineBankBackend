package com.OBS.lab;

import com.OBS.entity.Order;
import com.OBS.enums.OrderType;

import java.util.Objects;

public class AcceptedFinishedOrder implements OrderState {
    @Override
    public void action(String decision, OrderContext orderContext) {
        for (OrderType orderType : OrderType.values()) {
            if (Objects.equals(orderContext.orderType, orderType.getType())) {
                orderType.finishOrder(orderContext.systemService, orderContext.jsonb, new Order());
            }
        }
        orderContext.isActive = false;
        orderContext.decision = decision;
    }
}
