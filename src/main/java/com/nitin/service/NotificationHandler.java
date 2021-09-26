package com.nitin.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.streams.HeaderEnricher;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.nitin.common.Status;
import com.nitin.model.Notification;

@EnableKafka
@Configuration
public class NotificationHandler {
	
	private Logger log = LoggerFactory.getLogger(NotificationHandler.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
	@KafkaListener(topics = "email", groupId = "default", containerFactory = "kafkaListenerContainerFactory" )
	public void listenGroup(Notification order) {
		if (order.getStatus() == Status.SUCCESS)
			log.info("we have successful ordered and going to send success message: " + order.toString());
		else
			log.info("we have successful ordered and going to failed message: " + order.toString());			
	}
	
    @Bean
    public Map<String, Object> consumerConfigs() {
        JsonDeserializer<HeaderEnricher.Container<String,String>> deserializer = new JsonDeserializer<>(HeaderEnricher.Container.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        props.put(JsonDeserializer.TRUSTED_PACKAGES, Notification.class.getPackage().getName());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Notification.class);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, "false");
        deserializer.close();
        return props;
    }

    @Bean
    public ConsumerFactory<String, Notification> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Notification>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Notification> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
