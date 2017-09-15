package com.github.aguilasa.smartphone;

import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showOptionDialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.aguilasa.common.Chronometer;
import com.github.aguilasa.common.Processing;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ScreenApp extends JFrame {

	private static final long serialVersionUID = 6315955425937210662L;

	private JPanel moverPanel;
	protected JPanel panel;
	private JButton btnSearch;
	private JButton btnStart;

	private MulticastSocket multicastSocket = null;
	private SocketAddress socketAddress = null;
	private ReceiveReader receiveReader;
	private Thread receiveThread;
	private Sender sender;
	private final ScreenApp app;

	class ReceiveReader extends Processing {

		public ReceiveReader() {
			pause();
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
				if (json.has("type") && json.getInt("type") == 2) {
					System.out.println(json.getInt("port"));
					System.out.println(json.getString("ip"));
					//app.pause();
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
				json.put("type", 1);
				String message = json.toString();
				byte[] b = message.getBytes();
				DatagramPacket packet = new DatagramPacket(b, 0, b.length, socketAddress);
				multicastSocket.send(packet);
			} catch (IOException | JSONException e) {
				this.pause();
			}
		}
	}

	public ScreenApp() {
		this.app = this;
		initializeComponents();
		addListeners();
		setSizeLocation();
		initializeSockets();
	}

	private void setSizeLocation() {
		setSize(240, 480);
		setLocationRelativeTo(null);
	}

	private void addListeners() {
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getX() >= 90 && e.getX() <= 146 && e.getY() >= 442 && e.getY() <= 466) {
					closeApplication();
				}
			}
		});
	}

	private void initializeComponents() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setUndecorated(true);
		setVisible(true);

		setShape(new RoundRectangle2D.Float(0, 0, 240, 480, 60, 60));
		getContentPane().setLayout(null);

		moverPanel = new JPanel();
		moverPanel.setBounds(0, 0, 240, 45);
		getContentPane().add(moverPanel);

		new ComponentMover(this, moverPanel);

		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(9, 46, 222, 390);
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(5, 5));

		initializeButtons();
	}

	private void initializeButtons() {
		JPanel p = new JPanel(new GridLayout(1, 2));
		panel.add(p, BorderLayout.PAGE_START);

		btnSearch = new JButton("Procurar");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		btnStart = new JButton("Iniciar");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				start();
			}
		});

		btnSearch.setEnabled(true);
		btnStart.setEnabled(false);

		p.add(btnSearch);
		p.add(btnStart);
	}

	private void search() {
		resume();
		btnSearch.setEnabled(false);
	}

	private void resume() {
		receiveReader.resume();
		sender.resume();
	}

	private void pause() {
		receiveReader.pause();
		sender.pause();
	}

	private void start() {

	}

	private void initializeSockets() {
		socketAddress = new InetSocketAddress("228.5.6.7", 4321);

		try {
			multicastSocket = new MulticastSocket(4321);
			multicastSocket.setLoopbackMode(false);

			InetAddress localHost = InetAddress.getLocalHost();
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
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

	protected final void closeApplication() {
		closeApplication(false);
	}

	protected final void closeApplication(boolean force) {
		String buttons[] = { "Sim", "Não" };
		int result = !force ? showOptionDialog(null, "Deseja sair?", "Confirmação", DEFAULT_OPTION, WARNING_MESSAGE, null, buttons, buttons[1]) : YES_OPTION;
		if (result == YES_OPTION) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	protected void printBackground(Graphics g) {
		try {
			g.drawImage(getImage(), 0, 0, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedImage image = null;

	private Image getImage() throws IOException {
		if (image == null) {
			image = ImageIO.read(new File("images/smartphone.png"));
		}
		return image;
	}

	public void paint(Graphics g) {
		super.paint(g);
		printBackground(g);
	}
}
