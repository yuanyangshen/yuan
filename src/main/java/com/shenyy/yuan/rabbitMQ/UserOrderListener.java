package com.shenyy.yuan.rabbitMQ;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component("userOrderListener")
public class UserOrderListener implements ChannelAwareMessageListener {

    private static final Logger log = LoggerFactory.getLogger(UserOrderListener.class);

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            byte[] body = message.getBody();
            String mobile = new String(body, "UTF-8");
            //消费 处理 insert sql
            log.info("监听到的手机号：" + mobile);
            channel.basicAck(tag, true);
        } catch (Exception e) {
            log.error("抢单异常", e);
            channel.basicReject(tag, false);
        }
    }
}
