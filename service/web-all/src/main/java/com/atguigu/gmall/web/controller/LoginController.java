package com.atguigu.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author tkwrite
 * @create 2022-09-06-23:54
 */
@Controller
public class LoginController {

    /**
     * 登录页
     * @return
     * originUrl=http://list.gmall.com/list.html
     */
    @GetMapping("/login.html")
    public String loginPage(@RequestParam("originUrl")String originUrl, Model model){
        model.addAttribute("originUrl",originUrl);
     return "login";
    }


}
