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
import javax.swing.event.*;

import java.util.Vector;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

import java.awt.geom.*;

import javax.swing.plaf.ColorUIResource;

/**
* Modal tabbed dialog to configure jack_audio_send, jack_audio_receive and GUI options.
*/
//========================================================================
public class ConfigureDialog extends JDialog implements ChangeListener
{
	private static Main g;

	private static JPanel formSend;
	private static HostTextFieldWithLimit		text_name_s=new HostTextFieldWithLimit("",32,32);
	private static HostTextFieldWithLimit		text_sname_s=new HostTextFieldWithLimit("",32,32);
	private static ACheckbox 			checkbox_connect_s=new ACheckbox("Autoconnect");
	private static ACheckbox 			checkbox_nopause_s=new ACheckbox("No Pause On Sender Deny");
	private static ACheckbox 			checkbox_test_s=new ACheckbox("Enable Testmode");
	private static NumericTextFieldWithLimit 	text_limit_s=new NumericTextFieldWithLimit("",32,24);
	private static NumericTextFieldWithLimit 	text_drop_s=new NumericTextFieldWithLimit("",32,24);
	private static ACheckbox 			checkbox_verbose_s=new ACheckbox("Verbose Shell Output");
	private static NumericTextFieldWithLimit 	text_update_s=new NumericTextFieldWithLimit("",32,4);
	private static ACheckbox 			checkbox_lport_random_s=new ACheckbox("Use Random Port");
	private static NumericTextFieldWithLimit 	text_lport_s=new NumericTextFieldWithLimit("",32,5);
	private static ACheckbox 			checkbox_autostart_s=new ACheckbox("Autostart Transmission");

	private static JPanel formReceive;
	private static HostTextFieldWithLimit		text_name_r=new HostTextFieldWithLimit("",32,32);
	private static HostTextFieldWithLimit		text_sname_r=new HostTextFieldWithLimit("",32,32);
	private static ACheckbox 			checkbox_connect_r=new ACheckbox("Autoconnect");
	private static ACheckbox 			checkbox_test_r=new ACheckbox("Enable Testmode");
	private static NumericTextFieldWithLimit 	text_limit_r=new NumericTextFieldWithLimit("",32,24);
	private static ACheckbox 			checkbox_verbose_r=new ACheckbox("Verbose Shell Output");
	private static NumericTextFieldWithLimit 	text_update_r=new NumericTextFieldWithLimit("",32,4);
	private static NumericTextFieldWithLimit 	text_offset_r=new NumericTextFieldWithLimit("",32,24);
	private static NumericTextFieldWithLimit 	text_pre_r=new NumericTextFieldWithLimit("",32,24);
	private static NumericTextFieldWithLimit 	text_max_r=new NumericTextFieldWithLimit("",32,24);
	private static ACheckbox 			checkbox_rere_r=new ACheckbox("Rebuffer On Sender Restart");
	private static ACheckbox 			checkbox_reuf_r=new ACheckbox("Rebuffer On Underflow");
	private static ACheckbox 			checkbox_nozero_r=new ACheckbox("Re-Use Old Data On Underflow");
	private static ACheckbox 			checkbox_norbc_r=new ACheckbox("Disallow Ext. Buffer Control");
	private static ACheckbox 			checkbox_close_r=new ACheckbox("Stop Transmission On Incompat.");
	private static ACheckbox 			checkbox_autostart_r=new ACheckbox("Autostart Transmission");

	private static JPanel formGUI;
	private static ACheckbox 			checkbox_gui_osc_port_random=new ACheckbox("Use Random Port");
	private static NumericTextFieldWithLimit 	text_gui_osc_port_s=new NumericTextFieldWithLimit("",32,5);
	private static ACheckbox 			checkbox_gui_osc_port_random_r=new ACheckbox("Use Random Port");
	private static NumericTextFieldWithLimit 	text_gui_osc_port_r=new NumericTextFieldWithLimit("",32,5);
	private static ACheckbox 			checkbox_keep_cache=new ACheckbox("Use Cache");
	private static ACheckbox 			checkbox_both_panels=new ACheckbox("Show Both Panels On Start");

