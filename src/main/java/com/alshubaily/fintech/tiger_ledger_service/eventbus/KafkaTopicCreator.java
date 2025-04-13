package com.alshubaily.fintech.tiger_ledger_service.eventbus;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaTopicCreator {

    private final KafkaAdmin kafkaAdmin;

    private static final String[] TOPICS = {"transactions"};

    @PostConstruct
    public void createTopics() {
        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            for (String t : TOPICS) {
                NewTopic topic = new NewTopic(t, 1, (short) 1);
                try {
                    admin.createTopics(List.of(topic)).all().get();
                    System.out.println("Created topic: " + t);
                } catch (Exception e) {
                    System.out.println("Failed to create topic " + t + ": " + e.getMessage());
                }
            }
        }
    }
}
