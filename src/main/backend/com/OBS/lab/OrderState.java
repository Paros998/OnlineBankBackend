package com.OBS.lab;

public interface OrderState {
    void action(String decision, OrderContext orderContext);
}
