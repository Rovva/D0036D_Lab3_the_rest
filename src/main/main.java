package main;

import client.Controller;
public class main {
	
	public static void main(String[] args) {
		
		// PLAYER_SIZE is how big the circles that represent players should be.
		int PLAYER_SIZE = 30;
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {  
            public void run() {
        		Controller controller = new Controller(PLAYER_SIZE);
            }
        });
	}
}
