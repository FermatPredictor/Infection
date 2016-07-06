package Inflection;

import javax.swing.JFrame;


public class Main {
	private final static int width = 1200, height = 700;
	public static void main(String [] args){
		
		MyWindow window=new MyWindow();
		window.setName("Chess");
		window.setIPAddress("127.0.0.1").setPort(8000).connect();
		window.setLocation(100, 50);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(width, height);
		window.setVisible(true);
		window.setResizable(false);
	}

}
