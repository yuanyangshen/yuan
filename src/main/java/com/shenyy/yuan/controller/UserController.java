package com.shenyy.yuan.controller;

import com.github.pagehelper.PageInfo;
import com.shenyy.yuan.common.PageData;
import com.shenyy.yuan.model.User;
import com.shenyy.yuan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

//@RestController
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/hello")
    public String helloHtml() throws IOException {
        return "hello";
    }

    @RequestMapping(value = "/users",method = RequestMethod.GET)
    @ResponseBody
    public PageData<User> getUsers(){
        PageData<User> list = userService.getUsers(2,3);
        return list;
    }
}
