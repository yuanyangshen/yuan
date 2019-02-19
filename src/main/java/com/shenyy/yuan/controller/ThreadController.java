package com.shenyy.yuan.controller;

import com.shenyy.yuan.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/thread/")
public class ThreadController {

    @Autowired
    private TaskService taskService;

    @RequestMapping("/startTask")
    @ResponseBody
    public String helloTest()  {
        List<Integer> list = new ArrayList<>();
        for (int i = 0 ;i<3; i++ ){
            list.add(i);
        }
        taskService.taskCopy(list);
        //线程池
        return "result";
    }
}
