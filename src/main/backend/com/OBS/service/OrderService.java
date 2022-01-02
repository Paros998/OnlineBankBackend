package com.OBS.service;

import com.OBS.auth.entity.AppUser;
import com.OBS.entity.*;
import com.OBS.repository.OrderRepository;
import com.OBS.requestBodies.LoanBody;
import com.OBS.requestBodies.UserCredentials;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.json.bind.Jsonb;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.OBS.auth.AppUserRole.ADMIN;
import static com.OBS.enums.OrderType.*;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final SystemService systemService;
    private final Jsonb jsonb;

    private String orderNotFound(Long id) {
        return "Order with id: " + id + " doesn't exist in database";
    }

    public List<Order> getOrders(String role) {
        if (Objects.equals(role, ADMIN.name())) {
            return orderRepository.findAll();
        } else {
            List<Order> orders = orderRepository.findAll();
            orders.removeIf(order ->
                    order.getOrderType().equals(changeUser.name())
                            || order.getOrderType().equals(changeEmployee.name())
                            || order.getOrderType().equals(createUser.name())
            );
            return orders;
        }
    }

    public List<Order> getPriorityOrders(String role) {
        LocalDateTime today = LocalDateTime.now();
        if (Objects.equals(role, ADMIN.name())) {
            return orderRepository.findAllPriorityOrders(today);
        } else {
            List<Order> orders = orderRepository.findAllPriorityOrders(today);
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

    public void addOrder(Order order, String requestBody) {
        String newRequestBody = "";
        switch (order.getOrderType()) {
            case "Utworzenie użytkownika":
                newRequestBody = jsonb.toJson(requestBody, UserCredentials.class);
                break;
            case "Edycja danych klienta":
                newRequestBody = jsonb.toJson(requestBody, Client.class);
                break;
            case "Modyfikacja użytkownika":
                newRequestBody = jsonb.toJson(requestBody, AppUser.class);
                break;
            case "Modyfikacja danych pracownika":
                newRequestBody = jsonb.toJson(requestBody, Employee.class);
                break;
            case "Zablokowanie karty kredytowej":
            case "Wycofanie karty kredytowej":
            case "Wyrób nowej karty kredytowej":
            case "Odblokowanie karty kredytowej":
                newRequestBody = jsonb.toJson(requestBody, CreditCard.class);
                break;
            case "Podanie o kredyt":
                newRequestBody = jsonb.toJson(requestBody, Loan.class);
                break;
        }

        order.setRequestBody(newRequestBody);
        order.setIsActive(true);
        orderRepository.save(order);
    }

    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId))
            throw new IllegalStateException(orderNotFound(orderId));
        orderRepository.deleteById(orderId);
    }

    @Transactional
    public void finishOrder(Long orderId, String decision) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalStateException(orderNotFound(orderId))
        );

        if(Objects.equals(decision,"accepted")){
            switch (order.getOrderType()) {
                case "Utworzenie użytkownika":
                    systemService.createNewUser(jsonb.fromJson(order.getRequestBody(),UserCredentials.class));
                    break;
                case "Edycja danych klienta":
                    systemService.updateClient(jsonb.fromJson(order.getRequestBody(),Client.class));
                    break;
                case "Modyfikacja użytkownika":
                    systemService.updateAppUser(jsonb.fromJson(order.getRequestBody(),AppUser.class));
                    break;
                case "Modyfikacja danych pracownika":
                    systemService.updateEmployee(jsonb.fromJson(order.getRequestBody(),Employee.class));;
                    break;
                case "Zablokowanie karty kredytowej":
                    systemService.blockCreditCard(jsonb.fromJson(order.getRequestBody(),CreditCard.class));
                    break;
                case "Wycofanie karty kredytowej":
                    systemService.discardCreditCard(jsonb.fromJson(order.getRequestBody(),CreditCard.class));
                    break;
                case "Wyrób nowej karty kredytowej":
                    systemService.createCreditCard(jsonb.fromJson(order.getRequestBody(),CreditCard.class));
                    break;
                case "Odblokowanie karty kredytowej":
                    systemService.unblockCreditCard(jsonb.fromJson(order.getRequestBody(),CreditCard.class));
                    break;
                case "Podanie o kredyt":
                    systemService.createLoan(jsonb.fromJson(order.getRequestBody(), Loan.class));
                    break;
            }
        }

        order.setIsActive(false);
        order.setDecision(decision);
        orderRepository.save(order);
    }


    public List<Order> getClientOrders(Long clientId) {
        return orderRepository.findAllByClient_clientId(clientId);
    }
}
