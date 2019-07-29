package app;

import java.io.IOException;
import java.util.ArrayList;

import app.interfaces.Pausable;

public class Output implements Runnable, Pausable{
	static Thread thread;
	final static int REFRESH_RATE = 500;

	private ArrayList<Object> use = new ArrayList<Object>();
	private boolean paused = false;

	public void push(Object object){
		this.use.add(object);
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void pause() {
		this.print();
		this.paused = true;
	}

	@Override
	public void play() {
		this.paused = false;
	}

	@Override
	public boolean isPaused() {
		return this.paused;
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (!this.isPaused()) this.print();
				Thread.sleep(Output.REFRESH_RATE);
			} catch (InterruptedException e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}
	}

	private void print(){
		System.out.print("\033[H\033[2J");
		System.out.flush();
		for (Object object : this.use) {
			System.out.println(object.toString());
		}
	}
}