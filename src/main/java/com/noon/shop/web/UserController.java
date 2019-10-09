package com.noon.shop.web;

import com.noon.shop.pojo.User;
import com.noon.shop.service.UserService;
import com.noon.shop.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
@Autowired
    UserService userService;
@GetMapping("/users")
    public Page4Navigator<User> list(@RequestParam(defaultValue = "0")int start,@RequestParam(defaultValue = "5")int size)throws Exception{
    start=start<0?0:start;
    return userService.list(start,size,7);
}
}
