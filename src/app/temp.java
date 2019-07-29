package app;

	import java.util.*;
import java.lang.*;
import java.io.*;

import java.sql.*;

/*


class OracleCon{
    

public static void main(String args[]){  
try{  
//step1 load the driver class  
Class.forName("oracle.jdbc.driver.OracleDriver");  
  
//step2 create  the connection object  
Connection con=DriverManager.getConnection("jdbc:oracle:thin:@OSCTrain1DB01.oneshield.com:1521:Train1","arodrigues","password");  
  
//step3 create the statement object  
Statement stmt=con.createStatement();

//adding data to the db,adds in row 1
String queryadd = " insert into emp (id,name,age)"
        + " values (?, ?, ?)";
String querydel="delete from emp where id=1003";  
String queryupd="update emp set name='Pono',age=33 where id=902";
String querygetrow="select * from EMP where ROWNUM<6";
  PreparedStatement preparedStmt = con.prepareStatement(querygetrow);
//   preparedStmt.setInt    (1, 41);
//  preparedStmt.setString (2, "J-smalls");
//   preparedStmt.setInt (3, 29);

  
  preparedStmt.execute();
  //end of adding data
  
  
  
//step4 execute query  

ResultSet rs=stmt.executeQuery("select * from emp");  
while(rs.next())  
System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getInt(3));  
  
//step5 close the connection object  
con.close();  
  
}catch(Exception e){ System.out.println(e);}  
  
}  
}  




*/

	public class temp
	{
		/*
		//optional input class
				private boolean defaultOptionalFlagValue = true;

				public void doSomething(boolean optionalFlag) {
				   System.out.println("zzzz");
				}

				public void doSomething() {
				    doSomething(defaultOptionalFlagValue);
				}
				
				//optional input class
		*/
		
	   static int floor;

	    public static void main(String args[]) throws java.lang.Exception
	    {
	    	Scanner myInput = new Scanner( System.in );
	        floor = (int) (Math.random() * 10 + 1);
int ifloor=floor;
	        System.out.println("The elevator is now on floor " +floor);
	        System.out.print("Which floor are you at now (0-10) where 0 = basement: ");
	        int current_floor;
	        current_floor=myInput.nextInt();

	        if(floor == current_floor)
	        {
	            System.out.println("Enter the elevator");
	        }
	        else
	        {
	            MoveElevator(current_floor);
	        }


	        System.out.println("To which floor would you want to go (0-10) where 0 = basement");
	        int target_floor= myInput.nextInt();
            int itarget_floor=target_floor;
	        MoveElevator(target_floor);
	        
	        int wait_time=(Math.abs(ifloor-current_floor))+3;
	        int TAT=(Math.abs(current_floor-itarget_floor))+3+wait_time; 
	        System.out.println(ifloor+"ifloor\n"+itarget_floor+"itarget floor");
	        System.out.println("Total wait time of user= "+wait_time*5+"seconds"+"\nTotal turnaround time is = "+TAT*5+"seconds");
	       
	    }

	    public static void MoveElevator(int target_floor)
	    {
	    	
	        int direction;
	        if( target_floor > floor )
	        {
	            System.out.println("The elevator is on it's way up...");
	            direction = 1;
	        }else{
	            System.out.println("The elevator is on it's way down...");
	            direction = -1;
	        }
	      
	        
	      int a=0;
	     
	        while(target_floor != floor)
	        {
	        	try
	        	{
	        		/*
	        		if(a==1)
	        		{
	        			System.out.println("zzzz");
	        			a=0;
	        		}
	        		*/
	        	    Thread.sleep(1000);
	        	}
	        	catch(InterruptedException ex)
	        	{
	        	    Thread.currentThread().interrupt();
	        	}
	            floor += direction;
	            /*
	            if(keyPressed())
	            {
	            	
	            }
	            */
	            System.out.println(floor);
	        }
	               

	        System.out.println("The elevator has arrived");
	    }
/*
		public void keyPressed(int value) 
		{
			
			if(value==0)
			{
				value=255;
				
			}
			else
			value=0;
		}
		*/
	}

