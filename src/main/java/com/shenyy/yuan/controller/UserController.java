package com.shenyy.yuan.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shenyy.yuan.common.Result;
import com.shenyy.yuan.common.ResultUtil;
import com.shenyy.yuan.config.PageData;
import com.shenyy.yuan.model.User;
import com.shenyy.yuan.model.UserLog;
import com.shenyy.yuan.rabbitMQ.UserOrderService;
import com.shenyy.yuan.service.AsyncService;
import com.shenyy.yuan.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
//@Controller
@RequestMapping("/userMgr/")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment env;

    @Autowired
    private UserService userService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserOrderService userOrderService;

    @RequestMapping("/login")
    public String helloHtml() throws IOException {
        return "index/login";
    }

    @RequestMapping("/hello")
    public String helloTest() throws IOException {
        //线程池
        asyncService.executeAsync();
        return "index/login";
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public PageData<User> getUsers() {
        PageData<User> list = userService.getUsers(2, 3);
        return list;
    }

    @RequestMapping("/userLogin")
    public String userLogin(Model model, User user) {
        if (user == null) {
            return "login";
        }
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword(), false);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (IncorrectCredentialsException e) {
            //这最好把 所有的 异常类型都背会
            model.addAttribute("message", "密码错误");
            return "login";
        } catch (AuthenticationException e) {
            model.addAttribute("message", "登录失败");
            return "login";
        }
        return "view/index";
    }

    @GetMapping("/login")
    public String login() {
        User user = new User("yuan", "123");
        try {
            UserLog userLog = new UserLog(user.getUsername(), "Login", "login", objectMapper.writeValueAsString(user));
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("log.user.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("log.user.routing.key.name"));

            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(userLog)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
            rabbitTemplate.convertAndSend(message);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "over";
    }

    @GetMapping("/sendMail")
    public Result sendMail() {
        try {
            Message message = MessageBuilder.withBody("mail发送".getBytes("UTF-8")).build();
            rabbitTemplate.setExchange(env.getProperty("mail.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("mail.routing.key.nameA"));
            for (int i = 0; i < 10; i++) {
                rabbitTemplate.convertAndSend(message);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.info("邮件发送完毕----------");
        return ResultUtil.success();
    }

    @GetMapping("userOrder")
    public Result userOrder() throws InterruptedException {
        userOrderService.generateMultiThread();
        return ResultUtil.success("order");
    }


}
