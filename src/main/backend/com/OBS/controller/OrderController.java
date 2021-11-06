package com.OBS.controller;

import com.OBS.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    //TODO add endpoints
}
