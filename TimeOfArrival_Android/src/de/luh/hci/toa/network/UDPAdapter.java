package de.luh.hci.toa.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class UDPAdapter implements TapNetworkAdapter {

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
	
	@Override
	public void open(String connectionInfo) throws Exception{
		socket = new MulticastSocket(BROADCAST_PORT);
		socket.joinGroup(BROADCAST_GROUP);
	}

	@Override
	public boolean isOpen() {
		return !socket.isClosed();
	}

	@Override
	public void close() {
		socket.close();
	}

	@Override
	public void readTap(byte[] buffer) throws Exception {
		socket.receive(pack);
		System.arraycopy(pack.getData(), 0, buffer, 0, pack.getData().length);
	}
}
