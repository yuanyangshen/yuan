package com.shenyy.yuan.threadpool;

import java.util.concurrent.Callable;

public class TaskCopy implements Callable<Integer> {

    private Integer taskId;

    public TaskCopy(Integer taskId){
        this.taskId = taskId;
    }
    @Override
    public Integer call() throws Exception {
        System.out.println("copy线程正在执行taskid= "+this.taskId);
        Thread.sleep(5000);
        System.out.println("哦啦"+taskId);
        return 1000;
    }
}
