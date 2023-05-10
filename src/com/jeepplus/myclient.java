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
public class myclient {

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
    // 12:左右交叉移入 BX-6E Series BX-6M3
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
    // 37:向右移动
    // 38:向右连移
    // 39:向下移动
    public static void main(String[] args) throws Exception {
        // 1.建立一个Socket
        DatagramSocket socket = new DatagramSocket();// 1:静止显示
        Base64 base64 = new Base64();
        String text = "中央一号文件原指中共中央每年发布的第一份文件。1949年10月1日，中华人民共和国中央人民政府开始发布《第一号文件》。现在已成为中共中央、国务院重视农村问题的专有名词。 [1]  "
                + "中共中央在1982年至1986年连续五年发布以农业、农村和农民为主题的中央一号文件，对农村改革和农业发展作出具体部署。2004年至2021年又连续十八年发布以“三农”（农业、农村、农民）为主题的中央一号文件，"
                + "强调了“三农”问题在中年发布以农业、农村和农民为主题的中央一号文件，对农村改革和农业发展作出具体部署。2004年至2021年又连续十八年发布以“三农”（农业、农村、农民）为主题的中年发布以农业、农村和农民为主题的中央一号文件，对农村改革和农业发展作出具体部署。2004年至2021年又连续十八年发布以“三农”（农业、农村、农民）为主题的中国社会主义现代化时期“重中之重”的地位。";
    String text1 = "999999";

        System.out.println(text.length());
        byte[] textByte = text.getBytes("UTF-8");

        byte[] textByte1 = text1.getBytes("UTF-8");
        // 编码
        String encodedText = base64.encodeToString(textByte);
        String encodedText1 = base64.encodeToString(textByte1);

        // areaType 1是时钟 0是文字
        String msg = "{\"time_stamp\":1660183762,\"mac_addr\":\"aaaabbbb\",\"show_id\":2,\"pro\":\"LEDPro\","
        		+ "\"AreaList\":[{\"AreaName\":\"字幕\",\"Left\":0,\"Top\":0,\"TextArgs\":{\"Speed\":0,\"HAlignment\":0,\"color\":0,\"InStyle\":6,\"FontBold\":0,\"DelayTime\":3,"
        		+ "\"FontUnderLine\":0,\"FontSize\":12,\"FontName\":\"宋体\",\"TextFormat\":0,\"fontItalic\":0,\"IsVCenter\":0,\"TextContent\":\"170字幕\"},"
        		+ "\"Height\":96,\"AreaType\":0,\"Width\":192,\"AreaNo\":0}],\"version\":10,\"programTime\":0,\"cmd_type\":4,\"dev_id\":\"02c000814ccab630\"}";
       

        System.out.println(msg);
        InetAddress localhost = InetAddress.getByName("localhost");
        System.out.println(localhost);
        int port = 10000;
        // 数据，数据的长度起始， 要发送给谁
        DatagramPacket packet = new DatagramPacket(msg.getBytes(), 0, msg.getBytes().length, localhost, port);
        // 3.发送包
        socket.send(packet);
        // 4.关闭流
        socket.close();
    }

}
