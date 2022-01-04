package com.OBS;

import com.OBS.entity.Order;

public class Tester {
    public static void main(String[] args) {
        Order order = new Order();
        System.out.println(order.getEmployee());
        System.out.println(order.getEmployee() == null);
    }
}
