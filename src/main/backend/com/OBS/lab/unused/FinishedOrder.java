package com.OBS.lab.unused;

public class FinishedOrder implements OrderState {
    @Override
    public void action(String decision, OrderContext orderContext) {
        orderContext.decision = decision;
    }
}
