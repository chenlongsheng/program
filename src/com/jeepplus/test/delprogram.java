/**
 * 
 */
package com.jeepplus.test;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeepplus.bean.Program;

import onbon.bx06.Bx6GCommException;
import onbon.bx06.Bx6GEnv;
import onbon.bx06.Bx6GScreenClient;
import onbon.bx06.Bx6GScreen.Result;
import onbon.bx06.area.DateStyle;
import onbon.bx06.area.DateTimeBxArea;
import onbon.bx06.area.TextCaptionBxArea;
import onbon.bx06.area.TimeStyle;
import onbon.bx06.area.WeekStyle;
import onbon.bx06.area.page.TextBxPage;
import onbon.bx06.file.ProgramBxFile;
import onbon.bx06.message.global.ACK;
import onbon.bx06.series.Bx6E;
import onbon.bx06.series.Bx6M;
import onbon.bx06.utils.DisplayStyleFactory;
import onbon.bx06.utils.TextBinary;
import onbon.bx06.utils.DisplayStyleFactory.DisplayStyle;
import onbon.bx06.utils.TextBinary.Alignment;

/**
 * @author admin
 *
 */
public class delprogram {

	private static String ip = "192.168.3.113";
//	private static String ip = "192.168.10.144";
	private static int port = 5005;
	private static Bx6M x6M = null;
	private static Bx6E x6E = null;
	private static Bx6GScreenClient screen = null;
	// 创建节目 一个节目相当于一屏显示内容
	private static ProgramBxFile pf;
	private static String type = System.getenv("type");
	private static String Bx6 = System.getenv("Bx6");

	public static void main(String[] args) throws Exception {
		
 
		Bx6GEnv.initial(15000);
		screen = new Bx6GScreenClient("MyScreen", new Bx6M());

		try {
			screen.connect(ip, port);
		} catch (Exception e) {
			System.out.println("连接屏幕失败!!!!");
			e.printStackTrace();
		}
		// 连接控制器
		pf = new ProgramBxFile("P000", screen.getProfile());
		Result<ACK> deletePro = screen.deleteAllDynamic();
		screen.deletePrograms();
		System.out.println("是否删除成功:" + deletePro);

		List<String> pfs1 = screen.readProgramList();

		for (String program : pfs1) {
			System.out.println("删除后的节目列表: " + program);
		}
		System.out.println("删除节目列表打印完毕------:!!! ");

	}

}
