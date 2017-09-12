package com.github.aguilasa.common;

import java.util.Timer;
import java.util.TimerTask;

public abstract class Chronometer {
	private Timer timer;
	private int period;

	public Chronometer(int seconds) {
		period = seconds * 1000;
	}

	private void newTimer() {
		timer = new Timer();
		timer.schedule(new ChronometerTask(), 0, period);
	}

	protected abstract void task();

	private class ChronometerTask extends TimerTask {
		public void run() {
			task();
		}
	}

	public void pause() {
		timer.cancel();
	}

	public void resume() {
		newTimer();
	}

}
