package app;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.*;

class lift {
	int dir, curFloor;
	LinkedList<Integer> stops = new LinkedList<Integer>();

	void singleLift_optimize_Algo(int floor, int LiftNo) {
		int di = this.dir;
		int cF = this.curFloor;

		if (di == 0 && floor > cF) {
			this.stops.add(floor);
			this.dir = 1;
		}

		if (di == 0 && floor < cF) {
			this.stops.add(floor);
			this.dir = -1;
		}

		// up and floor > curFloor
		if (di == 1 && floor > cF) {
			int flag = 0;
			for (int i = 0; i < this.stops.size(); i++) {

				if (floor < this.stops.get(i)) {
					this.stops.add(i, floor);
					flag = 1;
					break;
				}
			}

			if (flag == 0 && floor != cF) {
				this.stops.add(floor);
			}
		}

		// up and floor < curFloor
		if (di == 1 && floor < cF) {
			int flag = 0, last = this.stops.size() - 1;
			if (floor < this.stops.get(last)) {
				this.stops.add(floor);
				flag = 1;
			}

			if (flag == 0 && floor != cF) {
				for (int i = last; i >= 0; i--) {
					if (floor < this.stops.get(last)) {
						this.stops.add(i + 1, floor);
						break;
					}
				}
			}
		}

		// down and floor < curFloor
		if (di == -1 && floor < cF) {
			int flag = 0;
			for (int i = 0; i < this.stops.size(); i++) {

				if (floor < this.stops.get(i)) {
					this.stops.add(i, floor);
					flag = 1;
					break;
				}
			}

			if (flag == 0 && floor != cF) {
				this.stops.add(floor);
			}
		}

		// down and floor > curFloor
		if (di == -1 && floor > cF) {
			int flag = 0, last = this.stops.size() - 1;
			if (floor > this.stops.get(last)) {
				this.stops.add(floor);
				flag = 1;
			}

			if (flag == 0 && floor != cF) {
				for (int i = last; i >= 0; i--) {
					if (floor > this.stops.get(i)) {
						this.stops.add(i + 1, floor);
						break;
					}
				}
			}
		}
	}

};

public class test {

	static void next(lift l[]) {

	}

	public static void main(String[] args) {
		lift l[] = new lift[5];
		for (int i = 1; i <= 4; i++) {
			l[i] = new lift();
		}

		l[1].dir = -1;
		l[1].curFloor = 2;
		l[1].stops.add(0);
		l[2].dir = 0;
		l[2].curFloor = 6;
		l[3].dir = 1;
		l[3].curFloor = 3;
		l[3].stops.add(5);
		l[3].stops.add(6);
		l[4].dir = 1;
		l[4].curFloor = 1;
		l[4].stops.add(3);
		l[4].stops.add(2);

		do {
			System.out.print("\n\nEnter Lift Call ( destFloor$liftNo / currFloor$Direction ) :  ");
			Scanner in = new Scanner(System.in);
			String s = in.nextLine();
			int floor = (int) s.charAt(0) - 48;
			int flg = 0;
			int directn = 0;

			char ch = s.charAt(1);

			if (ch == 'd') {
				directn = -1;
				flg = 1;
			} else if (ch == 'u') {
				directn = 1;
				flg = 1;
			}
			// call outside the lift
			if (flg == 1) {
				// 1. search lifts in same direction
				int diff = 99, temp, indx = 1, liftAvailable = 0;
				for (int i = 1; i <= 4; i++) {
					if (directn == l[i].dir || l[i].dir == 0) {
						temp = Math.abs(floor - l[i].curFloor);
						if (temp < diff) {
							diff = temp;
							indx = i;
						}
						liftAvailable = 1;
					}
				}

				if (liftAvailable == 1)
					l[indx].singleLift_optimize_Algo(floor, indx);
				// if all lifts are in opposite directn then check size of requested calls
				else {
					int min = l[1].stops.size();
					int t, idx = 1, flrDif = Math.abs(floor - l[1].curFloor);
					for (int i = 2; i <= 4; i++) {
						int check = 0;
						t = l[i].stops.size();
						if (t < min) {
							min = t;
							idx = i;
							flrDif = Math.abs(floor - l[i].curFloor);
							check = 1;
						}
						if (t == min && check == 0) {
							if (Math.abs(floor - l[i].curFloor) > flrDif) {
								min = t;
								idx = i;
								flrDif = Math.abs(floor - l[i].curFloor);
							}
						}
					}
					l[idx].singleLift_optimize_Algo(floor, idx);
				}

			}

			if (ch == 'l') {
				int liftNo = (int) s.charAt(3) - 48;
				l[liftNo].singleLift_optimize_Algo(floor, liftNo);
			}

			// display
			for (int i = 1; i <= 4; i++) {
				System.out.print("\n  Lift " + i + " :- \tcurrFloor = " + l[i].curFloor + " \t directn = " + l[i].dir
						+ " \t\tStops = " + l[i].stops);
				// update dir, curfloor, pop stops for each lifts
				/*
				 * for(int j=1; j<=4; j++) { //lifts moving up if(l[j].dir == 1) {
				 * l[j].curFloor++; if(l[j].curFloor == l[j].stops.getFirst()) {
				 * l[j].stops.removeFirst(); if(l[j].stops.getFirst() < l[j].curFloor) {
				 * l[j].dir = -1; } } } }
				 */

			}
		} while (true);

	}

}