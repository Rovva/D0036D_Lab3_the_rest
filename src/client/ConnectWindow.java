package client;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import client.GUI;

public class ConnectWindow {
	JFrame connectFrame;
	//JPanel panel;
	SpringLayout connectLayout;
	Container connectContentPane;
	JButton connectButton;
	JButton cancelButton;
	
	JTextField connectAddressField;
	
	GUI mainGUI;
	
	int x_size = 405, y_size = 75;
	
	public ConnectWindow() {
		connectFrame = new JFrame("Connection window");
		connectFrame.setSize(x_size, y_size);
		connectFrame.setVisible(true);
		connectLayout = new SpringLayout();
		connectContentPane = connectFrame.getContentPane();
		connectContentPane.setLayout(connectLayout);
		connectContentPane.setBackground(Color.WHITE);
		
		this.connectAddressField = new JTextField("127.0.0.1:4444", 20);
		this.connectButton = new JButton("Join");
		this.cancelButton = new JButton("Cancel");
		
		this.connectContentPane.add(connectAddressField);
		this.connectContentPane.add(connectButton);
		this.connectContentPane.add(cancelButton);

		this.connectLayout.putConstraint(SpringLayout.NORTH, connectAddressField, 5, SpringLayout.NORTH, connectContentPane);
		this.connectLayout.putConstraint(SpringLayout.WEST, connectAddressField, 5, SpringLayout.WEST, connectContentPane);

		this.connectLayout.putConstraint(SpringLayout.NORTH, connectButton, 5, SpringLayout.NORTH, connectContentPane);
		this.connectLayout.putConstraint(SpringLayout.WEST, connectButton, 5, SpringLayout.EAST, connectAddressField);
		
		this.connectLayout.putConstraint(SpringLayout.NORTH, cancelButton, 5, SpringLayout.NORTH, connectContentPane);
		this.connectLayout.putConstraint(SpringLayout.WEST, cancelButton, 5, SpringLayout.EAST, connectButton);
	}
	
	public JButton getConnectButton() {
		return connectButton;
	}
	
	public JButton getCancelButton() {
		return cancelButton;
	}
	
	public JTextField getTextField() {
		return connectAddressField;
	}
	
	public void setVisible() {
		this.connectFrame.setVisible(true);
	}
	
	public void setInvisible() {
		this.connectFrame.setVisible(false);
	}
	
}
