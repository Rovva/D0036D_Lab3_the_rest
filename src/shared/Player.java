package shared;

import java.awt.Point;

public class Player {
	
	int id;
	boolean dead;
	Point location;
	
	Player(int id, Point location) {
		this.id = id;
		this.location = location;
	}
	
	public int getID() {
		return id;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public void setLocation(Point newLocation) {
		this.location = newLocation;
	}
	
	public void setDead() {
		this.dead = true;
	}
	
	public void setLiving() {
		this.dead = false;
	}
	
	public boolean isDead() {
		return this.dead;
	}
}
