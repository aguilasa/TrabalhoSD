package com.github.aguilasa.server;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;

import javax.swing.JFrame;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.aguilasa.common.Chronometer;
import com.github.aguilasa.common.Processing;

public class BackupService {

	public final static String SERVER = "127.0.0.1";
	private final static int SERVICEPORT = 13267;

	private JFrame frame;
	private MulticastSocket multicastSocket = null;
	private SocketAddress socketAddress = null;
	private ReceiveReader receiveReader;
	private Thread receiveThread;
	private Sender sender;
	private boolean isBackupService = true;

	class ReceiveReader extends Processing {

		public ReceiveReader() {
			resume();
		}

		@Override
		public void doingRun() {
			byte[] buf = new byte[1024];
			DatagramPacket p = null;
			try {
				p = new DatagramPacket(buf, buf.length);
				multicastSocket.receive(p);
			} catch (IOException e) {
				e.printStackTrace();
			}

			String value = new String(p.getData(), 0, p.getLength());
			try {
				JSONObject json = new JSONObject(value);
				if (json.has("type") && json.getInt("type") == 1 && isBackupService) {
					sender.resume();
					pause();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	class Sender extends Chronometer {

		public Sender(int seconds) {
			super(seconds);
		}

		@Override
		protected void task() {
			try {
				JSONObject json = new JSONObject();
				json.put("type", 2);
				json.put("ip", SERVER);
				json.put("port", SERVICEPORT);

				String message = json.toString();
				byte[] b = message.getBytes();
				DatagramPacket packet = new DatagramPacket(b, 0, b.length, socketAddress);
				multicastSocket.send(packet);
				this.pause();
			} catch (IOException | JSONException e) {
				this.pause();
			}
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BackupService window = new BackupService();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BackupService() {
		this(true);
	}

	public BackupService(boolean isBackupService) {
		this.isBackupService = isBackupService;
		initialize();
		initializeSockets();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void initializeSockets() {
		socketAddress = new InetSocketAddress("228.5.6.7", 4321);

		try {
			multicastSocket = new MulticastSocket(4321);
			multicastSocket.setLoopbackMode(false);

			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			multicastSocket.joinGroup(socketAddress, networkInterface);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		receiveReader = new ReceiveReader();
		receiveThread = new Thread(receiveReader);
		receiveThread.start();
		sender = new Sender(2);
	}

}
