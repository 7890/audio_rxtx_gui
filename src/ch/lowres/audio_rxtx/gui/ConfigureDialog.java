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

import java.util.*;

import org.xnap.commons.i18n.*;


/**
* Modal tabbed dialog to configure jack_audio_send, jack_audio_receive and GUI options.
*/
//========================================================================
public class ConfigureDialog extends JDialog implements ChangeListener, ComponentListener
{
	private static Main m;
	private static GUI g;
	private static Fonts f;
	private static Languages l;

	private static JPanel formSend;

	private static HostTextFieldWithLimit		text_name_s=new HostTextFieldWithLimit("",32,32);
	private static HostTextFieldWithLimit		text_sname_s=new HostTextFieldWithLimit("",32,32);
	private static ACheckbox 			checkbox_connect_s=new ACheckbox(l.tr("Autoconnect"));
	private static ACheckbox 			checkbox_nopause_s=new ACheckbox(l.tr("No pause when receiver denies transmission"));
	private static ACheckbox 			checkbox_test_s=new ACheckbox(l.tr("Enable Testmode"));
	private static NumericTextFieldWithLimit 	text_limit_s=new NumericTextFieldWithLimit("",32,24);
	private static NumericTextFieldWithLimit 	text_drop_s=new NumericTextFieldWithLimit("",32,24);
	private static ACheckbox 			checkbox_verbose_s=new ACheckbox(l.tr("Verbose shell output"));
	private static NumericTextFieldWithLimit 	text_update_s=new NumericTextFieldWithLimit("",32,4);
	private static ACheckbox 			checkbox_lport_random_s=new ACheckbox(l.tr("Use random port"));
	private static NumericTextFieldWithLimit 	text_lport_s=new NumericTextFieldWithLimit("",32,5);
	private static ACheckbox 			checkbox_autostart_s=new ACheckbox(l.tr("Autostart transmission"));

	private static JPanel formReceive;

	private static HostTextFieldWithLimit		text_name_r=new HostTextFieldWithLimit("",32,32);
	private static HostTextFieldWithLimit		text_sname_r=new HostTextFieldWithLimit("",32,32);
	private static ACheckbox 			checkbox_connect_r=new ACheckbox(l.tr("Autoconnect"));
	private static ACheckbox 			checkbox_test_r=new ACheckbox(l.tr("Enable testmode"));
	private static NumericTextFieldWithLimit 	text_limit_r=new NumericTextFieldWithLimit("",32,24);
	private static ACheckbox 			checkbox_verbose_r=new ACheckbox(l.tr("Verbose shell output"));
	private static NumericTextFieldWithLimit 	text_update_r=new NumericTextFieldWithLimit("",32,4);
	private static NumericTextFieldWithLimit 	text_offset_r=new NumericTextFieldWithLimit("",32,24);
	private static NumericTextFieldWithLimit 	text_pre_r=new NumericTextFieldWithLimit("",32,24);
	private static NumericTextFieldWithLimit 	text_max_r=new NumericTextFieldWithLimit("",32,24);
	private static ACheckbox 			checkbox_rere_r=new ACheckbox(l.tr("Rebuffer on sender restart"));
	private static ACheckbox 			checkbox_reuf_r=new ACheckbox(l.tr("Rebuffer on underflow"));
	private static ACheckbox 			checkbox_nozero_r=new ACheckbox(l.tr("Re-Use old data on underflow"));
	private static ACheckbox 			checkbox_norbc_r=new ACheckbox(l.tr("Disallow external buffer control"));
	private static ACheckbox 			checkbox_close_r=new ACheckbox(l.tr("Stop transmission on incompatibility"));
	private static ACheckbox 			checkbox_autostart_r=new ACheckbox(l.tr("Autostart transmission"));

	private static JPanel formGUI;

	private static ListTextFieldWithLimit 		list_languages = new ListTextFieldWithLimit(l.languages,32,32);
	private static ACheckbox			checkbox_use_internal_font=new ACheckbox(l.tr("Use built-in font"));
	private static ListTextFieldWithLimit 		list_fonts = new ListTextFieldWithLimit(f.getAll(),32,32);
	private static NumericFloatTextFieldWithLimit 	text_font_size_normal=new NumericFloatTextFieldWithLimit("",4,128);
	private static ListTextFieldWithLimit 		list_font_styles = new ListTextFieldWithLimit(f.styles,32,32);

