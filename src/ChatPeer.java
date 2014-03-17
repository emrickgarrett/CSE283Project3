import java.awt.HeadlessException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * 
 * @author Garrett
 * CSE 283 Project 3
 * 
 * Class that is responsible for the Peer side of the project. Deals with connecting to the
 * server, sending requests to the server, and creating connections to another user to chat!
 */
public class ChatPeer implements ChatPeerInterfaceListener{

	ChatPeerInterface face;
	ServerSocket serverSocket = null;
	Socket server = null;
	ArrayList<ChatThread> clientConnections = new ArrayList<ChatThread>();
	InetAddress chatServer = null;
	
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	int PORT_NUMBER;
	
	private boolean listening = true;
	private String name = "default";
	
	/**
	 * Constructor for the ChatPeer
	 */
	ChatPeer(){
	
		createServerSocket();
		
		try {
			chatServer = InetAddress.getByName(JOptionPane.showInputDialog("Enter IP of server: "));
		} catch (HeadlessException e) {
			System.err.println("Error connecting to Server");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			System.err.println("Error connecting to Server");
			e.printStackTrace();
		}
		
		boolean invalidName = true;
		
		while(invalidName){
			String name = JOptionPane.showInputDialog("Enter Screen Name: ");
			
			//Name shoudn't be null, and should be greater than nothing
			if(name != null && name.length() > 0){
				
				
				if(addUser(name)){
					face = new ChatPeerInterface(this, name);
					invalidName = false;
					this.name = name;
				}
			}
			
		}
		
		listenLoop();
		
	}
	
	/**
	 * Loop that listens for requests for chat from other users!
	 */
	private void listenLoop(){
		
		while(listening){
			try{
				Socket s  = serverSocket.accept();
			
				clientConnections.add(new ChatThread(name, s));
			}catch(Exception ex){
				System.err.println("Error receiving connection");
				ex.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Loop that creates streams for input/output
	 */
	private void createStreams(){

		try {
			server = new Socket(chatServer, ChatServer.PORT_NUMBER);
		} catch (IOException e) {
			System.err.println("Error connecting to server");
			e.printStackTrace();
		}
		
		try{
			dis = new DataInputStream(server.getInputStream());
			dos = new DataOutputStream(server.getOutputStream());
		}catch(Exception ex){
			System.err.println("Error creating streams");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Creates the server socket. We only want this created once, so it's separate
	 * from the rest of the streams that connect and disconnect from the server.
	 */
	public void createServerSocket(){
		boolean needsPort = true;
		int port = 32500; //Default port
		
		while(needsPort){
			try {
				serverSocket = new ServerSocket(port);
				PORT_NUMBER = port;
				needsPort = false;
			} catch (IOException e){
				//e.printStackTrace();
				port++;
			}
		}
	}
	
	/**
	 * Close the streams that are consistently closed (NOT the server socket!)
	 */
	private void closeStreams(){
		try{
			server.close();
			dis.close();
			dos.close();
		}catch(Exception ex){
			System.err.println("Closing Streams error");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Adds the user to the server list
	 * @param name : The username
	 * @return : If the add was successful, false usually means name was taken.
	 */
	private boolean addUser(String name){
		//Need to create/close streams every server call
		createStreams();
		
		try{
			int tries = 0;
			boolean wasSuccess = false;
			
			while(tries < 3 && !wasSuccess){
				System.out.println("Adding ourselves to server...");
				dos.writeInt(1);
				byte ip[] = InetAddress.getLocalHost().getAddress();
				dos.write(ip);
				dos.writeInt(PORT_NUMBER);
				dos.writeUTF(name);
				wasSuccess = dis.readBoolean();
				tries++;
			}
			closeStreams();
			
			if(!wasSuccess){
				System.err.println("Error creating user on the server, try again with new name");
				return false;
			}
			
		}catch(Exception ex){
			System.err.println("Error adding user");
			return false;
		}
		
		return true;
	}
	
	
	
	
	
	
	
	/*******Overrided Methods Below*********/
	

	/**
	 * Contacts a friend on the list, creating a chat dialogue with them!
	 */
	@Override
	public void contactFriend(String friendName, int friendIndex) {
		//Update to reduce chances of picking someone not active anymore...
		updateFriendList();
		
		createStreams();
		
		try{
			
			dos.writeInt(4);
			dos.writeUTF(friendName);
			
			//If user is still connected to server
			if(dis.readBoolean()){
				
				byte ip[] = new byte[4];
				dis.read(ip);
				
				int port = dis.readInt();
				
				clientConnections.add(new ChatThread(name, new Socket(InetAddress.getByAddress(ip), port)));
			}else{
				System.err.println("Your friend is no longer on the server!");
			}
			
		}catch(Exception ex){
			System.err.println("Error connecting to your friend");
			ex.printStackTrace();
		}
		
		System.out.println("Contact " + friendName + " at Index " + friendIndex);
		closeStreams();
	}




	/**
	 * Quit the application, letting the server know we are quitting
	 */
	@Override
	public void quit() {
		createStreams();
		System.out.println("Quitting app, closing connection with server...");
		listening = false;
		
		//Start closing the connection, let server know we are bailing out.
		try{
			dos.writeInt(3);
			
			dos.writeUTF(name);
			byte ip[] = InetAddress.getLocalHost().getAddress();
			dos.write(ip);
			
			dos.writeInt(PORT_NUMBER);
			
			if(dis.readBoolean()){
				System.out.println("Connection closed Successfully...");
			}else{
				System.err.println("Error closing Connection...");
			}
			
			
		}catch(Exception ex){
			System.err.println("Error closing the connection, server will not remove your name.");
			ex.printStackTrace();
		}
		
		closeStreams();
		
		//Close the server socket
		try{
			serverSocket.close();
			
		}catch(Exception ex){
			System.err.println("Error closing the server socket!");
			ex.printStackTrace();
		}
		System.exit(0);
	}




	/**
	 * Ask the server to update our friends list
	 * 
	 * For efficiency, I'm doing checking to remove the user from his own friendslist on
	 * the client side. This is so we don't have to send a UTF, when we don't need to.
	 */
	@Override
	public void updateFriendList() {
		createStreams();
		try{
			dos.writeInt(2);
			
			int length = dis.readInt();
			
			face.clearList();
			int friendLength = 0;
			
			for(int i = 0; i < length; i++){
				String uname = dis.readUTF();
				
				if(!uname.equals(name)){
					face.addFriendToList(uname, friendLength++);
				}
			}
			
			if(dis.readInt() != 0){
				System.err.println("Error reading in friends, friends list may be short...");
			}
			
		}catch(Exception ex){
			System.err.println("Error updating Friends list...");
			ex.printStackTrace();
		}
		
		System.out.println("Contacting server to update friends list...");
		closeStreams();
	}
	
	/**
	 * Main Method to create an object
	 */
	public static void main(String[] args) {
		new ChatPeer();
	}




}
