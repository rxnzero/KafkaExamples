package com.dhlee.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class SimpleConsumer {

	public SimpleConsumer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		 Properties properties = new Properties();
	        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
	        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
	        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
	        
	        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "your_client_id");

	        // from-beginning
//	        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
	        
//	        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG , "false");
//	        properties.put("isolation.level", "read_committed");
	        
	        String topic = "test-string";
	        
	        KafkaConsumer consumer = new KafkaConsumer(properties);
	        List topics = new ArrayList();
	        topics.add(topic);
	        
	        consumer.subscribe(topics);
	        try{
	            while (true){
	                ConsumerRecords<String, ConsumerRecord> records = consumer.poll(10);
	                for (ConsumerRecord record: records){
	                    System.out.println(String.format("Topic - %s, Partition - %d, Value: %s", record.topic(), record.partition(), record.value()));
	                }
	                consumer.commitAsync();
	                Thread.sleep(10);
	            }
	        }catch (Exception e){
	            System.out.println(e.getMessage());
	        }finally {
	        	consumer.close();
	        }
	}

}
