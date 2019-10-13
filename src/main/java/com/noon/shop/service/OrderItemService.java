package com.noon.shop.service;

import com.noon.shop.dao.OrderItemDAO;
import com.noon.shop.pojo.Order;
import com.noon.shop.pojo.OrderItem;
import com.noon.shop.pojo.Product;
import com.noon.shop.pojo.User;
import com.noon.shop.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.List;
import java.util.Set;

@Service
public class OrderItemService {
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;
@Autowired
    StringRedisTemplate stringRedisTemplate;
    public void fill(Order order) {
        List<OrderItem> orderItems = this.listByOrder(order);
        float totalPrice = 0;
        int totalNumber = 0;
        for (OrderItem orderItem : orderItems) {
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
        OrderItem orderItem;
        String oiStr=stringRedisTemplate.opsForValue().get("orderItems:id"+id);
        if (oiStr==null){
            orderItem=orderItemDAO.getOne(id);
            oiStr= JsonUtils.obj2String(orderItem);
            stringRedisTemplate.opsForValue().set("orderItems:id"+id,oiStr);
        }else {
            orderItem=JsonUtils.string2Obj(oiStr,OrderItem.class);
        }
        return orderItem;
    }
    public void add(OrderItem orderItem){
        Set<String>keys=stringRedisTemplate.keys("orderItems:*");
        stringRedisTemplate.delete(keys);
        orderItemDAO.save(orderItem);
    }
    public void update(OrderItem orderItem){
        Set<String>keys=stringRedisTemplate.keys("orderItems:*");
        stringRedisTemplate.delete(keys);
        orderItemDAO.save(orderItem);
    }
    public void delete(int id){
        Set<String>keys=stringRedisTemplate.keys("orderItems:*");
        stringRedisTemplate.delete(keys);
        orderItemDAO.deleteById(id);
    }
    public int getSaleCount(Product product){
        List<OrderItem> ois=this.listByProduct(product);
        int count=0;
        for (OrderItem oi:ois) {
            if (null != oi.getOrder() && null != oi.getOrder().getPayDate()) {//付了款才算买了
                count += oi.getNumber();
            }
        }
        return count;
        }
    public List<OrderItem> listByProduct(Product product){
            List<OrderItem> ois;
            String oisStr=stringRedisTemplate.opsForValue().get("orderItems:pid"+product.getId());
            if (oisStr==null){
                ois=orderItemDAO.findByProduct(product);
                oisStr=JsonUtils.obj2String(ois);
                stringRedisTemplate.opsForValue().set("orderItems:pid"+product.getId(),oisStr);
            }
            else {
                ois=JsonUtils.string2List(oisStr,OrderItem.class);
            }
        return ois;
    }
    public List<OrderItem> listByOrder(Order order){
        List<OrderItem> ois;
        String oisStr=stringRedisTemplate.opsForValue().get("orderItems:oid"+order.getId());
        if (oisStr==null){
            ois=orderItemDAO.findByOrderOrderByIdDesc(order);
            oisStr=JsonUtils.obj2String(ois);
            stringRedisTemplate.opsForValue().set("orderItems:oid"+order.getId(),oisStr);
        }
        else {
            ois=JsonUtils.string2List(oisStr,OrderItem.class);
        }
        return ois;
    }
    public List<OrderItem> listByUser(User user){
        List<OrderItem> ois;
        String oisStr=stringRedisTemplate.opsForValue().get("orderItems:uid"+user.getId());
        if (oisStr==null){
            ois=orderItemDAO.findByUserAndOrderIsNull(user);
            oisStr=JsonUtils.obj2String(ois);
            stringRedisTemplate.opsForValue().set("orderItems:uid"+user.getId(),oisStr);
        }
        else {
            ois=JsonUtils.string2List(oisStr,OrderItem.class);
        }
        return ois;
    }
}