	private static ACheckbox 			checkbox_gui_osc_port_random_s=new ACheckbox(l.tr("Use random port"));
	private static NumericTextFieldWithLimit 	text_gui_osc_port_s=new NumericTextFieldWithLimit("",32,5);
	private static ACheckbox 			checkbox_gui_osc_port_random_r=new ACheckbox(l.tr("Use random port"));
	private static NumericTextFieldWithLimit 	text_gui_osc_port_r=new NumericTextFieldWithLimit("",32,5);
	private static ACheckbox 			checkbox_keep_cache=new ACheckbox(l.tr("Use Cache"));
	private static ACheckbox 			checkbox_both_panels=new ACheckbox(l.tr("Show both panels"));

	public static AButton 				button_cancel_settings=new AButton(l.removeMnemonic(l.tr("_Cancel")));
	public static AButton 				button_confirm_settings=new AButton(l.removeMnemonic(l.tr("_Apply")));

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
	public ConfigureDialog(Frame f, String title, boolean modality)
	{
		super(f,title,modality);
		createForm();
		addActionListeners();
		addWindowListeners();
		addComponentListener(this);
		setValues();
	}//end constructor

//========================================================================
	public void setValues()
	{
		text_name_s.setText(m.apis._name);
		text_sname_s.setText(m.apis._sname);
		checkbox_connect_s.setState(m.apis._connect);
		checkbox_nopause_s.setState(m.apis._nopause);
		checkbox_test_s.setState(m.apis.test_mode);

		if(m.apis.test_mode)
		{
			text_limit_s.setEnabled(true);
		}
		else
		{
			text_limit_s.setEnabled(false);
		}

		text_limit_s.setText(""+m.apis._limit);
		text_drop_s.setText(""+m.apis._drop);
		checkbox_verbose_s.setState(m.apis.verbose);
		text_update_s.setText(""+m.apis._update);
		checkbox_lport_random_s.setState(m.apis.lport_random);

		if(m.apis.lport_random)
		{
			text_lport_s.setEnabled(false);
		}
		else
		{
			text_lport_s.setEnabled(true);
		}

		text_lport_s.setText(""+m.apis._lport);
		checkbox_autostart_s.setState(m.apis.autostart);

		text_name_r.setText(m.apir._name);
		text_sname_r.setText(m.apir._sname);
		checkbox_connect_r.setState(m.apir._connect);
		checkbox_test_r.setState(m.apir.test_mode);

		if(m.apir.test_mode)
		{
			text_limit_r.setEnabled(true);
		}
		else
		{
			text_limit_r.setEnabled(false);
		}

		text_limit_r.setText(""+m.apir._limit);
		checkbox_verbose_r.setState(m.apir.verbose);
		text_update_r.setText(""+m.apir._update);
		text_offset_r.setText(""+m.apir._offset);
		text_pre_r.setText(""+m.apir._pre);
		text_max_r.setText(""+m.apir._max);
		checkbox_rere_r.setState(m.apir._rere);
		checkbox_reuf_r.setState(m.apir._reuf);
		checkbox_nozero_r.setState(m.apir._nozero);
		checkbox_norbc_r.setState(m.apir._norbc);
		checkbox_close_r.setState(m.apir._close);
		checkbox_autostart_r.setState(m.apir.autostart);

		text_font_size_normal.setText(""+f.fontDefaultSize);
		
		list_languages.setSelectedIndex(l.langIndex);

		checkbox_use_internal_font.setState(f.use_internal_font);

		if(f.use_internal_font)
		{
			list_fonts.setEnabled(false);
		}
		else
		{
			list_fonts.setEnabled(true);
		}

		list_fonts.setSelectedIndex(list_fonts.firstItemEqual(f.fontName));

		text_font_size_normal.setText(""+f.fontDefaultSize);
		list_font_styles.setSelectedIndex(f.fontNormalStyle);

		checkbox_keep_cache.setState(g.keep_cache);
		checkbox_both_panels.setState(g.show_both_panels);

		checkbox_gui_osc_port_random_s.setState(g.gui_osc_port_random_s);
		text_gui_osc_port_s.setText(""+g.gui_osc_port_s);
		if(g.gui_osc_port_random_s)
		{
			text_gui_osc_port_s.setEnabled(false);
		}
		else
		{
			text_gui_osc_port_s.setEnabled(true);
		}

		checkbox_gui_osc_port_random_r.setState(g.gui_osc_port_random_r);
		text_gui_osc_port_r.setText(""+g.gui_osc_port_r);
		if(g.gui_osc_port_random_r)
		{
			text_gui_osc_port_r.setEnabled(false);
		}
		else
		{
			text_gui_osc_port_r.setEnabled(true);
		}

		tabPanel.requestFocus();
	}//end setValues

//========================================================================
	public void dialogCancelled()
	{
		//reset values to previous
		setValues();
		setFocusedWidget();
		//close window, set status, bring main to front
		g.mainframe.toFront();
		setVisible(false);
		g.setStatus("Configuration Cancelled, Restored Values");
	}//end dialogCancelled

//========================================================================
	public void dialogConfirmed()
	{
		//read form, store values
		readForm();
		setFocusedWidget();
		//close window, set status, bring main to front
		g.mainframe.toFront();
		setVisible(false);
		g.setStatus("Configuration Confirmed, Using Values");
	}//end dialogConfirmed

//========================================================================
	private void createForm()
	{
		setIconImage(Images.appIcon);
		setLayout(new BorderLayout());

		//limits
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

		text_font_size_normal.setMinInclusive(6f);
		text_font_size_normal.setMaxInclusive(64f);

		text_gui_osc_port_s.setMinInclusive(1024);
		text_gui_osc_port_s.setMaxInclusive(65535);

		text_gui_osc_port_r.setMinInclusive(1024);
		text_gui_osc_port_r.setMaxInclusive(65535);

		tabPanel.setFont(f.fontNormal);

		formSend=new JPanel();
		formReceive=new JPanel();
		formGUI=new JPanel();

		formSend.setLayout(new GridBagLayout());
		formReceive.setLayout(new GridBagLayout());
		formGUI.setLayout(new GridBagLayout());

		formSend.setBackground(Colors.form_background);
		formReceive.setBackground(Colors.form_background);
		formGUI.setBackground(Colors.form_background);

		formSend.setOpaque(true);
		formReceive.setOpaque(true);
		formGUI.setOpaque(true);

		tabSend=new APanel(new BorderLayout());
		tabSend.add(formSend,BorderLayout.NORTH);

		scrollerTabSend=new JScrollPane (tabSend, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabSend.getViewport().setBackground(Colors.form_background);
		scrollerTabSend.setWheelScrollingEnabled(true);
//		scrollerTabSend.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		scrollbarSend=scrollerTabSend.getVerticalScrollBar();
		scrollbarSend.setUnitIncrement(g.scrollbarIncrement);
		scrollbarSend.setUI(new AScrollbarUI());
		//need to set horizontal too

		tabReceive=new APanel(new BorderLayout());
		tabReceive.add(formReceive,BorderLayout.NORTH);
		scrollerTabReceive=new JScrollPane (tabReceive, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabReceive.getViewport().setBackground(Colors.form_background);
		scrollerTabReceive.setWheelScrollingEnabled(true);
//		scrollerTabReceive.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		scrollbarReceive=scrollerTabReceive.getVerticalScrollBar();
		scrollbarReceive.setUnitIncrement(g.scrollbarIncrement);
		scrollbarReceive.setUI(new AScrollbarUI());
		//need to set horizontal too

		tabGUI=new APanel(new BorderLayout());
		tabGUI.add(formGUI,BorderLayout.NORTH);
		scrollerTabGUI=new JScrollPane (tabGUI, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabGUI.getViewport().setBackground(Colors.form_background);
		scrollerTabGUI.setWheelScrollingEnabled(true);
		scrollerTabGUI.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		scrollbarGUI=scrollerTabGUI.getVerticalScrollBar();
		scrollbarGUI.setUnitIncrement(g.scrollbarIncrement);
		scrollbarGUI.setUI(new AScrollbarUI());

		tabPanel.add(l.removeMnemonic(l.tr("_Send")),scrollerTabSend);
		tabPanel.add(l.removeMnemonic(l.tr("_Receive")),scrollerTabReceive);
		tabPanel.add(l.removeMnemonic(l.tr("_GUI")),scrollerTabGUI);

		//doesn't work on osx
		tabPanel.setMnemonicAt(0, l.getMnemonicKeyEvent(l.tr("_Send")));
		tabPanel.setMnemonicAt(1, l.getMnemonicKeyEvent(l.tr("_Receive")));
		tabPanel.setMnemonicAt(2, l.getMnemonicKeyEvent(l.tr("_GUI")));

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
/*
		tabPanel.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{

				setFocusedWidget();
			}
		});
*/

		//force redraw (probably remove focus indication)
		tabPanel.addFocusListener(
		new FocusListener()
		{
			public void focusLost(FocusEvent fe)
			{
				repaint();
			}
			public void focusGained(FocusEvent fe)
			{
				repaint();
			}
		}
		);


		//======
		add(tabPanel,BorderLayout.CENTER);

		g.formUtility.addLabel(l.tr("Connect to this JACK server")+":", formSend);
		g.formUtility.addLastField(text_sname_s, formSend);

		g.formUtility.addLabel(l.tr("Name of JACK client")+":", formSend);
		g.formUtility.addLastField(text_name_s, formSend);

		g.formUtility.addLabel(l.tr("JACK system:* ports")+":", formSend);
		g.formUtility.addLastField(checkbox_connect_s, formSend);

		g.formUtility.addLabel(l.tr("For 1:n Broadcast scenario")+":", formSend);
		g.formUtility.addLastField(checkbox_nopause_s, formSend);

		g.formUtility.addLabel(l.tr("Limit totally sent messages")+":", formSend);
		g.formUtility.addLastField(checkbox_test_s, formSend);

		checkbox_test_s.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					text_limit_s.setEnabled(true);
					text_limit_s.requestFocus();
				}
				else if (e.getStateChange() == ItemEvent.DESELECTED)
				{
					text_limit_s.setEnabled(false);
				}
			}
		});

