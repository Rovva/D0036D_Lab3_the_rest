/* GameState is responsible for keeping track of players. */

package shared;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;

public class GameState extends Observable {
		
	ArrayList<Player> players = new ArrayList<Player>();
	int PLAYER_SIZE;
	int playerID;
	
	public GameState(int PLAYER_SIZE) {
		this.PLAYER_SIZE = PLAYER_SIZE;
	}
	
	public int getPlayerID() {
		return playerID;
	}
	
	public void setPlayerID(int ID) {
		this.playerID = ID;
	}
	
	public int getPlayerSize() {
		return PLAYER_SIZE;
	}
	
	public int numberOfPlayers() {
		return this.players.size();
	}
	
	public ArrayList<Player> getPlayers() {
		return this.players;
	}
	// Add new player.
	public void newPlayer(int ID, Point location) {
		this.players.add(new Player(ID, location));
		this.revivePlayer(ID);
		
		setChanged();
        notifyObservers();
	}
	
	// Remove player.
	public void removePlayer(int ID) {
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).getID() == ID) {
				players.remove(i);
			}
		}
		setChanged();
        notifyObservers();
	}
	
	// Move the player.
	public void movePlayer(int ID, int x, int y) {
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).getID() == ID) {
				players.get(i).setLocation(new Point(x, y));
			}
		}
		setChanged();
        notifyObservers();
	}
	
	// Kill player.
	public void killPlayer(int ID) {
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).getID() == ID) {
				players.get(i).setDead();
			}
		}
		setChanged();
        notifyObservers();
	}
	
	// Revive the player.
	public void revivePlayer(int ID) {
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).getID() == ID) {
				players.get(i).setLiving();
			}
		}
		setChanged();
        notifyObservers();
	}
	
	// Check if the player is dead.
	public boolean checkDead(int ID) {
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).getID() == ID) {
				return players.get(i).isDead();
			}
		}
		return false;
	}
}
