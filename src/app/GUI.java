package app;

import javax.swing.*;
import java.awt.*;

public class GUI{
	final public static void renderExternalPanel(){
		final int totalFloors = 5;
		final ImageIcon UpButtonIcon = new ImageIcon("src/image/up.png");
		final ImageIcon DownButtonIcon = new ImageIcon("src/image/down.png");

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JFrame frame = new JFrame("Lift Floor Panel");
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(250, 400);
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(totalFloors+1, 3, 5, 10));

				panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

				panel.add(new JLabel("   ")); // Top floor does not need up button
				for (int i = totalFloors; i > 0; i--) {
					panel.add(new JLabel("      " + i)); // Floor [I]
					panel.add(new JButton(DownButtonIcon)); // Down [I]
					panel.add(new JButton(UpButtonIcon)); // Up [I-1]
				}
				panel.add(new JLabel("      0")); // Floor 0 ; breaks norms
				panel.add(new JLabel("   ")); // Ground floor does not need down

				frame.add(panel);
			}
		});
	}
	public static void main(String[] args) {
		GUI.renderExternalPanel();
	}
}
