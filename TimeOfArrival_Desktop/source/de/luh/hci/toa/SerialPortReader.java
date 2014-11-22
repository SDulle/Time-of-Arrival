package de.luh.hci.toa;

import static jssc.SerialPort.BAUDRATE_9600;
import static jssc.SerialPort.DATABITS_8;
import static jssc.SerialPort.PARITY_NONE;
import static jssc.SerialPort.STOPBITS_1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import jssc.SerialPort;
import jssc.SerialPortException;

@Deprecated
public class SerialPortReader extends Thread{
	SerialPort port;
	SensorModule module;
	boolean alive = true;
	
	public SerialPortReader(String portName, SensorModule sensorModule) {
		port = new SerialPort(portName);
		module = sensorModule;
	}
	
	private static long[] bytesToInts(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		buffer.order(ByteOrder.LITTLE_ENDIAN); //TODO: gucken was der arduino macht
		
		long[] ret = new long[bytes.length/4];
		
		for(int i=0; i<bytes.length/4; ++i) {
			ret[i] = buffer.getInt()&0x00000000ffffffffL;
		}
		
		return ret;
	}
	
	private static void normalize(long[] t) {
		long min = Long.MAX_VALUE;
		
		for(int i=0; i<t.length; ++i) {
			min = Math.min(min, t[i]);
		}
		
		for(int i=0; i<t.length; ++i) {
			t[i]-=min;
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		SerialPort port = new SerialPort("COM5");
		
		port.openPort();
		port.setParams(BAUDRATE_9600, DATABITS_8, STOPBITS_1, PARITY_NONE);
		
		while(true) {
			byte[] b = port.readBytes(4);
			System.out.println(bytesToInts(b)[0]);
		}
		
		
	}
	
	public void close() {
		alive = false;
		try {
			port.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			port.openPort();
			port.setParams(BAUDRATE_9600, DATABITS_8, STOPBITS_1, PARITY_NONE);
			
			while(alive) {
				byte[] bytes = port.readBytes(module.getSensorCount()*4);
				long[] times = bytesToInts(bytes);
				normalize(times);
				module.trigger(times);
				
				System.out.println(Arrays.toString(times));
			}
		} catch (SerialPortException e) {
			e.printStackTrace();
		} finally {
			try {
				if(port.isOpened()) port.closePort();
			} catch (SerialPortException e) {
				System.err.println("Error closing serial port");
				e.printStackTrace();
			}
		}
		
		
	}
	
}
