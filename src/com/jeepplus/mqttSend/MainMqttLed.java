/**
 * 
 */
package com.jeepplus.mqttSend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeepplus.test.client;

import onbon.bx06.Bx6GEnv;

/**
 * @author admin
 *
 */
public class MainMqttLed implements MqttCallback {

	static MqttClient client;
	static int qos = 1;
	static String pubTopic = "dev/pub/data";
	static String subTopic = "dev/sub/data/";
	static String devId;
	static String macAddr = "aaaabbbb";
	static String broker = "tcp://192.168.3.2";
	static String clientId;
	static MqttMessage message;
	static String userName = "leddev";
	static String passWord = "G9oPPpmoouBIP4jF";
	static String pythonPath = "";

	public static void main(String[] args) {

		// 读取配置文件
		readfile();
		ledtxt();

		Boolean judgeTest = MessageJudge.judgeTest("开启python脚本测试");
		if (!judgeTest) {
			System.out.println("请注意!!!!! python脚本启用失败,检查脚本路径.");
		} else {
			System.out.println("python脚本启用成功.");
		}

		try {
			// led板卡初始化
			Bx6GEnv.initial(300000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("软件版本:V10");
		System.out.println("设备devId: " + devId);
		System.out.println(" clientId: " + devId);
		System.out.println("订阅subTopic: " + subTopic);
		System.out.println("   心跳topic: " + pubTopic);

		// 心跳消息内容
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> mapHeart = new HashMap<String, Object>();
		mapHeart.put("rssi", 27);
		mapHeart.put("period", 20);

		map.put("cmd_type", 0);
		map.put("dev_id", devId);
		map.put("mac_addr", macAddr);
		map.put("pro", "LEDPro");
		map.put("time_stamp", new Date().getTime() / 1000);
		map.put("version", 10);
		map.put("heart", mapHeart);
		// 发送心跳的内容
		String content = JSONObject.toJSONString(map);

		MemoryPersistence persistence = new MemoryPersistence();
		try {
			client = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setUserName(userName);
			connOpts.setPassword(passWord.toCharArray());
			connOpts.setCleanSession(false);
			connOpts.setConnectionTimeout(10);
			connOpts.setKeepAliveInterval(20);
			connOpts.setAutomaticReconnect(true);

			// 设置回调
			client.setCallback(new MainMqttLed());
			// 建立连接
			System.out.println("Connecting to broker:" + broker);
			client.connect(connOpts);
			// 订阅
			client.subscribe(subTopic);
			// 发布消息
			MqttMessage messageMqtt = new MqttMessage(content.getBytes());
			messageMqtt.setQos(qos);
			while (true) {
				// 心跳消息发送轮询 20秒
				client.publish(pubTopic, messageMqtt);
				Thread.sleep(20 * 1000);
			}
		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * 
	 * 收到订阅消息后处理业务流程
	 */
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("接收消息主题：" + topic);
		System.out.println("接收消息Qos:" + message.getQos());
		System.out.println("接收消息内容：" + new String(message.getPayload()));
		try {

			JSONObject jsonObj = JSON.parseObject(new String(message.getPayload()));
			macAddr = jsonObj.getString("mac_addr");

			// led内容发送
			String sendLed = MqttMultiLedSend.SendLed(jsonObj);
			JSONObject parseSendLed = JSONObject.parseObject(sendLed);
			parseSendLed.put("dev_id", devId);
			parseSendLed.put("pro", "LEDPro");
			parseSendLed.put("time_stamp", new Date().getTime() / 1000);
			parseSendLed.put("mac_addr", macAddr);
			parseSendLed.put("version", 10);
			parseSendLed.put("progress", 20);

			// mqtt返回消息
			pubMessage(parseSendLed);

			System.out.println("返回客户端pubTopic:  " + pubTopic + "/" + macAddr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 发送消息方法
	public void pubMessage(JSONObject jsonMessge) throws MqttPersistenceException, MqttException {

		message = new MqttMessage(jsonMessge.toJSONString().getBytes());
		message.setQos(qos);
		System.out.println("返回客户端结果消息: " + jsonMessge.toJSONString());
		client.publish(pubTopic + "/" + macAddr, message);

	}

	/*
	 * 
	 * 发送消息成功后执行
	 */
	public void deliveryComplete(IMqttDeliveryToken token) {

		if (token.getTopics()[0].equals(pubTopic)) {
			System.out.println("20秒心跳发送");
		} else if (!token.getTopics()[0].equals(pubTopic)) {
			System.out.println("返回发送成功!-------" + token.isComplete());
		}
	}

	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		System.out.println("发生报错连接断开，重连中！");

	}

	/*
	 * 
	 * 读取配置文件jar包上级目录下的sn.txt
	 */
	public static void ledtxt() {

		String st;
		try {
			File file = new File("..", "sn.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((st = br.readLine()) != null) {
				System.out.println(st);
				devId = st;
				clientId = st;
				subTopic += st;
			}
		} catch (Exception e) {
			e.printStackTrace();
			devId = "02c000814ccab630";
			clientId = "02c000814ccab630";
			subTopic += "02c000814ccab630";
		}

	}

	/*
	 * 
	 * 读取配置文件jar包同级目录下的led.properties
	 */
	public static void readfile() {
		Properties pros = new Properties();
		try {

			File filename = new File(".", "led.properties");
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader

			pros.load(reader);

			if (pros.get("userName") != null) {
				userName = (String) pros.get("userName");
			}
			if (pros.get("passWord") != null) {
				passWord = (String) pros.get("passWord");
			}

			if (pros.get("pythonPath") != null) {
				pythonPath = (String) pros.get("pythonPath");
			}
			if (pros.get("broker") != null) {
				broker = (String) pros.get("broker");
			}
			System.out.println(pros.get("broker"));
			System.out.println(pros.get("userName"));
			System.out.println(pros.get("passWord"));
			System.out.println(pros.get("pythonPath"));

		} catch (Exception e) {
			e.printStackTrace();
			broker = "tcp://192.168.3.2";
			userName = "leddev";
			passWord = "G9oPPpmoouBIP4jF";
			pythonPath = "D://Users//admin//eclipse-workspace1//clean_language_project.py";

		}
	}

}
