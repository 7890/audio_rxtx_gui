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
import ch.lowres.audio_rxtx.gui.widgets.*;
import ch.lowres.audio_rxtx.gui.helpers.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.net.InetAddress;

/**
* Extended {@link Card}, showing send form.
*/
//========================================================================
public class FrontCardSend extends Card
{
	static ButtonGroup 			audio_transmission_format_group=new ButtonGroup();

	static ARadioButton 			checkbox_format_16=new ARadioButton("16 bit Integer");
	static ARadioButton 			checkbox_format_32=new ARadioButton("32 bit Float");

	static NumericTextFieldWithLimit 	text_input_channels= new NumericTextFieldWithLimit("",20,3);
	static HistorifiedHostTextFieldWithLimit text_target_host=new HistorifiedHostTextFieldWithLimit("",20,128);
	static NumericTextFieldWithLimit 	text_target_port=new NumericTextFieldWithLimit("",20,5);

//========================================================================
	public FrontCardSend() 
	{
	}

//========================================================================
	public void setValues()
	{
		checkbox_format_16.setState(m.apis._16);
		checkbox_format_32.setState(!m.apis._16);
		text_input_channels.setText(""+m.apis._in);
		text_target_host.setText(m.apis._target_host);
		//set as first valid item in history
		text_target_host.addHistoricItem(m.apis._target_host);
		text_target_port.setText(""+m.apis._target_port);
	}

//========================================================================
	public void createForm()
	{
		super.createForm();

		//limits
		text_input_channels.setMinInclusive(1);
		text_input_channels.setMaxInclusive(512);
		text_target_port.setMinInclusive(1024);
		text_target_port.setMaxInclusive(65535);	

		button_default.setLabel(m.tr("Start Transmission"));

		form.setLayout(new GridBagLayout());

		audio_transmission_format_group.add(checkbox_format_16);
		audio_transmission_format_group.add(checkbox_format_32);

		m.formUtility.addLabel(m.tr("Audio Format")+": ", form);
		m.formUtility.addLastField(checkbox_format_16, form);
		m.formUtility.addLabel("", form);
		m.formUtility.addLastField(checkbox_format_32, form);

		m.formUtility.addLabel(m.tr("Channels")+": ", form);
		m.formUtility.addLastField(text_input_channels, form);

		m.formUtility.addTitle(m.tr("Send To (Receiver)"), form);

		m.formUtility.addLabel(m.tr("IP / Hostname")+": ", form);
		m.formUtility.addLastField(text_target_host, form);

		m.formUtility.addLabel(m.tr("Port (UDP)")+": ", form);
		m.formUtility.addLastField(text_target_port, form);
	}//end createForm

//=============================================================================
	public boolean readForm()
	{

		FormHelper.validate(form);

		if(text_input_channels.getText().equals(""))
		{
			text_input_channels.setText(""+m.apis._in);
		}

		if(text_target_host.getText().equals(""))
		{
			text_target_host.setTextLast();
		}

		if(text_target_port.getText().equals(""))
		{
			text_target_port.setText(""+m.apis._target_port);
		}

		m.apis._16=checkbox_format_16.getState();
		m.apis._in=Integer.parseInt(text_input_channels.getText());
		m.apis._target_host=text_target_host.getText();
		m.apis._target_port=Integer.parseInt(text_target_port.getText());

		boolean formValid=true;

		setStatus("Looking Up Hostname...");

		try
		{
			InetAddress host=InetAddress.getByName(m.apis._target_host);
			//timeout (ms) -> blocks
/*
			if(host.isReachable(50))
			{
				setStatus(m.tr("Host is valid"));
			}
			else
			{
				throw new Exception("");
			}
*/
		}
		catch(Exception hostEx)
		{
			//System.out.println("/!\\ host '"+m.apis._target_host+"' not found.");
			formValid=false;

			setStatusError(m.tr("Host invalid or not found"));
			text_target_host.requestFocus();
			text_target_host.setAfterLast();
		}
		if(formValid)
		{
			text_target_host.addHistoricItem(m.apis._target_host);
		}

		return formValid;
	}

//========================================================================
	public void defaultAction()
	{
		if(readForm())
		{
			m.startTransmissionSend();
		}
	}

//========================================================================
	public void setLabel(int i, String s) {}

//========================================================================
	public void focusFirstInputWidget()
	{
		checkbox_format_16.requestFocus();
	}
}//end class FrontCardSend
