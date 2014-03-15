import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


public class ChatServer {

	ServerSocket serverSocket = null;
	final static int PORT_NUMBER = 32500;
	
	private ConcurrentHashMap<InetSocketAddress, String> users = new ConcurrentHashMap<InetSocketAddress, String>();

	public ChatServer(){
		
	}
	
	public void createSocket(){
		try {
			serverSocket = new ServerSocket(32500);
		} catch (IOException e) {
			System.err.println("Error opening the Server Socket");
			e.printStackTrace();
		}
	}
	
	public void closeSocket(){
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Error closing the Server Socket");
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a user to the list with said username
	 * @param ip : The ip/socket address of the user
	 * @param username : The username of the user
	 * @return : If the add was successful, will return false if the username exists.
	 */
	public boolean addUser(InetSocketAddress ip, String username){
		
		return false;
	}
	
	/**
	 * Remove user from the list
	 * @param ip : The ip/socket of the user
	 * @param username : The username of the user
	 */
	public void removeUser(InetSocketAddress ip, String username){
		
	}
	
	/**
	 * Main Method for the Server
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ChatServer();
	}

}


class ChatServerClientHandler{
	
	ChatServer chatServer = null;
	Socket client = null;
	
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	ChatServerClientHandler(Socket s, ChatServer cs){
		this.chatServer = cs;
		this.client = s;
		
		createStreams();
		processCommand(getCommand());
		closeConnection();
	}
	
	private void createStreams(){
		try {
			dis = new DataInputStream(client.getInputStream());
		} catch (IOException e) {
			System.err.println("Error getting input stream");
			e.printStackTrace();
		}
		
		try {
			dos = new DataOutputStream(client.getOutputStream());
		} catch (IOException e) {
			System.err.println("Error getting output stream");
			e.printStackTrace();
		}
	}
	
	private int getCommand(){

		int command = -1;
		
		try{
			command = dis.readInt();
		}catch(Exception ex){
			System.err.println("Error reading command code!");
			ex.printStackTrace();
			command = -1;
		}
		
		return command;
	}
	
	private void processCommand(int command){
		
		switch(command){
		default:
			System.err.println("No Command for: " + command);
			break;
		}
		
	}
	
	private void closeConnection(){
		try {
			dis.close();
			dos.close();
			client.close();
		} catch (IOException e) {
			System.err.println("Error closing Client connection");
			e.printStackTrace();
		}
	}
	
}
