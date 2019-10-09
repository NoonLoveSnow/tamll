package com.noon.shop.interceptor;


import com.noon.shop.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    static  String [] requiredAuthPages={
            "buy",
            "alipay",
            "payed",
            "cart",
            "bought",
            "orderConfirmed",

            "forebuyone",
            "forebuy",
            "foreaddCart",
            "forecart",
            "forechangeOrderItem",
            "foredeleteOrderItem",
            "forecreateOrder",
            "forepayed",
            "forebought",
            "foreorderConfirmed",
            "foreorderconfirmPay",
            "foredeleteOrder",
            "forereview",
            "foredoreview"
    };
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session=request.getSession();
        User u=new User();
        u.setId(2);
        u.setName("noon");
        session.setAttribute("user",u);
        String contextPath=session.getServletContext().getContextPath();
       // System.out.println("contextPath:"+contextPath);
        String uri=request.getRequestURI();
      //  System.out.println("uri:"+uri);
        String page= StringUtils.remove(uri,contextPath+"/");
       // System.out.println("page:"+page);

        for (String requiredAuthPage:requiredAuthPages
             ) {
            if (StringUtils.startsWith(page,requiredAuthPage)){
                User user=(User)session.getAttribute("user");
                if (user==null){
                    response.sendRedirect("login");
                    return  false;
                }
            }
            
        }
        return  true;
    }


}
