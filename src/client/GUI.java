package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import client.Controller;
import shared.GameState;
import client.ConnectWindow;

public class GUI implements Observer, ActionListener {
	
	Controller controller;
	GameState gameState;
	GamePanel gamePanel;
	ConnectWindow connectWindow;
	
	JFrame frame;
	JPanel panel;
	SpringLayout layout;
	Container contentPane;
	JButton connectButton;
	JButton disconnectButton;
	
	int x_size = 500, y_size = 500;
	
	public GUI(Controller controller, GameState gameState) {
		this.controller = controller;
		this.gameState = gameState;
		frame = new JFrame("Arena of death");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(x_size, y_size);
		frame.setVisible(true);
		layout = new SpringLayout();
		contentPane = frame.getContentPane();
		contentPane.setLayout(layout);
		contentPane.setBackground(Color.WHITE);

		this.connectButton = new JButton("Connect");
		this.disconnectButton = new JButton("Disconnect");
		
		this.contentPane.add(connectButton);
		this.contentPane.add(disconnectButton);
		
		connectButton.addActionListener(this);
		disconnectButton.addActionListener(this);

		connectButton.setEnabled(true);
		disconnectButton.setEnabled(false);
		
		this.layout.putConstraint(SpringLayout.NORTH, connectButton, 5, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.WEST, connectButton, 5, SpringLayout.WEST, contentPane);
		
		this.layout.putConstraint(SpringLayout.NORTH, disconnectButton, 5, SpringLayout.NORTH, contentPane);
		this.layout.putConstraint(SpringLayout.EAST, disconnectButton, -5, SpringLayout.EAST, contentPane);
		
		
		this.gamePanel = new GamePanel(gameState);
		this.contentPane.add(gamePanel);
		this.gamePanel.setFocusable(true);
		this.gamePanel.setVisible(false);

        gameState.addObserver(gamePanel);
        gameState.addObserver(this);
        
		this.layout.putConstraint(SpringLayout.NORTH, gamePanel, 5, SpringLayout.SOUTH, connectButton);
		
		connectWindow = new ConnectWindow();
		connectWindow.setInvisible();
		connectWindow.getConnectButton().addActionListener(this);
		connectWindow.getCancelButton().addActionListener(this);
	}
	
	public void addMoveListener(KeyListener movelistener) {
		gamePanel.grabFocus();
		gamePanel.addKeyListener(movelistener);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String op = arg0.getActionCommand();
		// User clicks on Connect button, show the connection window.
		if(op == "Connect") {
			connectWindow.setVisible();
		// If user clicks on Disconnect button, disconnect from server, set
		// Connect buttton clickable, make Disconnect button unclickable and
		// set the gamepanel as invisible.
		} else if(op == "Disconnect") {
			try {
				controller.Disconnect();
				connectButton.setEnabled(true);
				disconnectButton.setEnabled(false);
				gamePanel.setVisible(false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// When user clicks on Join button, connect to the ip address and port
		// specified, send join request to server, disable Connect button and enable
		// Disconnect button, make the gamepanel visible and make Connection window invisible.
		} else if(op == "Join") {
			String addressField = connectWindow.getTextField().getText();
			try {
				controller.Connect(addressField);
				controller.sendJoin();
				connectButton.setEnabled(false);
				disconnectButton.setEnabled(true);
				gamePanel.setVisible(true);
				connectWindow.setInvisible();
				initKeys();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// When user clicks on the Cancel button in the Connection window, make the window invisible.
		} else if(op == "Cancel") {
			connectWindow.setInvisible();
		}
	}
    private void initKeys() {
    	this.addMoveListener(
    			new KeyListener() {
    				@Override
    				public void keyPressed(KeyEvent ke) {
    			        int keyCode = ke.getKeyCode();
    			        int direction = 0;
    			        
    			        // Check if any of the pressed keys are valid.
    			        if(keyCode == KeyEvent.VK_LEFT) {
    			        	System.out.println("Left");
    			        	//gameState.movePlayer(1);
    			        	direction = 1;
    			        } else if(keyCode == KeyEvent.VK_UP) {
    			        	System.out.println("UP");
    			        	//gameState.movePlayer(2);
    			        	direction = 2;
    			        } else if(keyCode == KeyEvent.VK_RIGHT) {
    			        	System.out.println("Right");
    			        	//gameState.movePlayer(3);
    			        	direction = 3;
    			        } else if(keyCode == KeyEvent.VK_DOWN) {
    			        	System.out.println("Down");
    			        	//gameState.movePlayer(4);
    			        	direction = 4;
    			        } else if(keyCode == KeyEvent.VK_SPACE) {
    			        	System.out.println("Hit");
    			        	direction = 5;
    			        }
			        	try {
			        		
			        		controller.sendInput(direction);
			        		
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
    			      }

					@Override
					public void keyReleased(KeyEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void keyTyped(KeyEvent arg0) {
						// TODO Auto-generated method stub
						
					}
    			});
    }
    
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		frame.repaint();
	}
}
