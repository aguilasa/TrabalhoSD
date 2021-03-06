package com.github.aguilasa.server;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.aguilasa.common.Chronometer;
import com.github.aguilasa.common.Processing;
import com.github.aguilasa.common.SocketCommon;

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
	private Backup backup;
	private Thread backupThred;
	private JProgressBar progressBar = new JProgressBar();;

	class Backup extends Processing {

		@Override
		public void doingRun() {
			try (ServerSocket serverSocket = new ServerSocket(SERVICEPORT);) {
				while (true) {
					System.out.println("Waiting...");
					try (Socket socket = serverSocket.accept();) {
						System.out.println("Accepted connection : " + socket);

						SocketCommon.receiveMultipleFiles("received", socket, progressBar);
						System.out.println("Done.");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

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
				String message = createResponse();
				byte[] b = message.getBytes();
				DatagramPacket packet = new DatagramPacket(b, 0, b.length, socketAddress);
				multicastSocket.send(packet);
			} catch (IOException | JSONException e) {
				this.pause();
			}
		}

		private String createResponse() throws JSONException {
			JSONObject json = new JSONObject();
			json.put("type", 2);
			json.put("ip", SERVER);
			json.put("port", SERVICEPORT);

			String message = json.toString();
			return message;
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
		addProgressBar();
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

		backup = new Backup();
		backupThred = new Thread(backup);
		backupThred.start();
		backup.resume();
	}

	private void addProgressBar() {
		frame.getContentPane().add(progressBar, BorderLayout.PAGE_END);
		frame.repaint();
	}

	private void removeProgressBar() {
		frame.getContentPane().remove(progressBar);
		frame.repaint();
	}

}
