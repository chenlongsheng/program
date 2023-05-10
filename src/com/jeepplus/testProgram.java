/**
 * 
 */
package com.jeepplus;

import java.awt.Color;
import java.awt.Font;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import onbon.bx06.Bx6GEnv;
import onbon.bx06.Bx6GException;
import onbon.bx06.Bx6GScreenClient;
import onbon.bx06.area.TextCaptionBxArea;
import onbon.bx06.area.page.TextBxPage;
import onbon.bx06.file.ProgramBxFile;
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
public class testProgram {

	private static String ip = "192.168.3.20";

	private static int port = 5005;
	private static Bx6M x6M = null;
	private static Bx6E x6E = null;
	private static Bx6GScreenClient screen = null;

	private static String type = System.getenv("type");
	private static String Bx6 = System.getenv("Bx6");

	private static String showId = "P001";
	private static String showId1 = "P002";
	private static int stayTime = 800;

	// 创建节目 一个节目相当于一屏显示内容
	private static ProgramBxFile pf;
	private static ProgramBxFile pf1;

	public static void main(String[] args) throws Exception {
		Bx6GEnv.initial(300000);
		screen = new Bx6GScreenClient("MyScreen", new Bx6M());
		
		try {
			boolean connect = screen.connect(ip, port);
		} catch (Exception e) {
			e.printStackTrace();		 
		}
		pf = new ProgramBxFile(showId, screen.getProfile());

		TextCaptionBxArea area = new TextCaptionBxArea(0, 0, 192, 92, screen.getProfile());
		// 创建一个数据页
		// 第一行数据
		// *********
		DisplayStyle[] styles = DisplayStyleFactory.getStyles().toArray(new DisplayStyle[0]);
		Base64 base64 = new Base64();

		// ***********
		TextBxPage page = new TextBxPage("111111111");
		// 设置字体
		page.setFont(new Font(URLDecoder.decode("宋体", "UTF-8"), 0, 16));
		// 调整停留时间, 单位 10ms

		page.setStayTime(stayTime);

		// 速度
		page.setSpeed(10);

		// 设置文本水平对齐方式
		page.setHorizontalAlignment(TextBinary.Alignment.NEAR);
		// 设置文本垂直居中方式
		page.setVerticalAlignment(TextBinary.Alignment.CENTER);
		// 设置文本字体
		page.setFont(new Font("consolas", Font.PLAIN, 14)); // 字体

		page.setBackground(Color.darkGray);
		// 调整特技方式
		page.setDisplayStyle(styles[39]);
		area.addPage(page);

		pf.addArea(area);

		pf1 = new ProgramBxFile(showId1, screen.getProfile());

		TextCaptionBxArea area1 = new TextCaptionBxArea(0, 0, 192, 92, screen.getProfile());
		// 创建一个数据页
		// 第一行数据
		// *********

		// ***********
		TextBxPage page1 = new TextBxPage("222222");
		// 设置字体
		page1.setFont(new Font(URLDecoder.decode("宋体", "UTF-8"), 0, 16));
		// 调整停留时间, 单位 10ms

		page1.setStayTime(stayTime);

		// 速度
		page1.setSpeed(5);

		// 设置文本水平对齐方式
		page1.setHorizontalAlignment(TextBinary.Alignment.NEAR);
		// 设置文本垂直居中方式
		page1.setVerticalAlignment(TextBinary.Alignment.CENTER);
		// 设置文本字体
		page1.setFont(new Font("consolas", Font.PLAIN, 14)); // 字体

		page1.setBackground(Color.darkGray);
		// 调整特技方式
		page1.setDisplayStyle(styles[4]);
		area1.addPage(page);

		pf.addArea(area);

		pf1.addArea(area1);
		

		ArrayList<ProgramBxFile> plist = new ArrayList<ProgramBxFile>();
		plist.add(pf);
//		plist.add(pf1);
		long startTime = System.currentTimeMillis(); // 获取开始时间

		screen.writePrograms(plist);
		
		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
		  List<String> pgs = screen.readProgramList();
	        for (String pg : pgs) {
	            System.out.println(pg);
	        }
		screen.disconnect();				
		System.out.println("pppppppppppppp");
		
	}

}
