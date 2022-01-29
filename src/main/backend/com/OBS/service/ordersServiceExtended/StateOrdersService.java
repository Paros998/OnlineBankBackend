package com.OBS.service.ordersServiceExtended;

import com.OBS.entity.Order;
import com.OBS.repository.OrderRepository;
import com.OBS.service.ClientService;
import com.OBS.service.EmployeeService;
import com.OBS.service.OrderService;
import com.OBS.service.interfaces.SystemFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.json.bind.Jsonb;

@Service
public class StateOrdersService extends OrderService {
    @Autowired
    public StateOrdersService(OrderRepository orderRepository, SystemFacade systemService, EmployeeService employeeService, ClientService clientService, Jsonb jsonb) {
        super(orderRepository, systemService, employeeService, clientService, jsonb);
    }

    @Override
    public void finishOrder(Long orderId, String decision) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalStateException(orderNotFound(orderId))
        );

        order.setDecision(decision);

        orderRepository.save(order);
    }
}
