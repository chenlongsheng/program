/**
 * 
 */
package com.jeepplus.test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;

/**
 * @author admin
 *
 */
public class test {

	public static String mysql() {

		try {
			Connection con = null;
			Class.forName("com.mysql.jdbc.Driver");
			// mysql驱动
			con = (Connection) DriverManager.getConnection("jdbc:mysql://192.168.3.50:3306/collegefire", "root",
					"csit2012!@#");
			Statement ps = (Statement) con.createStatement();
			String sql = "select count(*) id from t_code ";
			ResultSet rs = ps.executeQuery(sql);
			while (rs.next()) {
				// 循环输出结果集
				String account = rs.getString("id");
				System.out.println("account:" + account);
				return account;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) throws UnsupportedEncodingException {

		Base64 base64 = new Base64();
		String text = "你好吗";
		byte[] textByte = text.getBytes("UTF-8");
		// 编码
		String encodedText = base64.encodeToString(textByte);
		System.out.println(encodedText);
		String msg = "{\"area_array\":[{\"textBinary\":1,\"areaType\":0,\"color\":0,\"displayStyle\":5,"
				+ "\"fontBold\":0,\"fontName\":\"宋体\",\"fontSize\":10,\"height\":20,\r\n\"message\":\"" + encodedText
				+ "\",\"speed\":5,\"stayTime\":200,\"width\":192,\"x\":0,\"y\":0}],\"cmd_type\":\"show\"}";

		System.out.println(msg);

		Properties props = new Properties();
		String value = null;
		try {

			InputStream in = new BufferedInputStream(new FileInputStream("D:/111.txt"));
			props.load(in);
			value = props.getProperty("jdbc");
			System.out.println(value);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void datedata() {

		Date da = new Date();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(da);

		calendar.add(Calendar.MONTH, 0);

		Date theDate = calendar.getTime();

		// 上个月第一天

		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();

		gcLast.setTime(theDate);

		gcLast.set(Calendar.DAY_OF_MONTH, 1);

		String day_first = df.format(gcLast.getTime());

		StringBuffer str = new StringBuffer().append(day_first).append(

				" 00:00:00");

		day_first = str.toString();

		System.out.println(day_first);

		// 上个月最后一天

		calendar.add(Calendar.MONTH, 1); // 加一个月

		calendar.set(Calendar.DATE, 1); // 设置为该月第一天

		calendar.add(Calendar.DATE, -1); // 再减一天即为上个月最后一天

		String day_last = df.format(calendar.getTime());

		StringBuffer endStr = new StringBuffer().append(day_last).append(

				" 23:59:59");

		day_last = endStr.toString();

		System.out.println(day_last);

	}
}
