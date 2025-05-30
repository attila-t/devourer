package com.autodesk.devourer;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.util.ErrorHandler;

@Configuration
@EnableJms
public class JmsConfiguration {

  private static final String QUALIFIER_AMQ = "amq";
  private static final String QUALIFIER_SPRING = "spring";

  @Value("${activemq_broker_url}")
  private String brokerUrl;
  @Value("${activemq_broker_user:#{null}}")
  private String brokerUser;
  @Value("${activemq_broker_password:#{null}}")
  private String brokerPassword;

  @Value("${activemq_concurrency:8-32}")
  private String concurrency;

  @Value("${activemq_connection_factory_max_connections:50}")
  private int maxConnections;

  @Bean
  public ErrorHandler errorHandler() {
    return new JmsErrorHandler();
  }

  @Bean
  @Qualifier(value = QUALIFIER_AMQ)
  public ActiveMQConnectionFactory amqConnectionFactory() {
    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
    factory.setBrokerURL(brokerUrl);
    if (brokerUser != null) {
      factory.setUserName(brokerUser);
    }
    if (brokerPassword != null) {
      factory.setPassword(brokerPassword);
    }
    return factory;
  }

  @Bean(destroyMethod = "stop")
  @Qualifier(value = QUALIFIER_SPRING)
  public ConnectionFactory pooledConnectionFactory(
      @Qualifier(value = QUALIFIER_AMQ) ActiveMQConnectionFactory amqConnectionFactory) {
    PooledConnectionFactory factory = new PooledConnectionFactory();
    factory.setConnectionFactory(amqConnectionFactory);
    factory.setMaxConnections(maxConnections);
    return factory;
  }

  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
      @Qualifier(value = QUALIFIER_SPRING) ConnectionFactory connectionFactory,
      ErrorHandler errorHandler) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setErrorHandler(errorHandler);
    factory.setConcurrency(concurrency);
    return factory;
  }

}
