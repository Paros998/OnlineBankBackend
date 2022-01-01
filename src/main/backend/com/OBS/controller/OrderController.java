package com.OBS.controller;

import com.OBS.entity.Order;
import com.OBS.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping(path = "{orderId}")
    public Order getOrder(@PathVariable Long orderId){return orderService.getOrder(orderId);}

    @PostMapping()
    public void addOrder(@RequestBody Order order,@RequestParam("requestBody") String requestBody){
        orderService.addOrder(order,requestBody);
    }

    @PutMapping(path = "{orderId}")
    public void finishOrder(@PathVariable Long orderId,@RequestParam("decision") String decision){
        orderService.finishOrder(orderId,decision);
    }

    @DeleteMapping(path = "{orderId}")
    public void deleteOrder(@PathVariable Long orderId){
        orderService.deleteOrder(orderId);
    }
}
