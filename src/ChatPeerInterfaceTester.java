import javax.swing.JOptionPane;

// Dummy class for testing the GUI events. 
public class ChatPeerInterfaceTester implements ChatPeerInterfaceListener
{
	ChatPeerInterface face;
	
	public ChatPeerInterfaceTester() {
		
		// Ask the user for a screen name.
		String name = JOptionPane.showInputDialog("Enter Screen Name: ");

		// Did the user enter a screen name?
		if( name != null ) {
			
			// Create the GUI interface
			face = new ChatPeerInterface(this, name );
	
			for(int i = 0; i < 5; i++){
				
				// How friends can be added to the list display
				face.addFriendToList("friend"+i, i);
			}
		}
		
	}
	
	
	public void contactFriend( String friendName, int friendIndex )
	{
		System.out.println("Contact " + friendName + " at index " + friendIndex  );
	
	} // end contactFriend
	
	
	public void quit()
	{
		System.out.println("Unregister with the server, close connections and clean up " +
						   "while the GUI disposes itself." );
		
	} // end quit
	
	public void updateFriendList()
	{
		System.out.println("Time to contact the server and update the list of friends." );
		
	} // end updateFriendList
	
	// main for testing purposes only.
	public static void main(String[] args) 
	{
		new ChatPeerInterfaceTester();

	} // end main
	
} // end ChatPeerInterfaceTester class