		g.formUtility.addLabel(l.tr("Message count limit")+":", formSend);
		g.formUtility.addLastField(text_limit_s, formSend);

		g.formUtility.addLabel(l.tr("Drop every Nth message")+":", formSend);
		g.formUtility.addLastField(text_drop_s, formSend);

		g.formUtility.addLabel(l.tr("jack_audio_send std passthrough")+":", formSend);
		g.formUtility.addLastField(checkbox_verbose_s, formSend);

		g.formUtility.addLabel(l.tr("Status update interval")+":", formSend);
		g.formUtility.addLastField(text_update_s, formSend);

		g.formUtility.addLabel(l.tr("UDP port for jack_audio_send")+":", formSend);
		g.formUtility.addLastField(checkbox_lport_random_s, formSend);

		checkbox_lport_random_s.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					text_lport_s.setEnabled(false);
				}
				else if (e.getStateChange() == ItemEvent.DESELECTED)
				{
					text_lport_s.setEnabled(true);
					text_lport_s.requestFocus();
				}
			}
		});

		g.formUtility.addLabel(l.tr("Fixed port (if not random)")+":", formSend);
		g.formUtility.addLastField(text_lport_s, formSend);

		g.formUtility.addLabel(l.tr("Start transmission after startup")+":", formSend);
		g.formUtility.addLastField(checkbox_autostart_s, formSend);

