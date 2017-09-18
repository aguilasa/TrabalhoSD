package com.github.aguilasa.common;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressBarManager {
	private JProgressBar progressBar = null;

	public ProgressBarManager(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public void setValues(int minimum, int maximum) {
		if (progressBar != null) {
			progressBar.setMinimum(minimum);
			progressBar.setMaximum(maximum);
		}
	}

	public void update(int percent) {
		if (progressBar != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					progressBar.setValue(percent);
				}
			});
		}
	}

}
