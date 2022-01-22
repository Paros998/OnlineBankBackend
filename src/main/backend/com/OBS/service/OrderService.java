package com.OBS.service;

import com.OBS.entity.Client;
import com.OBS.entity.Employee;
import com.OBS.entity.Order;
import com.OBS.enums.OrderType;
import com.OBS.enums.SearchOperation;
import com.OBS.lab.BuilderSpecification;
import com.OBS.lab.ImplementedSpecification;
import com.OBS.repository.OrderRepository;
import com.OBS.searchers.SearchCriteria;
import com.OBS.service.interfaces.SystemFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import javax.json.bind.Jsonb;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.OBS.auth.AppUserRole.ADMIN;
import static com.OBS.enums.OrderType.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final SystemFacade systemService;
    private final EmployeeService employeeService;
    private final ClientService clientService;
    private final Jsonb jsonb;
    private ImplementedSpecification<Order> specification;

    public ImplementedSpecification<Order> getSpecification() {
        return specification;
    }

    public void setSpecification(ImplementedSpecification<Order> specification) {
        this.specification = specification;
    }

    private String orderNotFound(Long id) {
        return "Order with id: " + id + " doesn't exist in database";
    }

    public List<Order> getOrders(String role) {
         specification = new BuilderSpecification<Order>()
                .add(new SearchCriteria("createDate", LocalDateTime.now().minusDays(1), SearchOperation.GREATER_THAN_DATE))
                .add(new SearchCriteria("employee", null, SearchOperation.EQUAL_NULL));
        if (!Objects.equals(role, ADMIN.name())) {
            ((BuilderSpecification<Order>) specification).add(new SearchCriteria("orderType", changeEmployee.toString(), SearchOperation.NOT_EQUAL))
                    .add(new SearchCriteria("orderType", changeUser.toString(), SearchOperation.NOT_EQUAL))
                    .add(new SearchCriteria("orderType", createUser.toString(), SearchOperation.NOT_EQUAL));
        }
        return orderRepository.findAll(specification, Sort.by(Sort.Direction.ASC,"createDate"));
    }

    public List<Order> getPriorityOrders(String role) {
        BuilderSpecification<Order> priorityOrdersSpecifications = new BuilderSpecification<Order>()
                .add(new SearchCriteria("createDate", LocalDateTime.now().minusDays(1), SearchOperation.LESS_THAN_EQUAL_DATE))
                .add(new SearchCriteria("employee",null, SearchOperation.EQUAL_NULL));
        if (!Objects.equals(role, ADMIN.name())) {
            priorityOrdersSpecifications = priorityOrdersSpecifications
                    .add(new SearchCriteria("orderType", changeEmployee.toString(), SearchOperation.NOT_EQUAL))
                    .add(new SearchCriteria("orderType", changeUser.toString(), SearchOperation.NOT_EQUAL))
                    .add(new SearchCriteria("orderType", createUser.toString(), SearchOperation.NOT_EQUAL));
        }
        return orderRepository.findAll(priorityOrdersSpecifications, Sort.by(Sort.Direction.ASC,"createDate"));
    }

    public List<Order> getEmployeeOrders(Long employeeId,boolean isActive) {
        BuilderSpecification<Order> employeeOrdersSpecifications = new BuilderSpecification<Order>()
                .add(new SearchCriteria("employee",employeeService.getEmployee(employeeId),SearchOperation.EQUAL))
                .add(new SearchCriteria("isActive", isActive,SearchOperation.EQUAL));
        return orderRepository.findAll(employeeOrdersSpecifications, Sort.by(Sort.Direction.ASC,"createDate"));
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
           String type = order.getOrderType();
           for(OrderType orderType: OrderType.values())
               if(Objects.equals(type, orderType.getType()))
                   orderType.finishOrder(systemService,jsonb,order);
        }

        order.setIsActive(false);
        order.setDecision(decision);
        orderRepository.save(order);
    }


    public List<Order> getClientOrders(Long clientId) {
        Client client = clientService.getClient(clientId);
        BuilderSpecification<Order> findAllByClient = new BuilderSpecification<Order>()
                .add(new SearchCriteria("client",client,SearchOperation.EQUAL));
        return orderRepository.findAll(findAllByClient, Sort.by(Sort.Direction.ASC,"createDate"));
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
