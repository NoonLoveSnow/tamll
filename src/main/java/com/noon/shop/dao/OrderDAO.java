package com.noon.shop.dao;

import com.noon.shop.pojo.Order;
import com.noon.shop.pojo.OrderItem;
import com.noon.shop.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDAO extends JpaRepository<Order,Integer> {
List<Order> findByUserAndStatusNotOrderByIdDesc(User user,String status);
}
