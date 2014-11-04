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
public class FrontCardSend extends Card
{
	static CheckboxGroup 			audio_transmission_format_group=new CheckboxGroup();
	static Checkbox 			checkbox_format_16=new Checkbox("16 bit Integer", audio_transmission_format_group, true);
	static Checkbox 			checkbox_format_32=new Checkbox("32 bit Float", audio_transmission_format_group, false);

	static NumericTextFieldWithLimit 	text_input_channels= new NumericTextFieldWithLimit("",6,3);
	static HostTextFieldWithLimit 		text_target_host=new HostTextFieldWithLimit("",20,128);
	static NumericTextFieldWithLimit 	text_target_port=new NumericTextFieldWithLimit("",6,5);

//========================================================================
	public FrontCardSend() 
	{
		button_default.setLabel("Start Transmission");
	}

//========================================================================
	public void setValues()
	{
		checkbox_format_16.setState(g.apis._16);
		checkbox_format_32.setState(!g.apis._16);
		text_input_channels.setText(""+g.apis._in);
		text_target_host.setText(g.apis._target_host);
		text_target_port.setText(""+g.apis._target_port);
	}

//========================================================================
	public void createForm()
	{
		super.createForm();

		form.setLayout(new GridBagLayout());

		g.formUtility.addLabel("Audio Format: ", form);
		g.formUtility.addLastField(checkbox_format_16, form);
		g.formUtility.addLabel("", form);
		g.formUtility.addLastField(checkbox_format_32, form);

		g.formUtility.addLabel("Channels: ", form);
		g.formUtility.addMiddleField(text_input_channels, form);
		g.formUtility.addLastLabel("", form);

		g.formUtility.addTitle("Send To (Receiver)", form);

		g.formUtility.addLabel("IP / Hostname: ", form);
		g.formUtility.addLastField(text_target_host, form);

		g.formUtility.addLabel("Port (UDP): ", form);
		g.formUtility.addMiddleField(text_target_port, form);
		g.formUtility.addLastLabel("", form);
	}//end createForm

//=============================================================================
	public boolean readForm()
	{
		if(text_input_channels.getText().equals(""))
		{
			text_input_channels.setText(""+g.apis._in);
		}

		if(text_target_host.getText().equals(""))
		{
			text_target_host.setText(""+g.apis._target_host);
		}

		if(text_target_port.getText().equals(""))
		{
			text_target_port.setText(""+g.apis._target_port);
		}

		g.apis._16=checkbox_format_16.getState();
		g.apis._in=Integer.parseInt(text_input_channels.getText());
		g.apis._target_host=text_target_host.getText();
		g.apis._target_port=Integer.parseInt(text_target_port.getText());

		boolean formValid=true;

		setStatus("Looking Up Hostname...");

		try
		{
			InetAddress host=InetAddress.getByName(g.apis._target_host);
		}
		catch(Exception hostEx)
		{
			//System.out.println("/!\\ host '"+g.apis._target_host+"' not found.");
			formValid=false;

			setStatus("Host Is Invalid Or Was Not Found");
			text_target_host.requestFocus();
		}
		return formValid;
	}

//========================================================================
	public void defaultAction()
	{
		if(readForm())
		{
			g.startTransmissionSend();
		}
	}

//========================================================================
	public void setLabel(int i, String s)
	{
	}

//========================================================================
	public void focusFirstInputWidget()
	{
		checkbox_format_16.requestFocus();
	}

//========================================================================
/*
	public void addActionListeners()
	{
		super.addActionListeners();
	}//end addActionListeners
*/
}//end class FrontCardSend
