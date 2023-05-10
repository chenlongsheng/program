package com.jeepplus.test;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TextRotation extends JPanel {
    String str = "Hello, World!";
    
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Font font = new Font("Serif", Font.PLAIN, 20);
        g2d.setFont(font);
        
        char[] chars = str.toCharArray();
        int x = 50;
        int y = 50;
        int angle = 0;
        for (char c : chars) {
            g2d.rotate(Math.toRadians(angle), x, y);
            g2d.drawChars(new char[] {c}, 0, 1, x, y);
            g2d.rotate(Math.toRadians(-angle), x, y);
            x += g2d.getFontMetrics().charWidth(c);
            angle += 360 / chars.length;
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Text Rotation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new TextRotation());
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
