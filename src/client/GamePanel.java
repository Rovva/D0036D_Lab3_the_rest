package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import shared.GameState;

import java.awt.*;
import javax.swing.*;
public class GamePanel extends JPanel implements Observer {
	
	GameState gameState;
	
	public GamePanel(GameState gameState) {
		this.gameState = gameState;
		Dimension d = new Dimension(300, 300);
		this.setMinimumSize(d);
		this.setPreferredSize(d);
		this.setMaximumSize(d);
		this.setBackground(Color.WHITE);
		this.setVisible(true);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		this.repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintCircles(g);
	}
	
	// Method for painting all the players in the gamepanel.
	private void paintCircles(Graphics g) {
		// Loop through all the players.
		for(int i = 0; i < gameState.numberOfPlayers(); i++) {
			// First check if the player ID is the same as the users own ID and also
			// if the player is dead and paint the circle black.
			if(gameState.getPlayers().get(i).getID() == gameState.getPlayerID() && 
						gameState.getPlayers().get(i).isDead()) {
					g.setColor(Color.black);
			// Then check if the player ID is the same as the users own ID, paint the
			// the circle blue.
			} else if(gameState.getPlayers().get(i).getID() == gameState.getPlayerID()) {
					g.setColor(Color.blue);
			// Check if the player is dead, then paint circle black.
			} else if(gameState.getPlayers().get(i).isDead()) {
				g.setColor(Color.black);
			// Otherwise paint the circle red.
			} else {
				g.setColor(Color.red);
			}
			g.fillOval((this.getWidth()/gameState.getPlayerSize())*gameState.getPlayers().get(i).getLocation().x,
					(this.getHeight()/gameState.getPlayerSize())*gameState.getPlayers().get(i).getLocation().y,
					(this.getWidth()/gameState.getPlayerSize()),
					(this.getHeight()/gameState.getPlayerSize()));
		}
	}
}
