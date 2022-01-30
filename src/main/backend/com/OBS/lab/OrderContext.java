package com.OBS.lab;

import com.OBS.service.interfaces.SystemFacade;

import javax.json.bind.Jsonb;
import java.util.Objects;

public class OrderContext {
    public String decision;
    public boolean isActive;
    public SystemFacade systemService;
    public Jsonb jsonb;
    public String orderType;

    private OrderState state;

    public OrderContext(String decision) {
        setDecision(decision);
    }

    public void setDecision(String decision) {
        if (Objects.equals(decision, "accepted")) {
            state = new AcceptedFinishedOrder();
        } else if (Objects.equals(decision, "denied")) {
            state = new FinishedOrder();
        } else {
            state = new InProgressOrder();
        }

        state.action(decision, this);
    }
}
