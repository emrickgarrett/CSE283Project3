import java.net.ServerSocket;


public class ListenForConnection {

	
	public static void main(String[] args) throws Exception
	{		
		ServerSocket serverSocket = new ServerSocket(31200);
		
		new ChatThread( "ListenForConnection", serverSocket.accept()  );
		
	}

}
