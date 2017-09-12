package com.github.aguilasa.smartphone;

import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showOptionDialog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BaseScreen extends JFrame {

	private static final long serialVersionUID = 6315955425937210662L;

	private JPanel moverPanel;
	protected JPanel panel;

	public BaseScreen() {
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

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getX() >= 90 && e.getX() <= 146 && e.getY() >= 442 && e.getY() <= 466) {
					closeApplication();
				}
			}

		});

		setSize(240, 480);
		setLocationRelativeTo(null);
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

	public final void paint(Graphics g) {
		super.paint(g);
		printBackground(g);
	}

	private void printBackground(Graphics g) {
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
}
