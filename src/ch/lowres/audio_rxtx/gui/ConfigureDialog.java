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

import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

//========================================================================
public class ConfigureDialog extends JDialog implements ChangeListener
{
	static Main g;

	static JPanel formSend;
	static HostTextFieldWithLimit		text_name_s=new HostTextFieldWithLimit("",32,32);
	static HostTextFieldWithLimit		text_sname_s=new HostTextFieldWithLimit("",32,32);
	static ACheckbox 			checkbox_connect_s=new ACheckbox("Autoconnect");
	static ACheckbox 			checkbox_nopause_s=new ACheckbox("No Pause On Sender Deny");
	static ACheckbox 			checkbox_test_s=new ACheckbox("Enable Testmode");
	static NumericTextFieldWithLimit 	text_limit_s=new NumericTextFieldWithLimit("",32,24);
	static NumericTextFieldWithLimit 	text_drop_s=new NumericTextFieldWithLimit("",32,24);
	static ACheckbox 			checkbox_verbose_s=new ACheckbox("Verbose Shell Output");
	static NumericTextFieldWithLimit 	text_update_s=new NumericTextFieldWithLimit("",32,4);
	static ACheckbox 			checkbox_lport_random_s=new ACheckbox("Use Random Port");
	static NumericTextFieldWithLimit 	text_lport_s=new NumericTextFieldWithLimit("",32,5);

	static JPanel formReceive;
	static HostTextFieldWithLimit		text_name_r=new HostTextFieldWithLimit("",32,32);
	static HostTextFieldWithLimit		text_sname_r=new HostTextFieldWithLimit("",32,32);
	static ACheckbox 			checkbox_connect_r=new ACheckbox("Autoconnect");
	static ACheckbox 			checkbox_test_r=new ACheckbox("Enable Testmode");
	static NumericTextFieldWithLimit 	text_limit_r=new NumericTextFieldWithLimit("",32,24);
	static ACheckbox 			checkbox_verbose_r=new ACheckbox("Verbose Shell Output");
	static NumericTextFieldWithLimit 	text_update_r=new NumericTextFieldWithLimit("",32,4);
	static NumericTextFieldWithLimit 	text_offset_r=new NumericTextFieldWithLimit("",32,24);
	static NumericTextFieldWithLimit 	text_pre_r=new NumericTextFieldWithLimit("",32,24);
	static NumericTextFieldWithLimit 	text_max_r=new NumericTextFieldWithLimit("",32,24);
	static ACheckbox 			checkbox_rere_r=new ACheckbox("Rebuffer On Sender Restart");
	static ACheckbox 			checkbox_reuf_r=new ACheckbox("Rebuffer On Underflow");
	static ACheckbox 			checkbox_nozero_r=new ACheckbox("Re-Use Old Data On Underflow");
	static ACheckbox 			checkbox_norbc_r=new ACheckbox("Disallow Ext. Buffer Control");
	static ACheckbox 			checkbox_close_r=new ACheckbox("Stop Transmission On Incompat.");

	static JPanel formGUI;
	static ACheckbox 			checkbox_gui_osc_port_random=new ACheckbox("Use Random Port");
	static NumericTextFieldWithLimit 	text_gui_osc_port_s=new NumericTextFieldWithLimit("",32,5);
	static ACheckbox 			checkbox_gui_osc_port_random_r=new ACheckbox("Use Random Port");
	static NumericTextFieldWithLimit 	text_gui_osc_port_r=new NumericTextFieldWithLimit("",32,5);
	static ACheckbox 			checkbox_keep_cache=new ACheckbox("Use Cache");

	static AButton 				button_cancel_settings=new AButton("Cancel");
	static AButton 				button_confirm_settings=new AButton("OK");

	static JScrollPane scrollerTabSend_;
	static JScrollPane scrollerTabReceive_;
	static JScrollPane scrollerTabGUI_;

	static JPanel tabSend_;
	static JPanel tabReceive_;
	static JPanel tabGUI_;

	static JTabbedPane tabPanel_ = new JTabbedPane();

