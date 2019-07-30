package app;

import javax.swing.*;
import java.awt.*;

public class GUI{
	static int totalFloors = 10;
	static int numberOfLifts = 4;

	private static final ImageIcon UpButtonIcon = new ImageIcon("src/image/up.png");
	private static final ImageIcon DownButtonIcon = new ImageIcon("src/image/down.png");

	final private JPanel getLiftFloorPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(totalFloors + 1, 3, 5, 10));

		panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		panel.add(new JLabel("   ")); // Top floor does not need up button
		for (int i = totalFloors; i > 0; i--) {
			panel.add(new JLabel("      " + i)); // Floor [I]
			panel.add(new JButton(DownButtonIcon)); // Down [I]
			panel.add(new JButton(UpButtonIcon)); // Up [I-1]
		}
		panel.add(new JLabel("      0")); // Floor 0 ; breaks norms
		panel.add(new JLabel("   ")); // Ground floor does not need down
		return panel;
	}

	final private JPanel getLiftInternalPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		JTextField textbox = new JTextField(8);
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.gridwidth = 8;
		// gbc.fill=GridBagConstraints.HORIZONTAL;
		panel.add(textbox, gbc);

		for (int i = 0; i < GUI.numberOfLifts; i++) {
			if(i%2==0){
				gbc.gridx = 2;
				gbc.gridy = 2 + i;
			} else {
				gbc.gridx = 12;
				gbc.gridy = 2 + i - 1;
			}
			panel.add(new JButton("L" + (i+1)), gbc);
		}

		return panel;
	}

	final public void render(){

		JPanel liftFloorPanel = this.getLiftFloorPanel();
		JPanel liftInternalPanel = this.getLiftInternalPanel();

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				JFrame frame = new JFrame("Lift Floor Panel");
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(250, 400);
				frame.add(liftFloorPanel);

				JFrame frame2 = new JFrame("Lift Internal Controller");
				frame2.setVisible(true);
				frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame2.setSize(150, 150);
				frame2.add(liftInternalPanel);
			}
		});
	}
	public static void main(String[] args) {
		GUI ui = new GUI();
		ui.render();
	}
}
