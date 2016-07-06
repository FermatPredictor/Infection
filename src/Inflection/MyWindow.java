package Inflection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;


public class MyWindow {
	private static final long serialVersionUID = 1L;
	
	private String destinationIPAddr;
	private int destinationPortNum;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private Board applet1;
	
	
	public MyWindow() {
		applet1 = new Board();
		setLayout(null);
		applet1.init();
		applet1.start();
		applet1.setFocusable(true);
		this.setContentPane(applet1);
	}
	
	public MyWindow(String IPAddress, int portNum) {
		this();
		
		this.destinationIPAddr = IPAddress;
		this.destinationPortNum = portNum;
	}
	
	public MyWindow setIPAddress(String IPAddress) {
		this.destinationIPAddr = IPAddress;
		return this;
	}
	
	public MyWindow setPort(int portNum) {
		this.destinationPortNum = portNum;
		return this;
	}
	
	public void connect() {
		try {
			socket = new Socket(destinationIPAddr, destinationPortNum);
			writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			ClientThread client = new ClientThread(reader);
			client.start();

		} catch (UnknownHostException e){
			e.printStackTrace();
				} catch (ConnectException e){
					e.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}
	}
	
	private void sendMessage(String message) {
//		System.out.println(SwingUtilities.isEventDispatchThread());
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(message);
		this.writer.println(sBuilder.toString());
		this.writer.flush();
	}
	
	class ClientThread extends Thread {
		private BufferedReader reader;
		public ClientThread(BufferedReader reader) {
			this.reader = reader;
		}
		public void run() {
			while(true) {
				System.out.print("");
				if(applet1.isClicked) {
					sendMessage("true");
					applet1.isClicked = false;
				}
			}
		}
	}

}
