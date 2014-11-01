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
public class ConfigureDialog extends Dialog
{
	static Main g;

	static Panel form;
	static HostTextFieldWithLimit		text_name=new HostTextFieldWithLimit("",32,32);
	static HostTextFieldWithLimit		text_sname=new HostTextFieldWithLimit("",32,32);
	static Checkbox 			checkbox_connect=new Checkbox("Autoconnect");
	static Checkbox 			checkbox_nopause=new Checkbox("No Pause On Sender Deny");
	static Checkbox 			checkbox_test=new Checkbox("Enable Testmode");
	static NumericTextFieldWithLimit 	text_limit=new NumericTextFieldWithLimit("",32,24);
	static NumericTextFieldWithLimit 	text_drop=new NumericTextFieldWithLimit("",32,24);
	static Checkbox 			checkbox_verbose=new Checkbox("Verbose Shell Output");
	static NumericTextFieldWithLimit 	text_update=new NumericTextFieldWithLimit("",32,4);
	static Checkbox 			checkbox_lport_random=new Checkbox("Use Random Port");
	static NumericTextFieldWithLimit 	text_lport=new NumericTextFieldWithLimit("",32,5);
	static Checkbox 			checkbox_gui_osc_port_random=new Checkbox("Use Random Port");
	static NumericTextFieldWithLimit 	text_gui_osc_port=new NumericTextFieldWithLimit("",32,5);
	static Checkbox 			checkbox_keep_cache=new Checkbox("Use Cache");
	static Button 				button_cancel_settings=new Button("Cancel");
	static Button 				button_confirm_settings=new Button("OK");

//========================================================================
	public ConfigureDialog(Frame f,String title, boolean modality) 
	{
		super(f,title,modality);

		setBackground(Colors.form_background);
		setForeground(Colors.form_foreground);
		setLayout(new BorderLayout());

		setIconImage(g.appIcon);

		createForm();
		addActionListeners();
		addWindowListeners();
	}//end constructor

//========================================================================
	void setValues()
	{
		text_name.setText(g.apis._name);
		text_sname.setText(g.apis._sname);
		checkbox_connect.setState(g.apis._connect);
		checkbox_nopause.setState(g.apis._nopause);
		checkbox_test.setState(g.apis.test_mode);
		text_limit.setText(""+g.apis._limit);
		text_drop.setText(""+g.apis._drop);
		checkbox_verbose.setState(g.apis.verbose);
		text_update.setText(""+g.apis._update);
		checkbox_lport_random.setState(g.apis.lport_random);
		text_lport.setText(""+g.apis._lport);

		checkbox_gui_osc_port_random.setState(g.gui_osc_port_random);
		text_gui_osc_port.setText(""+g.gui_osc_port);
		checkbox_keep_cache.setState(g.keep_cache);
	}//end setValues

//========================================================================
	void dialogCancelled()
	{
		//reset values to previous
		setValues();

		//close window, set status, bring main to front
		setVisible(false);
		g.mainframe.toFront();
		g.setStatus("Configuration Cancelled, Restored Values");
	}//end dialogCancelled

//========================================================================
	void dialogConfirmed()
	{
		//read form, store values
		readForm();

		//close window, set status, bring main to front
		setVisible(false);
		g.mainframe.toFront();
		g.setStatus("Configuration Confirmed, Using Values");
	}//end dialogConfirmed

//========================================================================
	void createForm()
	{
		form=new Panel();
		add(form,BorderLayout.NORTH);

		form.setLayout(new GridBagLayout());

		g.formUtility.addLabel("Connect To This JACK Server:", form);
		g.formUtility.addLastField(text_sname, form);

		g.formUtility.addLabel("Name Of JACK Client:", form);
		g.formUtility.addLastField(text_name, form);

		g.formUtility.addLabel("JACK system:* ports:", form);
		g.formUtility.addLabel("", form);
		g.formUtility.addLastField(checkbox_connect, form);

		g.formUtility.addLabel("For 1:n Broadcast Scenario:", form);
		g.formUtility.addLabel("", form);
		g.formUtility.addLastField(checkbox_nopause, form);

		g.formUtility.addLabel("Limit Totally Sent Messages:", form);
		g.formUtility.addLabel("", form);
		g.formUtility.addLastField(checkbox_test, form);

		g.formUtility.addLabel("Message Count Limit:", form);
		g.formUtility.addLastField(text_limit, form);

		g.formUtility.addLabel("Drop Every Nth Message:", form);
		g.formUtility.addLastField(text_drop, form);

		g.formUtility.addLabel("jack_audio_send std Passthrough:", form);
		g.formUtility.addLabel("", form);
		g.formUtility.addLastField(checkbox_verbose, form);

		g.formUtility.addLabel("Status Update Interval:", form);
		g.formUtility.addLastField(text_update, form);

		g.formUtility.addLabel("UDP Port For jack_audio_send:", form);
		g.formUtility.addLabel("", form);
		g.formUtility.addLastField(checkbox_lport_random, form);

		g.formUtility.addLabel("Fixed Port (If Not Random):", form);
		g.formUtility.addLastField(text_lport, form);

		g.formUtility.addSpacer(form);

		g.formUtility.addLabel("UDP Port For GUI:", form);
		g.formUtility.addLabel("", form);
		g.formUtility.addLastField(checkbox_gui_osc_port_random, form);

		g.formUtility.addLabel("Fixed Port (If Not Random):", form);
		g.formUtility.addLastField(text_gui_osc_port, form);

		g.formUtility.addLabel("Keep Dumped Resources In Cache:", form);
		g.formUtility.addLabel("", form);
		g.formUtility.addLastField(checkbox_keep_cache, form);

		g.formUtility.addSpacer(form);

		Panel button_panel=new Panel();
		button_cancel_settings=new Button("Cancel");
		button_confirm_settings=new Button("OK");
		button_panel.setLayout(new GridLayout(1,2)); //y, x
		button_panel.add(button_cancel_settings);
		button_panel.add(button_confirm_settings);

		g.formUtility.addButtons(button_panel, form, g.fontLarge);

		pack();

		//center on screen
		setLocation(
			(int)((g.screenDimension.getWidth()-getWidth()) / 2),
			(int)((g.screenDimension.getHeight()-getHeight()) / 2)
		);

		setResizable(false);

		//done in calling object
		//setVisible(true);
	}//end createForm

//=============================================================================
	static void readForm()
	{
		if(text_name.getText().equals(""))
		{
			text_name.setText(""+g.apis._name);
		}

		if(text_sname.getText().equals(""))
		{
			text_sname.setText(""+g.apis._sname);
		}

		if(text_limit.getText().equals(""))
		{
			text_limit.setText(""+g.apis._limit);
		}

		if(text_drop.getText().equals(""))
		{
			text_drop.setText(""+g.apis._drop);
		}

		if(text_update.getText().equals(""))
		{
			text_update.setText(""+g.apis._update);
		}

		if(text_lport.getText().equals(""))
		{
			text_lport.setText(""+g.apis._lport);
		}

		if(text_gui_osc_port.getText().equals(""))
		{
			text_gui_osc_port.setText(""+g.gui_osc_port);
		}

		g.apis._name=text_name.getText();
		g.apis._sname=text_sname.getText();
		g.apis._connect=checkbox_connect.getState();
		g.apis._nopause=checkbox_nopause.getState();
		g.apis.test_mode=checkbox_test.getState();
		g.apis._limit=Integer.parseInt(text_limit.getText()); //
		g.apis._drop=Integer.parseInt(text_drop.getText()); //
		g.apis.verbose=checkbox_verbose.getState();
		g.apis._update=Integer.parseInt(text_update.getText()); //
		g.apis.lport_random=checkbox_lport_random.getState();
		g.apis._lport=Integer.parseInt(text_lport.getText()); //

		g.gui_osc_port_random=checkbox_gui_osc_port_random.getState();
		g.gui_osc_port=Integer.parseInt(text_gui_osc_port.getText()); //
		g.keep_cache=checkbox_keep_cache.getState();
	}//end readForm

//========================================================================
	void addActionListeners()
	{
		button_cancel_settings.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dialogCancelled();
			}
		});

		button_confirm_settings.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dialogConfirmed();
			}
		});
	}//end addActionListeners

//========================================================================
	void addWindowListeners()
	{
		addWindowListener(new WindowListener()
		{
			public void windowClosed(WindowEvent arg0) {dialogCancelled();}
			public void windowClosing(WindowEvent arg0) {dialogCancelled();}
			public void windowActivated(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});
	}//end addWindowListeners
}//end class ConfigureDialog
