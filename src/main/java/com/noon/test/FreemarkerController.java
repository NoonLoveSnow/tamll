package com.noon.test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;


@Controller
public class FreemarkerController {
    @GetMapping("/hello2")
    public String hello(Model model){
        System.out.println(123);
        model.addAttribute("setting","port:8080");
        model.addAttribute("now",new Date());
        return "index";
    }
}