	static NormalFocusTraversalPolicy focusPolicySend;
	static NormalFocusTraversalPolicy focusPolicyReceive;
	static NormalFocusTraversalPolicy focusPolicyGUI;

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

		checkbox_gui_osc_port_random.setState(g.gui_osc_port_random_s);
		text_gui_osc_port_s.setText(""+g.gui_osc_port_s);
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
		formSend=new JPanel();
		formReceive=new JPanel();
		formGUI=new JPanel();

		formSend.setLayout(new GridBagLayout());
		formReceive.setLayout(new GridBagLayout());
		formGUI.setLayout(new GridBagLayout());

		formSend.setBackground(Colors.form_background);
		formReceive.setBackground(Colors.form_background);
		formGUI.setBackground(Colors.form_background);

		tabSend_=new JPanel(new BorderLayout());
		tabSend_.setBackground(Colors.form_background);
		tabSend_.add(formSend,BorderLayout.NORTH);
		scrollerTabSend_=new JScrollPane (tabSend_, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabSend_.getViewport().setBackground(Colors.form_background);

		tabReceive_=new JPanel(new BorderLayout());
		tabReceive_.setBackground(Colors.form_background);
		tabReceive_.add(formReceive,BorderLayout.NORTH);
		scrollerTabReceive_=new JScrollPane (tabReceive_, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabReceive_.getViewport().setBackground(Colors.form_background);

		tabGUI_=new JPanel(new BorderLayout());
		tabGUI_.setBackground(Colors.form_background);
		tabGUI_.add(formGUI,BorderLayout.NORTH);
		scrollerTabGUI_=new JScrollPane (tabGUI_, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabGUI_.getViewport().setBackground(Colors.form_background);

		tabPanel_.add("Send",scrollerTabSend_);
		tabPanel_.add("Receive",scrollerTabReceive_);
		tabPanel_.add("GUI",scrollerTabGUI_);

		//http://stackoverflow.com/questions/5183687/java-remove-margin-padding-on-a-jtabbedpane
		tabPanel_.setUI(new BasicTabbedPaneUI()
		{
			//top,left,right,bottom
			private final Insets borderInsets = new Insets(2, 2, 2, 2);
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

		tabPanel_.addChangeListener(this);

		//======
		add(tabPanel_,BorderLayout.CENTER);

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

//focus orders
		Vector<Component> orderSend = new Vector<Component>();
		orderSend.add(text_sname_s);
		orderSend.add(text_name_s);
		orderSend.add(checkbox_connect_s);
		orderSend.add(checkbox_nopause_s);
		orderSend.add(checkbox_test_s);
		orderSend.add(text_limit_s);
		orderSend.add(text_drop_s);
		orderSend.add(checkbox_verbose_s);
		orderSend.add(text_update_s);
		orderSend.add(checkbox_lport_random_s);
		orderSend.add(text_lport_s);
		orderSend.add(button_cancel_settings);
		orderSend.add(button_confirm_settings);

		focusPolicySend = new NormalFocusTraversalPolicy(orderSend);
		setFocusTraversalPolicy(focusPolicySend);

		Vector<Component> orderReceive = new Vector<Component>();
		orderReceive.add(text_sname_r);
		orderReceive.add(text_name_r);
		orderReceive.add(checkbox_connect_r);
		orderReceive.add(checkbox_test_r);
		orderReceive.add(text_limit_r);
		orderReceive.add(checkbox_verbose_r);
		orderReceive.add(text_update_r);
		orderReceive.add(text_offset_r);
		orderReceive.add(text_pre_r);
		orderReceive.add(text_max_r);
		orderReceive.add(checkbox_rere_r);
		orderReceive.add(checkbox_reuf_r);
		orderReceive.add(checkbox_nozero_r);
		orderReceive.add(checkbox_norbc_r);
		orderReceive.add(checkbox_close_r);
		orderReceive.add(button_cancel_settings);
		orderReceive.add(button_confirm_settings);

		focusPolicyReceive = new NormalFocusTraversalPolicy(orderReceive);

		Vector<Component> orderGUI = new Vector<Component>();
		orderGUI.add(checkbox_gui_osc_port_random);
		orderGUI.add(text_gui_osc_port_s);
		orderGUI.add(checkbox_gui_osc_port_random_r);
		orderGUI.add(text_gui_osc_port_r);
		orderGUI.add(checkbox_keep_cache);
		orderGUI.add(button_cancel_settings);
		orderGUI.add(button_confirm_settings);

		focusPolicyGUI = new NormalFocusTraversalPolicy(orderGUI);

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
	static void readForm()
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
		String tabname=tabPanel_.getTitleAt(tabPanel_.getSelectedIndex());
		setFocusedWidget(tabname);
	}

//========================================================================
	public void nextTab()
	{
		int newIndex=tabPanel_.getSelectedIndex();
		newIndex++;
		newIndex=newIndex % tabPanel_.getTabCount();
		tabPanel_.setSelectedIndex(newIndex);
	}

//========================================================================
	public void prevTab()
	{
		int newIndex=tabPanel_.getSelectedIndex();
		newIndex--;
		tabPanel_.setSelectedIndex(newIndex < 0 ? tabPanel_.getTabCount()-1 : newIndex);
	}

//========================================================================
	public void setFocusedWidget(String tabname)
	{
		if(tabname.equals("Send"))
		{
			text_name_s.requestFocus();
			setFocusTraversalPolicy(focusPolicySend);
		}
		else if(tabname.equals("Receive"))
		{
			text_name_r.requestFocus();
			setFocusTraversalPolicy(focusPolicyReceive);
		}
		else if(tabname.equals("GUI"))
		{
			checkbox_gui_osc_port_random.requestFocus();
			setFocusTraversalPolicy(focusPolicyGUI);
		}
	}

//========================================================================
	public void scrollUp()
	{
		JScrollPane jp=(JScrollPane)tabPanel_.getSelectedComponent();
		JScrollBar sb=jp.getVerticalScrollBar();
		int val=sb.getValue();
		sb.setValue(val-=30);
	}

//========================================================================
	public void scrollDown()
	{
		JScrollPane jp=(JScrollPane)tabPanel_.getSelectedComponent();
		JScrollBar sb=jp.getVerticalScrollBar();
		int val=sb.getValue();
		sb.setValue(val+=30);
	}

//========================================================================
	public void scrollTop()
	{
		JScrollPane jp=(JScrollPane)tabPanel_.getSelectedComponent();
		JScrollBar sb=jp.getVerticalScrollBar();
		sb.setValue(sb.getMinimum());
	}

//========================================================================
	public void scrollBottom()
	{
		JScrollPane jp=(JScrollPane)tabPanel_.getSelectedComponent();
		JScrollBar sb=jp.getVerticalScrollBar();
		sb.setValue(sb.getMaximum());
	}

//========================================================================
	public void stateChanged(ChangeEvent e)
	{
		setFocusedWidget();
	}

//========================================================================
//http://www.java2s.com/Tutorial/Java/0260__Swing-Event/UseFocusTraversalPolicy.htm
	public class NormalFocusTraversalPolicy extends FocusTraversalPolicy
	{
		Vector<Component> order;

//========================================================================
		public NormalFocusTraversalPolicy(Vector<Component> order)
		{
			this.order = new Vector<Component>(order.size());
			this.order.addAll(order);
		}

//========================================================================
		public Component getComponentAfter(Container focusCycleRoot, Component aComponent)
		{
			int idx = (order.indexOf(aComponent) + 1) % order.size();
			return order.get(idx);
		}

//========================================================================
		public Component getComponentBefore(Container focusCycleRoot, Component aComponent)
		{
			int idx = order.indexOf(aComponent) - 1;
			if (idx < 0)
			{
				idx = order.size() - 1;
			}
				return order.get(idx);
		}

		public Component getDefaultComponent(Container focusCycleRoot)
		{
			return order.get(0);
		}

		public Component getLastComponent(Container focusCycleRoot)
		{
			return order.lastElement();
		}

		public Component getFirstComponent(Container focusCycleRoot)
		{
			return order.get(0);
		}
	}//end inner class NormalFocusTraversalPolicy
}//end class ConfigureDialog
