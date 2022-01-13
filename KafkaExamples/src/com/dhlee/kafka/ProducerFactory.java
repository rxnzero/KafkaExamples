package com.dhlee.kafka;

import java.util.Properties;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerFactory {
	static GenericObjectPool pool;
	static int maxSize = 10;

	static public void init(Properties producerProperties) {
		ProducerObjectFactory factory = new ProducerObjectFactory();
		factory.setProducerProperties(producerProperties);
//		factory.setBoostrapServers(boostrapServers);
//		factory.setKeySerialzerClass(keySerialzerClass);
//		factory.setValueSerialzerClass(valueSerialzerClass);
//		
		pool = new GenericObjectPool(factory);
		pool.setMaxActive(maxSize);
		pool.setMaxWait(3000L);
		pool.setMaxIdle(maxSize);
		pool.setMinIdle(0);

		pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		try {
			for (int i = 0; i < maxSize; i++) {
				pool.returnObject(factory.makeObject());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// pool.setTestOnReturn(true);
	}

	static public int getPoolMaxSize() throws Exception {
		return maxSize;
	}

	static public synchronized KafkaProducer getProducer() throws Exception {
		return (KafkaProducer) pool.borrowObject();
	}

	static public synchronized void putProducer(KafkaProducer producer) throws Exception {
		pool.returnObject(producer);
	}

	static public int activeCount() {
		return pool.getNumActive();
	}

	static public void close() {
		if (pool != null) {
			try {
				pool.close();
				pool = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

class ProducerObjectFactory implements PoolableObjectFactory {
	private String boostrapServers = "localhost:9092";
	private String keySerialzerClass = "org.apache.kafka.common.serialization.StringSerializer";
	private String valueSerialzerClass = "org.apache.kafka.common.serialization.StringSerializer";
	private Properties producerProperties;

	public Properties getProducerProperties() {
		return producerProperties;
	}

	public void setProducerProperties(Properties producerProperties) {
		this.producerProperties = producerProperties;
	}

	public String getBoostrapServers() {
		return boostrapServers;
	}

	public void setBoostrapServers(String boostrapServers) {
		this.boostrapServers = boostrapServers;
	}

	public String getKeySerialzerClass() {
		return keySerialzerClass;
	}

	public void setKeySerialzerClass(String keySerialzerClass) {
		this.keySerialzerClass = keySerialzerClass;
	}

	public String getValueSerialzerClass() {
		return valueSerialzerClass;
	}

	public void setValueSerialzerClass(String valueSerialzerClass) {
		this.valueSerialzerClass = valueSerialzerClass;
	}

	public Object makeObject() throws Exception {
		Producer<String, String> producer = new KafkaProducer<String, String>(producerProperties);
		return producer;
	}

	public boolean validateObject(Object arg0) {
		// return false;
		System.out.println(" => validateObject - " + ((KafkaProducer) arg0));
		return (arg0 != null);
	}

	public void destroyObject(Object arg0) throws Exception {
		System.out.println(" => destroyObject - " + ((KafkaProducer) arg0));
		((KafkaProducer) arg0).close();
	}

	public void activateObject(Object arg0) throws Exception {
		System.out.println(" => activateObject - " + ((KafkaProducer) arg0));
	}

	public void passivateObject(Object arg0) throws Exception {
		System.out.println(" => passivateObject - " + ((KafkaProducer) arg0));
	}
}