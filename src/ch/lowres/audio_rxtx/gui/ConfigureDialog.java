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

import com.magelang.splitter.*;
import com.magelang.tabsplitter.*;

//========================================================================
public class ConfigureDialog extends Dialog implements TabSelectionListener
{
	static Main g;

	static Panel formSend;
	static HostTextFieldWithLimit		text_name_s=new HostTextFieldWithLimit("",32,32);
	static HostTextFieldWithLimit		text_sname_s=new HostTextFieldWithLimit("",32,32);
	static Checkbox 			checkbox_connect_s=new Checkbox("Autoconnect");
	static Checkbox 			checkbox_nopause_s=new Checkbox("No Pause On Sender Deny");
	static Checkbox 			checkbox_test_s=new Checkbox("Enable Testmode");
	static NumericTextFieldWithLimit 	text_limit_s=new NumericTextFieldWithLimit("",32,24);
	static NumericTextFieldWithLimit 	text_drop_s=new NumericTextFieldWithLimit("",32,24);
	static Checkbox 			checkbox_verbose_s=new Checkbox("Verbose Shell Output");
	static NumericTextFieldWithLimit 	text_update_s=new NumericTextFieldWithLimit("",32,4);
	static Checkbox 			checkbox_lport_random_s=new Checkbox("Use Random Port");
	static NumericTextFieldWithLimit 	text_lport_s=new NumericTextFieldWithLimit("",32,5);

	static Panel formReceive;
	static HostTextFieldWithLimit		text_name_r=new HostTextFieldWithLimit("",32,32);
	static HostTextFieldWithLimit		text_sname_r=new HostTextFieldWithLimit("",32,32);
	static Checkbox 			checkbox_connect_r=new Checkbox("Autoconnect");
	static Checkbox 			checkbox_test_r=new Checkbox("Enable Testmode");
	static NumericTextFieldWithLimit 	text_limit_r=new NumericTextFieldWithLimit("",32,24);
	static Checkbox 			checkbox_verbose_r=new Checkbox("Verbose Shell Output");
	static NumericTextFieldWithLimit 	text_update_r=new NumericTextFieldWithLimit("",32,4);
	static NumericTextFieldWithLimit 	text_offset_r=new NumericTextFieldWithLimit("",32,24);
	static NumericTextFieldWithLimit 	text_pre_r=new NumericTextFieldWithLimit("",32,24);
	static NumericTextFieldWithLimit 	text_max_r=new NumericTextFieldWithLimit("",32,24);
	static Checkbox 			checkbox_rere_r=new Checkbox(" Rebuffer On Sender Restart");
	static Checkbox 			checkbox_reuf_r=new Checkbox("Rebuffer On Underflow");
	static Checkbox 			checkbox_nozero_r=new Checkbox("Re-Use Old Data On Underflow");
	static Checkbox 			checkbox_norbc_r=new Checkbox("Disallow Ext. Buffer Control");
	static Checkbox 			checkbox_close_r=new Checkbox("Stop Transmission On Incompat.");

//	static Checkbox 			checkbox_lport_random_r=new Checkbox("Use Random Port");
//	static NumericTextFieldWithLimit 	text_lport_r=new NumericTextFieldWithLimit("",32,5);

	static Panel formGUI;
	static Checkbox 			checkbox_gui_osc_port_random=new Checkbox("Use Random Port");
	static NumericTextFieldWithLimit 	text_gui_osc_port=new NumericTextFieldWithLimit("",32,5);
	static Checkbox 			checkbox_gui_osc_port_random_r=new Checkbox("Use Random Port");
	static NumericTextFieldWithLimit 	text_gui_osc_port_r=new NumericTextFieldWithLimit("",32,5);
	static Checkbox 			checkbox_keep_cache=new Checkbox("Use Cache");

	static Button 				button_cancel_settings=new Button("Cancel");
	static Button 				button_confirm_settings=new Button("OK");

	static ScrollPane scroller;

	//tabs for send / receive
	static TabNamePanel tabSend;
	static TabNamePanel tabReceive;
	static TabNamePanel tabGUI;

	static TabPanel tabPanel;

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
		text_name_s.setText(g.apis._name);
		text_sname_s.setText(g.apis._sname);
		checkbox_connect_s.setState(g.apis._connect);
		checkbox_nopause_s.setState(g.apis._nopause);
		checkbox_test_s.setState(g.apis.test_mode);
		text_limit_s.setText(""+g.apis._limit);
		text_drop_s.setText(""+g.apis._drop);
		checkbox_verbose_s.setState(g.apis.verbose);
		text_update_s.setText(""+g.apis._update);
		checkbox_lport_random_s.setState(g.apis.lport_random);
		text_lport_s.setText(""+g.apis._lport);

