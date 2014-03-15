import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Garrett
 * CSE 283 Project 3
 * 
 * The ChatServer is the server that holds all the data for the users of the system. It allows
 * users to connect and download their fellow peers, thus allowing them to establish connections
 * to each other. Doesn't have a condition to end its' running.
 */
public class ChatServer {

	ServerSocket serverSocket = null;
	final static int PORT_NUMBER = 32500;
	
	private ConcurrentHashMap<String, InetSocketAddress> users = new ConcurrentHashMap<String, InetSocketAddress>();

	/**
	 * The Constructor for the Chat server
	 */
	public ChatServer(){
		createSocket();
		printServerInfo();
		serverLoop();
	}
	
	/**
	 * The server loop that runs while the program is running, listening for clients
	 */
	public void serverLoop(){
		
		while(true){//I don't think it's closeable at this point.
		
			try {
				new ChatServerClientHandler(serverSocket.accept(), this).start();
			} catch (IOException e) {
				System.err.println("Couldn't connect with client");
				e.printStackTrace();
			}
			
		
		}
		
	}
	
	/**
	 * Create the server socket that listens for connections
	 */
	private void createSocket(){
		try {
			serverSocket = new ServerSocket(32500);
		} catch (IOException e) {
			System.err.println("Error opening the Server Socket");
			e.printStackTrace();
		}
	}
	
	/**
	 * Close the ServerSocket. This may not actually be used
	 */
	private void closeSocket(){
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Error closing the Server Socket");
			e.printStackTrace();
		}
	}
	
	/**
	 * Print out the servers info to the console, launches on startup of the server
	 */
	public void printServerInfo(){
		// Display contact information.
		try {
			System.out.println( 
					"Number Server standing by to accept Clients:"			
							+ "\nIP Address: " + InetAddress.getLocalHost() 
							+ "\nPort: " + serverSocket.getLocalPort() 
							+ "\n\n" );
		} catch (UnknownHostException e) {
			System.err.println("Couldn't get local IP");
			e.printStackTrace();
		}	
	}
	
	/**
	 * Add a user to the list with said username
	 * @param ip : The ip/socket address of the user
	 * @param username : The username of the user
	 * @return : If the add was successful, will return false if the username exists.
	 */
	public synchronized boolean addUser(InetSocketAddress ip, String username){
		
		if(users.containsKey(username)){
			return false;
		}else{
			users.put(username, ip);
			return true;
		}
	}
	
	/**
	 * Remove user from the list
	 * @param ip : The ip/socket of the user
	 * @param username : The username of the user
	 */
	public synchronized void removeUser(InetSocketAddress ip, String username){
		//By using ip as well, it adds security from someone from a different InetAddress from removing someones username.
		users.remove(username, ip);
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


/**
 * 
 * @author Garrett
 * 
 * Class that runs on a separate thread from the user to handle individual requests from clients
 * that are trying to connect to the server. Connections are NOT persistent.
 */
class ChatServerClientHandler extends Thread{
	
	ChatServer chatServer = null;
	Socket client = null;
	
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	/**
	 * Constructor that takes in the socket with the client, and the ChatServer.
	 * @param s : Socket connection to the client
	 * @param cs : ChatServer object the client wanted to connect to.
	 */
	ChatServerClientHandler(Socket s, ChatServer cs){
		this.chatServer = cs;
		this.client = s;

	}
	
	/**
	 * Loop that handles the clients request to the server
	 */
	@Override
	public void run(){
		createStreams();
		processCommand(getCommand());
		closeConnection();
	}
	
	/**
	 * Create the streams (input/output) for inputting/outputting data to/from the client.
	 */
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
	
	/**
	 * Gets the command from the user.
	 * @return : Integer representing what the user wants, the command.
	 */
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
	
	/**
	 * Method that processes the command from the user, and acts appropriately
	 * @param command : The command to use (best used with getCommand())
	 */
	private void processCommand(int command){
		
		switch(command){
		case 1:
			System.out.println("Client is trying to add himself...");
			addUser();
			break;
		case 2:
			System.out.println("Client is asking for list of users...");
			getUsers();
			break;
		case 3:
			System.out.println("Client is trying to remove himself...");
			removeUser();
			break;
		default:
			System.err.println("No Command for: " + command);
			break;
		}
		
	}
	
	/**
	 * Adds a user to the ChatServers' user list.
	 */
	private void addUser(){
		
		byte ip[] = new byte[4];
		int userPort = 0;
		String userName = "";
		boolean wasSuccess = false;
		try {
			dis.read(ip);
			userPort = dis.readInt();
			userName = dis.readUTF();
			wasSuccess = true;
		} catch (IOException e) {
			System.err.println("Error adding user");
			e.printStackTrace();
		}
		
		if(wasSuccess){
			try {
				System.out.println("Adding user...");
				chatServer.addUser(new InetSocketAddress(InetAddress.getByAddress(ip), userPort), userName);
				dos.writeBoolean(wasSuccess);
			} catch (UnknownHostException e) {
				System.err.println("Error adding user");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Error sending response to client");
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Gets the users from the ChatServer and sends them to the client.
	 */
	private void getUsers(){
		System.out.println("Users gotten");
	}
	
	/**
	 * Removes the user from the ChatServer list, granted that they are in the list.
	 */
	private void removeUser(){
		System.out.println("User removed");
	}
	
	/**
	 * Closes the connection between the client and the server, and the input/output streams
	 */
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
