package com.noon.shop.dao;

import com.noon.shop.pojo.Order;
import com.noon.shop.pojo.OrderItem;
import com.noon.shop.pojo.Product;
import com.noon.shop.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem,Integer>{
List<OrderItem> findByOrderOrderByIdDesc(Order order);
List<OrderItem> findByProduct(Product product);
List<OrderItem> findByUserAndOrderIsNull(User user);
}
