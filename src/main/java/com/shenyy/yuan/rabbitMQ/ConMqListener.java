package com.shenyy.yuan.rabbitMQ;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shenyy.yuan.model.UserLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConMqListener {

    private static final Logger log = LoggerFactory.getLogger(ConMqListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "${log.user.queue.name}", containerFactory = "singleListenerContainer")
    public void consumUserLogQueue(@Payload byte[] message) {
        try {
            UserLog userLog = objectMapper.readValue(message, UserLog.class);
            //操作日志信息保存到数据库
            log.info(objectMapper.writeValueAsString(userLog));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 一个生产者对应多个消费者
     * mail消费者1
     * @param message
     */
    @RabbitListener(queues = "${mail.queue.nameA}", containerFactory = "singleListenerContainer")
    public void consumMailQueue(@Payload byte[] message) {
        try {
            String mail = new String(message, "UTF-8");
            //操作日志信息保存到数据库
            log.info("接收邮件内容A1：" + mail);
            //发送邮件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * mail消费者2
     * @param message
     */
    @RabbitListener(queues = "${mail.queue.nameA}", containerFactory = "singleListenerContainer")
    public void consumMailQueueB(@Payload byte[] message) {
        try {
            String mail = new String(message, "UTF-8");
            //操作日志信息保存到数据库
            log.info("接收邮件内容A2：" + mail);
            //发送邮件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
