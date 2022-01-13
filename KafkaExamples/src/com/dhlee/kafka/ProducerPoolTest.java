package com.dhlee.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerPoolTest {

	public ProducerPoolTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
	    String key = "string";
	    String message = "Kafka String send";
	    int partitions = 10; // topicÀÇ partition ¼ö
        int partition = 0;
        String topic = "test-string";
        KafkaProducer producer = null;
        
		String boostrapServers = "localhost:9092";
		String keySerialzerClass = "org.apache.kafka.common.serialization.StringSerializer";
		String valueSerialzerClass = "org.apache.kafka.common.serialization.StringSerializer";
		
    	Properties producerProperties = new Properties();
    	producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
		producerProperties.put(ProducerConfig.ACKS_CONFIG, "all");
		producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerialzerClass);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerialzerClass);
        ProducerFactory.init(producerProperties);
        
        for(int i = 0; i < 100; i++){
	        try{
	        	producer = ProducerFactory.getProducer();
	        	System.out.println("getProducer =" + producer);
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
		    } catch (Exception e){
		        e.printStackTrace();
		    } finally {
		    	if(producer != null)
					try {
						System.out.println("putProducer =" + producer);
						ProducerFactory.putProducer(producer);
					} catch (Exception e) {
						e.printStackTrace();
					}
		    }
        }
        try {
        	System.out.println("getPoolMaxSize=" + ProducerFactory.getPoolMaxSize());
			Thread.sleep(10*1000);
			System.out.println("ProducerFactory.close >>");
			ProducerFactory.close();
			Thread.sleep(10*1000);
		} catch (Exception e) {
			;
		}
    }

}
