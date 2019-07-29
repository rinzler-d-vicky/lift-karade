package app;

import java.util.*;

import app.interfaces.InputCallable;
import app.interfaces.Pausable;

public class InputScanner implements Runnable{
	final static char INTERRUPT_KEY = '.';
	static Thread thread;

	private boolean readyForInput = false;
	private ArrayList<Pausable> toPause = new ArrayList<Pausable>();
	private ArrayList<InputCallable> forInput = new ArrayList<InputCallable>();

	public void onInterrupt(Pausable obj){ this.toPause.add(obj); }

	public void onInput(InputCallable iCallable){ this.forInput.add(iCallable); }

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	Scanner inputReader = new Scanner(System.in);

	@Override
	public void run() {
		this.scanForInput();
	}

	private void scanForInput(){
		if(this.readyForInput){
			for (Pausable object : this.toPause) {
				object.pause();
			}
	
			// Please Enter Details as per format:
			String input = inputReader.nextLine();
			this.call(input);
	
			for (Pausable object : this.toPause) {
				object.play();
			}
			this.readyForInput = false;
		} else {
			char interruptedBy = inputReader.nextLine().charAt(0);
			if(interruptedBy == INTERRUPT_KEY)
				this.readyForInput = true;
		}
		scanForInput();
	}

	private void call(String i){
		for (InputCallable obj : this.forInput) {
			obj.input(i);
		}
	}

	@Override
	public String toString(){
		return this.readyForInput?
			"Please Enter the details as per format: "
			:"Press '"+ InputScanner.INTERRUPT_KEY +"' to enter Input";
	}
}