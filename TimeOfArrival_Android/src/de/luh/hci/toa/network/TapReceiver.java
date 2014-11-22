package de.luh.hci.toa.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TapReceiver extends Thread {

	public static InetAddress BROADCAST_GROUP;
	public static int BROADCAST_PORT = 22455;
	static {
		try {
			BROADCAST_GROUP = InetAddress.getByName("228.5.6.7");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private DatagramPacket pack = new DatagramPacket(new byte[3*8], 0, 3*8, BROADCAST_GROUP, BROADCAST_PORT);
	private MulticastSocket socket;
	private List<TapListener> listeners;
	
	public TapReceiver() throws IOException {
		socket = new MulticastSocket(BROADCAST_PORT);
		socket.joinGroup(BROADCAST_GROUP);
		
		listeners = new ArrayList<TapListener>();
	}
	
	public void end() {
		socket.close();
	}
	
	public void addTapListener(TapListener listener) {
		listeners.add(listener);
	}
	
	public void removeTapListener(TapListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void run() {
		
		while(!socket.isClosed()) {
			try {
				System.out.println("waiting for package at "+BROADCAST_GROUP+":"+BROADCAST_PORT);
				socket.receive(pack);
				dispatch(pack.getData());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private void dispatch(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		
		double x = buffer.getDouble();
		double y = buffer.getDouble();
		double theta = buffer.getDouble();
		
		for(TapListener t : listeners) {
			t.onTap(x, y, theta);
		}
	}
}
