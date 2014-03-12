/**
 * CSA 283 Project 1: Interface to capture events from
 * ChatInterface GUI.  
 * 
 * @author Eric Bachmann 
 * @version 1.0 September 10, 2007
 */
public interface ChatPeerInterfaceListener {
		
	/**
	 * This method will be call whenever the user completes the
	 * typing of some text and is ready to send it. It is called
	 * each time the user presses the send button.
	 * 
	 * @param textMessage completed text to be sent.
	 */
	public void contactFriend( String friendName, int friendIndex );
	
	/**
	 * This method will be call whenever the user closes the
	 * GUI. The implemented quit method should close all
	 * streams, close all sockets and stop all threads that
	 * are being used to support the interface.
	 * 
	 * @param textMessage completed text to be sent.
	 */
	public void quit();
	
	/**
	 * This method will be called at regular intervals by the GUI. When called
	 * the method should contact the server and obtain and updated list of peers.
	 * To update the list in the GUI. The method should first call the clearList
	 * method of the ChatPeerInterface and then repeatedly call the addFriendToList
	 * to add all the peer that are currently in the system.
	 */
	public void updateFriendList();

	
} // end ChatInterfaceListener interface
