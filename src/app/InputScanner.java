package app;

import java.util.*;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import app.interfaces.InputCallable;
import app.interfaces.Pausable;

public class InputScanner implements Runnable, InputCallable{
	final static char INTERRUPT_KEY = '.';
	static Thread thread;

	private Queue<String> callStack = new CircularFifoQueue<String>(10);
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
		try {
			App.Inputs.put(i);
			callStack.add(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString(){
		String str = "";
		str += "\n\nCall Stack:";
		for (String call : callStack)
			str += "\n" + call;
		str += "\n";
		str += this.readyForInput?
			"Please Enter the details as per format: "
			:"Press '"+ InputScanner.INTERRUPT_KEY +"' to enter Input";
		return str;
	}

	@Override
	public void input(String i) {
		this.call(i);
	}
}