//receive
		g.formUtility.addLabel(l.tr("Connect to this JACK server")+":", formReceive);
		g.formUtility.addLastField(text_sname_r, formReceive);

		g.formUtility.addLabel(l.tr("Name of JACK client")+":", formReceive);
		g.formUtility.addLastField(text_name_r, formReceive);

		g.formUtility.addLabel(l.tr("JACK system:* ports")+":", formReceive);
		g.formUtility.addLastField(checkbox_connect_r, formReceive);

		g.formUtility.addLabel(l.tr("Limit totally sent messages")+":", formReceive);
		g.formUtility.addLastField(checkbox_test_r, formReceive);

		checkbox_test_r.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					text_limit_r.setEnabled(true);
					text_limit_r.requestFocus();
				}
				else if (e.getStateChange() == ItemEvent.DESELECTED)
				{
					text_limit_r.setEnabled(false);
				}
			}
		});

		g.formUtility.addLabel(l.tr("Message count limit")+":", formReceive);
		g.formUtility.addLastField(text_limit_r, formReceive);

		g.formUtility.addLabel(l.tr("jack_audio_receive std passthrough")+":", formReceive);
		g.formUtility.addLastField(checkbox_verbose_r, formReceive);

		g.formUtility.addLabel(l.tr("Status update interval")+":", formReceive);
		g.formUtility.addLastField(text_update_r, formReceive);

		g.formUtility.addLabel(l.tr("Channel offset")+":", formReceive);
		g.formUtility.addLastField(text_offset_r, formReceive);

		g.formUtility.addLabel(l.tr("Initial buffer size (MCP)")+":", formReceive);
		g.formUtility.addLastField(text_pre_r, formReceive);

		g.formUtility.addLabel(l.tr("Max buffer size (>= initial)")+":", formReceive);
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

		g.formUtility.addLabel(l.tr("Start transmission after startup")+":", formReceive);
		g.formUtility.addLastField(checkbox_autostart_r, formReceive);

