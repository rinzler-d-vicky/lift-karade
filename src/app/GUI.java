package app;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import app.interfaces.InputCallable;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;

public class GUI implements Runnable{
	static int totalFloors = 0;
	static int totalLifts = 0;

	public void setTotalFloors(int i){ totalFloors = i; }
	public void setTotalLifts(int i){ totalLifts = i; }

	private static final ImageIcon UpButtonIcon = new ImageIcon("src/image/up.png");
	private static final ImageIcon DownButtonIcon = new ImageIcon("src/image/down.png");

	private InputCallable controller;

	public void setController(InputCallable callable) {
		this.controller = callable;
	}

	final private JPanel getLiftFloorPanel() {
		InputCallable controller = this.controller;

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(totalFloors + 1, 3, 5, 10));

		panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		panel.add(new JLabel("   ")); // Top floor does not need up button
		for (int i = totalFloors; i > 0; i--) {
			JLabel label = new JLabel("      " + i);
			JButton downButton = new JButton(DownButtonIcon);
			JButton upButton = new JButton(UpButtonIcon);

			final int floor = i;
			downButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					if (controller != null) {
						controller.input("call:down:" + floor);
					}

				}
			});
			upButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					if (controller != null) {
						controller.input("call:up:" + (floor - 1));
					}

				}
			});

			panel.add(label); // Floor [I]
			panel.add(downButton); // Down [I]
			panel.add(upButton); // Up [I-1]
		}
		panel.add(new JLabel("      0")); // Floor 0 ; breaks norms
		panel.add(new JLabel("   ")); // Ground floor does not need down
		return panel;
	}

	final private JPanel getLiftInternalPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(totalFloors);
		formatter.setAllowsInvalid(false);
		// formatter.setCommitsOnValidEdit(true);

		JFormattedTextField textbox = new JFormattedTextField(formatter);
		textbox.setColumns(8);
		textbox.setValue(null);
		textbox.setFocusLostBehavior(JFormattedTextField.PERSIST);

		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.gridwidth = 8;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		panel.add(textbox, gbc);

		for (int i = 0; i < GUI.totalLifts; i++) {
			if (i % 2 == 0) {
				gbc.gridx = 2;
				gbc.gridy = 2 + i;
			} else {
				gbc.gridx = 12;
				gbc.gridy = 2 + i - 1;
			}
			final int lift = i + 1;
			JButton button = new JButton("L" + lift);

			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String destination = textbox.getText();
					
					if (controller != null) {
						controller.input("lift:" + lift + ":goto:" + destination);
					}

					textbox.selectAll();
					textbox.grabFocus();
				}
			});

			panel.add(button, gbc);
		}

		return panel;
	}

	final public void render() {

		JPanel liftFloorPanel = this.getLiftFloorPanel();
		JPanel liftInternalPanel = this.getLiftInternalPanel();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
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

	static Thread thread;

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	// public static void main(String[] args) {
	// 	GUI ui = new GUI();

	// 	ui.setTotalFloors(10);
	// 	ui.setTotalLifts(4);

	// 	ui.render();
	// }

	@Override
	public void run() {
		this.render();
	}
}
