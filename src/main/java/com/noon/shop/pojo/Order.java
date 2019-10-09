package com.noon.shop.pojo;

import com.noon.shop.service.OrderService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "order_")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "uid")
    User user;

    String orderCode;
    String address;
    String post;
    String receiver;
    String mobile;
    String userMessage;
    Date createDate;
    Date payDate;
    Date deliveryDate;
    Date confirmDate;

    String status;

    @Transient
    @JsonIgnoreProperties({"order"})
    List<OrderItem> orderItems;
    @Transient
    float total;
    @Transient
    int totalNumber;

    @Transient
    String statusDesc; //状态Description

    public String getStatusDesc() {
        if (statusDesc != null)
            return statusDesc;
        String description ;
        switch (status) {
            case OrderService.waitPay:
                description = "待付款";
                break;
            case OrderService.waitDelivery:
                description = "待发货";
                break;
            case OrderService.waitConfirm:
                description = "待收货";
                break;
            case OrderService.waitReview:
                description = "待评价";
                break;
            case OrderService.finish:
                description = "完成";
                break;
            case OrderService.delete:
                description = "删除";
                break;
            default:
                description = "未知";
        }
        return description;
        }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }


}
