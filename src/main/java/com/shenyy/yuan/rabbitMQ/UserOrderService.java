package com.shenyy.yuan.rabbitMQ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CountDownLatch;

@Service
public class UserOrderService {
    private static final Logger log = LoggerFactory.getLogger(UserOrderService.class);
    private static int mobile = 0;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;

    public void generateMultiThread() throws InterruptedException {
        log.info("线程初始化------");
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for (int i = 0; i < 5; i++) {
            new Thread(new UserOrderThread(countDownLatch)).start();
        }
        countDownLatch.await();
        System.out.println("结束----");
    }

    private class UserOrderThread implements Runnable {
        private final CountDownLatch startLatch;

        private UserOrderThread(CountDownLatch startLatch) {
            this.startLatch = startLatch;
        }

        @Override
        public void run() {
            try {
                //startLatch.await();
                mobile += 1;
                sendMessage(String.valueOf(mobile));
            } finally {
                startLatch.countDown();
            }
        }
    }

    private void sendMessage(String mobile){
        try {
            rabbitTemplate.setExchange(env.getProperty("user.order.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("user.order.routing.key.name"));
            Message message = MessageBuilder.withBody(mobile.getBytes("UTF-8")).build();
            rabbitTemplate.send(message);
        } catch (UnsupportedEncodingException e) {
            log.error("发送抢单信息入队列发生异常: mobile={}",mobile);
        }


    }
}
