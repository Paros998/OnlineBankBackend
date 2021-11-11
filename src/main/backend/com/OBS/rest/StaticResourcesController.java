package com.OBS.rest;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(path = "/rest")
public class StaticResourcesController {

    @Value("classpath:public/NewVisits.json")
    Resource NewVisit;

    @Value("classpath:public/OrderType.json")
    Resource OrderType;

    @Value("classpath:public/TransferCategory.json")
    Resource TransferCategory;

    @GetMapping(path = "/visits")
    public Resource getVisits(){
        return NewVisit;
    }

    @GetMapping(path = "/orders")
    public Resource getOrders(){
        return OrderType;
    }

    @GetMapping(path = "/transfers")
    public Resource getTransfers(){
        return TransferCategory;
    }
}
