package com.shenyy.yuan.threadpool;


import java.util.List;
import java.util.concurrent.*;

public class ExcutePoolManager {

    private ExecutorService pool;

    public ExcutePoolManager(int maxPoolSizr) {
        this.pool = Executors.newFixedThreadPool(maxPoolSizr);
    }

    private void poolExecute(List<Integer> taskIds){
        for (Integer taskId : taskIds){
            TaskCopy taskCopy = new TaskCopy(3);
            Future<Integer> future = pool.submit(taskCopy);
        }
    }

    private void shutDown(){

    }
}
