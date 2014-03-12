import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author bachmann
 *
 * GUI interface to display communication between the ChatThreads. 
 * 
 */
public class ChatThreadGUI extends JDialog
{

	/**
	 * serial number to make compiler happy
	 */
	private static final long serialVersionUID = 1L;
	
	
	// The components of the the GUI. 
	private JButton btSend = new JButton("Send");
	private JButton btClear = new JButton("Clear");
	private JButton btQuit = new JButton("Quit");
	
	
	// Label and textareas in which text to be sent is entered
	private JLabel outLabel = new JLabel("Out:");
	private TextArea outText = new TextArea(10, 40);
	
	// Label and textarea in which received text is diplayed
	private JLabel inLabel = new JLabel("In:");
	private TextArea inText = new TextArea(10, 40);
	
	// Hold reference to the owner of the interface
	ChatThreadInterfaceListener owner;

	// Random number generator for randomly placing the GUIs
	static Random  rand = new Random();
	
	// Static variable shared by all members of class. Used to
	// place each chat interface in a slight different location.
	static Point locationTracker = 
		new Point( rand.nextInt( 800), rand.nextInt( 800 ) );

	
	/**
	 * Create a new ChatInterface window.
	 * 
	 */
	public ChatThreadGUI( String screenName ) {
		
		// Create a frame with screen name in the title bar
		this.setTitle( screenName);
		
		// Create all of the panels and add them to the window.
		Container contentPane = this.getContentPane();
		contentPane.add( buildOutPanel(), BorderLayout.NORTH);
		contentPane.add( buildInPanel(), BorderLayout.CENTER);
		contentPane.add( buildButtonPanel(), BorderLayout.SOUTH);
			
		// Set up a listener to catch closing events from the
		// title bar
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) { 
				endChatSession();
	
			}
		} );
		

		// Set the window size, pack the components, and display the window.
		this.setLocation( locationTracker );
		this.setSize(400, 600);
		this.pack();
		this.setVisible( true );
		

	} // end constructor
	
	
	/**
	 * Creates a panel that will be used for entering outgoing text.
	 * 
	 * @return panel containing a textarea for the outgoing text.
	 */
	private JPanel buildOutPanel()
	{
		JPanel outPanel = new JPanel(new BorderLayout());		
		
		// Text area for the text
		outPanel.add(outLabel, BorderLayout.NORTH);	
		outPanel.add(outText, BorderLayout.CENTER);
		
		return outPanel;
		
	} // end buildOutPanel
	
	
	/**
	 * Creates a panel that will be used for displaying incoming text.
	 * 
	 * @return panel containing a textarea for incoming text
	 */
	private JPanel buildInPanel()
	{
		JPanel inPanel = new JPanel(new BorderLayout());
		
		// Disable editing for the text area
		inText.setEditable(false);
		
		// Text area for the message
		inPanel.add(inLabel, BorderLayout.NORTH);	
		inPanel.add(inText, BorderLayout.CENTER);
		
		return inPanel;
		
	} // end buildMessagePanel
	
	
	/**
	 * Creates a panel contain three buttons. Buttons allow the user
	 * to clear the text fields, send a message or quit the program.
	 * 
	 * @return panel containing "clear," "send," and "quit" buttons
	 */
	private JPanel buildButtonPanel()
	{
		// Create a panel for the JButtons and add listeners to the JButtons.
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		btSend.addActionListener(new SendListener());
		btClear.addActionListener(new ClearListener());
		btQuit.addActionListener(new QuitListener());
		buttonPanel.add(btSend);
		buttonPanel.add(btClear);
		buttonPanel.add(btQuit);
		
		return buttonPanel;
		
	} // end buildButtonPanel

	
	/**
	 * Clears all text areas.
	 *
	 */
	private void clearFields()
	{
		outText.setText("");
		inText.setText("");
		
	} // end clearFields
	
	
	/**
	 * Adds a new incoming message to the incoming text area.
	 * 
	 * @param message
	 */
	public void appendToIn(String message)
	{
		inText.append(message + "\n");
		
	} // end appendToIn
	
	
	/**
	 * Called to close the GUI and have the owner of the GUI
	 * perform necessary closing operations such as closing
	 * socket connections and stopping threads.
	 */
	public void endChatSession(){
		
		// Stop the thread of the owner ChatThread object
		owner.quit();
		
		// Close and dispose of the GUI
		this.dispose();
		
	} // end endChatSession
	

	// Inner class Handler for the Send-JButton event
	class SendListener implements ActionListener 
	{	
		
		/**
		 * Called when the send button is pressed. 
		 * Passes text to be sent to the owner.
		 */
		public void actionPerformed(ActionEvent event) 
		{
			// Give outgoing text to the owning ChatThread object
			owner.sendMessage( outText.getText());
			
			// Clear the outgoing text area
			outText.setText("");

		} // end actionPerformed
	
	
	} // end SendListener class


	// Inner class to Clear the fields on the GUI when clear JButton is pressed. 
	class ClearListener implements ActionListener 
	{
		/**
		 * Called when the clear button is pressed.
		 */
		public void actionPerformed(ActionEvent e) 
		{
			clearFields();
		
		} // end actionPerformed
	
		
	} // end ClearListener class

	
	// Inner class to handle Quit JButton events
	class QuitListener implements ActionListener 
	{
		/**
		 * Called when the quit button is pressed.
		 * 
		 */
		public void actionPerformed(ActionEvent e) 
		{
			endChatSession();

		} // end actionPerformed
			
	} // end QuitListener class
	
	
	/**
	 * Registers a listener for the interface. The listening
	 * object must implement the ChatInterfaceListener interface.
	 * 
	 * @param owner reference for the object that will handle events 
	 * from the tic tac toe board
	 */
	public void addChatThreadGUIListener(  ChatThreadInterfaceListener owner )
	{
		this.owner = owner;
		
	} // end addChatGUIListener
	
	
	// main for testing purposes only.
	public static void main(String[] args) 
	{
		ChatThreadGUI face = new ChatThreadGUI("unknown");
		
		GUITester a = new GUITester();
		
		face.addChatThreadGUIListener(a);
		
		// Test setting the title
		face.setTitle("Another Unknown");
		
		face.appendToIn("Sample message text.");
		
	} // end main

	
} // end MailInterface class


// Dummy class for testing the GUI events
class GUITester implements ChatThreadInterfaceListener
{
	
	public void sendMessage( String textMessage)
	{
		System.out.println("Received to Send: " + textMessage );
	
	} // end sendMessage
	
	
	public void quit()
	{
		System.out.println("Closing connections and cleaning up " +
						   "before the qui disposes itself." );
		
	} // quit
	
} // end GUITester class

