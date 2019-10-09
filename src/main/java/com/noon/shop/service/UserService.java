package com.noon.shop.service;

import com.noon.shop.dao.UserDAO;
import com.noon.shop.pojo.User;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;
    public Page4Navigator<User> list(int start ,int size,int navigatePages){
        Pageable pageable=PageRequest.of(start,size, Sort.Direction.DESC,"id");
        Page page=userDAO.findAll(pageable);
        return new Page4Navigator<>(page,navigatePages);
    }
    public  User getByName(String name){
       return  userDAO.findByName(name);
    }
    public boolean isExist(String name){
        User user = userDAO.findByName(name);
    return user!=null;
    }
    public void add(User user){
        userDAO.save(user);
    }
    public User getByNameAnndPassword(String name,String password){
        return userDAO.findByNameAndPassword(name,password);
    }
}
