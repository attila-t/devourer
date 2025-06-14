package com.acme.devourer;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.util.Enumeration;
import java.util.Map;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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
    @SuppressWarnings("unchecked")
    Enumeration<String> propertyNames = message.getPropertyNames();

    SortedMap<String, Object> properties = enumerationAsStream(propertyNames)
        .collect(TreeMap::new, (m, propertyName) -> {
          try {
            m.put(propertyName, message.getObjectProperty(propertyName));

          } catch (JMSException e) {
            LOGGER.error("Unhandled exception caught", e);
          }
        }, Map::putAll);
    if (properties.size() != 0) {
      LOGGER.info("Message properties: {}", properties);
    }

    int count = counter.incrementAndGet();
    if (count % 100000 == 0) {
      LOGGER.info("{} messages devoured", Integer.valueOf(count));
    }
  }

  private <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
    return StreamSupport.stream(
        new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {

          @Override
          public boolean tryAdvance(Consumer<? super T> action) {
            if (e.hasMoreElements()) {
              action.accept(e.nextElement());
              return true;
            }
            return false;
          }

          @Override
          public void forEachRemaining(Consumer<? super T> action) {
            while(e.hasMoreElements()) {
              action.accept(e.nextElement());
            }
          }

        }, false);
  }

}
