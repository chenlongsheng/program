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
import com.jeepplus.bean.Dynamic;
import com.jeepplus.bean.Program;

import onbon.bx06.Bx6GEnv;
import onbon.bx06.Bx6GScreenClient;
import onbon.bx06.Bx6GScreen.Result;
import onbon.bx06.area.DateStyle;
import onbon.bx06.area.DateTimeBxArea;
import onbon.bx06.area.DynamicBxArea;
import onbon.bx06.area.TextCaptionBxArea;
import onbon.bx06.area.TimeStyle;
import onbon.bx06.area.WeekStyle;
import onbon.bx06.area.page.ImageFileBxPage;
import onbon.bx06.area.page.TextBxPage;
import onbon.bx06.cmd.dyn.DynamicBxAreaRule;
import onbon.bx06.cmd.led.ScreenTimingOnOffCmd;
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
public class dynamicSend {

	private static String ip = "192.168.3.241";
	private static int port = 5005;
	private static Bx6M x6M = null;
	private static Bx6E x6E = null;
	private static Bx6GScreenClient screen = null;
	// 创建节目 一个节目相当于一屏显示内容
	private static ProgramBxFile pf;

	public static void main(String[] args) {
		try {
			int port0 = 10000;
			// 1.构建DatagramSocket实例，指定本地端口
			DatagramSocket socket = new DatagramSocket(port0);
			System.out.println("只适用6E系列板卡动态区域发布版本!!! 不支持6M系列");
			System.out.println("启动DatagramSocket服务端口: " + ip + ":" + port0);

			Bx6GEnv.initial(120000);
			System.out.println("**** Bx6GEnv.initial 120000*********");

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

				info = SendLed(receive);
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

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String SendLed(String receive) throws Exception {

		DateTimeBxArea addTime = null;
		Dynamic dynamic = null;
		Dynamic dynamic1 = null;
		Dynamic dynamic2 = null;

		TextCaptionBxArea addpic = null;
		TextCaptionBxArea addpic1 = null;

		DisplayStyle[] styles = DisplayStyleFactory.getStyles().toArray(new DisplayStyle[0]);
		// 第二个参数是控制卡型号，只有型号对才能正常通讯，否则会出现逾时未回应，如果使用的型号API中未定义，用new Bx6M()替代

		screen = new Bx6GScreenClient("MyScreen", new Bx6E());
		boolean connect = screen.connect(ip, port);
		if (!connect) {
			return "false";
		}

		// 连接控制器

		JSONObject jsonObj = JSON.parseObject(receive);
		String entity = jsonObj.getString("cmd_type");

		String showId = jsonObj.getString("show_id");

		if (showId == null) {
			showId = "P001";
		}

		System.out.println("获取cmd_type = " + entity);

		if (entity.equals("del")) {
			screen.deleteAllDynamic();
			screen.deletePrograms();
			return "";
		}else if(entity.equals("dely")) {
			screen.deleteAllDynamic();			 
			return "";
		}
		
				
		

		if (entity.equals("show")) {// 发送节目

			try {
				pf = new ProgramBxFile(showId, screen.getProfile());
			} catch (Exception e) {
				e.printStackTrace();

			}

			pf.setProgramTimeSpan(0);

		}

//		screen.deleteAllDynamic();
//		screen.deletePrograms();

		System.out.println("先清空节目!");

		JSONArray ja = jsonObj.getJSONArray("area_array");

		String showTime = "300";
		int runMode = 0;
		int id = 1;
		int immediatePlay = 1;
		for (int i = 0; i < ja.size(); i++) {

			Program program = JSONObject.parseObject(ja.get(i).toString(), Program.class);
			showTime = program.getShowTime();
//            runMode = program.getRunMode();
//            id = program.getId();
//            immediatePlay = program.getImmediatePlay();
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

			System.out.println("showTime = " + showTime);
			System.out.println("runMode = " + runMode);
			System.out.println("id = " + id);
			System.out.println("immediatePlay = " + immediatePlay);
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

//            if (width == 11111) {
//                screen.deleteAllDynamic();
//                screen.deletePrograms();
//                System.out.println("清空节目成功");
//                return "success";
//            }
			System.out.println(stayTime);
			if (areaType.equals("0")) {
				if (dynamic == null) {
					dynamic = addDynamic(program, styles);
				} else if (dynamic1 == null) {
					dynamic1 = addDynamic(program, styles);
				} else {
					dynamic2 = addDynamic(program, styles);
				}
			} else {

				if (addpic == null) {
					if (stayTime == 300) {

						addpic = sendpictrue(stayTime, program, styles);
					} else {
						addpic1 = sendpictrue(stayTime, program, styles);
					}

				}
			}

		}
		String sendDynamic = SendDynamic(addpic, addpic1, dynamic, dynamic1, dynamic2);
		return sendDynamic;
	}

	public static TextCaptionBxArea sendpictrue(int state, Program program, DisplayStyle[] styles) {

		String left = "";
		String right = "";
		ImageFileBxPage iPage = null;
		
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

		String windowsPro = System.getProperty("os.name");
		
	//	System.out.println(new ClassPathResource("left.jpg").getFile().exists());

		if (windowsPro.contains("Windows")) {

			left = "C:/Users/admin/Desktop/left.jpg"; // 本地
			right = "C:/Users/admin/Desktop/right.jpg"; // 生产
		} else {

			left = "/home/left.jpg"; // 本地
			right = "/home/right.jpg"; // 生产

		}
		// 数据页可以是图片
		if (state == 300) {

			iPage = new ImageFileBxPage(left);
			
		} else {
			iPage = new ImageFileBxPage(right);

		}
		// 设置显示特技为快速打出
		iPage.setDisplayStyle(styles[program.getDisplayStyle()]);

		// 速度
		iPage.setSpeed(program.getSpeed());

		TextCaptionBxArea area = new TextCaptionBxArea(program.getX(), program.getY(), program.getWidth(),
				program.getHeight(), screen.getProfile());
		area.addPage(iPage);

		// 将area添加到节目中，节目中可以添加多个area
		return area;
	}

	public static Dynamic addDynamic(Program program, DisplayStyle[] styles) throws UnsupportedEncodingException {

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
		// 第一个动态
		DynamicBxAreaRule rule = new DynamicBxAreaRule();
		// 设定动态区ID ，此处ID为0 ，多个动态区ID不能相同
		rule.setId(0);

		// 设定异步节目停止播放，仅播放动态区
		// 0:与异步节目一起播放
		// 1:异步节目 停止播放，仅播放动态区
		// 2:当播放完节目编号坐高的异步节目后播放该动态区
		rule.setImmediatePlay((byte) 0);

		// 设定动态区循环播放
		// 0:循环显示
		// 1:显示完成后静止显示最后一页数据
		// 2:循环显示，超过设定时间后数据仍未更新时不再显示
		// 3:循环显示，超过设定时间后数据仍未更新时显示Logo信息
		// 4:循环显示，显示完成最后一页后就不再显示
		rule.setRunMode((byte) 0);

		if (program.getShowTime() == null || program.getShowTime() == "") {
			program.setShowTime("300");
		}
		System.out.println("showTime::" + program.getShowTime());

		rule.setTimeout(Integer.parseInt(program.getShowTime()));
		DynamicBxArea area = new DynamicBxArea(program.getX(), program.getY(), program.getWidth(), program.getHeight(),
				screen.getProfile());
		// *********

		String text = program.getMessage();

		program.setText(program.getMessage());
		// ***********
		TextBxPage page = null;
		if (text != null) {
			String[] split = text.split("/r/n");
			for (int i = 0; i < split.length; i++) {
				if (i == 0) {
					page = new TextBxPage(split[i]);
				} else {
					page.newLine(split[i]);
				}
			}
		}
		// 设置字体
		page.setFont(new Font("宋体", program.getFontBold(), program.getFontSize()));

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

		// 设置显示特技为快速打出
		page.setDisplayStyle(styles[program.getDisplayStyle()]);

		// 设置显示颜色
		page.setForeground(color0);
		area.addPage(page);

		System.out.println("************************");
		System.out.println("数据长度:" + page.getText().length() + "  最终发送的数据  :  " + page.getText());
		System.out.println("************************");

		Dynamic dynamic = new Dynamic(rule, area);
		return dynamic;
	}

	public static DateTimeBxArea addTime(Program program) {
		System.out.println("时钟===");
		// 创建一个时间区
		DateTimeBxArea dtArea = new DateTimeBxArea(program.getX(), program.getY(), program.getWidth(),
				program.getHeight(), screen.getProfile());

		// 设定时间区多行显示
		if (program.getIsMultLine() == 0) {
			dtArea.setMultiline(false);
		} else {
			dtArea.setMultiline(true);
		}
		// 设定日期显示格式 NULL表示不显示日期
		Integer dateSty = program.getDateFormat();
		if (dateSty != null) {
			DateStyle ds = null;
			switch (dateSty) {
			case 1:
				ds = DateStyle.YYYY_MM_DD_3;
				break;
			case 2:
				ds = DateStyle.DD_MM_YYYY_2;
				break;
			case 3:
				ds = DateStyle.YYYY_MM_DD_1;
				break;

			}
			System.out.println("ds=  " + ds);
			dtArea.setDateStyle(ds);
			// 设定时间显示格式 NULL表示不显示时间
		}
		Integer timeSty = program.getTimeFormat();

		if (timeSty != null) {

			TimeStyle timeStyle = null;
			switch (timeSty) {
			case 1:
				timeStyle = TimeStyle.HH_MM_SS_2;
				break;
			case 2:
				timeStyle = TimeStyle.HH_MM_SS_1;
				break;
			case 3:
				timeStyle = TimeStyle.AMPM_HH_MM;
				break;
			case 4:
				timeStyle = TimeStyle.HH12_MM_SS_1;
				break;

			}
			System.out.println("timeStyle=  " + timeStyle);
			dtArea.setTimeStyle(timeStyle);
		}

		// 设定星期显示格式 NULL表示不显示星期
		Integer week = program.getWeekFormat();
		if (week != null) {
			WeekStyle weekStyle = null;
			switch (week) {
			case 1:
				weekStyle = WeekStyle.CHINESE;
				break;
			case 2:
				weekStyle = WeekStyle.ENG;
				break;
			case 3:
				weekStyle = WeekStyle.ENGLISH;
				break;

			}
			System.out.println("weekStyle=  " + weekStyle);
			dtArea.setWeekStyle(weekStyle);
		}
		// 设定时间区字体
		dtArea.setFont(new Font(program.getFontName(), program.getFontBold(), program.getFontSize()));
		// dtArea.setForeground(Color.red);
		return dtArea;

	}

	// 动态区单独播放
	public static String SendDynamic(TextCaptionBxArea addpic, TextCaptionBxArea addpic1, Dynamic dynamic,
			Dynamic dynamic1, Dynamic dynamic2) throws Exception {

		long startTime = System.currentTimeMillis(); // 获取开始时间

		Result<ACK> writeDynamic = null;
		if (dynamic != null) {
			writeDynamic = screen.writeDynamic(dynamic.getDynamicBxAreaRule(), dynamic.getDynamicBxArea());
			System.out.println(writeDynamic.toString());
		}

		if (dynamic1 != null) {
			writeDynamic = screen.writeDynamic(dynamic1.getDynamicBxAreaRule(), dynamic1.getDynamicBxArea());
			System.out.println(writeDynamic.toString());

		}
		if (dynamic2 != null) {
			writeDynamic = screen.writeDynamic(dynamic2.getDynamicBxAreaRule(), dynamic2.getDynamicBxArea());
			System.out.println(writeDynamic.toString());
		}

		if (addpic1 != null) {
			pf.addArea(addpic1);

			System.out.println("111");
		}

		if (addpic != null) {
			pf.addArea(addpic);
			System.out.println("222");
			boolean writeProgram = screen.writeProgram(pf);
		}

		System.out.println("发送节目列表打印完毕------:!!! ");
		screen.disconnect();
		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

		return resultMessage("show", "", null);

	}

	public static String resultMessage(String cmd_type, String result, String showId) {

		JSONObject resultMessage = new JSONObject();

		resultMessage.put("cmd_type", cmd_type);

		if (result.equals("ACK：正確")) {
			return "success";
//            resultMessage.put("result", 0);
		} else {
			return "false";
//            resultMessage.put("result", 1);
		}

	}

}
