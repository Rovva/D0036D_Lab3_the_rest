package server;

import java.net.*;

import shared.Messages;

import java.io.*;

 
public class ServerThread extends Thread {
    private Socket socket = null;
    private Protocol proto;
    private DatagramSocket udpSocket;
    private int udpPort = 0;
    private int playerID = 0;
    private DataOutputStream out;
    
    private boolean runThread;
 
    public ServerThread(Socket socket, DatagramSocket udpSocket, Protocol proto) {
        super("ServerThread");
        this.socket = socket;
        this.udpSocket = udpSocket;
        this.udpPort = udpSocket.getLocalPort();
        System.out.println("UDP port is: " + this.udpPort);
        this.proto = proto;
        this.runThread = true;
    }
    
    // Method to send data to the client.
    public void sendMessage(byte[] data) throws IOException {
    	if(runThread) {
        	out.write(data);
        	out.flush();
    	}
    }
    
    public void sendUDPMessage(byte[] data) {
    	if(runThread) {
    		new DatagramPacket(data, 4, socket.getInetAddress(), udpPort);
    	}
    }
    
    public void sendJoinMessage(int id) throws IOException {
    	if(runThread) {
    		byte[] data = new byte[4];
    		data[0] = (byte) Messages.JOIN.ordinal();
    		data[1] = (byte) id;
    		
    		byte[] temp = new byte[2];
    		
    		temp[0] = (byte) (this.udpPort & 0xFF);
    		temp[1] = (byte) (((this.udpPort) >> 8) & 0xFF);
    		
    		data[2] = temp[0];
    		data[3] = temp[1];
    		out.write(data);
    		out.flush();
    	}
    }
    
    // This method is invoked when the thread must close.
    public void stopThread() {
    	this.runThread = false;
    }
     
    public void run() {
			try {
			out = new DataOutputStream(socket.getOutputStream());
	        DataInputStream in = new DataInputStream(socket.getInputStream());
            while(runThread) {
            	byte[] data = new byte[4];
            	in.read(data);
                if(data != null) {
                    proto.processInput(data, this);
                }
            }
            System.out.println("ServerThread is stopping...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
}