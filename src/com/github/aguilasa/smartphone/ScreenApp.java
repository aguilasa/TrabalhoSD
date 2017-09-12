package com.github.aguilasa.smartphone;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;

import com.github.aguilasa.common.Chronometer;
import com.github.aguilasa.common.Processing;

public class ScreenApp extends BaseScreen {

	private static final long serialVersionUID = -6145320864740849476L;

	private MulticastSocket multicastSocket = null;
	private SocketAddress socketAddress = null;
	private ReceiveReader receiveReader;
	private Thread receiveThread;
	private Sender sender;

	public ScreenApp() {
		super();

		socketAddress = new InetSocketAddress("228.5.6.7", 4321);

		try {
			multicastSocket = new MulticastSocket(4321);
			multicastSocket.setLoopbackMode(false);

			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
			multicastSocket.joinGroup(socketAddress, networkInterface);
		} catch (Exception e) {
			closeApplication(true);
		}

		receiveReader = new ReceiveReader();
		receiveThread = new Thread(receiveReader);
		receiveThread.start();
		sender = new Sender(2);
		sender.resume();
	}

	class ReceiveReader extends Processing {

		public ReceiveReader() {
			pause();
		}

		@Override
		public void doingRun() {
			System.out.println("oi");
			byte[] buf = new byte[1024];
			DatagramPacket p = null;
			try {
				p = new DatagramPacket(buf, buf.length);
				multicastSocket.receive(p);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// String line = new String(p.getData(), 0, p.getLength());
		}
	}

	class Sender extends Chronometer {

		public Sender(int seconds) {
			super(seconds);
		}

		@Override
		protected void task() {
			try {
				String buf = "";
				byte[] b = buf.getBytes();
				DatagramPacket packet = new DatagramPacket(b, 0, b.length, socketAddress);
				multicastSocket.send(packet);
			} catch (IOException e) {
				this.pause();
			}
		}
	}

}
