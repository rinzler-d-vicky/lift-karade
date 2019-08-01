package app;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class App {
	public static BlockingQueue<String> Inputs = new LinkedBlockingQueue<String>();

	public static void main(String[] args) {
		try {
			ElevatorManager manager = new ElevatorManager();
			manager.start();

	
			ElevatorController controller = new ElevatorController();
			Output output = new Output();
			InputScanner inputScanner = new InputScanner();
	
			inputScanner.onInterrupt(controller);
			inputScanner.onInterrupt(output);
	
			// inputScanner.onInput(controller);
	
			output.push(controller);
			output.push(inputScanner);
	
			GUI ui = new GUI();
			ui.setController(inputScanner);
			ui.setTotalFloors(manager.getTotalFloors());
			ui.setTotalLifts(manager.getTotalLifts());

			new Thread(controller).start();
			new Thread(inputScanner).start();
			new Thread(output).start();
			new Thread(ui).start();
		} catch (Exception e) {
			System.err.println("System Terminated");
			e.printStackTrace();
		}
	}
}