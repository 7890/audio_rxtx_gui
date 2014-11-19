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
	static ACheckbox 			checkbox_format_16=new ACheckbox("16 bit Integer", audio_transmission_format_group, true);
	static ACheckbox 			checkbox_format_32=new ACheckbox("32 bit Float", audio_transmission_format_group, false);

	static NumericTextFieldWithLimit 	text_output_channels= new NumericTextFieldWithLimit("",20,3);
	static NumericTextFieldWithLimit 	text_lport=new NumericTextFieldWithLimit("",20,5);

//========================================================================
	public FrontCardReceive()
	{
		button_default.setLabel("Start Transmission");
		text_output_channels.setMinInclusive(1);
		text_output_channels.setMaxInclusive(512);
		text_lport.setMinInclusive(1024);
		text_lport.setMaxInclusive(65535);
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
		g.formUtility.addLastField(text_output_channels, form);

		g.formUtility.addTitle("Receive On", form);

		g.formUtility.addLabel("Port (UDP): ", form);
		g.formUtility.addLastField(text_lport, form);

	}//end createForm

//=============================================================================
	public boolean readForm()
	{
		FormHelper.validate(form);

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
