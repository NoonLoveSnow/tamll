package com.noon.shop.interceptor;

import com.noon.shop.pojo.Category;
import com.noon.shop.pojo.Order;
import com.noon.shop.pojo.OrderItem;
import com.noon.shop.pojo.User;
import com.noon.shop.service.CategoryService;
import com.noon.shop.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
@Component
public class OtherInterceptor implements HandlerInterceptor{
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        HttpSession session=request.getSession();
        User user=(User)session.getAttribute(("user"));
        int cartTotalItemNumber=0;
        if (null!=user){
            List<OrderItem>ois=orderItemService.listByUser(user);
            for (OrderItem o:ois
                 ) {
                cartTotalItemNumber+=o.getNumber();
                
            }

        }
       List<Category>cs=categoryService.list();
        String contextPath=session.getServletContext().getContextPath();
       request.getServletContext().setAttribute("categories_below_search",cs);
       session.setAttribute("cartTotalItemNumber", cartTotalItemNumber);
        request.getServletContext().setAttribute("contextPath", "/");

    //    System.out.println(request.getServletPath());

      //  System.out.println(session.getServletContext()==request.getServletContext());

    }

   }
