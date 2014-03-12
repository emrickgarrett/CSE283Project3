
import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author bachmann
 *
 * GUI interface to allow the user to pick fiends to connect to. 
 * 
 */
public class ChatPeerInterface extends JDialog
{
	/**
	 * serial number to make compiler happy
	 */
	private static final long serialVersionUID = 1L;
	
	// The components of the the GUI. 
	
	// The "quit" button
	private JButton btQuit = new JButton("Quit");
	
	// Label and List for displaying connected friends
	private JLabel friendListLabel = new JLabel("Friends:");
	// List that only allows single selection
	private List friendList = new List(10,false);

	// Hold reference to the owner of the interface
	ChatPeerInterfaceListener owner;
	
	// Static variable shared by all members of class. Used to
	// place each chat interface in a slight different location.
	final Point location = new Point( 200, 200 );
	
	Timer listUpdateTimer;
	
	FriendListUpdater friendListUpdater;
		
	/**
	 * Create a new ChatPeerInterface window.
	 * 
	 */
	public ChatPeerInterface( ChatPeerInterfaceListener owner, String screenName ) 
	{
		// Save a reference to the object that will be handling events
		// and making the network connections.
		this.owner = owner;
		
		// Create a frame with screen name in the title bar
		this.setTitle( screenName );
		
		// Create all of the panels and add them to the window.
		Container contentPane = this.getContentPane();

		// Add the list and a "quit" button.
		contentPane.add( buildListPanel(), BorderLayout.NORTH);
		contentPane.add( buildButtonPanel(), BorderLayout.SOUTH);
			
		// Set up a listener to catch closing events from the title bar
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) { 
				endChatPeer();
			}
		} );
		
		// Set the window location and size, pack the components, and display the window.
		this.setLocation( location );
		this.setSize(400, 600);
		this.pack();
		this.setVisible( true );
		
		listUpdateTimer = new Timer(); 
		
		friendListUpdater = new FriendListUpdater();
		
		listUpdateTimer.scheduleAtFixedRate(friendListUpdater, 0, 10000);

	} // end constructor
	
	
	/**
	 * Allow a friend to be added to the list at a specified index.
	 * 
	 * @param friendName
	 * @param friendIndex
	 */
	public void addFriendToList( String friendName, int friendIndex )
	{
		friendList.add(friendName, friendIndex);
		
	} // end addFriendToList
	
	/**
	 * Clears all text areas.
	 *
	 */
	public void clearList()
	{
		friendList.removeAll();
		
	} // end clearFields
	
	
	/**
	 * Called to close the GUI and have the owner of the GUI
	 * perform necessary closing operations such as closing
	 * socket connections and stopping ChatThreads.
	 */
	public void endChatPeer(){
		
		// Stop the owner
		owner.quit();
		
		// Stop the time for list updates
		listUpdateTimer.cancel();
		
		// Close and dispose of the GUI
		this.dispose();
		
	} // end endChatPeer
	
	
	/**
	 * Creates a panel that will be used for displaying a list of connected friends.
	 * 
	 * @return panel containing a label and list.
	 */
	private JPanel buildListPanel()
	{
		JPanel listPanel = new JPanel(new BorderLayout());		
		
		// Label and list to display connected friends
		listPanel.add(friendListLabel, BorderLayout.NORTH);	
		listPanel.add(friendList, BorderLayout.CENTER);
		friendList.addActionListener(new FriendListListener());
		return listPanel;
		
	} // end buildListPanel
	
	
	/**
	 * Creates a panel contain two buttons. which allow the use
	 * to clear the friend list and quit the program.
	 * 
	 * @return panel "quit" button
	 */
	private JPanel buildButtonPanel()
	{
		// Create a panel for the JButtons and add listeners to the JButtons.
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		btQuit.addActionListener(new QuitListener());
		buttonPanel.add(btQuit);
		
		return buttonPanel;
		
	} // end buildButtonPanel

	
	// Inner class to react to List selection events. 
	class FriendListListener implements ActionListener 
	{
		/**
		 * Called when a friend on the list is selected.
		 * 
		 */
		public void actionPerformed(ActionEvent e) 
		{
			owner.contactFriend(friendList.getSelectedItem(), friendList.getSelectedIndex() );

		} // end actionPerformed
		
	} // end FriendListListener class
	
	
	// Inner class to handle Quit JButton events
	class QuitListener implements ActionListener 
	{
		/**
		 * Called when the quit button is pressed.
		 * 
		 */
		public void actionPerformed(ActionEvent e) 
		{
			endChatPeer();

		} // end actionPerformed
			
	} // end QuitListener class
	
	
	// Inner class to drive periodic updates of the friend list. 
	class FriendListUpdater extends TimerTask
	{
		
		public void run()
		{
			owner.updateFriendList();
			
		} // end run
		
	} // end FriendListUpdater class
	

} // end MailInterface class

