package com.fr.demo.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.demo.model.dto.ProjectDto;
import com.fr.demo.model.enums.ProjectType;

@Service
@EnableKafka
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Value("${spring.kafka.producer.topic-name}")
    private String topicName;

    private KafkaTemplate<String, String> stringKafkaTemplate; // For String events

    public KafkaProducer(KafkaTemplate<String, String> stringKafkaTemplate) {
        this.stringKafkaTemplate = stringKafkaTemplate;
    }

    public void sendMessage(String message) {
        CompletableFuture<SendResult<String, String>> future = stringKafkaTemplate.send(topicName, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message=[{}] with offset=[{}]", topicName, result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send message=[{}] due to : {}", message, ex.getMessage());
            }
        });
    }

    public void sendProjectList(List<ProjectDto> projectDtos) {
        sendMessage(toJsonString(projectDtos));
    }

    public void sendProject(ProjectDto projectDto) {
        sendMessage(toJsonString(projectDto));
    }

    public void sendProjectTypes(List<ProjectType> projectTypes) {
        sendMessage(toJsonString(projectTypes));
    }

    public <T> String toJsonString(T obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("JSON serialization failed", e);
            return "{}";
        }
    }

}
