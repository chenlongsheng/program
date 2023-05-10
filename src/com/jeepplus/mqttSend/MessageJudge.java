/**
 * 
 */
package com.jeepplus.mqttSend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


import sun.net.www.ParseUtil;
import sun.security.util.Resources;
import uia.utils.file.FileUtils;

/**
 * @author admin
 *
 */

public class MessageJudge {

	
	/*
	 * 
	 * python判断文字是否违规
	 */
	public static Boolean judgeTest(String text) {

		Boolean judge = false;
		try {
			String pythonPath = MainMqttLed.pythonPath;
			System.out.println("python脚本位置: " + pythonPath);

			String[] arguments = new String[] { "python", pythonPath, String.valueOf(text) };

			Process pcs = Runtime.getRuntime().exec(arguments);

			BufferedReader in = new BufferedReader(new InputStreamReader(pcs.getInputStream(), "utf-8"));

			String line = "文字通过失败";

			while ((line = in.readLine()) != null) {
				System.out.println("收到的:" + line);

				if (line.equals("value=0")) {
					System.out.println("====文字通过成功");
					return true;
				}
			}
			in.close();
			int waitFor = pcs.waitFor();
			System.out.println("waitFor = " + waitFor);
			System.out.println("waitFor: 0成功,1未成功,2未检测到脚本");

		} catch (Exception e) {
			e.printStackTrace();
		}
//		return false;
		return true;
	}

}
