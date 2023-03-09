package com.digitalAlpha.middleware.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final Producer<String, String> producer;
    public RecordMetadata publishMessage(String kafkaTopic, String key, String message) {

        RecordMetadata recordMetadata;

        log.info("Preparing message for topic: " + kafkaTopic + " with key: " + key + " and message: " + message);
        try {
            Future<RecordMetadata> recordData = producer.send(new ProducerRecord<>(kafkaTopic, key, message));
            recordMetadata = recordData.get();
            log.info("Message with key " + key + " successfully sent.");
            return recordMetadata;
        } catch (NullPointerException | ExecutionException | InterruptedException ex) {
            log.error(ex.getMessage());
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Publish failed for message with key: " + key);
        }
    }
}
