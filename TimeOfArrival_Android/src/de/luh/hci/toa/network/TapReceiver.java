package de.luh.hci.toa.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TapReceiver extends Thread {
	
	private boolean running = true;
	private TapNetworkAdapter networkAdapter;
	private List<TapListener> listeners = new ArrayList<TapListener>();
	
	
	
	public void addTapListener(TapListener listener) {
		listeners.add(listener);
	}
	
	public void removeTapListener(TapListener listener) {
		listeners.remove(listener);
	}
	
	public void setNetworkAdapter(TapNetworkAdapter newNetworkadapter) {
		if(networkAdapter != null) {
			try {
				networkAdapter.close();
			} catch(Exception e) {}
		}
		
		networkAdapter = newNetworkadapter;
	}
	
	public TapNetworkAdapter getNetworkAdapter() {
		return networkAdapter;
	}
	
	public void end() {
		running = false;
		
		if(networkAdapter != null) {
			try {
				networkAdapter.close();
			} catch(Exception e) {}
		}
	}
	
	@Override
	public void run() {
		byte[] data = new byte[3*8];
		
		while(running) {
			try {
				
				System.out.println("waiting for tap...");
				
				try {
					if(networkAdapter != null) networkAdapter.readTap(data);
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				dispatch(data);
			} catch (Exception e) {
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
