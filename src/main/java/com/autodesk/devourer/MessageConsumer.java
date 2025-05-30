package com.autodesk.devourer;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

  private final AtomicInteger counter = new AtomicInteger(0);

  @JmsListener(destination = "${activemq_target_queue}")
  public void devour(Message message) throws JMSException {
    int c = counter.incrementAndGet();
    if (c % 100000 == 0) {
      LOGGER.info("{} messages devoured", Integer.valueOf(c));
    }
  }

}
