package app;

import java.util.LinkedList;
import java.util.stream.Collectors;

import app.interfaces.Pausable;

public class Elevator implements Runnable, Pausable{
	static Thread thread;
	private boolean paused = false;

	private int direction = 0;
	private int currentFloor = 0;
	private LinkedList<Integer> stops = new LinkedList<Integer>();
	private Direction status = Direction.STILL;
	private int nextDestination = -1;

	private int maxFloors = 0;

	Elevator(int MaxFloors){
		this.maxFloors= MaxFloors;
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void pause() {
		this.paused = true;
	}

	@Override
	public void play() {
		this.paused = false;
		this.runLift();
	}

	@Override
	public boolean isPaused() {
		return this.paused;
	}

	private void internalPause(int duration) throws InterruptedException {
		duration = duration / 100;
		int i = 0;
		while (i < duration) {
			Thread.sleep(100);
			if (!this.paused)
				i++;
		}
	}

	@Override
	public void run() {
		this.runLift();
	}

	private void runLift() {
		try {
			if(this.paused){ return; }
			else{
				if(this.stops.isEmpty()) return;
				int stop = this.stops.removeFirst();
				this.nextDestination = stop;
				int duration = (Math.abs(this.currentFloor - stop) + 1) * ElevatorController.FLOOR_TRAVEL_TIME;

				// Travel to destination
				int i = 0;
				int j = 0;
				final int floorChange = ElevatorController.FLOOR_TRAVEL_TIME / 100;
				this.status = this.currentFloor - stop > 0 ? Direction.DOWN: Direction.UP;
				duration = duration / 100;
				while (i < duration) {
					Thread.sleep(100);
					if (!this.paused){
						i++;
						j++;
					}
					if(j>floorChange){
						j=0;
						// Update floor to new location
						if(Direction.UP==this.status) this.currentFloor++;
						else if(Direction.DOWN==this.status) this.currentFloor--;

						if(this.currentFloor<0) this.currentFloor = 0;
						if(this.currentFloor>this.maxFloors) this.currentFloor = this.maxFloors;
					}
				}

				// Open Door
				this.status = Direction.OPEN_DOOR;
				duration = ElevatorController.DOOR_CHANGING_TIME;
				this.internalPause(duration);

				// Keep door Open
				this.status = Direction.OPEN;
				duration = ElevatorController.DOOR_OPEN_TIME;
				this.internalPause(duration);

				// Close Door
				this.status = Direction.CLOSE_DOOR;
				duration = ElevatorController.DOOR_CHANGING_TIME;
				this.internalPause(duration);

				// set to rest state
				this.status = Direction.STILL;
				this.nextDestination = -1;

				this.runLift(); // goto next stop
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int Cost(int floor, Direction _direction){
		int direction = ElevatorController.Direction2Int(_direction);

		int cost = 0;

		// Lift is not in motion
		if (this.direction == 0) {
			cost = 5 * Math.abs(floor - this.currentFloor);
			return cost;
		}

		int max = 0, min = 99;
		if (this.stops.size() != 0) {
			max = this.stops.get(0);
			min = this.stops.get(0); // check if max=min
			for (int m = 1; m < this.stops.size(); m++) {
				if (this.stops.get(m) > max) { // find max floor
					max = this.stops.get(m);
				}
				if (this.stops.get(m) < min) { // find min floor
					min = this.stops.get(m);
				}
			}
		}

		// UseCases when lift dir = up
		if (this.direction == 1) {
			// Set1. floor > curFloor
			if (floor > this.currentFloor) {
				if (direction == 1) { // UseCase 1
					cost = 5 * Math.abs(floor - this.currentFloor);
					for (int j = 0; j < this.stops.size(); j++) {
						if ((this.stops.get(j) < floor) && (this.stops.get(j) > this.currentFloor)) {
							cost += 10;
						}
					}
				}
				if (direction == -1) { // UseCase 2.1

					if (max >= floor) {
						cost = 5 * (Math.abs(max - this.currentFloor) + Math.abs(max - floor));
						for (int j = 0; j < this.stops.size(); j++) {
							if (this.stops.get(j) > this.currentFloor) {
								cost += 10;
							}
						}
					}
					if (max < floor) { // UseCase 2.2
						cost = 5 * (Math.abs(max - this.currentFloor) + Math.abs(floor - max));
						for (int j = 0; j < this.stops.size(); j++) {
							if (this.stops.get(j) > this.currentFloor) {
								cost += 10;
							}
						}
					}
				}
			}

			// Set 2. floor < curFloor
			if (floor < this.currentFloor) {
				if (direction == 1) { // UseCase 3
					cost = 5 * (Math.abs(max - this.currentFloor) + Math.abs(max - min) + Math.abs(floor - min));
					for (int j = 0; j < this.stops.size(); j++) {
						cost += 10;
					}
				}
				if (direction == -1) { // UseCase 4
					cost = 5 * (Math.abs(max - this.currentFloor) + Math.abs(max - floor));
					for (int j = 0; j < this.stops.size(); j++) {
						if (this.stops.get(j) > floor) {
							cost += 10;
						}
					}
				}
			}
			// Set 3. floor = curFloor
			if (floor == this.currentFloor) { // UseCase 5
				if (direction == 1) {
					cost = 0;
				}
				if (direction == -1) { // UseCase 6
					cost = 5 * (Math.abs(max - this.currentFloor) + Math.abs(max - floor));
					for (int j = 0; j < this.stops.size(); j++) {
						if (this.stops.get(j) > floor) {
							cost += 10;
						}
					}
				}
			}
		}

		if (this.direction == -1) {
			// Set 4. floor > curFloor
			if (floor > this.currentFloor) {
				if (direction == 1) { // UseCase 7
					cost = 5 * (Math.abs(this.currentFloor - min) + Math.abs(floor - min));
					for (int j = 0; j < this.stops.size(); j++) {
						if (this.stops.get(j) < floor) {
							cost += 10;
						}
					}
				}
				if (direction == -1) { // UseCase 8
					cost = 5 * (Math.abs(this.currentFloor - min) + Math.abs(max - min) + Math.abs(max - floor));
					for (int j = 0; j < this.stops.size(); j++) {
						cost += 10;
					}
				}
			}
			// Set 5. floor < curFloor
			if (floor < this.currentFloor) {
				if (direction == 1) { // UseCase 9
					cost = 5 * (Math.abs(this.currentFloor - min) + Math.abs(floor - min));
					for (int j = 0; j < this.stops.size(); j++) {
						if (this.stops.get(j) < this.currentFloor) {
							cost += 10;
						}
					}
				}
				if (direction == -1) { // UseCase 10.1
					if (min <= floor) {
						cost = 5 * Math.abs(this.currentFloor - floor);
						for (int j = 0; j < this.stops.size(); j++) {
							if ((this.stops.get(j) < this.currentFloor) && (this.stops.get(j) > floor)) {
								cost += 10;
							}
						}
					}
					if (min > floor) { // UseCase 10.2
						cost = 5 * (Math.abs(this.currentFloor - min) + Math.abs(min - floor));
						for (int j = 0; j < this.stops.size(); j++) {
							if ((this.stops.get(j) < this.currentFloor) && (this.stops.get(j) > floor)) {
								cost += 10;
							}
						}
					}
				}
			}
			// Set 6. floor = curFloor
			if (floor == this.currentFloor) { // UseCase 11
				if (direction == -1) {
					cost = 0;
				}
				if (direction == 1) { // UseCase 12
					cost = 5 * (Math.abs(this.currentFloor - min) + Math.abs(floor - min));
					for (int j = 0; j < this.stops.size(); j++) {
						if (this.stops.get(j) < floor) {
							cost += 10;
						}
					}
				}
			}
		}

		return cost;
	}

	public void InsideCall(int floor){
		int direction = this.direction;
		int currentFloor = this.currentFloor;

		// if lift is not in motion
		// and floor is above
		if (
			direction == 0 &&
			(floor > currentFloor || floor < currentFloor)
		) this.stops.add(floor);

		// if going up and floor is above current floor
		if (direction == 1 && floor > currentFloor) {
			int flag = 0;
			for (int i = 0; i < this.stops.size(); i++) {
				if (floor == this.stops.get(i)) {
					flag = 1;
					break;
				}
				if (floor < this.stops.get(i)) {
					this.stops.add(i, floor);
					flag = 1;
					break;
				}
			}
			if (flag == 0 && floor != currentFloor) this.stops.add(floor);
		}

		// if going up and destination floor is below current floor 
		if (direction == 1 && floor < currentFloor) {
			int flag = 0, last = this.stops.size() - 1;

			if (floor < this.stops.get(last)) {
				this.stops.add(floor);
				flag = 1;
			}

			if (flag == 0 && floor != currentFloor) {
				for (int i = last; i >= 0; i--) {
					if (floor == this.stops.get(i))
						break;

					if (floor < this.stops.get(i)) {
						this.stops.add(i + 1, floor);
						break;
					}
				}
			}
		}

		// going down and destination floor is below
		if (direction == -1 && floor < currentFloor) {
			int flag = 0;
			for (int i = 0; i < this.stops.size(); i++) {
				if (floor == this.stops.get(i)) {
					flag = 1;
					break;
				}
				if (floor > this.stops.get(i)) {
					this.stops.add(i, floor);
					flag = 1;
					break;
				}
			}

			if (flag == 0 && floor != currentFloor) {
				this.stops.add(floor);
			}
		}

		// going down and destination floor is above
		if (direction == -1 && floor > currentFloor) {
			int flag = 0, last = this.stops.size() - 1;
			if (floor > this.stops.get(last)) {
				this.stops.add(floor);
				flag = 1;
			}

			if (flag == 0 && floor != currentFloor) {
				for (int i = last; i >= 0; i--) {
					if (floor == this.stops.get(i))
						break;
					if (floor > this.stops.get(i)) {
						this.stops.add(i + 1, floor);
						break;
					}
				}
			}
		}

		this.runLift();

	}

	@Override
	public String toString(){
		String str = "";
		switch (this.status) {
			case UP: str+="Up:Floor#" + this.nextDestination + " Now@"; break;
			case DOWN: str+="Down:Floor#" + this.nextDestination + " Now@"; break;
			case STILL: str+="is@"; break;
			case OPEN_DOOR: str+="Door Opening@"; break;
			case CLOSE_DOOR: str+="Door Closing@"; break;
			case OPEN: str+="Door Open@"; break;
		}
		str+="Floor#" + this.currentFloor;
		String stops = this.stops.stream().map(Object::toString).collect(Collectors.joining(", "));
		str+="\tStops["+stops+"]";
		return str;
	}

}