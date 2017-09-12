package com.github.aguilasa.common;

public abstract class Processing implements Runnable {

	private volatile boolean running = true;
	private volatile boolean paused = true;
	private final Object pauseLock = new Object();

	public abstract void doingRun();

	@Override
	public void run() {
		while (running) {
			synchronized (pauseLock) {
				if (!running) {
					break;
				}
				if (paused) {
					try {
						pauseLock.wait();
					} catch (InterruptedException ex) {
						break;
					}
					if (!running) {
						break;
					}
				}
			}
			doingRun();
		}
	}

	public final void stop() {
		running = false;
		resume();
	}

	public final void pause() {
		paused = true;
	}

	public final void resume() {
		synchronized (pauseLock) {
			paused = false;
			pauseLock.notifyAll();
		}
	}

}
