package com.jeepplus.test;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.Scanner;

public class test1 {

	
	  public static void main(String[] args) {
		  String str = "1";
	        StringBuilder sb = new StringBuilder();
	        char[] chars = str.toCharArray();
	        for (int i = chars.length - 1; i >= 0; i--) {
	            char c = chars[i];
	            int code = c;
	            if (Character.isHighSurrogate(c) && i > 0) {
	                char high = c;
	                char low = chars[i - 1];
	                code = Character.toCodePoint(high, low);
	                i--;
	            }
	            String hex = Integer.toHexString(code);
	            StringBuilder reversed = new StringBuilder();
	            for (int j = hex.length() - 1; j >= 0; j--) {
	                reversed.append(hex.charAt(j));
	            }
	            sb.append(Character.toChars(Integer.parseInt(reversed.toString(), 16)));
	        }
	        System.out.println(sb.toString());
	    }
}
