package de.luh.hci.toa;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class TapTransmitter implements TapListener {

	/*
	DataOutputStream out;
	
	public TapTransmitter(SerialPort port) {
		
		try {
			port.openPort();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
		
		out = new DataOutputStream(new SerialPortOutputStream(port));
	}
	
	public TapTransmitter(Socket socket) {
		
		try {
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	*/
	public static InetAddress BROADCAST_GROUP;
	public static int BROADCAST_PORT = 22455;
	
	static {
		try {
			BROADCAST_GROUP = InetAddress.getByName("228.5.6.7");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private MulticastSocket socket;
	
	
	public TapTransmitter() throws IOException {
		socket = new MulticastSocket(BROADCAST_PORT);
		socket.joinGroup(BROADCAST_GROUP);
	}
	
	@Override
	public void onTap(double x, double y, double theta) {
		ByteBuffer buffer = ByteBuffer.allocate(3*8);
		buffer.asDoubleBuffer().put(x).put(y).put(theta);
		
		DatagramPacket data = new DatagramPacket(buffer.array(), 0, 3*8, BROADCAST_GROUP, BROADCAST_PORT);
		
		try {
			socket.send(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
