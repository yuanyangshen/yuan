package com.shenyy.yuan.service.serviceImpl;

import com.shenyy.yuan.service.TaskService;
import com.shenyy.yuan.threadpool.ExcutePoolManager;
import com.shenyy.yuan.threadpool.TaskCopy;
import com.sun.javafx.tk.Toolkit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class TaskServiceImpl implements TaskService {
    private ExecutorService pool;
    @Override
    public void taskCopy(List<Integer> taskIds) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(taskIds);
            }
        });
        thread.start();

/*


        pool = Executors.newFixedThreadPool(5);

        for (Integer taskId : taskIds){
            TaskCopy taskCopy = new TaskCopy(taskId);
            Future<Integer> future = pool.submit(taskCopy);
            */
/*try{
                Integer i = future.get();
                System.out.println("jieguo="+i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*//*


        }
        pool.shutdown();
*/
        System.out.println("end!!!!!");
    }
}
