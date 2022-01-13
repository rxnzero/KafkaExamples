package com.dhlee.kafka;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;



public class SimpleProcucer {

	public SimpleProcucer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        
//        properties.put("enable.idempotence", "true");
//        properties.put("transactional.id", "test-1");
        
        String topic = "test-string";
        
        int partitions = 10; // topicÀÇ partition ¼ö
        
        Producer<String, String> producer = new KafkaProducer<String, String>(properties);
        
        String key = "string";
        String message = "Kafka String send";
        int partition = 0;
        try{
            for(int i = 0; i < 100; i++){
            	System.out.printf("Producing record: %s\t%s%n", key, message + i);
            	partition = i % partitions;
                producer.send(new ProducerRecord<String, String>(topic, partition, key, message+i), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata m, Exception e) {
                      if (e != null) {
                        e.printStackTrace();
                      } else {
                        System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n", m.topic(), m.partition(), m.offset());
                      }
                    }
                });
                producer.flush();
                Thread.sleep(20);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            producer.close();
        }

	}

}
