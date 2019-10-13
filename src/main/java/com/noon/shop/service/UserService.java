package com.noon.shop.service;

import com.noon.shop.dao.UserDAO;
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

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    public Page4Navigator<User> list(int start ,int size,int navigatePages){
        Page4Navigator<User> page4Navigator;
        String pageStr=stringRedisTemplate.opsForValue().get("users:page"+start+"-size"+size);
        if (pageStr==null){
            Pageable pageable=PageRequest.of(start,size, Sort.Direction.DESC,"id");
            Page page=userDAO.findAll(pageable);
            page4Navigator=new Page4Navigator<>(page,navigatePages);
            pageStr= JsonUtils.obj2String(page4Navigator);
           stringRedisTemplate.opsForValue().set("users:page"+start+"-size"+size,pageStr);
        }else{
            page4Navigator=JsonUtils.string2Obj(pageStr,Page4Navigator.class);
            List<User> users =JsonUtils.linkedHashMap2List(page4Navigator.getContent(),User.class);
            page4Navigator.setContent(users);
        }

        return page4Navigator;
    }
    public  User getByName(String name){
       User user;
       String userStr=stringRedisTemplate.opsForValue().get("users:name"+name);
       if (userStr==null){
           user=userDAO.findByName(name);
           userStr=JsonUtils.obj2String(user);
           stringRedisTemplate.opsForValue().set("users:name"+name,name);
       }else{
           user=JsonUtils.string2Obj(userStr,User.class);
       }
        return  user;
    }
    public boolean isExist(String name){
        User user = userDAO.findByName(name);
    return user!=null;
    }
    public void add(User user){
        Set<String> keys=stringRedisTemplate.keys("users:*");
        stringRedisTemplate.delete(keys);
        userDAO.save(user);
    }
    public User getByNameAnndPassword(String name,String password){
        return userDAO.findByNameAndPassword(name,password);
    }
}
