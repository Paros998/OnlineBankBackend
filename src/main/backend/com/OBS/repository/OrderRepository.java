package com.OBS.repository;

import com.OBS.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o where o.isActive = true and o.createDate < :date")
    List<Order> findAllPriorityOrders(@Param("date")LocalDate today);
}
