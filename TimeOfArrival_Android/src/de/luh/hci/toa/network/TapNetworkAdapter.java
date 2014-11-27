package de.luh.hci.toa.network;

public interface TapNetworkAdapter {
	public void open(String connectionInfo) throws Exception;
	
	public boolean isOpen();
	
	public void close() throws Exception;
	
	public void readTap(byte[] buffer) throws Exception;
}
