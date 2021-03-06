package com.noon.shop.web;

import com.noon.shop.Comparator.*;
import com.noon.shop.pojo.*;
import com.noon.shop.service.*;
import com.noon.shop.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import sun.security.util.Password;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService imageService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    OrderService orderService;

    @GetMapping("/forehome")
    public Object home() {
        List<Category> cs = categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
       // categoryService.removeCategoryFromProduct(cs);
        return cs;
    }

    @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        String name = user.getName();
        String password = user.getPassword();
        boolean exist = userService.isExist(name);
        if (exist) {
            String message = "用户名已经被使用，换一个注册吧";
            return Result.fail(message);
        }
        String salt=new SecureRandomNumberGenerator().nextBytes().toString();
         String encodedPassword=new SimpleHash("md5", password ,salt,2).toString();
         user.setPassword(encodedPassword);
         user.setSalt(salt);
         userService.add(user);
         return Result.success();
    }

    @PostMapping("/forelogin")
    public Object longin(@RequestBody User user, HttpSession session) {
        User user1 = userService.getByNameAnndPassword(user.getName(), user.getPassword());
        if (user1 == null) {
            return Result.fail("账号或密码错误");
        } else {
            session.setAttribute("user", user1);
            return Result.success();
        }
    }

    @GetMapping("foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid) {
        Product product = productService.get(pid);
        imageService.setFirstProductImage(product);
        List<ProductImage> singleImages = imageService.listSingleProductImage(product);
        product.setProductSingleImages(singleImages);
        List<ProductImage> detailImages = imageService.listDetailProductImage(product);
        product.setProductDetailImages(detailImages);
        productService.setReviewAndSaleCount(product);
        List<PropertyValue> pvs = propertyValueService.list(product);
        List<Review> reviews = reviewService.list(product);
        Map<String, Object> map = new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);
        return Result.success(map);
    }

    @GetMapping("forecheckLogin")
    public Object foreCheckLogin(HttpSession session) {
        if (session.getAttribute("user") == null)
            return Result.fail("未登录");
        else return Result.success();
    }

    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable("cid") int cid, String sort) {
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setReviewAndSaleCount(category.getProducts());
        categoryService.removeCategoryFromProduct(category);

        if (null != sort) {
            switch (sort) {
                case "review":
                    Collections.sort(category.getProducts(), new PproductReviewComparator());
                    break;
                case "date":
                    Collections.sort(category.getProducts(), new ProductDateComparator());
                    break;
                case "saleCount":
                    Collections.sort(category.getProducts(), new ProductSaleComparator());
                    break;
                case "price":
                    Collections.sort(category.getProducts(), new ProductPriceComparator());
                    break;
                case "all":
                    Collections.sort(category.getProducts(), new ProductAllComparator());
                    break;
            }
        }
        return category;
    }

    @PostMapping("foresearch")
    public Object search(String keyword) {
        if (null == keyword)
            keyword = "";
        List<Product> products = productService.search(keyword, 0, 20);
        imageService.setFirstProductImage(products);
        productService.setReviewAndSaleCount(products);
        return products;
    }

    @GetMapping("forebuyone")//如果直接购买前端请求该方法，然后前端将产品id放在url上，跳转到结算页，结算页再请求forebuy方法参数从url上取
    public Object buyone(int pid, int num, HttpSession session) {  //产品页立即购买将 返回一个订单项id，成功后前端将id放在url上跳转到结算页，根据url上保存的订单项id请求数据
        return buyoneAndAddCart(pid, num, session);
    }

    @GetMapping("forebuy")
    public Object forebuy(int[] oiid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        float total = 0;
        List<OrderItem> ois = new ArrayList<>();
        for (int id : oiid
        ) {
            OrderItem orderItem = orderItemService.get(id);
            if (orderItem == null || orderItem.getUser().getId() != user.getId())//为安全起见，订单不属于本人，则失败
                return Result.fail("非法操作");
            total += orderItem.getProduct().getPromotePrice() * orderItem.getNumber();
            ois.add(orderItem);
        }
        imageService.setFirstProdutImagesOnOrderItems(ois);
        session.setAttribute("orderItems", ois);  //在购物车购买，就需要将选中的订单项存入session，结算时确定总金额
        Map<String, Object> map = new HashMap<>();
        map.put("orderItems", ois);
        map.put("total", total);
        return Result.success(map);
    }

    int buyoneAndAddCart(int pid, int num, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem o : ois
        ) {
            if (o.getProduct().getId() == pid) { //找到直接返回
                o.setNumber(o.getNumber() + num);
                orderItemService.update(o);
                return o.getId();
            }
        }
        Product product = productService.get(pid);
        OrderItem tarOrderItem = new OrderItem(); //没找到新建订单项
        tarOrderItem.setNumber(num);
        tarOrderItem.setProduct(product);
        tarOrderItem.setUser(user);
        orderItemService.add(tarOrderItem);
        return tarOrderItem.getId();
    }

    @GetMapping("foreaddCart")   //产品页包含的imgAndInfo请求添加到购物车
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid, num, session);
        return Result.success();
    }

    @GetMapping("forecart")//top的购物车从forePageController跳转到购物车页面，购物车页面请求订单项
    public Object cart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> orderItems = orderItemService.listByUser(user);
        imageService.setFirstProdutImagesOnOrderItems(orderItems);
        return orderItems;
    }

    @GetMapping("forechangeOrderItem")
    public Object changrOrderItem(int pid, int num, HttpSession session) {
        User user = (User) session.getAttribute("user");//有登录拦截器，user不可能为空
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem orderItem : ois) {
            if (orderItem.getProduct().getId() == pid) {
                orderItem.setNumber(num);
                orderItemService.update(orderItem);
                break;
            }
        }
        return Result.success();
    }

    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(int oiid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        OrderItem orderItem = orderItemService.get(oiid);
        if (orderItem == null || orderItem.getUser().getId() != user.getId()
                || orderItem.getOrder() != null)//当前订单项不属于本人，或者已经支付了，则操作失败
            return Result.fail("非法操作");
        orderItemService.delete(oiid);
        return Result.success();
    }

    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody Order order, HttpSession session) {
        User user = (User) session.getAttribute("user");
        order.setUser(user);
        String ordercode = System.currentTimeMillis() + String.valueOf(RandomUtils.nextInt(10000));
        order.setOrderCode(ordercode);
        order.setCreateDate(new Date());
        order.setStatus(OrderService.waitPay);
        List<OrderItem> orderItems = (List<OrderItem>) session.getAttribute("orderItems");
        float total = orderService.add(order, orderItems);
        Map<String, Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);
        return Result.success(map);
    }

    @GetMapping("forepayed")
    public Object payed(int oid) { //假装支付
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return Result.success();
    }

    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Order> os = orderService.listOrderWithoutDelete(user);
        orderItemService.fill(os);
        // orderService.removeOrderFromOrderItem(os);
        return os;
    }

    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid, HttpSession session) {
        Order order = orderService.get(oid);
        User user = (User) session.getAttribute("user");
        if (order == null || order.getUser().getId() != user.getId() || !order.getStatus().equals(OrderService.waitConfirm))
            return Result.fail("非法操作");//不属于本人订单,订单状态不是待确认收货,则不允许查看
        orderItemService.fill(order);
        orderService.calculaAndSetTotalPrice(order);
        // orderService.removeOrderFromOrderItem(order);
        System.out.println(order);
        return order;
    }

    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed(int oid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Order order = orderService.get(oid);
        if (order == null || order.getUser().getId() != user.getId() || !order.getStatus().equals(OrderService.waitConfirm))
            return Result.fail("非法操作");//不属于本人订单,订单状态不是待确认收货,则不允许修改
        order.setConfirmDate(new Date());
        order.setStatus(OrderService.waitReview);
        orderService.update(order);
        return Result.success();
    }

    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Order order = orderService.get(oid);
        if (order == null || order.getUser().getId() != user.getId())
            return Result.fail("非法操作");//不属于本人订单,则不允许修改
        order.setStatus(OrderService.delete);
        orderService.update(order);
        return Result.success();
    }

    @GetMapping("forereview")
    public Object review(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        // orderService.removeOrderFromOrderItem(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(p);
        productService.setReviewAndSaleCount(p);
        Map<String, Object> map = new HashMap<>();
        map.put("p", p);
        map.put("o", o);
        map.put("reviews", reviews);

        return Result.success(map);
    }

    @PostMapping("foredoreview")
    public Object doreview(HttpSession session, int oid, int pid, String content) {
        Order o = orderService.get(oid);
        List<OrderItem> ois = orderItemService.listByOrder(o);
        Product p = productService.get(pid);
        User user = (User) session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewService.add(review);
        return Result.success();
    }
}