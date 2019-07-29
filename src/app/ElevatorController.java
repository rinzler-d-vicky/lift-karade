package app;

import java.util.HashMap;

import app.interfaces.InputCallable;
import app.interfaces.Pausable;

public class ElevatorController implements Runnable, Pausable, InputCallable{
	static Thread thread;
	private boolean paused = false;

	public volatile HashMap<String, Elevator> elevators = new HashMap<String, Elevator>();

	public boolean loadFromDatabase(){
		return true;
	}

	public void start(){
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {
		MainProcess();
	}

	private void MainProcess(){
		if(!this.isPaused()){
			// TODO: Controller logic here
		}
	}

	// Pause all threads till input
	public void pause(){

		this.paused = true;
	}

	public boolean isPaused(){ return this.paused; }
   
	public void play(){
		this.paused = false;
	}

	@Override
	public void input(String i) {
		System.out.println("Got Input : " + i);
	}

	@Override
	public String toString(){
		String str = "Elevator(s) Status:\n";
		if(this.elevators.size()==0){
			str += "NO LIFTS IN THE SYSTEM";
		}
		else{}
		return str;
	}
}