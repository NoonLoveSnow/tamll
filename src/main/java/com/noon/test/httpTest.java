package com.noon.test;

import com.pojo.User;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class httpTest {
   /* @GetMapping(path = "{city_id}/{user_id}")
    public Object findUser(@PathVariable("city_id")String cityId,
                           @PathVariable ("user_id")String userId){
        Map<String,Object> params=new HashMap<>();
        params.clear();
        params.put("cityId",cityId);
        params.put("userId", userId);
        return params;
    }*/

    @PostMapping("testjson")
    public Object testjson(@RequestBody User user, HttpServletRequest request, HttpSession session) {
      user.setCreateTime(new Date());
      System.out.println(user);

     String path=session.getServletContext().getRealPath("static");
     System.out.println(path);

    //new File(path+File.separator+"1.txt");
        return user;
    }

}
