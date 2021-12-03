package com.OBS.service;

import com.OBS.entity.Order;
import com.OBS.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.OBS.enums.OrderType.*;
import static com.OBS.auth.AppUserRole.*;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private String orderNotFound(Long id) { return "Order with id: " + id + " doesn't exist in database";}

    public List<Order> getOrders(String role) {
        if(Objects.equals(role, ADMIN.name())) {
            return orderRepository.findAll();
        }else{
            List<Order> orders =  orderRepository.findAll();
            orders.removeIf(order ->
                    order.getOrderType().equals(changeUser.name())
                            || order.getOrderType().equals(changeEmployee.name())
                            || order.getOrderType().equals(createUser.name())
            );
            return orders;
        }
    }

    public List<Order> getPriorityOrders(String role) {
        LocalDate today = LocalDate.now();
        if(Objects.equals(role, ADMIN.name())) {
            return orderRepository.findAllPriorityOrders(today);
        }else{
            List<Order> orders =  orderRepository.findAllPriorityOrders(today);
            orders.removeIf(order ->
                    order.getOrderType().equals(changeUser.name())
                            || order.getOrderType().equals(changeEmployee.name())
                            || order.getOrderType().equals(createUser.name())
            );
            return orders;
        }
    }


    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalStateException(orderNotFound(orderId))
        );
    }

    public void addOrder(Order order) {
        order.setIsActive(true);
        orderRepository.save(order);
    }

    public void deleteOrder(Long orderId) {
        if(!orderRepository.existsById(orderId))
            throw new IllegalStateException(orderNotFound(orderId));
        orderRepository.deleteById(orderId);
    }

    @Transactional
    public void setInactive(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalStateException(orderNotFound(orderId))
        );
        order.setIsActive(false);
        orderRepository.save(order);
    }


}
