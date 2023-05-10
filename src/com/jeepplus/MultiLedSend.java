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
public class MultiLedSend {

    private static String ip = "192.168.3.241";

    private static int port = 5005;
    private static Bx6GScreenClient screen = null;

    // 创建节目 一个节目相当于一屏显示内容
    private static ProgramBxFile pf;

    public static void main(String[] args) {

        try {
            int port0 = 10000;
            // 1.构建DatagramSocket实例，指定本地端口
            DatagramSocket socket = new DatagramSocket(port0);
            System.out.println("ip: " + ip);
            Bx6GEnv.initial(300000);
            System.out.println("**多节目发送版本*****");
            System.out.println("6E板卡修改成changeOutputBuffer(1024*30)");

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
                    screen.disconnect();
                } catch (Exception e) {
                    info = resultMessage("show", "1", null);

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
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    /*
     * 多节目发送
     */
    public static String SendLed(String receive) throws Exception {

        // 创建一个list
//		ArrayList<ProgramBxFile> plist = new ArrayList<ProgramBxFile>();
        DisplayStyle[] styles = DisplayStyleFactory.getStyles().toArray(new DisplayStyle[0]);
        // 第二个参数是控制卡型号，只有型号对才能正常通讯，否则会出现逾时未回应，如果使用的型号API中未定义，用new Bx6M()替代
        screen = new Bx6GScreenClient("MyScreen", new Bx6M());

        // 初始化屏幕链接

        boolean connect = screen.connect(ip, port);
        if (!connect) {

            return resultMessage("connect", "", null);

        }

        JSONObject jsonObj = JSON.parseObject(receive);
        String entity = jsonObj.getString("cmd_type");
        String showId = jsonObj.getString("show_id");
        System.out.println("获取cmd_type = " + entity);
        DateTimeBxArea addTime = null;
        TextCaptionBxArea addtext = null;
        TextCaptionBxArea addtext1 = null;

        if (entity.equals("show")) {// 发送节目

            try {
                pf = new ProgramBxFile(showId, screen.getProfile());
            } catch (Exception e) {
                e.printStackTrace();
                return resultMessage("show", "", showId);
            }
            Integer programTime = jsonObj.getInteger("programTime");

            pf.setProgramTimeSpan(programTime / 100);

            System.out.println(programTime / 100 + "==programTime设置");

        } else if (entity.equals("off")) {// 关机

            Result<ACK> turnOff = screen.turnOff();
            return resultMessage("off", turnOff.toString(), null);

        } else if (entity.equals("ding")) {// 定时开机关机

            ScreenTimingOnOffCmd cmd = new ScreenTimingOnOffCmd();
            cmd.addTime(15, 39, 15, 40);
            Result<ACK> setupTimingOnOff = screen.setupTimingOnOff(cmd);
            screen.cancelTimingOnOff();
            return resultMessage("ding", "", null);
        } else if (entity.equals("on")) {// 开机

            Result<ACK> turnOn = screen.turnOn();
            return resultMessage("on", turnOn.toString(), null);

        } else if (entity.equals("light")) {// 设置屏幕亮度
            int level = jsonObj.getInteger("level");
            Result<ACK> manualBrightness = screen.manualBrightness((byte) level);

            return resultMessage("light", manualBrightness.toString(), null);

        } else if (entity.equals("del")) {// 删除节目

            if (showId.equals("P10000")) {
                String series = screen.getControllerType();
                boolean contains = series.contains("6E");
                if (contains) {
                    screen.deleteAllDynamic();
                }
                Result<ACK> deletePrograms = screen.deletePrograms();
                return resultMessage("del", deletePrograms.toString(), showId);
            }

            Result<ACK> deleteProgram = screen.deleteProgram(showId);
            List<String> pfs1 = screen.readProgramList();
            for (String program : pfs1) {
                System.out.println("删除后剩下节目列表: " + program);
            }
            return resultMessage("del", deleteProgram.toString(), showId);

        } else if (entity.equals("sync_time")) {// 校准时间
            String time = jsonObj.getString("time");

            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

            Date date = format.parse(time);
            System.out.println(date);
            Result<ACK> syncTime = screen.syncTime(date);
            return resultMessage("sync_time", syncTime.toString(), null);

        } else {
            return resultMessage(entity, "", null);
        }

        JSONArray ja = jsonObj.getJSONArray("area_array");
        for (int i = 0; i < ja.size(); i++) {
            Program program = JSONObject.parseObject(ja.get(i).toString(), Program.class);

            program.setShowId(showId);// 设置节目id

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
            System.out.println("选择getIsMultLine = " + program.getIsMultLine());
//			System.out.println("选择M或者E,Bx6 = " + Bx6);
            if (areaType == null) {
                program.setAreaType("0");
            }
            if (fontName == null) {
                program.setFontName("宋体");
            }
            if (fontSize == null) {
                program.setFontSize(12);
            }
            if (fontBold == null) {
                program.setFontBold(0);
            }
            if (stayTime == null) {
                program.setStayTime(5000);
            }
            if (speed == null) {
                program.setSpeed(4);
            }
            if (displayStyle == null) {
                program.setDisplayStyle(5);
            }
            if (textBinary == null) {
                program.setTextBinary(0);
            }

            if (areaType.equals("1")) {
                try {
                    addTime = addTime(program);
                } catch (Exception e) {
                    return resultMessage("show", "", showId);
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
        System.out.println("文字===");

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
            String showId) throws Exception {

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
        String strPro = "";
        for (int i = 0; i < pfs.size(); i++) {
            System.out.println(pfs.get(i));
//            String program = pfs.get(i);
//            String str = program.substring(0, 1);
//            if (str.equals("P")) {
//                int pro = Integer.parseInt(program.substring(1, 4));
//                strPro += pro;
//                if (i != pfs.size() - 1) {
//                    strPro += ",";
//                }
//            }
        }
        long endTime = System.currentTimeMillis(); // 获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

        if (writeProgram) {
            return resultMessage("show", "ACK：正確", showId);
        } else {
            return resultMessage("show", "", showId);
        }
    }

    public static String resultMessage(String cmd_type, String result, String showId) {

        JSONObject resultMessage = new JSONObject();

        resultMessage.put("cmd_type", cmd_type);

        if (result.equals("ACK：正確")) {
            resultMessage.put("result", 0);
        } else {
            resultMessage.put("result", 1);
        }

        if (showId != null) {
            resultMessage.put("show_id", showId);
        }
        return resultMessage.toString();

    }

}
