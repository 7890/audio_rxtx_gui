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

import java.net.InetAddress;

//========================================================================
public class FrontCardReceive extends Panel
{
	static Main g;

	static Panel form;

	static CheckboxGroup 			audio_transmission_format_group=new CheckboxGroup();
	static Checkbox 			checkbox_format_16=new Checkbox("16 bit Integer", audio_transmission_format_group, true);
	static Checkbox 			checkbox_format_32=new Checkbox("32 bit Float", audio_transmission_format_group, false);

	static NumericTextFieldWithLimit 	text_output_channels= new NumericTextFieldWithLimit("",6,3);
	static NumericTextFieldWithLimit 	text_lport=new NumericTextFieldWithLimit("",6,5);

	static Button 				button_start_transmission=new Button("Start Transmission");

//========================================================================
	public FrontCardReceive() 
	{
		setBackground(Colors.form_background);
		setForeground(Colors.form_foreground);
		setLayout(new BorderLayout());

		createForm();
		addActionListeners();
	}

//========================================================================
	void setValues()
	{
		checkbox_format_16.setState(g.apir._16);
		checkbox_format_32.setState(!g.apir._16);
		text_output_channels.setText(""+g.apir._out);
		text_lport.setText(""+g.apir._lport);
	}

//========================================================================
	void createForm()
	{
		form=new Panel();
		add(form,BorderLayout.NORTH);

		form.setLayout(new GridBagLayout());

		g.formUtility.addLabel("Audio Format: ", form);
		g.formUtility.addLastField(checkbox_format_16, form);
		g.formUtility.addLabel("", form);
		g.formUtility.addLastField(checkbox_format_32, form);

		g.formUtility.addLabel("Channels: ", form);
		g.formUtility.addMiddleField(text_output_channels, form);
		g.formUtility.addLastLabel("", form);

		g.formUtility.addTitle("Receive On", form);

		g.formUtility.addLabel("Port (UDP): ", form);
		g.formUtility.addMiddleField(text_lport, form);
		g.formUtility.addLastLabel("", form);

		button_start_transmission.setBackground(Colors.button_background);
		button_start_transmission.setForeground(Colors.button_foreground);
		button_start_transmission.setFont(g.fontLarge);
		add(button_start_transmission,BorderLayout.SOUTH);
	}//end createForm

//=============================================================================
	static boolean readForm()
	{
		if(text_output_channels.getText().equals(""))
		{
			text_output_channels.setText(""+g.apir._out);
		}

		if(text_lport.getText().equals(""))
		{
			text_lport.setText(""+g.apir._lport);
		}

		g.apir._16=checkbox_format_16.getState();
		g.apir._out=Integer.parseInt(text_output_channels.getText());
		g.apir._lport=Integer.parseInt(text_lport.getText());

		//boolean formValid=true;
		//return formValid;
		return true;
	}

//========================================================================
	void addActionListeners()
	{
		button_start_transmission.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(readForm())
				{
					///////////////////////////////
					//g.startTransmission();

System.out.println("***** "+g.apir.getCommandLineString());
//g.runningReceive.setVisible(true);
g.cardLayReceive.show(g.cardPanelReceive, "2");
				}
			}
		});
	}//end addActionListeners
}//end class FrontCardReceive