		text_name_r.setText(g.apir._name);
		text_sname_r.setText(g.apir._sname);
		checkbox_connect_r.setState(g.apir._connect);
		checkbox_test_r.setState(g.apir.test_mode);
		text_limit_r.setText(""+g.apir._limit);
		checkbox_verbose_r.setState(g.apir.verbose);
		text_update_r.setText(""+g.apir._update);
		text_offset_r.setText(""+g.apir._offset);
		text_pre_r.setText(""+g.apir._pre);
		text_max_r.setText(""+g.apir._max);
		checkbox_rere_r.setState(g.apir._rere);
		checkbox_reuf_r.setState(g.apir._reuf);
		checkbox_nozero_r.setState(g.apir._nozero);
		checkbox_norbc_r.setState(g.apir._norbc);
		checkbox_close_r.setState(g.apir._close);

//		checkbox_lport_random_r.setState(g.apir.lport_random);
//		text_lport_r.setText(""+g.apir._lport);

		checkbox_gui_osc_port_random.setState(g.gui_osc_port_random_s);
		text_gui_osc_port.setText(""+g.gui_osc_port_s);
		checkbox_gui_osc_port_random_r.setState(g.gui_osc_port_random_r);
		text_gui_osc_port_r.setText(""+g.gui_osc_port_r);

		checkbox_keep_cache.setState(g.keep_cache);
	}//end setValues

//========================================================================
	void dialogCancelled()
	{
		//reset values to previous
		setValues();
		setFocusedWidget();
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
		setFocusedWidget();
		//close window, set status, bring main to front
		setVisible(false);
		g.mainframe.toFront();
		g.setStatus("Configuration Confirmed, Using Values");
	}//end dialogConfirmed

//========================================================================
	void createForm()
	{
		formSend=new Panel();
		formReceive=new Panel();
		formGUI=new Panel();

		formSend.setLayout(new GridBagLayout());
		formReceive.setLayout(new GridBagLayout());
		formGUI.setLayout(new GridBagLayout());

		try {
			tabSend=new TabNamePanel();
			tabSend.setName("Send");
			tabSend.setLayout(new BorderLayout());
			tabSend.setTabName("Send");
			tabSend.add(formSend,BorderLayout.NORTH);

			tabReceive=new TabNamePanel();
			tabReceive.setName("Receive");
			tabReceive.setLayout(new BorderLayout());
			tabReceive.setTabName("Receive");
			tabReceive.add(formReceive,BorderLayout.NORTH);

			tabGUI=new TabNamePanel();
			tabGUI.setName("GUI");
			tabGUI.setLayout(new BorderLayout());
			tabGUI.setTabName("GUI");
			tabGUI.add(formGUI,BorderLayout.NORTH);

			tabPanel=new TabPanel();
			tabPanel.setName("TabPanel");
			tabPanel.add(tabSend, tabSend.getName());
			tabPanel.add(tabReceive, tabReceive.getName());
			tabPanel.add(tabGUI, tabGUI.getName());

			tabPanel.setBackground(Colors.form_background);
			tabPanel.setForeground(Colors.form_foreground);

			tabPanel.setTabColors(new java.awt.Color[] {new Color(50,50,50),new Color(50,50,50),new Color(50,50,50)});
			tabPanel.setTabColorsSelected(new java.awt.Color[] {Colors.form_background, Colors.form_background, Colors.form_background});

			tabPanel.addTabSelectionListener(this);
		} catch (java.lang.Throwable ex)
		{///
		}

		scroller=new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);

		Adjustable vadjust=scroller.getVAdjustable();
		Adjustable hadjust=scroller.getHAdjustable();
		hadjust.setUnitIncrement(10);
		vadjust.setUnitIncrement(10);

		scroller.add(tabPanel);

		add(scroller,BorderLayout.CENTER);
