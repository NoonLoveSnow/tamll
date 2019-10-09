package com.noon.shop.service;

import com.noon.shop.dao.OrderItemDAO;
import com.noon.shop.pojo.Order;
import com.noon.shop.pojo.OrderItem;
import com.noon.shop.pojo.Product;
import com.noon.shop.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.List;

@Service
public class OrderItemService {
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;

    public void fill(Order order) {
        List<OrderItem> orderItems = orderItemDAO.findByOrderOrderByIdDesc(order);
        float totalPrice = 0;
        int totalNumber = 0;
        for (OrderItem orderItem : orderItems
                ) {
            totalPrice += orderItem.getProduct().getPromotePrice() * orderItem.getNumber();
            totalNumber += orderItem.getNumber();
            productImageService.setFirstProductImage(orderItem.getProduct());
        }
        order.setTotal(totalPrice);
        order.setTotalNumber(totalNumber);
        order.setOrderItems(orderItems);
    }

    public void fill(List<Order> orders) {
        for (Order order :
                orders) {
            fill(order);
        }
    }
    public OrderItem get(int id){
        return orderItemDAO.getOne(id);
    }
    public void add(OrderItem orderItem){
        orderItemDAO.save(orderItem);
    }
    public void update(OrderItem orderItem){
        orderItemDAO.save(orderItem);
    }
    public void delete(int id){
        orderItemDAO.deleteById(id);
    }
    public int getSaleCount(Product product){
        List<OrderItem> ois=orderItemDAO.findByProduct(product);
        int count=0;
        for (OrderItem oi:ois
             ) {
            if (null != oi.getOrder() && null != oi.getOrder().getPayDate()) {//付了款才算买了
                count += oi.getNumber();
            }
        }
        return count;
        }
    public List<OrderItem> listByProduct(Product product){
        return orderItemDAO.findByProduct(product);
    }
    public List<OrderItem> listByOrder(Order order){
        return  orderItemDAO.findByOrderOrderByIdDesc(order);
    }
    public List<OrderItem> listByUser(User user){
        return orderItemDAO.findByUserAndOrderIsNull(user);
    }
}
