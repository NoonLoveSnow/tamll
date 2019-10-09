package com.noon.shop.service;

import com.noon.shop.dao.OrderDAO;
import com.noon.shop.dao.OrderItemDAO;
import com.noon.shop.pojo.Order;
import com.noon.shop.pojo.OrderItem;
import com.noon.shop.pojo.User;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    @Autowired
    OrderDAO orderDAO;
@Autowired
OrderItemService orderItemService;
    public Page4Navigator<Order> list(int start, int size, int navNum) {
        Pageable pageable = PageRequest.of(start, size, Sort.Direction.DESC, "id");
        Page page = orderDAO.findAll(pageable);
        return new Page4Navigator<>(page, navNum);
    }

    public void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem oi : orderItems
                ) {
            oi.setOrder(null);

        }
    }

    public void removeOrderFromOrderItem(List<Order> orders) {
        for (Order o : orders
                ) {
            removeOrderFromOrderItem(o);

        }
    }
    public Order get(int oid){
        return orderDAO.getOne(oid);
    }
    public void update(Order order){
        orderDAO.save(order);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
    public float add(Order order,List<OrderItem>orderItems){
        orderDAO.save(order);
        float total=0;
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
            orderItemService.update(orderItem);
            total+=orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
        }
        if (false){throw new RuntimeException();}//模拟异常
        return  total;
    }
  public   List<Order> listOrderWithoutDelete(User user){
        List<Order>orders= orderDAO.findByUserAndStatusNotOrderByIdDesc(user,OrderService.delete);
        return orders;
    }

    public void calculaAndSetTotalPrice(Order order){
      List<OrderItem> orderItems=orderItemService.listByOrder(order);
      float total=0;
        for (OrderItem orderItem : orderItems) {
total+=orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
        }
        order.setTotal(total);
    }

}
