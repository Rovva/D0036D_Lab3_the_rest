package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import client.GUI;
import shared.GameState;
import shared.Messages;

public class Controller extends Observable {
	
	GameState gameState;
    GUI gui;
    int PLAYER_SIZE;
    
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    
    messageReader messages;
    Thread thread;
    
    public Controller(int PLAYER_SIZE) {
    	this.PLAYER_SIZE = PLAYER_SIZE;
    	gameState = new GameState(PLAYER_SIZE);
    	gui = new GUI(this, gameState);
    }
    
    // This method is used to connect to a specified ip address and port number
	void Connect(String ipPort) throws IOException { 
		String toParse = ipPort;
		String delims = "[:]";
		String[] parsed = toParse.split(delims);
        String hostName = parsed[0];
        int portNumber = Integer.parseInt(parsed[1]);
 
        try {
            socket = new Socket(hostName, portNumber);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
	}
	
	// When a player want to disconnect this method is called.
	void Disconnect() throws IOException {
		messages.runThread = false;
		this.sendLeave();
		out.close();
		in.close();
		socket.close();
	}
	
	// messageReader is a thread that is responsible for interpreting 
	// data recieved from a server.
	public class messageReader implements Runnable {
		DataInputStream in;
		GameState state;
		// runThread is used to determine if the thread should stop or not.
		volatile boolean runThread;
		
		public messageReader(DataInputStream in, GameState state) {
			this.in = in;
			this.state = state;
		}
		
		public void messageProcessor(byte[] data) {
			
			if(data[0] == Messages.JOIN.ordinal()) {
				
				int newID = data[1];
				int newX = data[2];
				int newY = data[3];
				// Add the new players data to gameState.
				this.state.newPlayer(newID, new Point(newX, newY));
		        
			} else if(data[0] == Messages.PLAYER_MOVED.ordinal()) {
				int moveID = (int) data[1];
				int moveX = (int) data[2];
				int moveY = (int) data[3];
				// First check if the player exist already
				if(checkIfExist(moveID)) {
					// The player exist so we only need to change it's location.
					this.state.movePlayer(moveID, moveX, moveY);
				} else {
					// The player did not exist so we add the player with recieved location.
					this.state.newPlayer(moveID, new Point(moveX, moveY));
				}
			} else if(data[0] == Messages.PLAYER_KILLED.ordinal()) {
				//System.out.println("Killed player ID: " + (int) data[1]);
				
				// First check if the player shot exist and change the gamestate
				// so that the player is dead and cannot move or shoot.
				int killID = (int) data[1];
				if(checkIfExist(killID)) {
					this.state.killPlayer(killID);
				}
				this.state.killPlayer(killID);
			} else if(data[0] == Messages.RESET.ordinal()) {
				int resetID = (int) data[1];
				int resetX = (int) data[2];
				int resetY = (int) data[3];
				
				// When reseting the game we need to check if all the players
				// received from the server exist in gamestate already and 
				// if the player exist, change location and revive him.
				// Otherwise just simply add the new player.
				if(!checkIfExist(resetID)) {
					this.state.newPlayer(resetID, new Point(resetX, resetY));
				} else {
					this.state.movePlayer(resetID, resetX, resetY);
					this.state.revivePlayer(resetID);
				}
			} else if(data[0] == Messages.LEAVE.ordinal()) {
				int leaveID = data[1];
				// If player exist, remove the player from gamestate.
				if(checkIfExist(leaveID)) {
					this.state.removePlayer(leaveID);
				}
			}
		}
		
		public void run() {
	        byte[] data = new byte[4];
			System.out.println("Starting messageReader...");
			this.runThread = true;
			// A loop that runs as long as runThread is true, otherwise
			// close the thread.
	        while(runThread) {
	        	if(!socket.isClosed()) {
		            try {
			            in.read(data);
			            if(data != null) {
			            	messageProcessor(data);
			            }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
	        	}
	        }
        }
	}
	
	// The method used to checks if a player with a certain ID exist
	// in the gamestate.
	public boolean checkIfExist(int ID) {
		for(int i = 0; i < gameState.numberOfPlayers(); i++) {
			if(gameState.getPlayers().get(i).getID() == ID) {
				return true;
			}
		}
		return false;
	}
	
	// This method is used when joining a server.
	public void sendJoin() throws IOException {
		// First send a message to the server.
		out.writeByte(Messages.JOIN.ordinal());
		byte[] data = new byte[4];
		// Read response and store in the byte array data.
		in.read(data);
		System.out.println("Recieved after join: " + data[0] + " " + data[1] + " " + data[2]);
		// Cast all the data into integer variables.
		int playerID = data[1];
		int playerX = data[2];
		int playerY = data[3];
		
		// Add the received ID and location to the gamestate and set own ID to the ID
		// received from server.
		gameState.newPlayer(playerID, new Point(playerX, playerY));
		gameState.setPlayerID(playerID);
		
		// Start the thread messageReader that will process all the data from server.
		messages = new messageReader(this.in, this.gameState);
		thread = new Thread(messages);
		thread.start();
	}
	
	// Method for sending input like move or hit.
	public void sendInput(int direction) throws IOException {
		byte[] data = new byte[3];
		data[0] = (byte) Messages.PLAYER_INPUT.ordinal();
		data[1] = (byte) gameState.getPlayerID();
		data[2] = (byte) direction;
		out.write(data);
	}
	
	// Method for sending info to server that oneself is leaving.
	public void sendLeave() throws IOException {
		byte[] data = new byte[2];
		data[0] = (byte) Messages.LEAVE.ordinal();
		data[1] = (byte) gameState.getPlayerID();
		out.write(data);
	}
	
}
