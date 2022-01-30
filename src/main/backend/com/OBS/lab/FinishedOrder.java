package com.OBS.lab;

public class FinishedOrder implements OrderState {
    @Override
    public void action(String decision, OrderContext orderContext) {
        orderContext.decision = decision;
    }
}
