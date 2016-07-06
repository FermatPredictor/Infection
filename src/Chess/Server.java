package Chess;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	
	private int[] port = new int[2];
	private String[] player = new String[2];
	public boolean isPlayerOneTurn = true;
	private ServerSocket serverSocket;
	private List<ConnectionThread> connections = new ArrayList<ConnectionThread>();
	
	public Server(int portNum) {
		try {
			this.serverSocket = new ServerSocket(portNum);
			System.out.printf("Server starts listening on port %d.\n", portNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runForever() {
		System.out.println("Server starts waiting for client.");
		player[0] = "player0";
		player[1] = "player1";
	    
		for(int i = 0; i < 2; i ++) {
			try {
				Socket connectionToClient = this.serverSocket.accept();
				System.out.println("Get connection from client " + connectionToClient.getInetAddress() + ":"
						+ connectionToClient.getPort());
				port[i] = connectionToClient.getPort();
				ConnectionThread connThread = new ConnectionThread(connectionToClient);
				connThread.start();
				this.connections.add(connThread);
				
			} catch (IOException e){
				e.printStackTrace();
				}
		}
	}
	
	private void broadcast(String message) {
		for (ConnectionThread connection: connections) {
			connection.sendMessage(message);
		}
	}
	
	class ConnectionThread extends Thread {
		private Socket socket;
		private BufferedReader reader;
		private PrintWriter writer;
		public ConnectionThread(Socket socket) {
			this.socket = socket;
			try {
				this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		public void run() {
			while(true) {
				try {
					int isRight = 1;
					String line = this.reader.readLine();
					//System.out.println(line);
					
					if(line.length() > 0) {
						if(isPlayerOneTurn == true) {
							isPlayerOneTurn = false;
						}
						else {
							isPlayerOneTurn = true;
						}
						broadcast("123");
						System.out.println("isPlayerOneTurn : " + isPlayerOneTurn);
					}
					
//					for(int i = 0; i < 2; i ++) {
//						if(port[i] == this.socket.getPort() && line.length() > 0)
//							    player[i] = line;
//					}
//					
//					for(int i = 0; i < 2; i ++) {
//						if(player[i].equals("player" + Integer.toString(i)))
//							isRight = 0;
//					}
//					
//					if(isRight == 1) {
//						for(int i = 1; i < 2; i ++) {
//						    if(!player[i].equals(player[0]))
//						    	isRight = 0;
//						}
//						if(isRight == 1) {
//						    broadcast("true");
//						}
//						else {
//						    broadcast("NotMatch");
//						}
//						for(int i = 0; i < 2; i ++) {
//							player[i] = "player" + Integer.toString(i);
//						}
//					}
					
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
		public void sendMessage(String message) {
			this.writer.println(message);
			this.writer.flush();
				}
			}
	
	public static void main(String[] args) {
		
		Server server = new Server(8000);
		server.runForever();

	}

}
