package de.luh.hci.toa;

import java.io.IOException;
import java.io.InputStream;

import jssc.SerialPort;
import jssc.SerialPortException;

public class SerialPortInputStream extends InputStream {

	SerialPort port;

	public SerialPortInputStream(SerialPort port) {
		this.port = port;
	}

	@Override
	public int read() throws IOException {
		try {
			return port.readIntArray(1)[0];
		} catch(SerialPortException e) {
			throw new IOException(e);
		}
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		try {
			byte[] buf = port.readBytes(len);
			System.arraycopy(buf, 0, b, off, len);
			return len;
		} catch(SerialPortException e) {
			throw new IOException(e);
		}

	}

	@Override
	public int available() throws IOException {
		try {
			return port.getInputBufferBytesCount();
		} catch(SerialPortException e) {
			throw new IOException(e);
		}
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
