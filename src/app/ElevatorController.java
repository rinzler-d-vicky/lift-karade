package app;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import app.interfaces.Pausable;

enum Direction {
	UP, DOWN, STILL, OPEN_DOOR, CLOSE_DOOR, OPEN,
}

public class ElevatorController implements Runnable, Pausable {
	static Thread thread;
	private boolean paused = false;
	private int liftCount = 0;

	final public static int FLOOR_TRAVEL_TIME = 2000;
	final public static int DOOR_OPEN_TIME = 500;
	final public static int DOOR_CHANGING_TIME = 500;

	public HashMap<Integer, Elevator> elevators = new HashMap<Integer, Elevator>();

	private int MaxFloors = 0;

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
		ElevatorManager elevatorManager = new ElevatorManager();
		this.liftCount = elevatorManager.getTotalLifts();
		this.MaxFloors = elevatorManager.getTotalFloors();

		for (int i = 1; i <= this.liftCount; i++) {
			this.elevators.put(i, new Elevator(this.MaxFloors));
		}
		MainProcess();
	}

	private void MainProcess() {
		while(true){
			if (!this.isPaused()) {
				try {
					String command = App.Inputs.take();
					CompletableFuture.runAsync(()->{
						this.input(command);
					});
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void pause() {
		this.elevators.forEach((key, elevator)->{ elevator.pause(); });
		this.paused = true;
	}
	
	public boolean isPaused() {
		return this.paused;
	}
	
	public void play() {
		this.elevators.forEach((key, elevator)->{ elevator.play(); });
		this.paused = false;
	}

	public void callElevator(int floor, Direction direction) throws InterruptedException {
		HashMap<Integer, Integer> costs = new HashMap<Integer, Integer>();
		ExecutorService es = Executors.newCachedThreadPool();
		this.elevators.forEach((key, elevator) -> {
			es.execute(new Runnable(){
				@Override
				public void run() {
					costs.put(key, elevator.Cost(floor, direction));
				}
			});
		});
		es.shutdown();
		es.awaitTermination(1, TimeUnit.MINUTES);

		Entry<Integer, Integer> Minimum = null;
		for (Entry<Integer, Integer> entry : costs.entrySet()) {
			if(Minimum==null || Minimum.getValue() > entry.getValue() ){
				Minimum = entry;
			}
		}
		this.elevatorGoto(Minimum.getKey(), floor);
	}

	public void elevatorGoto(int lift, int floor) {
		try {
			this.elevators.get(lift).InsideCall(floor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void userGoto(int id, int lift, int floor) throws SQLException {
		User user = new User(id);
		if(user.isFloorAllowed(floor)) this.elevators.get(lift).InsideCall(floor);
		else System.err.println("User#"+id+"("+user.getName()+") is not allowed to floor#"+floor);
	}

	public void input(String i) {
		System.out.println("Got Input : " + i);

		String[] args = i.split(":");
		switch (args[0]) {
		case "call": {
			Direction direction = args[1] == "down" ? Direction.DOWN : Direction.UP;
			int floor = Integer.parseInt(args[2]);
			try {
				this.callElevator(floor, direction);
			} catch (InterruptedException e) {
				System.out.println("CALL failed");
				e.printStackTrace();
			}
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
			try {
				this.userGoto(userId, lift, floor);
			} catch (SQLException e) {
				System.err.println("USER GOTO FAILED");
				e.printStackTrace();
			}
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
			for (Entry<Integer, Elevator> liftEntry : this.elevators.entrySet()) {
				str += "Lift#" + liftEntry.getKey() + "\t" + liftEntry.getValue() + "\n";
			}
		}
		return str;
	}

	public static void SetLiftCount(int count) throws SQLException {
		if (count < 1)
			throw new Error("Minimum Lifts should be 1");
		Database.updateKeyValue("LIFTS", count+"");
	}

	public static void SetFloorCount(int count) throws SQLException {

		if (count <= 0)
			throw new Error("Minimum floors should be 1");
		Database.updateKeyValue("FLOORS", count + "");
	}

	public static void SetCommonFloor(int floor) throws SQLException {
		ElevatorManager em = new ElevatorManager();
		ArrayList<Integer> commonFloors = em.getCommonFloors();

		if (floor < 0)
			throw new Error("Minimum Floor number should be 0");
		if (floor > em.getTotalFloors())
			throw new Error("Floor number cannot be more than max");
		if (commonFloors.contains(floor))
			throw new Error("Floor is already a common floor");

		commonFloors.add(floor);
		Collections.sort(commonFloors);
		String finalCommonFloors = commonFloors.stream().map(Object::toString).collect(Collectors.joining(","));
		Database.updateKeyValue("COMMON_FLOORS", finalCommonFloors + "");
	}

	public static void UnsetCommonFloor(int floor) throws SQLException {
		ElevatorManager em = new ElevatorManager();
		ArrayList<Integer> commonFloors = em.getCommonFloors();

		if (floor < 0)
			throw new Error("Minimum Floor number should be 0");
		if (floor > em.getTotalFloors())
			throw new Error("Floor number cannot be more than max");
		if (!commonFloors.contains(floor))
			throw new Error("Floor must be already a common floor");

		int index = commonFloors.indexOf(floor);
		commonFloors.remove(index);

		Collections.sort(commonFloors);
		String finalCommonFloors = commonFloors.stream().map(Object::toString).collect(Collectors.joining(","));
		Database.updateKeyValue("COMMON_FLOORS", finalCommonFloors + "");
	}

	public static int Direction2Int(Direction _direction) {
		if(_direction == Direction.STILL) return 0;
		if(_direction == Direction.DOWN) return -1;
		if(_direction == Direction.UP) return 1;
		return 0;
	}
}