//send
		g.formUtility.addLabel("Connect To This JACK Server:", formSend);
		g.formUtility.addLastField(text_sname_s, formSend);

		g.formUtility.addLabel("Name Of JACK Client:", formSend);
		g.formUtility.addLastField(text_name_s, formSend);

		g.formUtility.addLabel("JACK system:* ports:", formSend);
		g.formUtility.addLabel("", formSend);
		g.formUtility.addLastField(checkbox_connect_s, formSend);

		g.formUtility.addLabel("For 1:n Broadcast Scenario:", formSend);
		g.formUtility.addLabel("", formSend);
		g.formUtility.addLastField(checkbox_nopause_s, formSend);

		g.formUtility.addLabel("Limit Totally Sent Messages:", formSend);
		g.formUtility.addLabel("", formSend);
		g.formUtility.addLastField(checkbox_test_s, formSend);

		g.formUtility.addLabel("Message Count Limit:", formSend);
		g.formUtility.addLastField(text_limit_s, formSend);

		g.formUtility.addLabel("Drop Every Nth Message:", formSend);
		g.formUtility.addLastField(text_drop_s, formSend);

		g.formUtility.addLabel("jack_audio_send std Passthrough:", formSend);
		g.formUtility.addLabel("", formSend);
		g.formUtility.addLastField(checkbox_verbose_s, formSend);

		g.formUtility.addLabel("Status Update Interval:", formSend);
		g.formUtility.addLastField(text_update_s, formSend);

		g.formUtility.addLabel("UDP Port For jack_audio_send:", formSend);
		g.formUtility.addLabel("", formSend);
		g.formUtility.addLastField(checkbox_lport_random_s, formSend);

		g.formUtility.addLabel("Fixed Port (If Not Random):", formSend);
		g.formUtility.addLastField(text_lport_s, formSend);

//receive
		g.formUtility.addLabel("Connect To This JACK Server:", formReceive);
		g.formUtility.addLastField(text_sname_r, formReceive);

		g.formUtility.addLabel("Name Of JACK Client:", formReceive);
		g.formUtility.addLastField(text_name_r, formReceive);

		g.formUtility.addLabel("JACK system:* ports:", formReceive);
		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_connect_r, formReceive);

		g.formUtility.addLabel("Limit Totally Sent Messages:", formReceive);
		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_test_r, formReceive);

		g.formUtility.addLabel("Message Count Limit:", formReceive);
		g.formUtility.addLastField(text_limit_r, formReceive);

		g.formUtility.addLabel("jack_audio_receive std Passthrough:", formReceive);
		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_verbose_r, formReceive);

		g.formUtility.addLabel("Status Update Interval:", formReceive);
		g.formUtility.addLastField(text_update_r, formReceive);

		g.formUtility.addLabel("Channel Offset", formReceive);
		g.formUtility.addLastField(text_offset_r, formReceive);

		g.formUtility.addLabel("Initial Buffer Size (MCP)", formReceive);
		g.formUtility.addLastField(text_pre_r, formReceive);

		g.formUtility.addLabel("Max Buffer Size (>= Init)", formReceive);
		g.formUtility.addLastField(text_max_r, formReceive);

		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_rere_r, formReceive);

		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_reuf_r, formReceive);

		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_nozero_r, formReceive);

		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_norbc_r, formReceive);

		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_close_r, formReceive);

//GUI
		g.formUtility.addLabel("UDP Port For GUI:", formGUI);
		g.formUtility.addLabel("", formGUI);
		g.formUtility.addLastField(checkbox_gui_osc_port_random, formGUI);

		g.formUtility.addLabel("Fixed Port (If Not Random):", formGUI);
		g.formUtility.addLastField(text_gui_osc_port, formGUI);

		g.formUtility.addSpacer(formGUI);

		g.formUtility.addLabel("UDP Port For GUI:", formGUI);
		g.formUtility.addLabel("", formGUI);
		g.formUtility.addLastField(checkbox_gui_osc_port_random_r, formGUI);

		g.formUtility.addLabel("Fixed Port (If Not Random):", formGUI);
		g.formUtility.addLastField(text_gui_osc_port_r, formGUI);

		g.formUtility.addSpacer(formGUI);

		g.formUtility.addLabel("Keep Dumped Resources In Cache:", formGUI);
		g.formUtility.addLabel("", formGUI);
		g.formUtility.addLastField(checkbox_keep_cache, formGUI);

//buttons
		Panel button_panel=new Panel();
		button_cancel_settings=new Button("Cancel");
		button_confirm_settings=new Button("OK");

		button_cancel_settings.setBackground(Colors.button_background);
		button_cancel_settings.setForeground(Colors.button_foreground);
		button_cancel_settings.setFont(g.fontLarge);

		button_confirm_settings.setBackground(Colors.button_background);
		button_confirm_settings.setForeground(Colors.button_foreground);
		button_confirm_settings.setFont(g.fontLarge);

		button_panel.setLayout(new GridLayout(1,2)); //y, x
		button_panel.add(button_cancel_settings);
		button_panel.add(button_confirm_settings);

		add(button_panel,BorderLayout.SOUTH);

