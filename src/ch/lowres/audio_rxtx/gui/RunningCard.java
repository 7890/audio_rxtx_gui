/* part of audio_rxtx GUI
 * https://github.com/7890/audio_rxtx_gui
 *
 * Copyright (C) 2014 Thomas Brand <tom@trellis.ch>
 *
 * This program is free software; feel free to redistribute it and/or 
 * modify it.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. bla.
*/

package ch.lowres.audio_rxtx.gui;

import java.awt.*;
import java.awt.event.*;

//========================================================================
public class RunningCard extends Panel
{
	static jack_audio_send_GUI g;
	static jack_audio_send_cmdline_API api;

	static Panel form;

	static Label label_1=new Label("");
	static Label label_2=new Label("");
	static Label label_3=new Label("");
	static Label label_4=new Label("");
	static Label label_5=new Label("");
	static Label label_6=new Label("");
	static Label label_7=new Label("");
	static Label label_8=new Label("");
	static Label label_9=new Label("");

	static Button button_stop_transmission=new Button("Stop Transmission");

//========================================================================
	public RunningCard() 
	{
		setBackground(Colors.form_background);
		setForeground(Colors.form_foreground);
		setLayout(new BorderLayout());

		createForm();
		addActionListeners();
	}

//========================================================================
	void clearLabels()
	{
		label_1.setText("");
		label_2.setText("");
		label_3.setText("");
		label_4.setText("");
		label_5.setText("");
		label_6.setText("");
		label_7.setText("");
		label_8.setText("");
		label_9.setText("");
		button_stop_transmission.setLabel("Stop Transmission");
	}

//========================================================================
	void createForm()
	{
		form=new Panel();
		add(form,BorderLayout.NORTH);

		form.setLayout(new GridBagLayout());

		g.formUtility.addLastLabel(label_1, form);
		g.formUtility.addLastLabel(label_2, form);
		g.formUtility.addLastLabel(label_3, form);
		g.formUtility.addLastLabel(label_4, form);
		g.formUtility.addLastLabel(label_5, form);
		g.formUtility.addLastLabel(label_6, form);
		g.formUtility.addLastLabel(label_7, form);
		g.formUtility.addLastLabel(label_8, form);
		g.formUtility.addLastLabel(label_9, form);

		button_stop_transmission.setBackground(Colors.button_background);
		button_stop_transmission.setForeground(Colors.button_foreground);
		button_stop_transmission.setFont(g.fontLarge);
		add(button_stop_transmission,BorderLayout.SOUTH);
	}//end createForm

//=============================================================================
	void addActionListeners()
	{
		button_stop_transmission.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				g.stopTransmission();
			}
		});
	}//end addActionListeners
}//end class RunningCard
