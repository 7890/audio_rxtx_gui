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
public class FrontCardReceive extends Card
{
	static CheckboxGroup 			audio_transmission_format_group=new CheckboxGroup();
	static Checkbox 			checkbox_format_16=new Checkbox("16 bit Integer", audio_transmission_format_group, true);
	static Checkbox 			checkbox_format_32=new Checkbox("32 bit Float", audio_transmission_format_group, false);

	static NumericTextFieldWithLimit 	text_output_channels= new NumericTextFieldWithLimit("",6,3);
	static NumericTextFieldWithLimit 	text_lport=new NumericTextFieldWithLimit("",6,5);

//========================================================================
	public FrontCardReceive()
	{
		button_default.setLabel("Start Transmission");
	}

//========================================================================
	public void setValues()
	{
		checkbox_format_16.setState(g.apir._16);
		checkbox_format_32.setState(!g.apir._16);
		text_output_channels.setText(""+g.apir._out);
		text_lport.setText(""+g.apir._lport);
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
		g.formUtility.addMiddleField(text_output_channels, form);
		g.formUtility.addLastLabel("", form);

		g.formUtility.addTitle("Receive On", form);

		g.formUtility.addLabel("Port (UDP): ", form);
		g.formUtility.addMiddleField(text_lport, form);
		g.formUtility.addLastLabel("", form);
	}//end createForm

//=============================================================================
	public boolean readForm()
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
	public void defaultAction()
	{
		if(readForm())
		{
			g.startTransmissionReceive();
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
	}//end addActionListeners
*/
}//end class FrontCardReceive