	private static AButton 				button_cancel_settings=new AButton("Cancel");
	private static AButton 				button_confirm_settings=new AButton("OK");

	private static JScrollPane scrollerTabSend;
	private static JScrollPane scrollerTabReceive;
	private static JScrollPane scrollerTabGUI;

	private static JScrollBar scrollbarSend;
	private static JScrollBar scrollbarReceive;
	private static JScrollBar scrollbarGUI;

	private static APanel tabSend;
	private static APanel tabReceive;
	private static APanel tabGUI;

	private static JTabbedPane tabPanel = new JTabbedPane()
	{
        	@Override
		public void paintComponent(Graphics g) 
		{
			//FocusPaint.gradient(g,tabPanel);
			super.paintComponent(g);
			FocusPaint.paint(g,tabPanel);
		}        
	};

//========================================================================
	public ConfigureDialog(Frame f,String title, boolean modality)
	{
		super(f,title,modality);

		text_limit_s.setMinInclusive(1);
		text_drop_s.setMinInclusive(0);
		text_update_s.setMinInclusive(1);
		text_lport_s.setMinInclusive(1024);
		text_lport_s.setMaxInclusive(65535);

		text_limit_r.setMinInclusive(1);
		text_update_r.setMinInclusive(1);

		text_offset_r.setMinInclusive(0);
		text_pre_r.setMinInclusive(0);
		//! max >= pre
		text_max_r.setMinInclusive(10);

		text_gui_osc_port_s.setMinInclusive(1024);
		text_gui_osc_port_s.setMaxInclusive(65535);

		text_gui_osc_port_r.setMinInclusive(1024);
		text_gui_osc_port_r.setMaxInclusive(65535);

		setBackground(Colors.form_background);
		setForeground(Colors.form_foreground);
		setLayout(new BorderLayout());

		setIconImage(g.appIcon);

		createForm();
		addActionListeners();
		addWindowListeners();
	}//end constructor

//========================================================================
	public void setValues()
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
		checkbox_autostart_s.setState(g.apis.autostart);

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
		checkbox_autostart_r.setState(g.apir.autostart);

		checkbox_gui_osc_port_random.setState(g.gui_osc_port_random_s);
		text_gui_osc_port_s.setText(""+g.gui_osc_port_s);
		checkbox_gui_osc_port_random_r.setState(g.gui_osc_port_random_r);
		text_gui_osc_port_r.setText(""+g.gui_osc_port_r);

		checkbox_keep_cache.setState(g.keep_cache);
		checkbox_both_panels.setState(g.show_both_panels);
	}//end setValues

