package com.OBS.lab;

public class InProgressOrder implements OrderState {
    @Override
    public void action(String newDecision, OrderContext orderContext) {
        orderContext.isActive = true;
    }
}
