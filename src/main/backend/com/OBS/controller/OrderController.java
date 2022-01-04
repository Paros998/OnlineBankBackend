package com.OBS.controller;

import com.OBS.entity.Order;
import com.OBS.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping(path = "{orderId}")
    public Order getOrder(@PathVariable Long orderId){return orderService.getOrder(orderId);}

    @GetMapping(path = "client/{clientId}")
    public List<Order> getClientOrders(@PathVariable Long clientId){return orderService.getClientOrders(clientId);}

    @GetMapping(path = "employee/{employeeId}/active")
    public List<Order> getEmployeeActiveOrders(@PathVariable Long employeeId){return orderService.getEmployeeOrders(employeeId,true);}

    @GetMapping(path = "employee/{employeeId}/inactive")
    public List<Order> getEmployeeInactiveOrders(@PathVariable Long employeeId){return orderService.getEmployeeOrders(employeeId,false);}

    @PostMapping()
    public void addOrder(@RequestBody Order order,@RequestParam("requestBody") String requestBody){
        orderService.addOrder(order,requestBody);
    }

    @PutMapping(path = "{orderId}")
    public void finishOrder(@PathVariable Long orderId,@RequestParam("decision") String decision){
        orderService.finishOrder(orderId,decision);
    }

    @PutMapping(path = "{orderId}/assign-employee/{employeeId}")
    public void assignEmployee(@PathVariable("orderId") Long orderId,@PathVariable("employeeId") Long employeeId){
        orderService.assignEmployee(orderId,employeeId);
    }

    @DeleteMapping(path = "{orderId}")
    public void deleteOrder(@PathVariable Long orderId){
        orderService.deleteOrder(orderId);
    }
}
