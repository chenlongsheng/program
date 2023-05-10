/**
 * 
 */
package com.jeepplus;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeepplus.bean.Program;

import onbon.bx06.Bx6GEnv;
import onbon.bx06.Bx6GScreenClient;
import onbon.bx06.Bx6GScreen.Result;
import onbon.bx06.area.DateStyle;
import onbon.bx06.area.DateTimeBxArea;
import onbon.bx06.area.TextCaptionBxArea;
import onbon.bx06.area.TimeStyle;
import onbon.bx06.area.WeekStyle;
import onbon.bx06.area.page.ImageFileBxPage;
import onbon.bx06.area.page.TextBxPage;
import onbon.bx06.file.ProgramBxFile;
import onbon.bx06.message.global.ACK;
import onbon.bx06.message.led.ReturnControllerStatus;
import onbon.bx06.message.led.ReturnCurrentFirmwareStatus;
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
public class ledSend {
	private static String ip = "192.168.3.241";
	private static int port = 5005;
	private static Bx6M x6M = null;
	private static Bx6E x6E = null;
	private static Bx6GScreenClient screen = null;
	// 创建节目 一个节目相当于一屏显示内容
	private static ProgramBxFile pf;
	private static String type = System.getenv("type");
	private static String Bx6 = System.getenv("Bx6");

