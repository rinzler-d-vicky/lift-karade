package app;

import java.util.ArrayList;

public class User {
	private int id = 0;
	private String name = "N/A";
	private String card = "N/A";
	private ArrayList<Integer> floors = new ArrayList<Integer>();

	User(
		String name,
		String card,
		ArrayList<Integer> floors
	){
		this.name = name;
		this.card = card;
		this.floors = floors;
	}

	public int getId(){ return this.id; }
	public String getName(){ return this.name; }
	public String getCard(){ return this.card; }
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

	public void save(){
		// save to database
	}
	public static void Delete(int id){
		// delete specific from database
	}
	public static ArrayList<User> List(){
		ArrayList<User> userList = new ArrayList<User>();

		return userList;
	}
    
}