//========================================================================
	public void dialogCancelled()
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
	public void dialogConfirmed()
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
	private void createForm()
	{

//http://stackoverflow.com/questions/2120728/controlling-color-in-java-tabbed-pane
tabPanel.setOpaque(true);

UIManager.put("TabbedPane.selected", Colors.form_background);
UIManager.put("TabbedPane.background",Colors.form_background.brighter().brighter().brighter());
UIManager.put("TabbedPane.selectedForeground",Colors.form_foreground);

/*
TabbedPane.actionMap	ActionMap
TabbedPane.ancestorInputMap	InputMap
TabbedPane.background	Color
TabbedPane.borderHightlightColor	Color
TabbedPane.contentAreaColor	Color
TabbedPane.contentBorderInsets	Insets
TabbedPane.contentOpaque	Boolean
TabbedPane.darkShadow	Color
TabbedPane.focus	Color
TabbedPane.focusInputMap	InputMap
TabbedPane.font	Font
TabbedPane.foreground	Color
TabbedPane.highlight	Color
TabbedPane.light	Color
TabbedPane.opaque	Boolean
TabbedPane.selected	Color
TabbedPane.selectedForeground	Color
TabbedPane.selectedTabPadInsets	Insets
TabbedPane.selectHighlight	Color
TabbedPane.selectionFollowsFocus	Boolean
TabbedPane.shadow	Color
TabbedPane.tabAreaBackground	Color
TabbedPane.tabAreaInsets	Insets
TabbedPane.tabInsets	Insets
TabbedPane.tabRunOverlay	Integer
TabbedPane.tabsOpaque	Boolean
TabbedPane.tabsOverlapBorder	Boolean
TabbedPane.textIconGap	Integer
TabbedPane.unselectedBackground	Color
TabbedPane.unselectedTabBackground	Color
TabbedPane.unselectedTabForeground	Color
TabbedPane.unselectedTabHighlight	Color
TabbedPane.unselectedTabShadow	Color
TabbedPaneUI	String
*/

		formSend=new JPanel();
		formReceive=new JPanel();
		formGUI=new JPanel();

		formSend.setLayout(new GridBagLayout());
		formReceive.setLayout(new GridBagLayout());
		formGUI.setLayout(new GridBagLayout());

		formSend.setBackground(Colors.form_background);
		formReceive.setBackground(Colors.form_background);
		formGUI.setBackground(Colors.form_background);

		formSend.setOpaque(false);
		formReceive.setOpaque(false);
		formGUI.setOpaque(false);

		tabSend=new APanel(new BorderLayout());
		tabSend.setBackground(Colors.form_background);
		tabSend.add(formSend,BorderLayout.NORTH);

		scrollerTabSend=new JScrollPane (tabSend, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabSend.getViewport().setBackground(Colors.form_background);
		scrollerTabSend.setWheelScrollingEnabled(true);

		scrollbarSend=scrollerTabSend.getVerticalScrollBar();
		scrollbarSend.setUnitIncrement(g.scrollbarIncrement);

		tabReceive=new APanel(new BorderLayout());
		tabReceive.setBackground(Colors.form_background);
		tabReceive.add(formReceive,BorderLayout.NORTH);
		scrollerTabReceive=new JScrollPane (tabReceive, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabReceive.getViewport().setBackground(Colors.form_background);
		scrollerTabReceive.setWheelScrollingEnabled(true);

		scrollbarReceive=scrollerTabReceive.getVerticalScrollBar();
		scrollbarReceive.setUnitIncrement(g.scrollbarIncrement);

		tabGUI=new APanel(new BorderLayout());
		tabGUI.setBackground(Colors.form_background);
		tabGUI.add(formGUI,BorderLayout.NORTH);
		scrollerTabGUI=new JScrollPane (tabGUI, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabGUI.getViewport().setBackground(Colors.form_background);
		scrollerTabGUI.setWheelScrollingEnabled(true);

		scrollbarGUI=scrollerTabGUI.getVerticalScrollBar();
		scrollbarGUI.setUnitIncrement(g.scrollbarIncrement);

		tabPanel.add("Send",scrollerTabSend);
		tabPanel.add("Receive",scrollerTabReceive);
		tabPanel.add("GUI",scrollerTabGUI);

		//http://stackoverflow.com/questions/5183687/java-remove-margin-padding-on-a-jtabbedpane
		tabPanel.setUI(new BasicTabbedPaneUI()
		{
			//top,left,right,bottom
			private final Insets borderInsets = new Insets(0, 0, 0, 0);
			@Override
			protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex)
			{
			}
			@Override
			protected Insets getContentBorderInsets(int tabPlacement)
			{
				return borderInsets;
			}
		});

		tabPanel.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				//setFocusedWidget();
			}
		});

		tabPanel.addFocusListener(
		new FocusListener()
		{
			Component c=null;
			public void focusLost(FocusEvent fe)
			{
				c=null;
				repaint();
			}
		        public void focusGained(FocusEvent fe)
			{
				c=fe.getComponent();
				repaint();
			}
		}
		);

		//======
		add(tabPanel,BorderLayout.CENTER);

		g.formUtility.addLabel("Connect To This JACK Server:", formSend);
		g.formUtility.addLastField(text_sname_s, formSend);

		g.formUtility.addLabel("Name Of JACK Client:", formSend);
		g.formUtility.addLastField(text_name_s, formSend);

		g.formUtility.addLabel("JACK system:* ports:", formSend);
		g.formUtility.addLastField(checkbox_connect_s, formSend);

		g.formUtility.addLabel("For 1:n Broadcast Scenario:", formSend);
		g.formUtility.addLastField(checkbox_nopause_s, formSend);

		g.formUtility.addLabel("Limit Totally Sent Messages:", formSend);
		g.formUtility.addLastField(checkbox_test_s, formSend);

		g.formUtility.addLabel("Message Count Limit:", formSend);
		g.formUtility.addLastField(text_limit_s, formSend);

		g.formUtility.addLabel("Drop Every Nth Message:", formSend);
		g.formUtility.addLastField(text_drop_s, formSend);

		g.formUtility.addLabel("jack_audio_send std Passthrough:", formSend);
		g.formUtility.addLastField(checkbox_verbose_s, formSend);

		g.formUtility.addLabel("Status Update Interval:", formSend);
		g.formUtility.addLastField(text_update_s, formSend);

		g.formUtility.addLabel("UDP Port For jack_audio_send:", formSend);
		g.formUtility.addLastField(checkbox_lport_random_s, formSend);

		g.formUtility.addLabel("Fixed Port (If Not Random):", formSend);
		g.formUtility.addLastField(text_lport_s, formSend);

		g.formUtility.addLabel("Start Transmission On Startup:", formSend);
		g.formUtility.addLastField(checkbox_autostart_s, formSend);