	public static void main(String[] args) {

		try {
			int port0 = 10000;
			// 1.构建DatagramSocket实例，指定本地端口
			DatagramSocket socket = new DatagramSocket(port0);
			System.out.println("ip: " + ip);
			try {
				Bx6GEnv.initial(1200000);
				System.out.println("******没有字数限制*********");
//				System.out.println("changeOutputBuffer(1024*60)");
			} catch (Exception e1) {
			}
			byte[] buf = new byte[102400];
			// 2.构建需要收发的DatagramPacket报文--构造 DatagramPacket，用来接收长度为 length 的数据包。
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			while (true) {
				// 收报文---从此套接字接收数据报包，此方法在接收到数据报前一直阻塞
				socket.receive(packet);
				System.out.println("本机ip:" + packet.getAddress().getHostAddress());
				// 接收信息
				String receive = new String(packet.getData(), 0, packet.getLength());

				System.out.println("收到的数据:  " + new String(packet.getData(), 0, packet.getLength()));
				String info = "";
				try {
					info = SendLed(receive);
				} catch (Exception e) {
					info = "false";
					e.printStackTrace();
				}
				// 构造数据报包，用来将长度为 length 的包发送到指定主机上的指定端口号。
				DatagramPacket dp = new DatagramPacket(info.getBytes(), info.getBytes().length, packet.getAddress(),
						packet.getPort());
				// 为此包设置数据缓冲区。将此 DatagramPacket 的偏移量设置为 0，长度设置为 buf 的长度
				dp.setData(info.getBytes());
				System.out.println("发送回复的消息:  " + info);
				socket.send(dp);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String SendLed(String receive) throws Exception {

		DateTimeBxArea addTime = null;
		TextCaptionBxArea addtext = null;
		TextCaptionBxArea addtext1 = null;
		TextCaptionBxArea addpic = null;

		DisplayStyle[] styles = DisplayStyleFactory.getStyles().toArray(new DisplayStyle[0]);
		// 第二个参数是控制卡型号，只有型号对才能正常通讯，否则会出现逾时未回应，如果使用的型号API中未定义，用new Bx6M()替代

		screen = new Bx6GScreenClient("MyScreen", new Bx6M());
		// 初始化屏幕链接

		boolean connect = screen.connect(ip, port);
		if (!connect) {
			System.out.println("屏幕链接失败!");

		}
		// 连接控制器
		pf = new ProgramBxFile("P001", screen.getProfile());
		pf.setProgramTimeSpan(0);
		JSONObject jsonObj = JSON.parseObject(receive);

		String entity = jsonObj.getString("cmd_type");
		System.out.println("获取删除是否:" + entity);

		JSONArray ja = jsonObj.getJSONArray("area_array");

		for (int i = 0; i < ja.size(); i++) {

			Program program = JSONObject.parseObject(ja.get(i).toString(), Program.class);
			// ip = program.getIp(); //获取ip

			String areaType = program.getAreaType();
			String message = program.getMessage();
			String fontName = program.getFontName();
			Integer x = program.getX();
			Integer y = program.getY();
			Integer width = program.getWidth();
			Integer height = program.getHeight();
			Integer color = program.getColor();
			Integer fontSize = program.getFontSize();
			Integer fontBold = program.getFontBold();
			Integer speed = program.getSpeed();
			Integer stayTime = program.getStayTime();
			Integer displayStyle = program.getDisplayStyle();
			Integer textBinary = program.getTextBinary();

			System.out.println("areaType = " + areaType);
			System.out.println("ip = " + ip);
			System.out.println("port = " + port);
			System.out.println("message = " + message);
			System.out.println("x = " + x);
			System.out.println("y = " + y);
			System.out.println("width = " + width);
			System.out.println("height = " + height);
			System.out.println("color =" + color);
			System.out.println("fontName = " + fontName);
			System.out.println("fontSize = " + fontSize);
			System.out.println("fontBold = " + fontBold);
			System.out.println("stayTime = " + stayTime);
			System.out.println("speed = " + speed);
			System.out.println("displayStyle = " + displayStyle);
			System.out.println("textBinary = " + textBinary);
			System.out.println("选择节目还是动态type = " + type);
			System.out.println("选择M或者E,Bx6 = " + Bx6);
			if (areaType == null) {
				areaType = "0";
			}
			if (fontName == null) {
				fontName = "宋体";
			}
			if (fontSize == null) {
				fontSize = 12;
			}
			if (fontBold == null) {
				fontBold = 0;
			}
			if (stayTime == null) {
				stayTime = 500;
			}
			if (speed == null) {
				speed = 4;
			}
			if (displayStyle == null) {
				displayStyle = 5;
			}
			if (textBinary == null) {
				textBinary = 0;
			}

			if (areaType.equals("1")) {
				addpic = sendpictrue(program, styles);
			}
			if (areaType.equals("0")) {
				if (addtext == null) {
					addtext = addtext(program, styles);
				} else {
					addtext1 = addtext(program, styles);
				}
			}

		}
		String sendProgram = sendProgram(addpic, addtext, addtext1);
		return sendProgram;
	}

	public static TextCaptionBxArea addtext(Program program, DisplayStyle[] styles)
			throws UnsupportedEncodingException {

		Color color0 = null;
		if (program.getColor() == 0) {
			color0 = Color.red;
		} else if (program.getColor() == 1) {
			color0 = Color.blue;
		} else if (program.getColor() == 2) {
			color0 = Color.green;
		} else {
			color0 = Color.red;
		}
		TextCaptionBxArea area = new TextCaptionBxArea(program.getX(), program.getY(), program.getWidth(),
				program.getHeight(), screen.getProfile());
		// 创建一个数据页
		// 第一行数据
		// *********
		Base64 base64 = new Base64();
		String text = new String(base64.decode(program.getMessage()), "UTF-8");
		program.setText(text);
		// ***********
		TextBxPage page = new TextBxPage(text);
		// 设置字体
		page.setFont(new Font(URLDecoder.decode(program.getFontName(), "UTF-8"), program.getFontBold(),
				program.getFontSize()));
		// 调整停留时间, 单位 10ms
		page.setStayTime(program.getStayTime());
		// 速度
		page.setSpeed(program.getSpeed());

		Alignment align = null;
		if (program.getTextBinary() == 0) {
			align = TextBinary.Alignment.NEAR;
		} else if (program.getTextBinary() == 1) {
			align = TextBinary.Alignment.CENTER;
		} else if (program.getTextBinary() == 2) {
			align = TextBinary.Alignment.FAR;
		} else {
			align = TextBinary.Alignment.CENTER;
		}
		// 字体012.左中右.
		page.setHorizontalAlignment(align);

		// 设置显示颜色
		page.setForeground(color0);

		// 设置显示特技为快速打出
		page.setDisplayStyle(styles[program.getDisplayStyle()]);

		area.addPage(page);
		System.out.println("************************");
		System.out.println("数据长度:" + page.getText().length() + "  最终发送的数据  :  " + page.getText());
		System.out.println("************************");
		return area;
	}

	public static DateTimeBxArea addTime(Program program) {

		// 创建一个时间区
		DateTimeBxArea dtArea = new DateTimeBxArea(program.getX(), program.getY(), program.getWidth(),
				program.getHeight(), screen.getProfile());

		// 设定时间区多行显示
		dtArea.setMultiline(true);
		// 设定日期显示格式 NULL表示不显示日期
		dtArea.setDateStyle(DateStyle.YYYY_MM_DD_1);
		// 设定时间显示格式 NULL表示不显示时间
		dtArea.setTimeStyle(TimeStyle.HH12_MM_SS_1);
		// 设定星期显示格式 NULL表示不显示星期
		dtArea.setWeekStyle(WeekStyle.CHINESE);
		// 设定时间区字体
		dtArea.setFont(new Font(program.getFontName(), program.getFontBold(), program.getFontSize()));

		return dtArea;

	}

	public static TextCaptionBxArea sendpictrue(Program program, DisplayStyle[] styles) {

		// 数据页可以是图片
		ImageFileBxPage iPage = new ImageFileBxPage("C:/Users/admin/Desktop/1.png");
		System.out.println("===0===");
		TextCaptionBxArea area = new TextCaptionBxArea(program.getX(), program.getY(), program.getWidth(),
				program.getHeight(), screen.getProfile());
		area.addPage(iPage);

		// 将area添加到节目中，节目中可以添加多个area
	 
		return area;

	}

	public static String sendProgram(TextCaptionBxArea addTime, TextCaptionBxArea addtext, TextCaptionBxArea addtext1)
			throws Exception {

		if (addtext != null) {
			pf.addArea(addtext);
			System.out.println("====1==");
		}
		if (addtext1 != null) {
			pf.addArea(addtext1);
			System.out.println("===2===");
		}
		if (addTime != null) {
			
			screen.deletePrograms();
			screen.deleteAllDynamic();
			pf.addArea(addTime);
			System.out.println("===3===");
			
		}

		long startTime = System.currentTimeMillis(); // 获取开始时间
		screen.deletePrograms();
		
//        String series = screen.getControllerType();
//        boolean contains = series.contains("6E");
//        if (contains) {
//            System.out.println("设置buffer为1024*60");
//            screen.changeOutputBuffer(1024 * 60);
//        }
				
		boolean writeProgram = screen.writeProgram(pf);		

		List<String> pfs = screen.readProgramList();
		for (String program : pfs) {
			System.out.println("已有的节目列表: " + program);
		}
		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
		screen.disconnect();

		if (writeProgram) {
			return "success";
		} else {
			return "false";
		}
	}

}
