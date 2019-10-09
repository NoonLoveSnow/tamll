package com.noon.shop.web;

import com.noon.shop.pojo.Order;
import com.noon.shop.service.OrderItemService;
import com.noon.shop.service.OrderService;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;
    @GetMapping("/orders")
    public Page4Navigator<Order> list(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start<0?0:start;
        Page4Navigator<Order> page =orderService.list(start, size, 5);
        orderItemService.fill(page.getContent());
        System.out.println(page.getContent().get(0).getConfirmDate());
      //  orderService.removeOrderFromOrderItem(page.getContent());
        return page;
    }

    @PutMapping("/deliveryOrder/{oid}")
    public Object deliveryOrder(@PathVariable(name = "oid")int oid){
        Order order=orderService.get(oid);
        order.setStatus(orderService.waitPay);
        orderService.update(order);
        return  order;
    }
}
