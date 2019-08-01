package app;

public class App {
	public static void main(String[] args) {
		try {
			ElevatorManager manager = new ElevatorManager();
			manager.start();
	
			ElevatorController controller = new ElevatorController();
			Output output = new Output();
			InputScanner inputScanner = new InputScanner();
	
			inputScanner.onInterrupt(controller);
			inputScanner.onInterrupt(output);
	
			inputScanner.onInput(controller);
	
			output.push(controller);
			output.push(inputScanner);
	
			controller.start();
			inputScanner.start();
			output.start();
	
			GUI ui = new GUI();
			ui.setController(controller);
			ui.setTotalFloors(manager.getTotalFloors());
			ui.setTotalLifts(manager.getTotalLifts());
			ui.start();			
		} catch (Exception e) {
			System.err.println("System Terminated");
			e.printStackTrace();
		}
	}
}