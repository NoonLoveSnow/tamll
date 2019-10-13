package com.noon.shop.service;

import com.noon.shop.dao.OrderDAO;
import com.noon.shop.dao.OrderItemDAO;
import com.noon.shop.pojo.Order;
import com.noon.shop.pojo.OrderItem;
import com.noon.shop.pojo.User;
import com.noon.shop.util.JsonUtils;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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
@Autowired
    StringRedisTemplate stringRedisTemplate;
    public Page4Navigator<Order> list(int start, int size, int navNum) {
        Page4Navigator page4Navigator;
      String  pageStr=stringRedisTemplate.opsForValue().get("orders:page"+start+"-size"+size);
      if (pageStr==null){
          Pageable pageable = PageRequest.of(start, size, Sort.Direction.DESC, "id");
          Page page = orderDAO.findAll(pageable);
          page4Navigator=new Page4Navigator<>(page, navNum);
          pageStr= JsonUtils.obj2String(page4Navigator);
        stringRedisTemplate.opsForValue().set("orders:page"+start+"-size"+size,pageStr);
      }else
      {
          page4Navigator=JsonUtils.string2Obj(pageStr,Page4Navigator.class);
          List<Order> orders=JsonUtils.linkedHashMap2List(page4Navigator.getContent(),Order.class);
          page4Navigator.setContent(orders);
      }

        return page4Navigator;
    }

    public void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem oi : orderItems) {
            oi.setOrder(null);
        }
    }

    public void removeOrderFromOrderItem(List<Order> orders) {
        for (Order o : orders) {
            removeOrderFromOrderItem(o);
        }
    }
    public Order get(int oid){
        Order order;
        String orderStr=stringRedisTemplate.opsForValue().get("order:id"+oid);
        if (orderStr==null){
            order=orderDAO.getOne(oid);
            orderStr=JsonUtils.obj2String(order);
            stringRedisTemplate.opsForValue().set("order:id"+oid,orderStr);
        }else {
            order=JsonUtils.string2Obj(orderStr,Order.class);
        }

        return order;
    }
    public void update(Order order){
        Set<String> keys=stringRedisTemplate.keys("orders:*");
        stringRedisTemplate.delete(keys);
        orderDAO.save(order);
    }
    public  void add(Order order){
        Set<String> keys=stringRedisTemplate.keys("orders:*");
        stringRedisTemplate.delete(keys);
        orderDAO.save(order);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
    public float add(Order order,List<OrderItem>orderItems){
        this.add(order);
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
