package de.luh.hci.toa;

import java.io.IOException;
import java.io.OutputStream;

import jssc.SerialPort;
import jssc.SerialPortException;

public class SerialPortOutputStream extends OutputStream {

	SerialPort port;
	
	public SerialPortOutputStream(SerialPort port) {
		this.port = port;
	}

	@Override
	public void write(int b) throws IOException {
		try {
			port.writeInt(b);
		} catch(SerialPortException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		try {
			port.writeBytes(b);
		} catch(SerialPortException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		byte[] buf = new byte[len];
		System.arraycopy(b, off, buf, 0, len);
		write(buf);
	}

	@Override
	public void close() throws IOException {
		try {
			port.closePort();
		} catch(SerialPortException e) {
			throw new IOException(e);
		}
	}
}
