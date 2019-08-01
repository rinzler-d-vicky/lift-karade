package app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class ElevatorManager {

	private int totalFloors = 0;
	private int totalLifts = 0;
	private ArrayList<Integer> commonFloors = new ArrayList<Integer>();

	public int getTotalFloors() {
		return totalFloors;
	}

	public int getTotalLifts() {
		return totalLifts;
	}

	public ArrayList<Integer> getCommonFloors() {
		return this.commonFloors;
	}

	private Scanner cin = new Scanner(System.in);

	ElevatorManager() {
		try {
			this.initialize();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void initialize() throws SQLException {

		ResultSet floorRS = Database.fetchResultSet("SELECT VALUE FROM L_OPTIONS WHERE NAME='FLOORS'");
		this.totalFloors = Integer.parseInt(floorRS.getString(1));
		floorRS.close();

		ResultSet liftsRS = Database.fetchResultSet("SELECT VALUE FROM L_OPTIONS WHERE NAME='LIFTS'");
		this.totalLifts = Integer.parseInt(liftsRS.getString(1));
		liftsRS.close();

		ResultSet commonFloorRS = Database.fetchResultSet("SELECT VALUE FROM L_OPTIONS WHERE NAME='COMMON_FLOORS'");
		String[] stringCommonFloor = commonFloorRS.getString(1).split(",");
		this.commonFloors = new ArrayList<Integer>();
		for (int i = 0; i < stringCommonFloor.length; i++) {
			this.commonFloors.add(Integer.parseInt(stringCommonFloor[i]));
		}
		commonFloorRS.close();

	}

	public void start() throws SQLException {
		this.initialize();

		int option = -1;
		do {
			System.out.print("\033[H\033[2J");
			System.out.flush();

			System.out.println("Please Select and Option: ");
			System.out.println("0. Start Lifts");
			System.out.println("1. Set Lift Count");
			System.out.println("2. Set Floor Count");
			System.out.println("3. Set Common Floor");
			System.out.println("4. Delete Common Floor");
			System.out.println("5. Add User");
			System.out.println("6. Delete User");
			System.out.println("7. List User");

			option = cin.nextInt();
			System.out.print("\033[H\033[2J");
			System.out.flush();

			try {
				switch (option) {
				case 0:
					this.skipMenu();
					break;
				case 1:
					this.setLiftCount();
					break;
				case 2:
					this.setFloorCount();
					break;
				case 3:
					this.setCommonFloor();
					break;
				case 4:
					this.unsetCommonFloor();
					break;
				case 5:
					this.addUser();
					break;
				case 6:
					this.deleteUser();
					break;
				case 7:
					this.listUsers();
					break;
				default:
					System.err.println("Incorrect Choice");
					break;
				}
				cin.nextLine();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}

		} while (option != 0);
	}

	private void skipMenu() throws SQLException {
		if (this.getTotalLifts() == 0) {
			System.err.println("Cannot Start system with 0 Lifts");
			this.start();
		}
	}

	private void setLiftCount() throws SQLException {
		this.initialize();

		System.out.println("Enter Number of Lifts: ");
		int count = cin.nextInt();
		ElevatorController.SetLiftCount(count);

		this.initialize();
	}

	private void setFloorCount() throws SQLException {
		this.initialize();

		System.out.println("Enter Number of Floors: ");
		int count = cin.nextInt();
		ElevatorController.SetFloorCount(count);

		this.initialize();
	}

	private void setCommonFloor() throws SQLException {
		this.initialize();

		System.out.print("The existing common floors are: ");
		for (int floor : this.commonFloors)
			System.out.print(floor + ", ");
		System.out.println("\nEnter a floor number to make it common (between 0 and " + this.totalFloors + "):");
		int floor = cin.nextInt();
		ElevatorController.SetCommonFloor(floor);

		this.initialize();
	}

	private void unsetCommonFloor() throws SQLException {
		this.initialize();

		System.out.println("The existing common floors are: ");
		for (int floor : this.commonFloors)
			System.out.print(floor + ", ");
		System.out.println("Enter a floor number to remove it from common:");
		int floor = cin.nextInt();
		ElevatorController.UnsetCommonFloor(floor);

		this.initialize();
	}

	private void addUser() throws SQLException {
		this.initialize();

		System.out.println("Enter Client Name: ");
		String name = cin.nextLine();
		System.out.println("Enter Client Access Card Number");
		String card = cin.nextLine();

		System.out.println("How many floors can he access (excluding common floor): ");
		int fCount = cin.nextInt();
		ArrayList<Integer> floors = new ArrayList<Integer>();
		for (int i = 0; i < fCount; i++) {
			System.out.println("Enter Floor Number: ");
			int f = cin.nextInt();
			if (this.commonFloors.contains(f)) {
				System.err.println("This is a common floor, input ignored");
				i--;
			} else if (floors.contains(f)) {
				System.err.println("This floor was already in input. input ignored");
				i--;
			} else {
				floors.add(f);
			}
		}

		System.out.println("Adding User");
		User user = new User(name, card, floors);
		user.save();
	}

	private void listUsers() {
		ArrayList<User> users = User.List();
		System.out.println("ID\t\tName\t\tCard\t\tFloor");
		for (User user : users) {
			System.out.print(user.getId() + "\t\t" + user.getName() + "\t\t" + user.getCard() + "\t\t");
			for (int floor : user.getFloors())
				System.out.print(floor + ", ");
			System.out.println("");
		}
	}

	private void deleteUser() {
		this.listUsers();
		System.out.println("Enter User ID: ");
		int id = cin.nextInt();

		User.Delete(id);
		System.out.println("Deleted!");
	}
}