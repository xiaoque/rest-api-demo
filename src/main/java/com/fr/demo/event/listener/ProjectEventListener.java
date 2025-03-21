package com.fr.demo.event.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fr.demo.event.cache.ProjectCache;

@Service
public class ProjectEventListener {
    private static final Logger logger = LoggerFactory.getLogger(ProjectEventListener.class);

    @Autowired
    private ProjectCache projectCache;

    @KafkaListener(topics = "my-topic", groupId = "my-consumer-group")
    public void listen(String message) {
        logger.info("Received message: {}", message);
        System.out.println("-------------------------------");
        System.out.println(message);
    }
}
