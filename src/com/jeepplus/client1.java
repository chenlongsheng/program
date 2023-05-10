/**
 * 
 */
package com.jeepplus;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.commons.codec.binary.Base64;

/**
 * @author admin
 *
 */
public class client1 {

	// 2:快速打出2,5,13,16,17,18,27,28,39
	// 3:向左移动
	// 4:向左连移
	// 5:向上移动
	// 6:向上连移
	// 7:闪烁
	// 8:飘雪
	// 9:冒泡
	// 10:中间移出
	// 11:左右移入
	// 12:左右交叉移入
	// 13:上下交叉移入
	// 14:花卷闭合
	// 15:花卷打开
	// 16:向左拉伸
	// 17:向右拉伸
	// 18:向上拉伸
	// 19:向下拉伸
	// 20:向左镭射
	// 21:向右镭射
	// 22:向上镭射
	// 23:向下镭射
	// 24:左右交叉拉幕
	// 25:上下交叉拉幕
	// 26:分散左拉
	// 27:水平百叶
	// 28:垂直百叶
	// 29:向左拉幕
	// 30:向右拉幕
	// 31:向上拉幕
	// 32:向下拉幕
	// 33:左右闭合
	// 34:左右对开
	// 35:上下闭合
	// 36:上下对开
	// 37;向右移动
	// 38:向右连移
	// 39:向下移动
	public static void main(String[] args) throws Exception {
		// 1.建立一个Socket
		DatagramSocket socket = new DatagramSocket();// 1:静止显示
		Base64 base64 = new Base64();

		String leftText = "61";
		String rightText = "51";
		String rightText1 = "剩余充电桩";

//图片节目
//		String msg = "{\"area_array\":[{\"textBinary\":1,\"areaType\":1,"
//				+ "\"color\":2,\"displayStyle\":38,\"show_id\":\"P001\",\"fontBold\":0,\"fontName\":\"宋体\","
//				+ "\"fontSize\":50,\"message\":\"0\",\"speed\":20,\"stayTime\":301,\"height\":50,\"width\":40,\"x\":40,\"y\":0},"
//				+ "{\"textBinary\":1,\"areaType\":1,"
//				+ "\"color\":2,\"displayStyle\":4,\"show_id\":\"P004\",\"fontBold\":0,\"fontName\":\"宋体\","
//				+ "\"fontSize\":30,\"message\":\"0\",\"speed\":20,\"stayTime\":300,\"height\":50,\"width\":40,\"x\":0,\"y\":50}],\"cmd_type\":\"show\"}";
//动态数字
		String msg = "{\"area_array\":[{\"textBinary\":1,\"areaType\":0,"
				+ "\"color\":0,\"displayStyle\":2,\"show_id\":\"P003\",\"fontBold\":0,\"fontName\":\"宋体\","
				+ "\"fontSize\":40,\"message\":\"" + leftText + ""
				+ "\",\"speed\":50,\"stayTime\":200,\"height\":40,\"width\":40,\"x\":0,\"y\":6},{\"textBinary\":1,\"areaType\":0,"
				+ "\"color\":0,\"displayStyle\":2,\"show_id\":\"P002\",\"fontBold\":0,\"fontName\":\"宋体\","
				+ "\"fontSize\":40,\"message\":\"" + rightText + ""
				+ "\",\"speed\":30,\"stayTime\":200,\"height\":40,\"width\":40,\"x\":40,\"y\":58}],\"cmd_type\":\"show\"}";

//		String msg = "{\"area_array\":[{\"textBinary\":1,\"areaType\":0,"
//				+ "\"color\":0,\"displayStyle\":2,\"show_id\":\"P005\",\"fontBold\":0,\"fontName\":\"宋体\","
//				+ "\"fontSize\":16,\"message\":\"" + rightText1 + ""
//				+ "\",\"speed\":30,\"stayTime\":200,\"height\":40,\"width\":80,\"x\":0,\"y\":105}],\"cmd_type\":\"show\"}";
//清空屏幕
		// String msg = "{\"cmd_type\":\"del\"}";

		
		System.out.println(msg);
		InetAddress localhost = InetAddress.getByName("10.8.0.111");
		int port = 10000;
		// 数据，数据的长度起始， 要发送给谁
		DatagramPacket packet = new DatagramPacket(msg.getBytes(), 0, msg.getBytes().length, localhost, port);
		// 3.发送包
		socket.send(packet);
		// 4.关闭流
		socket.close();
	}

}