//		pack();
//		setSize(600,500);
		int panelWidth=600;
		int panelHeight=500;
		setSize(
			panelWidth+getInsets().left+getInsets().right,
			panelHeight+getInsets().top+getInsets().bottom
		);

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
		if(text_name_s.getText().equals(""))
		{
			text_name_s.setText(""+g.apis._name);
		}

		if(text_sname_s.getText().equals(""))
		{
			text_sname_s.setText(""+g.apis._sname);
		}

		if(text_limit_s.getText().equals(""))
		{
			text_limit_s.setText(""+g.apis._limit);
		}

		if(text_drop_s.getText().equals(""))
		{
			text_drop_s.setText(""+g.apis._drop);
		}

		if(text_update_s.getText().equals(""))
		{
			text_update_s.setText(""+g.apis._update);
		}

		if(text_lport_s.getText().equals(""))
		{
			text_lport_s.setText(""+g.apis._lport);
		}

		g.apis._name=text_name_s.getText();
		g.apis._sname=text_sname_s.getText();
		g.apis._connect=checkbox_connect_s.getState();
		g.apis._nopause=checkbox_nopause_s.getState();
		g.apis.test_mode=checkbox_test_s.getState();
		g.apis._limit=Integer.parseInt(text_limit_s.getText());
		g.apis._drop=Integer.parseInt(text_drop_s.getText());
		g.apis.verbose=checkbox_verbose_s.getState();
		g.apis._update=Integer.parseInt(text_update_s.getText());
		g.apis.lport_random=checkbox_lport_random_s.getState();
		g.apis._lport=Integer.parseInt(text_lport_s.getText());

		if(text_name_r.getText().equals(""))
		{
			text_name_r.setText(""+g.apir._name);
		}

		if(text_sname_r.getText().equals(""))
		{
			text_sname_r.setText(""+g.apir._sname);
		}

		if(text_limit_r.getText().equals(""))
		{
			text_limit_r.setText(""+g.apir._limit);
		}

		if(text_update_r.getText().equals(""))
		{
			text_update_r.setText(""+g.apir._update);
		}

		if(text_offset_r.getText().equals(""))
		{
			text_offset_r.setText(""+g.apir._offset);
		}

		if(text_pre_r.getText().equals(""))
		{
			text_pre_r.setText(""+g.apir._pre);
		}

		if(text_max_r.getText().equals(""))
		{
			text_max_r.setText(""+g.apir._pre);
		}

		g.apir._name=text_name_r.getText();
		g.apir._sname=text_sname_r.getText();
		g.apir._connect=checkbox_connect_r.getState();
		g.apir.test_mode=checkbox_test_r.getState();
		g.apir._limit=Integer.parseInt(text_limit_r.getText());
		g.apir.verbose=checkbox_verbose_r.getState();
		g.apir._update=Integer.parseInt(text_update_r.getText());
		g.apir._offset=Integer.parseInt(text_offset_r.getText());
		g.apir._pre=Integer.parseInt(text_pre_r.getText());
		g.apir._max=Integer.parseInt(text_max_r.getText());
		g.apir._rere=checkbox_rere_r.getState();
		g.apir._reuf=checkbox_reuf_r.getState();
		g.apir._nozero=checkbox_nozero_r.getState();
		g.apir._norbc=checkbox_norbc_r.getState();
		g.apir._close=checkbox_close_r.getState();

//		g.apir.lport_random=checkbox_lport_random_r.getState();
//		g.apir._lport=Integer.parseInt(text_lport_r.getText());

		if(text_gui_osc_port.getText().equals(""))
		{
			text_gui_osc_port.setText(""+g.gui_osc_port_s);
		}

		if(text_gui_osc_port_r.getText().equals(""))
		{
			text_gui_osc_port_r.setText(""+g.gui_osc_port_r);
		}

		g.gui_osc_port_random_s=checkbox_gui_osc_port_random.getState();
		g.gui_osc_port_s=Integer.parseInt(text_gui_osc_port.getText());

		g.gui_osc_port_random_r=checkbox_gui_osc_port_random_r.getState();
		g.gui_osc_port_r=Integer.parseInt(text_gui_osc_port_r.getText());

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

//========================================================================
	public void setFocusedWidget()
	{
		String tabname=tabPanel.getSelectedName();
		setFocusedWidget(tabname);
	}

//========================================================================
	public void setFocusedWidget(String tabname)
	{
		if(tabname.equals("Send"))
		{
			text_name_s.requestFocus();
		}
		else if(tabname.equals("Receive"))
		{
			text_name_r.requestFocus();
		}
		else if(tabname.equals("GUI"))
		{
			checkbox_gui_osc_port_random.requestFocus();
		}
	}

//========================================================================
	public void tabSelected(TabSelectionEvent e)
	{
		setFocusedWidget(e.getSelectedName());
	}
}//end class ConfigureDialog
