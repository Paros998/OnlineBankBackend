package com.OBS.service;


import com.OBS.alternativeBodies.CreateCreditCardModel;

import com.OBS.entity.*;
import com.OBS.enums.SearchOperation;
import com.OBS.repository.OrderRepository;
import com.OBS.alternativeBodies.UserCredentials;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import lombok.AllArgsConstructor;
import org.mockito.internal.matchers.Null;
import org.springframework.stereotype.Service;
import javax.json.bind.Jsonb;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.OBS.auth.AppUserRole.ADMIN;
import static com.OBS.enums.OrderType.*;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final SystemService systemService;
    private final EmployeeService employeeService;
    private final Jsonb jsonb;

    private String orderNotFound(Long id) {
        return "Order with id: " + id + " doesn't exist in database";
    }

    public List<Order> getOrders(String role) {
        Specifications<Order> normalOrdersSpecifications = new Specifications<Order>()
                .add(new SearchCriteria("createDate", LocalDateTime.now().minusDays(1), SearchOperation.GREATER_THAN_DATE))
                .add(new SearchCriteria("employee", null, SearchOperation.EQUAL_NULL));
        if (!Objects.equals(role, ADMIN.name())) {
            normalOrdersSpecifications = normalOrdersSpecifications
                    .add(new SearchCriteria("orderType", changeEmployee.toString(), SearchOperation.NOT_EQUAL))
                    .add(new SearchCriteria("orderType", changeUser.toString(), SearchOperation.NOT_EQUAL))
                    .add(new SearchCriteria("orderType", createUser.toString(), SearchOperation.NOT_EQUAL));
        }
        return orderRepository.findAll(normalOrdersSpecifications);
    }

    //TODO fix LocalDateTime
    public List<Order> getPriorityOrders(String role) {
        Specifications<Order> priorityOrdersSpecifications = new Specifications<Order>()
                .add(new SearchCriteria("createDate", LocalDateTime.now().minusDays(1), SearchOperation.LESS_THAN_EQUAL_DATE))
                .add(new SearchCriteria("employee",null, SearchOperation.EQUAL_NULL));
        if (!Objects.equals(role, ADMIN.name())) {
            priorityOrdersSpecifications = priorityOrdersSpecifications
                    .add(new SearchCriteria("orderType", changeEmployee.toString(), SearchOperation.NOT_EQUAL))
                    .add(new SearchCriteria("orderType", changeUser.toString(), SearchOperation.NOT_EQUAL))
                    .add(new SearchCriteria("orderType", createUser.toString(), SearchOperation.NOT_EQUAL));
        }
        return orderRepository.findAll(priorityOrdersSpecifications);
    }

    public List<Order> getEmployeeOrders(Long employeeId,boolean isActive) {
        Specifications<Order> employeeOrdersSpecifications = new Specifications<Order>()
                .add(new SearchCriteria("employee",employeeService.getEmployee(employeeId),SearchOperation.EQUAL))
                .add(new SearchCriteria("isActive", isActive,SearchOperation.EQUAL));
        return orderRepository.findAll(employeeOrdersSpecifications);
    }


    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalStateException(orderNotFound(orderId))
        );
    }

    public void addOrder(Order order, String requestBody) {

        order.setRequestBody(requestBody);
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
                    systemService.updateAppUser(jsonb.fromJson(order.getRequestBody(),UserCredentials.class));
                    break;
                case "Modyfikacja danych pracownika":
                    systemService.updateEmployee(jsonb.fromJson(order.getRequestBody(),Employee.class));
                    break;
                case "Zablokowanie karty kredytowej":
                    systemService.blockCreditCard(jsonb.fromJson(order.getRequestBody(),CreditCard.class));
                    break;
                case "Wycofanie karty kredytowej":
                    systemService.discardCreditCard(jsonb.fromJson(order.getRequestBody(),CreditCard.class));
                    break;
                case "Wyrób nowej karty kredytowej":
                    systemService.createCreditCard(jsonb.fromJson(order.getRequestBody(), CreateCreditCardModel.class));
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

    @Transactional
    public void assignEmployee(Long orderId, Long employeeId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                ()-> new IllegalStateException(orderNotFound(orderId))
        );
        Employee employee = employeeService.getEmployee(employeeId);
        order.setEmployee(employee);
        orderRepository.save(order);
    }


}
