package server;

import shared.GameState;
import shared.Messages;

import java.awt.Point;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Random;
import server.ServerThread;

public class Protocol {
	
	GameState serverGameState;
	int portNumber;
	ArrayList<ServerThread> threads;
	
	public Protocol(int portNumber) {
		serverGameState = new GameState(30);
		this.portNumber = portNumber;
		this.threads = new ArrayList<ServerThread>();
		startServer();
	}
	
	private void startServer() {
		boolean listening = true;
		try (ServerSocket serverSocket = new ServerSocket(this.portNumber)) { 
			int i = 0;
			// While listening is true, listen to incoming connections and
			// start a new ServerThread thread.
            while (listening) {
            	System.out.println("Listening and stuff at port: " + this.portNumber);
                threads.add(new ServerThread(serverSocket.accept(), this));
                threads.get(i).start();
                i++;
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + this.portNumber);
            System.exit(-1);
        }
	}
	
	// This method loops through all the threads stored in a ArrayList and
	// calls the method sendMessage that sends data to the connected client.
	private void sendToAll(byte[] data) throws IOException {
		for(int i = 0; i < threads.size(); i++) {
			threads.get(i).sendMessage(data);
		}
	}
	
	// This method adds a new player returns a valid ID and a random location
	// that the player will start at.
	private byte[] newPlayer() {
		int ID, x, y;
		Random randX = new Random();
		Random randY = new Random();
		x = randX.nextInt(30);
		y = randY.nextInt(30);
		ID = serverGameState.numberOfPlayers();
		serverGameState.newPlayer(ID, new Point(x, y));
		byte[] data = new byte[4];
		data[0] = (byte) Messages.JOIN.ordinal();
		data[1] = (byte) ID;
		data[2] = (byte) x;
		data[3] = (byte) y;
		return data;
	}
	
	// Checks if it is possible to move or if the space is occupied.
	private boolean canPlayerMove(int ID, int direction) {
		int originalX = 0, originalY = 0, newX = 0, newY = 0;
		
		// Go through all players and if the ID is the same as that particular
		// player, retrieve the players location and save in originalX and originalY.
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			if(serverGameState.getPlayers().get(i).getID() == ID) {
				originalX = serverGameState.getPlayers().get(i).getLocation().x;
				originalY = serverGameState.getPlayers().get(i).getLocation().y;
			}
		}
		
		newX = originalX;
		newY = originalY;
		
		// Depending on direction change coordinates for the new potential location.
		if(direction == 1) {
			newX--;
		} else if(direction == 2) {
			newY--;
		} else if(direction == 3) {
			newX++;
		} else if(direction == 4) {
			newY++;
		}
		
		// Check the position so the player cannot move outside the gameborders.
		if(newX < 0) {
			return false;
		} else if(newY < 0) {
			return false;
		}
		