//receive
		g.formUtility.addLabel("Connect To This JACK Server:", formReceive);
		g.formUtility.addLastField(text_sname_r, formReceive);

		g.formUtility.addLabel("Name Of JACK Client:", formReceive);
		g.formUtility.addLastField(text_name_r, formReceive);

		g.formUtility.addLabel("JACK system:* ports:", formReceive);
		g.formUtility.addLastField(checkbox_connect_r, formReceive);

		g.formUtility.addLabel("Limit Totally Sent Messages:", formReceive);
		g.formUtility.addLastField(checkbox_test_r, formReceive);

		g.formUtility.addLabel("Message Count Limit:", formReceive);
		g.formUtility.addLastField(text_limit_r, formReceive);

		g.formUtility.addLabel("jack_audio_receive std Passthrough:", formReceive);
		g.formUtility.addLastField(checkbox_verbose_r, formReceive);

		g.formUtility.addLabel("Status Update Interval:", formReceive);
		g.formUtility.addLastField(text_update_r, formReceive);

		g.formUtility.addLabel("Channel Offset:", formReceive);
		g.formUtility.addLastField(text_offset_r, formReceive);

		g.formUtility.addLabel("Initial Buffer Size (MCP):", formReceive);
		g.formUtility.addLastField(text_pre_r, formReceive);

		g.formUtility.addLabel("Max Buffer Size (>= Init):", formReceive);
		g.formUtility.addLastField(text_max_r, formReceive);

		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_rere_r, formReceive);

		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_reuf_r, formReceive);

		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_nozero_r, formReceive);

		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_norbc_r, formReceive);

		g.formUtility.addLabel("", formReceive);
		g.formUtility.addLastField(checkbox_close_r, formReceive);

		g.formUtility.addLabel("Start Transmission On Startup:", formReceive);
		g.formUtility.addLastField(checkbox_autostart_r, formReceive);