//GUI
		g.formUtility.addLabel(l.tr("Language")+": ", formGUI);
		list_languages.setTitle(l.tr("Choose Language"));
		g.formUtility.addLastField(list_languages, formGUI);

		checkbox_use_internal_font.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					list_fonts.setEnabled(false);
				}
				else if (e.getStateChange() == ItemEvent.DESELECTED)
				{
					list_fonts.setEnabled(true);
					list_fonts.requestFocus();
				}
			}
		});

		g.formUtility.addLabel("", formGUI);
		g.formUtility.addLastField(checkbox_use_internal_font, formGUI);

		g.formUtility.addLabel(l.tr("Local system font")+":", formGUI);
		g.formUtility.addLastField(list_fonts, formGUI);
		list_fonts.setTitle(l.tr("Choose Font"));

		g.formUtility.addLabel(l.tr("Font size \"normal\" [pt]")+":", formGUI);
		g.formUtility.addLastField(text_font_size_normal, formGUI);

		g.formUtility.addLabel(l.tr("Font style")+":", formGUI);
		g.formUtility.addLastField(list_font_styles, formGUI);
		list_font_styles.setTitle(l.tr("Choose Font Style"));

		g.formUtility.addLabel(l.tr("After program startup")+":", formGUI);
		g.formUtility.addLastField(checkbox_both_panels, formGUI);

		g.formUtility.addLabel(l.tr("UDP port for GUI (send)")+":", formGUI);
		g.formUtility.addLastField(checkbox_gui_osc_port_random_s, formGUI);

		checkbox_gui_osc_port_random_s.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					text_gui_osc_port_s.setEnabled(false);
				}
				else if (e.getStateChange() == ItemEvent.DESELECTED)
				{
					text_gui_osc_port_s.setEnabled(true);
					text_gui_osc_port_s.requestFocus();
				}
			}
		});

		g.formUtility.addLabel(l.tr("Fixed port (if not random)")+":", formGUI);
		g.formUtility.addLastField(text_gui_osc_port_s, formGUI);

		g.formUtility.addLabel(l.tr("UDP port for GUI (receive)")+":", formGUI);
		g.formUtility.addLastField(checkbox_gui_osc_port_random_r, formGUI);

		checkbox_gui_osc_port_random_r.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					text_gui_osc_port_r.setEnabled(false);
				}
				else if (e.getStateChange() == ItemEvent.DESELECTED)
				{
					text_gui_osc_port_r.setEnabled(true);
					text_gui_osc_port_r.requestFocus();
				}
			}
		});

		g.formUtility.addLabel(l.tr("Fixed port (if not random)")+":", formGUI);
		g.formUtility.addLastField(text_gui_osc_port_r, formGUI);

		g.formUtility.addLabel(l.tr("Keep dumped resources in cache")+":", formGUI);
		g.formUtility.addLastField(checkbox_keep_cache, formGUI);