		// Go through all players and check if another player already occupies the new location.
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			if(serverGameState.getPlayers().get(i).getLocation().x == newX && 
					serverGameState.getPlayers().get(i).getLocation().y == newY) {
				// Return false as the new location is already occupied.
				return false;
			}
		}
		// Return true because no player occupies the new location.
		return true;
	}
	
	// The method to move a player to another location.
	public byte[] movePlayer(int ID, int direction) {
		int originalX = 0, originalY = 0, newX = 0, newY = 0;
		
		// Retrieve the old location of the player.
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			if(serverGameState.getPlayers().get(i).getID() == ID) {
				originalX = serverGameState.getPlayers().get(i).getLocation().x;
				originalY = serverGameState.getPlayers().get(i).getLocation().y;
			}
		}
		
		newX = originalX;
		newY = originalY;
		
		// Set new location depending on what direction the player moves in.
		if(direction == 1) {
			newX = originalX - 1;
		} else if(direction == 2) {
			newY = originalY - 1;
		} else if(direction == 3) {
			newX = originalX + 1;
		} else if(direction == 4) {
			newY = originalY + 1;
		}
		
		byte[] returnValue = {(byte) ID,(byte) newX,(byte) newY};
		
		// Change the location for the player in the gamestate.
		serverGameState.getPlayers().get(ID).setLocation(new Point(newX, newY));
		
		// Return a valid byte array that will be used to send updates to all clients.
		return returnValue;
	}
	
	// This method checks who the player might be able to kill and returns what player occupies that space.
	public int canKillWho(int ID, int direction) {
		int originalX = 0, originalY = 0;
		
		// Retrieve the location of the player that want to shoot.
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			if(serverGameState.getPlayers().get(i).getID() == ID) {
				originalX = serverGameState.getPlayers().get(i).getLocation().x;
				originalY = serverGameState.getPlayers().get(i).getLocation().y;
			}
		}
		
		// Loop through all players locations to check if anyone is on the specified location
		// a player can shoot.
		if(direction == 1) {
			for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
				if(serverGameState.getPlayers().get(i).getLocation().x == (originalX-1) && 
						serverGameState.getPlayers().get(i).getLocation().y == originalY) {
					return i;
				}
			}
		} else if(direction == 2) {
			for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
				if(serverGameState.getPlayers().get(i).getLocation().x == originalX && 
						serverGameState.getPlayers().get(i).getLocation().y == (originalY-1)) {
					return i;
				}
			}
		} else if(direction == 3) {
			for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
				if(serverGameState.getPlayers().get(i).getLocation().x == (originalX+1) && 
						serverGameState.getPlayers().get(i).getLocation().y == originalY) {
					return i;
				}
			}
		} else if(direction == 4) {
			for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
				if(serverGameState.getPlayers().get(i).getLocation().x == originalX && 
						serverGameState.getPlayers().get(i).getLocation().y == (originalY+1)) {
					return i;
				}
			}
		}
		// If the player cannot kill anyone, return -1.
		return -1;
	}
	
	public boolean checkIfDead(int ID) {
		if(serverGameState.checkDead(ID)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void killPlayer(int ID) {
		serverGameState.getPlayers().get(ID).setDead();
	}
	
	public void removePlayer(int ID) {
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			if(serverGameState.getPlayers().get(i).getID() == ID) {
				serverGameState.removePlayer(i);
			}
		}
	}
	
	public boolean isEveryoneButOneDead() {
		if(serverGameState.numberOfPlayers() >= 2) {
			int deadCount = 0;
			for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
				if(serverGameState.getPlayers().get(i).isDead() == true) {
					deadCount++;
				}
			}
			if(deadCount == (serverGameState.numberOfPlayers() - 1)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	// resetGame is the method to restart the game and randomize all the player locations.
	public void resetGame() throws IOException {
		int x = 0, y = 0, ID = 0;
		boolean isUnique = false;
		
		Random rand = new Random();
		
		ArrayList<Point> placesTaken = new ArrayList<Point>();
		
		// Go through all players and randomize locations and store the taken
		// locations in the ArrayList placesTaken.
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			isUnique = true;
			x = rand.nextInt(30);
			y = rand.nextInt(30);
			for(int j = 0; j < placesTaken.size(); j++) {
				if(placesTaken.get(j).x == x && placesTaken.get(j).y == y) {
					isUnique = false;
				}
			}
			if(isUnique) {
				placesTaken.add(new Point(x, y));
			} else {
				i--;
			}
		}
		
		byte[] sendValues = new byte[4];
		// Loop through all players and assign the new location and change everyone to "living".
		for(int i = 0; i < serverGameState.numberOfPlayers(); i++) {
			ID = serverGameState.getPlayers().get(i).getID();
			serverGameState.getPlayers().get(i).setLocation(placesTaken.get(i));
			serverGameState.getPlayers().get(i).setLiving();
			sendValues[0] = (byte) Messages.RESET.ordinal();
			sendValues[1] = (byte) ID;
			sendValues[2] = (byte) placesTaken.get(i).x;
			sendValues[3] = (byte) placesTaken.get(i).y;
			sendToAll(sendValues);
		}
		
	}
	
    public void processInput(byte[] data, ServerThread thread) throws IOException {
    	
    	// First check how many is dead, if all but one dead the game resets.
    	if(isEveryoneButOneDead()) {
    		resetGame();
    		
    	// If the message is JOIN, add the new player to gamestate and announce to
        // all clients the new players id.
    	} else if(data[0] == Messages.JOIN.ordinal()) {
    		System.out.println("Adding new player...");
    		byte[] newplayer = newPlayer();
    		sendToAll(newplayer);
    		resetGame();
    	// If the message is PLAYER_INPUT then check the last byte to know if the player
    	// is moving or hitting other players.
    	} else if(data[0] == Messages.PLAYER_INPUT.ordinal()) {
    		if(data[2] <= 4) {
    			// When player is moving, check if the player can move in the received
    			// direction and then check if the player is dead. If a player is
    			// already at the direction or the moving player is dead the move is invalid and ignored.
    			if(canPlayerMove((int) data[1], (int) data[2])) {
	    			if(!checkIfDead((int) data[1])) {
	        			byte[] temp = new byte[3];
	        			byte[] movePlayer = new byte[4];
	        			temp = movePlayer((int) data[1], (int) data[2]);
	        			movePlayer[0] = (byte) Messages.PLAYER_MOVED.ordinal();
	        			movePlayer[1] = temp[0];
	        			movePlayer[2] = temp[1];
	        			movePlayer[3] = temp[2];
	        			sendToAll(movePlayer);
	    			}
    			}
    		} else {
    			int killedID = -1;
	    		byte[] killPlayer;
	    		
	    		// Loop through all the directions the player is at when the player want to hit
	    		// someone to check who is there, then check if the player who wants to hit
	    		// is dead and then kill all the players that is around.
	    		for(int i = 1; i <= 4; i++) {
	    			killedID = -1;
	    			killedID = canKillWho((int) data[1], i);
	    			killPlayer = new byte[2];
	    			System.out.println("Checking nearby players...");
	    			if(killedID != -1) {
	    				if(!checkIfDead(killedID)) {
	    					System.out.println("Killed player: " + killedID);
	        				killPlayer(killedID);
	        				killPlayer[0] = (byte) Messages.PLAYER_KILLED.ordinal();
	        				killPlayer[1] = (byte) killedID;
	        				sendToAll(killPlayer);
	    				}
	    			}
	    		}
    		}	
    	// When a client sends the LEAVE message then remove the player from gamestate and
    	// send to all clients which player has left.
    	} else if(data[0] == Messages.LEAVE.ordinal()) {
    		byte[] temp = new byte[2];
    		temp[0] = (byte) Messages.LEAVE.ordinal();
    		temp[1] = data[1];
    		sendToAll(temp);
    		removePlayer((int) data[1]);
    		thread.stopThread();
    		resetGame();
    	}
    }
    
    
}
