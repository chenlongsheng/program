/**
 * 
 */
package com.jeepplus.test;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @author admin
 *
 */
public class mqttSend {

	public static void main(String[] args) {
		
	 
		
		
		String subTopic = "dev/pub/data";
//		String pubTopic = "dev/sub/data/02c000814ccab630";
		String content = "hello javafasong ";
		int qos = 1;
		String broker = "tcp://192.168.3.2";
		String clientId = "mqttinId";
		MemoryPersistence persistence = new MemoryPersistence();
		try {
			MqttClient client = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setUserName("leddev");
			connOpts.setPassword("G9oPPpmoouBIP4jF".toCharArray());
			
			connOpts.setCleanSession(true);
	        connOpts.setConnectionTimeout(100);
	        connOpts.setKeepAliveInterval(20);
	        connOpts.setAutomaticReconnect(true);
 
	        
			// 设置回调
//			client.setCallback(new messageCall());

			// 建立连接
			System.out.println("Connecting to broker:" + broker);
			client.connect(connOpts);

			// 订阅
			client.subscribe(subTopic);

			// 发布消息
//			MqttMessage message = new MqttMessage(content.getBytes());
//			message.setQos(qos);
//			client.publish(pubTopic, message);
//			System.out.println("Message published");

//    		client.disconnect();
//    		System.out.println("Disconnected");
//    		client.close();
// 		System.exit(0);
		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}
		System.out.println("Hello World!");
	}

}
