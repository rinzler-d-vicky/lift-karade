package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

import app.interfaces.InputCallable;
import app.interfaces.Pausable;

enum Direction {
	UP, DOWN, STILL,
}

public class ElevatorController implements Runnable, Pausable, InputCallable {
	static Thread thread;
	private boolean paused = false;

	public volatile HashMap<String, Elevator> elevators = new HashMap<String, Elevator>();

	private volatile Stack<String> callStack = new Stack<String>();

	public boolean loadFromDatabase() {
		return true;
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {
		MainProcess();
	}

	private void MainProcess() {
		if (!this.isPaused()) {
			// TODO: Controller logic here
		}
	}

	// Pause all threads till input
	public void pause() {

		this.paused = true;
	}

	public boolean isPaused() {
		return this.paused;
	}

	public void play() {
		this.paused = false;
	}

	public void callElevator(int floor, Direction direction) {
		// TODO: outside call to elevator with direction
	}

	public void elevatorGoto(int lift, int floor) {
		// TODO: inside call to the elevator to destination
	}

	public void userGoto(int userId, int lift, int floor) {
		// TODO: user is inside and tells elevator to go to destination
	}

	@Override
	public void input(String i) {
		System.out.println("Got Input : " + i);
		callStack.push(i);

		String[] args = i.split(":");
		switch (args[0]) {
		case "call": {
			Direction direction = args[1] == "down" ? Direction.DOWN : Direction.UP;
			int floor = Integer.parseInt(args[2]);
			this.callElevator(floor, direction);
		}
			break;
		case "lift": {
			int lift = Integer.parseInt(args[1]);
			int floor = Integer.parseInt(args[3]);
			this.elevatorGoto(lift, floor);
		}
			break;
		case "user": {
			int userId = Integer.parseInt(args[1]);
			int lift = Integer.parseInt(args[3]);
			int floor = Integer.parseInt(args[5]);
			this.userGoto(userId, lift, floor);
		}
			break;
		default:
			System.err.println("Invalid command for controller");
			break;
		}
	}

	@Override
	public String toString() {
		String str = "Elevator(s) Status:\n";
		if (this.elevators.size() == 0) {
			str += "NO LIFTS IN THE SYSTEM";
		} else {
		}
		str += "\n\nCall Stack:";
		for (String call : callStack)
			str += "\n" + call;
		return str;
	}

	public static void SetLiftCount(int count) throws SQLException {
		if (count < 1)
			throw new Error("Minimum Lifts should be 1");

		Connection conn = Database.getConnection();
		String query = "UPDATE L_OPTIONS SET VALUE='" + count + "' WHERE NAME='LIFTS'";
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(query);
	}

	public static void SetFloorCount(int count) throws SQLException {

		if (count <= 0)
			throw new Error("Minimum floors should be 1");
		Connection conn = Database.getConnection();
		String query = "UPDATE L_OPTIONS SET VALUE='" + count + "' WHERE NAME='FLOORS'";
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(query);
	}

	public static void SetCommonFloor(int floor) throws SQLException {
		ElevatorManager elevatorManager = new ElevatorManager();
		ArrayList<Integer> commonFloors = elevatorManager.getCommonFloors();

		if (floor < 0)
			throw new Error("Minimum Floor number should be 0");
		if (floor > elevatorManager.getTotalFloors())
			throw new Error("Floor number cannot be more than max");
		if (commonFloors.contains(floor))
			throw new Error("Floor is already a common floor");

		commonFloors.add(floor);
		Collections.sort(commonFloors);
		// TODO: Merge
	}

	public static void UnsetCommonFloor(int floor) {
		// no neg
		// must be in 0 - max floors
		// warn if already in lift
	}
}