//GUI
		g.formUtility.addLabel("UDP Port For GUI (Send):", formGUI);
		g.formUtility.addLastField(checkbox_gui_osc_port_random, formGUI);

		g.formUtility.addLabel("Fixed Port (If Not Random):", formGUI);
		g.formUtility.addLastField(text_gui_osc_port_s, formGUI);

		g.formUtility.addSpacer(formGUI);

		g.formUtility.addLabel("UDP Port For GUI (Receive):", formGUI);
		g.formUtility.addLastField(checkbox_gui_osc_port_random_r, formGUI);

		g.formUtility.addLabel("Fixed Port (If Not Random):", formGUI);
		g.formUtility.addLastField(text_gui_osc_port_r, formGUI);

		g.formUtility.addSpacer(formGUI);

		g.formUtility.addLabel("Keep Dumped Resources In Cache:", formGUI);
		g.formUtility.addLastField(checkbox_keep_cache, formGUI);

		g.formUtility.addLabel("", formGUI);
		g.formUtility.addLastField(checkbox_both_panels, formGUI);

//buttons
		Panel button_panel=new Panel();

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

//		setResizable(false);

		//done in calling object
		//setVisible(true);
	}//end createForm

//=============================================================================
	public void readForm()
	{
		FormHelper.validate(formSend);
		FormHelper.validate(formReceive);
		FormHelper.validate(formGUI);

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
		g.apis.autostart=checkbox_autostart_s.getState();

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
		g.apir.autostart=checkbox_autostart_r.getState();

		if(text_gui_osc_port_s.getText().equals(""))
		{
			text_gui_osc_port_s.setText(""+g.gui_osc_port_s);
		}

		if(text_gui_osc_port_r.getText().equals(""))
		{
			text_gui_osc_port_r.setText(""+g.gui_osc_port_r);
		}

		g.gui_osc_port_random_s=checkbox_gui_osc_port_random.getState();
		g.gui_osc_port_s=Integer.parseInt(text_gui_osc_port_s.getText());

		g.gui_osc_port_random_r=checkbox_gui_osc_port_random_r.getState();
		g.gui_osc_port_r=Integer.parseInt(text_gui_osc_port_r.getText());

		g.keep_cache=checkbox_keep_cache.getState();
		g.show_both_panels=checkbox_both_panels.getState();
	}//end readForm

//========================================================================
	private void addActionListeners()
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
	private void addWindowListeners()
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
//tabstate
	public void stateChanged(ChangeEvent e)
	{

/*
tabPanel.setBackgroundAt(tabPanel.getSelectedIndex(),Colors.form_foreground);
tabPanel.setForegroundAt(tabPanel.getSelectedIndex(),Colors.form_background);
tabPanel.repaint();
*/

		//setFocusedWidget();
	}

//========================================================================
	public void setFocusedWidget()
	{
		String tabname=tabPanel.getTitleAt(tabPanel.getSelectedIndex());
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
	public void nextTab()
	{
		int newIndex=tabPanel.getSelectedIndex();
		newIndex++;
		newIndex=newIndex % tabPanel.getTabCount();
		tabPanel.setSelectedIndex(newIndex);
	}

//========================================================================
	public void prevTab()
	{
		int newIndex=tabPanel.getSelectedIndex();
		newIndex--;
		tabPanel.setSelectedIndex(newIndex < 0 ? tabPanel.getTabCount()-1 : newIndex);
	}

//========================================================================
	public void scrollUp()
	{
		JScrollPane jp=(JScrollPane)tabPanel.getSelectedComponent();
		JScrollBar sb=jp.getVerticalScrollBar();
		int val=sb.getValue();
		sb.setValue(val-=30);
	}

//========================================================================
	public void scrollDown()
	{
		JScrollPane jp=(JScrollPane)tabPanel.getSelectedComponent();
		JScrollBar sb=jp.getVerticalScrollBar();
		int val=sb.getValue();
		sb.setValue(val+=30);
	}

//========================================================================
	public void scrollTop()
	{
		JScrollPane jp=(JScrollPane)tabPanel.getSelectedComponent();
		JScrollBar sb=jp.getVerticalScrollBar();
		sb.setValue(sb.getMinimum());
	}

//========================================================================
	public void scrollBottom()
	{
		JScrollPane jp=(JScrollPane)tabPanel.getSelectedComponent();
		JScrollBar sb=jp.getVerticalScrollBar();
		sb.setValue(sb.getMaximum());
	}
}//end class ConfigureDialog
