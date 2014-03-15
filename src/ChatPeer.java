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


public class ChatPeer implements ChatPeerInterfaceListener{

	ChatPeerInterface face;
	ServerSocket serverSocket = null;
	Socket server = null;
	ArrayList<ChatThread> clientConnections = new ArrayList<ChatThread>();
	InetAddress chatServer = null;
	
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	int PORT_NUMBER;
	
	ChatPeer(){
	
		try {
			chatServer = InetAddress.getByName(JOptionPane.showInputDialog("Enter IP of server: "));
		} catch (HeadlessException e) {
			System.err.println("Error connecting to Server");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			System.err.println("Error connecting to Server");
			e.printStackTrace();
		}
		
		createStreams();
		
		boolean invalidName = true;
		
		while(invalidName){
			String name = JOptionPane.showInputDialog("Enter Screen Name: ");
			
			//Name shoudn't be null, and should be greater than nothing
			if(name != null && name.length() > 0){
				
				
				if(addUser(name)){
					face = new ChatPeerInterface(this, name);
					invalidName = false;
				}
			}
			
		}
		
		
		
	}
	
	private void createStreams(){
		
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
	
	
	private boolean addUser(String name){
		
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
	

	@Override
	public void contactFriend(String friendName, int friendIndex) {
		System.out.println("Contact " + friendName + " at Index " + friendIndex);
	}




	@Override
	public void quit() {
		System.out.println("Quitting app, closing connection with server...");
		System.exit(0);
	}




	@Override
	public void updateFriendList() {
		System.out.println("Contacting server to update friends list...");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ChatPeer();
	}




}