//buttons
		JPanel button_panel=new JPanel();
		button_panel.setLayout(new GridLayout(1,2)); //y, x

		button_cancel_settings.setMnemonic(l.getMnemonicKeyEvent(l.tr("_Cancel")));
		button_confirm_settings.setMnemonic(l.getMnemonicKeyEvent(l.tr("_Apply")));

		button_panel.add(button_cancel_settings);
		button_panel.add(button_confirm_settings);

		add(button_panel,BorderLayout.SOUTH);

		repack();
		g.setDialogCentered(this);

		//setResizable(false);

		//done in calling object
		//setVisible(true);
	}//end createForm

//=============================================================================
	public void repack()
	{
		//DisplayMode mode = this.getGraphicsConfiguration().getDevice().getDisplayMode();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());

		pack();

		int w=(int)Math.min(getPreferredSize().getWidth(),
			g.screenDimension.getWidth()-insets.left-insets.right
		);
		int h=(int)Math.min(getPreferredSize().getHeight(),
			g.screenDimension.getHeight()-insets.top-insets.bottom
		);

		setSize(w,h);
	}

//=============================================================================
	public void readForm()
	{
		FormHelper.validate(formSend);
		FormHelper.validate(formReceive);
		FormHelper.validate(formGUI);

		if(text_name_s.getText().equals(""))
		{
			text_name_s.setText(""+m.apis._name);
		}

		if(text_sname_s.getText().equals(""))
		{
			text_sname_s.setText(""+m.apis._sname);
		}

		if(text_limit_s.getText().equals(""))
		{
			text_limit_s.setText(""+m.apis._limit);
		}

		if(text_drop_s.getText().equals(""))
		{
			text_drop_s.setText(""+m.apis._drop);
		}

		if(text_update_s.getText().equals(""))
		{
			text_update_s.setText(""+m.apis._update);
		}

		if(text_lport_s.getText().equals(""))
		{
			text_lport_s.setText(""+m.apis._lport);
		}

		m.apis._name=text_name_s.getText();
		m.apis._sname=text_sname_s.getText();
		m.apis._connect=checkbox_connect_s.getState();
		m.apis._nopause=checkbox_nopause_s.getState();
		m.apis.test_mode=checkbox_test_s.getState();
		m.apis._limit=Integer.parseInt(text_limit_s.getText());
		m.apis._drop=Integer.parseInt(text_drop_s.getText());
		m.apis.verbose=checkbox_verbose_s.getState();
		m.apis._update=Integer.parseInt(text_update_s.getText());
		m.apis.lport_random=checkbox_lport_random_s.getState();
		m.apis._lport=Integer.parseInt(text_lport_s.getText());
		m.apis.autostart=checkbox_autostart_s.getState();

		if(text_name_r.getText().equals(""))
		{
			text_name_r.setText(""+m.apir._name);
		}

		if(text_sname_r.getText().equals(""))
		{
			text_sname_r.setText(""+m.apir._sname);
		}

		if(text_limit_r.getText().equals(""))
		{
			text_limit_r.setText(""+m.apir._limit);
		}

		if(text_update_r.getText().equals(""))
		{
			text_update_r.setText(""+m.apir._update);
		}

		if(text_offset_r.getText().equals(""))
		{
			text_offset_r.setText(""+m.apir._offset);
		}

		if(text_pre_r.getText().equals(""))
		{
			text_pre_r.setText(""+m.apir._pre);
		}

		if(text_max_r.getText().equals(""))
		{
			text_max_r.setText(""+m.apir._pre);
		}

		m.apir._name=text_name_r.getText();
		m.apir._sname=text_sname_r.getText();
		m.apir._connect=checkbox_connect_r.getState();
		m.apir.test_mode=checkbox_test_r.getState();
		m.apir._limit=Integer.parseInt(text_limit_r.getText());
		m.apir.verbose=checkbox_verbose_r.getState();
		m.apir._update=Integer.parseInt(text_update_r.getText());
		m.apir._offset=Integer.parseInt(text_offset_r.getText());
		m.apir._pre=Integer.parseInt(text_pre_r.getText());
		m.apir._max=Integer.parseInt(text_max_r.getText());
		m.apir._rere=checkbox_rere_r.getState();
		m.apir._reuf=checkbox_reuf_r.getState();
		m.apir._nozero=checkbox_nozero_r.getState();
		m.apir._norbc=checkbox_norbc_r.getState();
		m.apir._close=checkbox_close_r.getState();
		m.apir.autostart=checkbox_autostart_r.getState();

		if(text_gui_osc_port_s.getText().equals(""))
		{
			text_gui_osc_port_s.setText(""+g.gui_osc_port_s);
		}

		if(text_gui_osc_port_r.getText().equals(""))
		{
			text_gui_osc_port_r.setText(""+g.gui_osc_port_r);
		}

		g.keep_cache=checkbox_keep_cache.getState();
		g.show_both_panels=checkbox_both_panels.getState();

		g.gui_osc_port_random_s=checkbox_gui_osc_port_random_s.getState();
		g.gui_osc_port_s=Integer.parseInt(text_gui_osc_port_s.getText());

		g.gui_osc_port_random_r=checkbox_gui_osc_port_random_r.getState();
		g.gui_osc_port_r=Integer.parseInt(text_gui_osc_port_r.getText());

		l.set(list_languages.getText());

		f.use_internal_font=checkbox_use_internal_font.getState();

		if(text_font_size_normal.getText().equals(""))
		{
			text_font_size_normal.setText(""+f.fontDefaultSize);
		}

		f.fontName=list_fonts.getText();
		f.fontDefaultSize=Float.parseFloat(text_font_size_normal.getText());
		f.fontNormalStyle=list_font_styles.getSelectedIndex();

/////////
//should recreate conditionally..
		Fonts.init();
		g.updateFont();

	}//end readForm

