package app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class User {
	private int id = 0;
	private String name = "N/A";
	private String card = "N/A";
	private ArrayList<Integer> floors = new ArrayList<Integer>();
	private boolean active = true;

	User(int id) throws SQLException {
		ResultSet rs = Database.fetchResultSet("SELECT * FROM L_USERS WHERE ID=" + id);
		ArrayList<Integer> floors = new ArrayList<Integer>();
		String[] r = rs.getString(4).split(",");
		for (String f : r)
			floors.add(Integer.parseInt(f));

		floors.addAll(new ElevatorManager().getCommonFloors());
		Collections.sort(floors);

		this.id = rs.getInt(1);
		this.name = rs.getString(2);
		this.card = rs.getString(3);
		this.floors = floors;
		this.active = rs.getInt(5) != 0;
	}

	User(
		String name,
		String card,
		ArrayList<Integer> floors
	){
		this.name = name;
		this.card = card;
		this.floors = floors;
	}

	User(
		int id,
		String name,
		String card,
		ArrayList<Integer> floors
	){
		this.id = id;
		this.name = name;
		this.card = card;
		this.floors = floors;
	}

	public int getId(){ return this.id; }
	public String getName(){ return this.name; }
	public String getCard(){ return this.card; }
	public boolean getActive(){ return this.active; }
	public ArrayList<Integer> getFloors(){ return this.floors; }

	@Override
	public String toString(){
		String str = "";
		str += "Name: " + this.name  + "\n";
		str += "ID: " + this.id + "\n";
		str += "Card# : " + this.card + "\n";
		str += "Access to: ";
		for (int floor : this.floors) str += floor + ", ";
		str += "\n\n";
		return str;
	}

	public void save() throws SQLException{
		String _floor = this.floors.stream().map(Object::toString).collect(Collectors.joining(","));
		String query = "INSERT INTO L_USERS (NAME, CARD, FLOOR, ACTIVE) VALUES"
		+ "('"+ this.name +"','"+this.card+"','"+_floor+"',1)";
		Database.executeUpdate(query);
	}
	public static void Delete(int id) throws SQLException {
		String query = "UPDATE L_USERS SET ACTIVE=0 WHERE ID=" + id;
		Database.executeUpdate(query);
	}
	public static ArrayList<User> List() throws SQLException {
		ArrayList<User> userList = new ArrayList<User>();
		ResultSet rs = Database.fetchResultSet("SELECT * FROM L_USERS WHERE ACTIVE=1");
		do {
			ArrayList<Integer> floors = new ArrayList<Integer>();
			String[] r = rs.getString(4).split(",");
			for (String f : r)
				floors.add(Integer.parseInt(f));

			floors.addAll(new ElevatorManager().getCommonFloors());
			Collections.sort(floors);
			
			User user  = new User(
				rs.getInt(1),
				rs.getString(2),
				rs.getString(3),
				floors
			);

			userList.add(user);
		} while (rs.next());
		return userList;
	}

	public boolean isFloorAllowed(int floor){ return this.active && this.floors.contains(floor); }
    
}