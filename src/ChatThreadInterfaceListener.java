/**
 * CSA 283 Project 1: Interface to capture events from
 * ChatInterface GUI.  
 * 
 * @author Eric Bachmann 
 * @version 1.0 September 10, 2007
 */
public interface ChatThreadInterfaceListener {
		
	/**
	 * This method will be call whenever the user completes the
	 * typing of some text and is ready to send it. It is called
	 * each time the user presses the send button.
	 * 
	 * @param textMessage completed text to be sent.
	 */
	public void sendMessage( String textMessage);
	
	/**
	 * This method will be call whenever the user closes the
	 * GUI. The implemented quit method should close all
	 * streams, close all sockets and stop all threads that
	 * are being used to support the interface.
	 * 
	 * @param textMessage completed text to be sent.
	 */
	public void quit();

	
} // end ChatInterfaceListener interface