//========================================================================
	private void addActionListeners()
	{
		button_cancel_settings.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dialogCancelled();
			}
		});

		button_confirm_settings.addActionListener(new ActionListener()
		{
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
			public void windowClosed(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {dialogCancelled();}
			public void windowActivated(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});

		addWindowStateListener(new WindowStateListener()
		{
			//not sent by all window managers
			public void windowStateChanged(WindowEvent e)
			{
				if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED)
				{
					//m.p("minimized");
				}
				else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH)
				{
					//m.p("maximized");
				}
			}
		});
	}//end addWindowListeners

//========================================================================
//http://www.java2s.com/Tutorial/Java/0240__Swing/JDialogisspecifythatpressingtheEscapekeycancelsthedialog.htm
	@Override
	protected JRootPane createRootPane()
	{
		JRootPane rootPane = new JRootPane();

		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		//InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		KeyStroke keyEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0);
		Action actionListenerConfirm = new AbstractAction("CONFIRM")
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				dialogConfirmed();
			}
		};

		inputMap.put(keyEnter, "ENTER");
		rootPane.getActionMap().put("ENTER", actionListenerConfirm);

		KeyStroke keyEscape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0);
		KeyStroke keyCtrlD = KeyStroke.getKeyStroke(KeyEvent.VK_D,m.ctrlOrCmd);
		Action actionListenerCancel = new AbstractAction("CANCEL")
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				dialogCancelled();
			}
		};

		inputMap.put(keyEscape, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", actionListenerCancel);

		inputMap.put(keyCtrlD, "CTRL_D");
		rootPane.getActionMap().put("CTRL_D", actionListenerCancel);

		KeyStroke keyPageUp = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP,0);
		Action actionListenerPrevTab = new AbstractAction("PREV_TAB")
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				prevTab();
			}
		};

		inputMap.put(keyPageUp, "PAGE_UP");
		rootPane.getActionMap().put("PAGE_UP", actionListenerPrevTab);

		KeyStroke keyPageDown = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN,0);
		Action actionListenerNextTab = new AbstractAction("PREV_TAB")
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				nextTab();
			}
		};

		inputMap.put(keyPageDown, "PAGE_DOWN");
		rootPane.getActionMap().put("PAGE_DOWN", actionListenerNextTab);

		//add keystroke actions to mimic mnemonic on tabs on osx
		if(m.os.isMac())
		{
/*
			tabPanel.setMnemonicAt(0, l.getMnemonicKeyEvent(l.tr("_Send")));
			tabPanel.setMnemonicAt(1, l.getMnemonicKeyEvent(l.tr("_Receive")));
			tabPanel.setMnemonicAt(2, l.getMnemonicKeyEvent(l.tr("_GUI")));
*/

			KeyStroke keyAltGotoSend = KeyStroke.getKeyStroke(l.getMnemonicKeyEvent(l.tr("_Send")),InputEvent.ALT_MASK);
			Action actionListenerGotoSendTab = new AbstractAction("GOTO_SEND_TAB")
			{
				public void actionPerformed(ActionEvent actionEvent)
				{
					tabPanel.setSelectedIndex(0);
				}
			};

			inputMap.put(keyAltGotoSend, "GOTO_SEND_TAB");
			rootPane.getActionMap().put("GOTO_SEND_TAB", actionListenerGotoSendTab);

			KeyStroke keyAltGotoReceive = KeyStroke.getKeyStroke(l.getMnemonicKeyEvent(l.tr("_Receive")),InputEvent.ALT_MASK);
			Action actionListenerGotoReceiveTab = new AbstractAction("GOTO_RECEIVE_TAB")
			{
				public void actionPerformed(ActionEvent actionEvent)
				{
					tabPanel.setSelectedIndex(1);
				}
			};

			inputMap.put(keyAltGotoReceive, "GOTO_RECEIVE_TAB");
			rootPane.getActionMap().put("GOTO_RECEIVE_TAB", actionListenerGotoReceiveTab);

			KeyStroke keyAltGotoGUI = KeyStroke.getKeyStroke(l.getMnemonicKeyEvent(l.tr("_GUI")),InputEvent.ALT_MASK);
			Action actionListenerGotoGUITab = new AbstractAction("GOTO_GUI_TAB")
			{
				public void actionPerformed(ActionEvent actionEvent)
				{
					tabPanel.setSelectedIndex(2);
				}
			};

			inputMap.put(keyAltGotoGUI, "GOTO_GUI_TAB");
			rootPane.getActionMap().put("GOTO_GUI_TAB", actionListenerGotoGUITab);
		}//end if os.isMac()
		return rootPane;
	}//end createRootPane()

//componentlistener
//========================================================================
	public void componentHidden(ComponentEvent e)
	{
		//m.p(e.getComponent().getClass().getName() + " --- Hidden");
	}

//========================================================================
	public void componentMoved(ComponentEvent e)
	{
		//m.p(e.getComponent().getClass().getName() + " --- Moved");
		//m.p(e.toString());
//////////
		//move all visible listdialogs along
	}

//========================================================================
	public void componentResized(ComponentEvent e)
	{
		//m.p(e.getComponent().getClass().getName() + " --- Resized ");
	}

//========================================================================
	public void componentShown(ComponentEvent e)
	{
		//m.p(e.getComponent().getClass().getName() + " --- Shown ");
	}

//========================================================================
//tabstate
	public void stateChanged(ChangeEvent e)
	{
		setFocusedWidget();
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
/*
		if(tabname.equals(l.tr("Send")))
		{
			text_name_s.requestFocus();
		}
		else if(tabname.equals(l.tr("Receive")))
		{
			text_name_r.requestFocus();
		}
		else if(tabname.equals(l.tr("GUI")))
		{
			checkbox_gui_osc_port_random_s.requestFocus();
		}
*/
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

/*
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
*/
}//end class ConfigureDialog
