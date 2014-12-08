package de.luh.hci.toa;

import static jssc.SerialPort.BAUDRATE_115200;
import static jssc.SerialPort.DATABITS_8;
import static jssc.SerialPort.PARITY_NONE;
import static jssc.SerialPort.STOPBITS_1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import de.luh.hci.toa.SensorEq.LayerFunction;

import jssc.SerialPort;

public abstract class Main {

	public static final boolean USE_COMPORT = false;

	public static final String COMPORT_IN_NAME = "COM5";
	public static final String COMPORT_OUT_NAME = "COM8";

	public static final int BAUDRATE = BAUDRATE_115200;
	public static final int DATABITS = DATABITS_8;
	public static final int STOPBITS = STOPBITS_1;
	public static final int PARITY   = PARITY_NONE;


	public static void main(String[] args) throws Exception {

		//Zunächst wird ein neues Objekt der SensorModule-Klasse erstellt
		SensorModule sensorModule = new SensorModule();

		//Hier werden alle bekannten Sensoren mit ihrer Koordinate installiert. Die Einheit beträgt dabei Meter
		//In diesem Beispiel sind die Sensoren also in einem Quadrat mit Seitenlänge 20cm angeordnet, wobei der Ursprung
		//im Zentrum dieses Quadrats liegt.
		sensorModule.addSensor(-0.11,  0.0775);
		sensorModule.addSensor( 0.11,  0.0775);
		sensorModule.addSensor( 0.11, -0.0775);
		sensorModule.addSensor(-0.11, -0.0775);
		
		
		//24 x 17.5

		//Ist eine Verbindung zum Arduino möglich, so wird diese hergestellt
		if(USE_COMPORT) {

			//Erstellt ein neues SerialPort Objekt der JSSC-Library und öffnet den Port.
			//COMPORT_IN_NAME bezeichnet dabei den String-Identifier, den der Comport trägt. In der Regel
			//hat dieser die Form: "COM<Nummer>"
			SerialPort port = new SerialPort(COMPORT_IN_NAME);
			port.openPort();
			
			//port.writeBytes("haloooll".getBytes());

			//Setzt die Parameter für die Comport-Verbindung.
			port.setParams(BAUDRATE, DATABITS, STOPBITS, PARITY);

			//Nun kann dem sensorModule der Comport als Eingabequelle gesetzt werden.
			//Die Methode setIn() erwartet als Parameter einen java.io.Inputstream. So können später ohne Probleme
			//auch andere Verbindungen, wie Bluetooth, TCP/UDP oder das Lesen aus einer Datei verwendet werden.
			//Im Falle eines Comports, kann die Adapterklasse SerialPortInputStream verwendet werden.
			sensorModule.setIn(new SerialPortInputStream(port));
			
			//Damit die Informationen verarbeitet werden können muss das SensorModule gestartet werden
			//WICHTIG: Nachdem das SensorModul gestartet wurde, sollten keine neuen Sensoren installiert werden
			sensorModule.start();
		}

		//Nun kann eine Anwendung über das TapListener-Interface auf Tap-Ereignisse reagieren
		sensorModule.addTapListener(new TapListener() {
			@Override
			public void onTap(double x, double y, double theta) {
				//x,y ist die berechnete Position im Sensor-Koordinatensystem
				//theta ist der berechnetet Winkel ausgehend vom Mittelpunkt aller Sensoren
			
				//System.out.println("x="+x+" y="+y+" theta="+theta);
			}
		});
		
		sensorModule.addTapListener(new TapTransmitter());

		//Wenn gewünscht, kann nun die GUI gestartet werden, über welche die Tap-Ereignisse visulaisiert werden kann
		createGUI(sensorModule);
		
		//blueToothTest();
	}
	
	public static Socket getConnection() throws IOException {
		
		ServerSocket s = new ServerSocket(8332);
		
		
		return s.accept();
	}

	public static void createGUI(final SensorModule sm) {
		final GUI gui = new GUI(sm);

		JFrame frame = new JFrame("Time of Arrival - Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(200, 200, 800, 600);
		frame.setContentPane(gui);


		JMenuBar menuBar = new JMenuBar();

		final JToggleButton b0 = new JToggleButton("Draw Iterations");
		b0.setSelected(true);
		b0.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gui.showIterations = b0.isSelected();
				gui.repaint();
			}
		});

		final JToggleButton b1 = new JToggleButton("Draw Field");
		b1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gui.showField = b1.isSelected();
				gui.repaint();
			}
		});

		final JToggleButton b2 = new JToggleButton("Auto Tap");
		b2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gui.autoTap = b2.isSelected();
				gui.repaint();
			}
		});

		final JButton b3 = new JButton("Reset");
		b3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gui.reset();
			}
		});
		
		final JToggleButton b4 = new JToggleButton("Calibration");
		b4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(b4.isSelected()) {
					sm.startCalibration(new PointProvider() {
						@Override
						public Point2D getPoint() {
							
							String s = JOptionPane.showInputDialog(b4, "x y");
							if(s == null) return null;
							
							String[] input = s.split(" ");
							double x = Double.parseDouble(input[0]);
							double y = Double.parseDouble(input[1]);
							
							return new Point2D.Double(x, y);
						}
						
					});
				} else {
					sm.endCalibration();
				}
			}
		});

		final JTextField fuzzyVal = new JTextField("0.01");
		fuzzyVal.setSize(fuzzyVal.getHeight(), 80);
		fuzzyVal.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SensorModule.fuzzyFactor = Double.parseDouble(fuzzyVal.getText());
				} catch(NumberFormatException ex) {
					ex.printStackTrace();
				}
			}
		});
		
		final JComboBox<SensorEq.LayerFunction> layerFunc = new JComboBox<>(LayerFunction.values());
		layerFunc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SensorEq.layerFunction = (LayerFunction)layerFunc.getSelectedItem();
				gui.repaint();
			}
		});
		

		menuBar.add(b0);
		menuBar.add(b1);
		menuBar.add(b2);
		menuBar.add(b3);
		menuBar.add(b4);
		menuBar.add(fuzzyVal);
		menuBar.add(layerFunc);
		menuBar.add(new JLabel("   Left-Click: Tap   |"));
		menuBar.add(new JLabel("   Right-Click: Add Sensor"));

		frame.setJMenuBar(menuBar);

		frame.setVisible(true);
	}
}
