package app;

public class App {
	public static void main(String[] args) throws Exception {
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
	}
}