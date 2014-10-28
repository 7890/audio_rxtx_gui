package ch.lowres.audio_rxtx.gui;

import java.awt.*;
import java.awt.event.*;

import java.net.InetAddress;

public class FrontCard extends Panel
{
	static jack_audio_send_GUI g;
	static jack_audio_send_cmdline_API api;

	static Panel form;

	static CheckboxGroup 			audio_transmission_format_group=new CheckboxGroup();
	static Checkbox 			checkbox_format_16=new Checkbox("16 bit Integer", audio_transmission_format_group, true);
	static Checkbox 			checkbox_format_32=new Checkbox("32 bit Float", audio_transmission_format_group, false);

	static NumericTextFieldWithLimit 	text_input_channels= new NumericTextFieldWithLimit("",6,3);
	static NumericTextFieldWithLimit 	text_lport=new NumericTextFieldWithLimit("",6,5);
	static HostTextFieldWithLimit 		text_target_host=new HostTextFieldWithLimit("",20,128);
	static NumericTextFieldWithLimit 	text_target_port=new NumericTextFieldWithLimit("",6,5);

	static Button 				button_start_transmission	=new Button("Start Transmission");

//========================================================================
	public FrontCard() 
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
		checkbox_format_16.setState(api._16);
		checkbox_format_32.setState(!api._16);
		text_input_channels.setText(""+api._in);
		text_lport.setText(""+api._lport);
		text_target_host.setText(api._target_host);
		text_target_port.setText(""+api._target_port);
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
		g.formUtility.addMiddleField(text_input_channels, form);
		g.formUtility.addLastLabel("", form);

		g.formUtility.addTitle("Send To (Receiver)", form);

		g.formUtility.addLabel("IP / Hostname: ", form);
		g.formUtility.addLastField(text_target_host, form);

		g.formUtility.addLabel("Port (UDP): ", form);
		g.formUtility.addMiddleField(text_target_port, form);
		g.formUtility.addLastLabel("", form);

		//g.formUtility.addFullButton(button_start_transmission, form, g.fontLarge);
		button_start_transmission.setBackground(Colors.button_background);
		button_start_transmission.setForeground(Colors.button_foreground);
		button_start_transmission.setFont(g.fontLarge);
		add(button_start_transmission,BorderLayout.SOUTH);

	}//end createForm

//=============================================================================
	static boolean readForm()
	{
		if(text_input_channels.getText().equals(""))
		{
			text_input_channels.setText(""+api._in);
		}

		if(text_target_host.getText().equals(""))
		{
			text_target_host.setText(""+api._target_host);
		}

		if(text_target_port.getText().equals(""))
		{
			text_target_port.setText(""+api._target_port);
		}

		api._16=checkbox_format_16.getState();
		api._in=Integer.parseInt(text_input_channels.getText());
		api._target_host=text_target_host.getText();
		api._target_port=Integer.parseInt(text_target_port.getText());

		boolean formValid=true;

		g.setStatus("Looking Up Hostname...");

		try
		{
			InetAddress host=InetAddress.getByName(api._target_host);
		}
		catch(Exception hostEx)
		{
			//System.out.println("/!\\ host '"+api._target_host+"' not found.");
			formValid=false;

			g.setStatus("Host Is Invalid Or Was Not Found");
			text_target_host.requestFocus();
		}

		return formValid;

	}

//========================================================================
	void addActionListeners()
	{
		button_start_transmission.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				if(readForm())
				{
					g.startTransmission();
				}
			}
		});
	}//end addActionListeners
}//end class FrontCard
