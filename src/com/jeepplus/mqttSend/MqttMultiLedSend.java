/**
 * 
 */
package com.jeepplus.mqttSend;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeepplus.bean.MapEntity;
import com.jeepplus.bean.Program;

import onbon.bx06.Bx6GEnv;
import onbon.bx06.Bx6GScreenClient;
import onbon.bx06.Bx6GScreen.Result;
import onbon.bx06.area.DateStyle;
import onbon.bx06.area.DateTimeBxArea;
import onbon.bx06.area.TextCaptionBxArea;
import onbon.bx06.area.TimeStyle;
import onbon.bx06.area.WeekStyle;
import onbon.bx06.area.page.TextBxPage;
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
public class MqttMultiLedSend {

	private static String ip = "192.168.3.241";

	private static int port = 5005;
	private static Bx6GScreenClient screen = null;

	// 创建节目 一个节目相当于一屏显示内容
	private static ProgramBxFile pf;

	/*
	 * 多节目发送
	 */
	public static String SendLed(JSONObject jsonObj) throws Exception {

		// 创建一个list
//		ArrayList<ProgramBxFile> plist = new ArrayList<ProgramBxFile>();
		DisplayStyle[] styles = DisplayStyleFactory.getStyles().toArray(new DisplayStyle[0]);
		// 第二个参数是控制卡型号，只有型号对才能正常通讯，否则会出现逾时未回应，如果使用的型号API中未定义，用new Bx6M()替代
		screen = new Bx6GScreenClient("MyScreen", new Bx6M());

		// 初始化屏幕链接

		Integer entity = jsonObj.getInteger("cmd_type");
		Integer showId = jsonObj.getInteger("show_id");
		boolean connect = screen.connect(ip, port);
		if (!connect) {
			System.out.println("屏幕链接异常!!!");
			return resultMessage(entity, "", showId);

		}
		String showIdScreen = "P" + String.format("%03d", jsonObj.getInteger("show_id"));

		System.out.println("获取showId = " + showId);
		DateTimeBxArea addTime = null;
		TextCaptionBxArea addtext = null;
		TextCaptionBxArea addtext1 = null;

		if (entity == 4) {// 发送节目 4

			try {
				pf = new ProgramBxFile(showIdScreen, screen.getProfile());
			} catch (Exception e) {
				e.printStackTrace();
				return resultMessage(4, "", showId);
			}
			Integer programTime = jsonObj.getInteger("programTime");
			pf.setProgramTimeSpan(programTime / 100);
			System.out.println(programTime / 100 + "==programTime设置");

		} else if (entity == 30) {

			Integer state = jsonObj.getInteger("state");

			if (state == 0) {// 关机
				Result<ACK> turnOff = screen.turnOff();
				return resultMessage(30, turnOff.toString(), null);

			} else if (state == 1) {// 开机
				Result<ACK> turnOn = screen.turnOn();
				return resultMessage(30, turnOn.toString(), null);
			}

		} else if (entity == 31) {// 设置屏幕亮度
			int level = jsonObj.getInteger("level");

			Result<ACK> manualBrightness = screen.manualBrightness((byte) level);

			return resultMessage(31, manualBrightness.toString(), null);

		} else if (entity == 32) {// 删除节目

			if (showId == null) {// 校准时间
				Long time = jsonObj.getLong("time_stamp");
				System.out.println("time:==  " + time);
				System.out.println("时间戳日期:" + transForDate3(time));
				Result<ACK> syncTime = screen.syncTime(transForDate3(time));
				return resultMessage(32, syncTime.toString(), null);
			}
			if (showId == 10000) {
				String series = screen.getControllerType();
				boolean contains = series.contains("6E");
				if (contains) {
					screen.deleteAllDynamic();
				}
				Result<ACK> deletePrograms = screen.deletePrograms();
				return resultMessage(32, deletePrograms.toString(), showId);
			}

			Result<ACK> deleteProgram = screen.deleteProgram(showIdScreen);
			List<String> pfs1 = screen.readProgramList();

			for (String program : pfs1) {
				System.out.println("删除后剩下节目列表: " + program);
			}
			return resultMessage(32, deleteProgram.toString(), showId);

		} else if (entity == 33) {// 校准时间
			Long time = jsonObj.getLong("time_stamp");

			System.out.println("time:==  " + time);

			System.out.println("时间戳日期:" + transForDate3(time));
			Result<ACK> syncTime = screen.syncTime(transForDate3(time));
			return resultMessage(33, syncTime.toString(), null);

		} else {
			return resultMessage(32, "", null);
		}
		// 以下发送节目过程
		JSONArray ja = jsonObj.getJSONArray("AreaList");

		Program program;
		for (int i = 0; i < ja.size(); i++) {
			MapEntity map = JSONObject.parseObject(ja.get(i).toString(), MapEntity.class);
			JSONObject TextArgs = (JSONObject) map.get("TextArgs");//文字
			JSONObject ClockArgs = (JSONObject) map.get("ClockArgs");//时钟

			program = new Program();
			// System.out.println(map.toString());

			program.setX(Integer.parseInt(map.get("Left").toString()));
			program.setY(Integer.parseInt(map.get("Top").toString()));
			program.setHeight(Integer.parseInt(map.get("Height").toString()));
			program.setWidth(Integer.parseInt(map.get("Width").toString()));

			String areaType = map.get("AreaType").toString();
			program.setAreaType(map.get("AreaType").toString());
			System.out.println("AreaType: " + map.get("AreaType").toString());

			if (TextArgs != null) {// 文字数据

				if (!MessageJudge.judgeTest(TextArgs.getString("TextContent"))) {
					return resultMessage(4, "过滤失败", showId);

				}
				program.setMessage(TextArgs.getString("TextContent"));//文字内容
				program.setSpeed(TextArgs.getInteger("Speed"));//速度
				program.setTextBinary(TextArgs.getInteger("HAlignment"));// 左中右
				program.setColor(TextArgs.getInteger("color"));//颜色

				program.setDisplayStyle(resultStyle(TextArgs.getInteger("InStyle")));// 进入样式

				program.setFontBold(TextArgs.getInteger("FontBold"));
				program.setStayTime(TextArgs.getInteger("DelayTime"));//停留时间
				program.setFontSize(TextArgs.getInteger("FontSize"));
				program.setFontName(TextArgs.getString("FontName"));

			}
			if (ClockArgs != null) {// 时钟数据

				program.setWeekFormat(ClockArgs.getInteger("WeekFormat"));//星期格式
				program.setTimeFormat(ClockArgs.getInteger("TimeFormat"));//时间格式
				program.setFontSize(ClockArgs.getInteger("FontSize"));
				program.setFontName(ClockArgs.getString("FontName"));
				program.setFontBold(ClockArgs.getInteger("FontBold"));
				program.setStayTime(ClockArgs.getInteger("StayTime"));//停留时间
				program.setDateFormat(ClockArgs.getInteger("DateFormat"));//日期显示
				program.setIsMultLine(ClockArgs.getBoolean("IsMultipleLine") ? 1 : 0);//是否多行

			}

			if (areaType.equals("1")) {
				try {
					addTime = addTime(program);
				} catch (Exception e) {
					return resultMessage(4, "", showId);
				}
			}
			if (areaType.equals("0")) {
				if (addtext == null) {
					addtext = addtext(program, styles);
				} else {
					addtext1 = addtext(program, styles);
				}
			}
		}

		String sendProgram = sendProgram(addTime, addtext, addtext1, showId);
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
		String text = java.net.URLDecoder.decode(program.getMessage(), "utf-8");
		program.setText(text);
		// ***********
		TextBxPage page = new TextBxPage(text);

		// 设置字体
		page.setFont(new Font(java.net.URLDecoder.decode(program.getFontName(), "utf-8"), program.getFontBold(),
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
		System.out.println("数据长度:" + text + "  最终发送的数据  :  " + page.getText());
		System.out.println("************************");
		return area;
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

//		dtArea.setForeground(Color.red);
		return dtArea;

	}

	public static String sendProgram(DateTimeBxArea addTime, TextCaptionBxArea addtext, TextCaptionBxArea addtext1,
			Integer showId) throws Exception {

		System.out.println("获取板卡版本信息: " + screen.getControllerType());
		System.out.println("获取tos: " + screen.getProfile().toString());
		System.out.println("屏幕宽" + screen.readConfig().getScreenWidth());
		System.out.println("屏幕高" + screen.readConfig().getScreenHeight());
		String series = screen.getControllerType();
		boolean contains = series.contains("6E");

		if (contains) {
			System.out.println("设置buffer为1024*60");
			screen.changeOutputBuffer(1024 * 60);
		}

		if (addtext != null) {
			pf.addArea(addtext);
		}
		if (addtext1 != null) {
			pf.addArea(addtext1);
		}
		if (addTime != null) {
			pf.addArea(addTime);
		}

		long startTime = System.currentTimeMillis(); // 获取开始时间

		boolean writeProgram = screen.writeProgram(pf);
		List<String> pfs = screen.readProgramList();

		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("程序发送节目时间：" + (endTime - startTime) + "ms");

		if (writeProgram) {
			return resultMessage(4, "ACK：正確", showId);
		} else {
			return resultMessage(4, "", showId);
		}
	}

	/**
	 * 时间戳转日期
	 * 
	 * @param ms
	 * @return
	 */
	public static Date transForDate3(Long ms) {

		long msl = (long) ms * 1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date temp = null;
		if (ms != null) {
			try {
				String str = sdf.format(msl);
				temp = sdf.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return temp;
	}

	/*
	 * 
	 * 转换进入风格
	 */
	public static Integer resultStyle(int style) {

		Integer reStyle = 2;
		switch (style) {
		case 0:
			reStyle = 2;// 立即
			break;
		case 1:
			reStyle = 6;// 随机
			break;
		case 2:
			reStyle = 3;// 左移
			break;
		case 3:
			reStyle = 37;// 右
			break;
		case 4:
			reStyle = 5;// 上
			break;
		case 5:
			reStyle = 39;// 下
			break;
		case 6:
			reStyle = 4;// 向左连移
			break;
		case 7:
			reStyle = 38;// 向右连移
			break;
		case 8:
			reStyle = 6;// 向上连移
			break;
		case 9:
			reStyle = 40;// 向下
			break;
		default:
			break;
		}

		return reStyle;
	}

	/*
	 * 
	 * 返回json数据结果给客户端
	 */
	public static String resultMessage(int cmd_type, String result, Integer showId) {

		JSONObject resultMessage = new JSONObject();

		resultMessage.put("cmd_type", cmd_type);

		if (result.equals("过滤失败")) {
			resultMessage.put("result", 4);
		}

		else if (result.equals("ACK：正確")) {
			resultMessage.put("result", 0);
		} else {
			resultMessage.put("result", cmd_type == 4 ? 3 : 1);

		}

		if (showId != null) {
			resultMessage.put("show_id", showId);
		}
		return resultMessage.toString();

	}

}
