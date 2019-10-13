package com.noon.shop.config;


import com.noon.shop.interceptor.LoginInterceptor;
import com.noon.shop.interceptor.OtherInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {
   @Autowired
    OtherInterceptor otherInterceptor;
    String[] exclude = new String[] {"/js/**","/img/**","/css/**","/webapp/**"};
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(otherInterceptor).addPathPatterns("/**").excludePathPatterns(exclude);
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**").excludePathPatterns(exclude);
    }
}
