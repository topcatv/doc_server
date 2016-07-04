package org.topcat.docserver.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by topcat on 16/7/4.
 */
public class TestConsumer {

    public TestConsumer() throws IOException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("topic_test"));

        Gson gson = new Gson();

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                String value = record.value();
                Properties propFromJSON = gson.fromJson(value, Properties.class);
                System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), propFromJSON.getProperty("key.serializer"));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new TestConsumer();
    }
}
