package com.shenyy.yuan.controller;

import com.github.pagehelper.PageInfo;
import com.shenyy.yuan.common.PageData;
import com.shenyy.yuan.model.User;
import com.shenyy.yuan.service.AsyncService;
import com.shenyy.yuan.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Security;
import java.util.HashMap;
import java.util.List;

//@RestController
@Controller
@RequestMapping("/userMgr/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AsyncService asyncService;

    @RequestMapping("/login")
    public String helloHtml() throws IOException {
        return "index/login";
    }

    @RequestMapping("/hello")
    public String helloTest() throws IOException {
        asyncService.executeAsync();
        return "index/login";
    }

    @RequestMapping(value = "/users",method = RequestMethod.GET)
    @ResponseBody
    public PageData<User> getUsers(){
        PageData<User> list = userService.getUsers(2,3);
        return list;
    }

    @RequestMapping("/userLogin")
    public String userLogin(Model model, User user){
        if(user == null){
            return "login";
        }
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(),user.getPassword(),false);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        }catch (IncorrectCredentialsException e){
            //这最好把 所有的 异常类型都背会
            model.addAttribute("message", "密码错误");
            return "login";
        }catch (AuthenticationException e){
            model.addAttribute("message", "登录失败");
            return "login";
        }
        return "view/index";
    }
}
