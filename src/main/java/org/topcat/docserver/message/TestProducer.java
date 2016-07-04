package org.topcat.docserver.message;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Properties;


public class TestProducer {
    private Producer<String, String> producer;

    public TestProducer(String topic) throws IOException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);
        Gson gson = new Gson();

        for(int i = 0; i < 100; i++)
            producer.send(new ProducerRecord<>(topic, Integer.toString(i), gson.toJson(props)));

        producer.close();
    }

    public static void main(String[] args) throws IOException {
        new TestProducer("topic_test");
    }

} 