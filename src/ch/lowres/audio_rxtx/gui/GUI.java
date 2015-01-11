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
import ch.lowres.audio_rxtx.gui.api.*;
import ch.lowres.audio_rxtx.gui.osc.*;

import java.awt.*;
import java.awt.event.*;

import com.illposed.osc.*;
import java.net.*;
import java.util.*;

import java.io.File;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

//========================================================================
public class GUI
{
	private static Main m;
	private static Fonts f;
	private static Languages l;

	public static int commonWidgetHeight=(int)(f.fontNormalSize*1.6);

	//relative to fontDefaultSize * fontLargeFactor (font on buttons)
	public static float buttonHeightScale=2.3f;
	public static int scrollbarIncrement=16;

	//osc gui io
	//will be set by .properties file
	public static boolean gui_osc_port_random_s=false;
	public static int gui_osc_port_s=-1;

	public static boolean gui_osc_port_random_r=false;
	public static int gui_osc_port_r=-1;

	public static boolean keep_cache=false;
	public static boolean show_both_panels=false;

	//handler for osc messages
	public static GuiOscListenerSend goscs;
	public static GuiOscListenerReceive goscr;

	//dalogs
	public static ConfigureDialog configure;
	public static AboutDialog about;
	public static InfoDialog info;

	//the main window
	public static JFrame mainframe;

	public static AppMenu applicationMenu;

	public static JScrollPane scrollerTabSend;
	public static JScrollPane scrollerTabReceive;

	public static JScrollBar scrollbarSend;
	public static JScrollBar scrollbarReceive;

	public static JPanel tabSend;
	public static JPanel tabReceive;

	public static JTabbedPane tabPanel = new JTabbedPane()
	{
		@Override
		public void paintComponent(Graphics g) 
		{
			//FocusPaint.gradient(g,tabPanel);
			super.paintComponent(g);
			FocusPaint.paint(g,tabPanel);
		}	
	};

	public static JPanel mainGrid;

	public static JPanel cardPanelSend;
	public static CardLayout cardLaySend;

	public static JPanel cardPanelReceive;
	public static CardLayout cardLayReceive;

	//cards
	public static FrontCardSend frontSend;
	public static RunningCardSend runningSend;

	public static FrontCardReceive frontReceive;
	public static RunningCardReceive runningReceive;

	public static StatusLabel labelStatus;

	//convenience class to add widgets to form
	public static FormUtility formUtility;

	public static Dimension screenDimension;

	//map containing all global key actions
	public static HashMap<KeyStroke, Action> actionMap = new HashMap<KeyStroke, Action>();

//========================================================================
	public static void init()
	{
		Images.init();
		Fonts.init();

		setCrossPlatformLAF();

		screenDimension=Toolkit.getDefaultToolkit().getScreenSize();

		formUtility=new FormUtility();

		//dialogs
		configure=new ConfigureDialog(mainframe,"Configure "+m.progName, true);
		about=new AboutDialog(mainframe, "About "+m.progName, true);
		info=new InfoDialog(mainframe, "Information "+m.progName, true);

		createForm();

		if(m.os.isMac())
		{
			Mac.init();
		}

		if(show_both_panels)
		{
			FormHelper.viewBothPanels();			
		}
		else
		{
			FormHelper.viewSendPanel();
		}

		if(m.apis.autostart || m.apir.autostart)
		{
			try{Thread.sleep(500);}catch(Exception ign){}
		}

		if(m.apis.autostart)
		{
			m.startTransmissionSend();
			if(m.apir.autostart)
			{
				try{Thread.sleep(100);}catch(Exception ign){}
			}
		}
		if(m.apir.autostart)
		{
			m.startTransmissionReceive();
		}

	}//end constructor

//http://stackoverflow.com/questions/1065691/how-to-set-the-background-color-of-a-jbutton-on-the-mac-os
//========================================================================
	public static void setCrossPlatformLAF()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			if(m.os.isMac())
			{
				//http://stackoverflow.com/questions/7252749/how-to-use-command-c-command-v-shortcut-in-mac-to-copy-paste-text
				InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
				im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
				im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
				im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
				im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
			}


			UIManager.put("TabbedPane.background",Colors.form_background);
			UIManager.put("TabbedPane.foreground",Colors.form_foreground);
			UIManager.put("TabbedPane.selected", Colors.form_background);
			UIManager.put("TabbedPane.selectedForeground",Colors.form_foreground);
			UIManager.put("TabbedPane.background",Colors.form_background.brighter().brighter());
			UIManager.put("TabbedPane.tabsOverlapBorder",false);

			//around the tabs
			//Insets(int top, int left, int bottom, int right) 
			UIManager.put("TabbedPane.tabAreaInsets",new Insets(10,10,0,0));
			//for all tabs, around inner text
			UIManager.put("TabbedPane.tabInsets",new Insets(5,5,5,5));

			//puts currently selected higher
			UIManager.put("TabbedPane.selectedTabPadInsets",new Insets(5,0,0,0));

			UIManager.put("TabbedPane.highlight",Colors.black);

			//"hide" focus
			UIManager.put("TabbedPane.focus",Colors.form_background);

		} catch (Exception e)
		{
			m.w(l.tr("Unable to set cross-platform look and feel")+": " + e);
		}
	}

//========================================================================
	private static void createForm()
	{
		//setCrossPlatformLAF();
		//setNativeLAF();

		mainframe=new JFrame(m.progName);
		mainframe.setLayout(new BorderLayout());
		mainframe.setIconImage(Images.appIcon);

		//"cards" / for switching views / all in one window
		cardPanelSend=new JPanel();
		cardLaySend=new CardLayout();
		cardPanelSend.setLayout(cardLaySend);

		cardPanelReceive=new JPanel();
		cardLayReceive=new CardLayout();
		cardPanelReceive.setLayout(cardLayReceive);

		tabSend=new JPanel(new BorderLayout());
		tabSend.add(cardPanelSend,BorderLayout.CENTER);

		scrollerTabSend=new JScrollPane (tabSend, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabSend.setWheelScrollingEnabled(true);

		scrollbarSend=scrollerTabSend.getVerticalScrollBar();
		scrollbarSend.setUnitIncrement(scrollbarIncrement);

		tabReceive=new JPanel(new BorderLayout());
		tabReceive.add(cardPanelReceive,BorderLayout.CENTER);
		scrollerTabReceive=new JScrollPane (tabReceive, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabReceive.setWheelScrollingEnabled(true);

		scrollbarReceive=scrollerTabReceive.getVerticalScrollBar();
		scrollbarReceive.setUnitIncrement(scrollbarIncrement);

		tabPanel.setFont(f.fontNormal);
		tabPanel.add(l.tr("Send"), scrollerTabSend);
		tabPanel.add(l.tr("Receive"), scrollerTabReceive);
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

		//http://stackoverflow.com/questions/5183687/java-remove-margin-padding-on-a-jtabbedpane
		tabPanel.setUI(new BasicTabbedPaneUI()
		{
			//top,left,right,bottom
			private final Insets borderInsets = new Insets(0,0,0,0);
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


		mainGrid=new JPanel(new GridLayout(1,2)); //y,x

		mainGrid.add(tabPanel);

		mainframe.add(mainGrid,BorderLayout.CENTER);

		//menu always on top
		//JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		applicationMenu=new AppMenu();
		mainframe.setJMenuBar(applicationMenu);

		frontSend=new FrontCardSend();
		frontSend.setValues();
		cardPanelSend.add(frontSend, "1");

		runningSend=new RunningCardSend();
		runningSend.setValues();
		cardPanelSend.add(runningSend, "2");

		frontReceive=new FrontCardReceive();
		frontReceive.setValues();
		cardPanelReceive.add(frontReceive, "1");

		runningReceive=new RunningCardReceive();
		runningReceive.setValues();
		cardPanelReceive.add(runningReceive, "2");

		labelStatus=new StatusLabel(l.tr("Ready"));

		mainframe.add(labelStatus,BorderLayout.SOUTH);

		frontSend.checkbox_format_16.requestFocus();

		addWindowListeners();

		addGlobalKeyListeners();

		mainframe.pack();
		mainframe.setResizable(false);
		setWindowCentered(mainframe);

		//"run" GUI
		mainframe.setVisible(true);

	}//end createForm

//========================================================================
	public static void setStatus(String message)
	{
		if(labelStatus!=null)
		{
			labelStatus.setStatus(message,2000);
		}
	}

//========================================================================
	public static void setStatusError(String message)
	{
		if(labelStatus!=null)
		{
			labelStatus.setStatusError(message,2000);
		}
	}

//========================================================================
	private static void addWindowListeners()
	{
		mainframe.addWindowListener(new WindowListener() 
		{
			@Override
			public void windowClosed(WindowEvent arg0){}
			@Override
			public void windowClosing(WindowEvent arg0)
			{
				//
				System.exit(0);		
			}
			@Override
			public void windowActivated(WindowEvent arg0) {/*p("---activated");*/}
			@Override
			public void windowDeactivated(WindowEvent arg0) {/*p("---deactivated");*/}
			@Override
			public void windowDeiconified(WindowEvent arg0) {/*p("---deiconified");*/}
			@Override
			public void windowIconified(WindowEvent arg0) {/*p("---iconified");*/}
			@Override
			public void windowOpened(WindowEvent arg0) {/*p("---opened");*/}
		});
	}//end addWindowListeners

//========================================================================
	public static void setWindowCentered(Frame f)
	{
		f.setLocation(
			(int)((screenDimension.getWidth()-f.getWidth()) / 2),
			(int)((screenDimension.getHeight()-f.getHeight()) / 2)
		);
	}

//========================================================================
	public static void setDialogCentered(Dialog d)
	{
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(d.getGraphicsConfiguration());

		d.setLocation(
			(int)((screenDimension.getWidth()-insets.left-insets.right-d.getWidth()) / 2),
			(int)((screenDimension.getHeight()-insets.top-insets.bottom-d.getHeight()) / 2)
		);
	}

//========================================================================
	public static void nextTab()
	{
		if(tabPanel.getTabCount()<1)
		{
			return;
		}
		int newIndex=tabPanel.getSelectedIndex();
		newIndex++;
		newIndex=newIndex % tabPanel.getTabCount();
		tabPanel.setSelectedIndex(newIndex);
	}

//========================================================================
	public static void prevTab()
	{
		if(tabPanel.getTabCount()<1)
		{
			return;
		}
		int newIndex=tabPanel.getSelectedIndex();
		newIndex--;
		tabPanel.setSelectedIndex(newIndex < 0 ? tabPanel.getTabCount()-1 : newIndex);
	}

//========================================================================
	private static void addGlobalKeyListeners()
	{
		JRootPane rootPane = mainframe.getRootPane();

		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		//InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

/*
		KeyStroke keyEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0);

		Action actionListenerConfirm = new AbstractAction("CONFIRM")
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				//check if a menu is selected, enter will act like a click or space
				SingleSelectionModel ssm=mainframe.getJMenuBar().getSelectionModel();
				//p("is selected: "+ssm.isSelected()+" index: "+ssm.getSelectedIndex());
				if(ssm.isSelected())
				{
					//http://www.java2s.com/Tutorial/Java/0240__Swing/GettingtheCurrentlySelectedMenuorMenuItem.htm
					MenuElement[] path = MenuSelectionManager.defaultManager().getSelectedPath();
					if (path.length > 1)
					{
						Component c = path[path.length-1].getComponent();
						if (c instanceof JMenuItem)
						{
							JMenuItem mi = (JMenuItem) c;
							MenuSelectionManager.defaultManager().clearSelectedPath();
							mi.doClick();
						}
					}
				}//end if menu is selected
			}
		};

		inputMap.put(keyEnter, "ENTER");
		rootPane.getActionMap().put("ENTER", actionListenerConfirm);
*/

		//for debug
		KeyStroke keyAltRight = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,InputEvent.ALT_MASK);
		actionMap.put(keyAltRight, new AbstractAction("DEBUG_FOCUS") 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				m.p("focus: "+KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
			}
		});

		//http://stackoverflow.com/questions/100123/application-wide-keyboard-shortcut-java-swing
		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kfm.addKeyEventDispatcher( new KeyEventDispatcher() 
		{
			@Override
			public boolean dispatchKeyEvent(KeyEvent e)
			{
				KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
				if ( actionMap.containsKey(keyStroke) )
				{
					final Action a = actionMap.get(keyStroke);
					final ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), null );
					SwingUtilities.invokeLater( new Runnable()
					{
						@Override
						public void run()
						{
							a.actionPerformed(ae);
						}
					} ); 
					return true;
				}
				return false;
			}
		});
	}//end addGlobalKeyListeners

//========================================================================
	public static void updateFont()
	{
		Fonts.change(mainframe);

		Fonts.change(configure);
		configure.repack();
		setDialogCentered(configure);

		Fonts.change(about);
		Fonts.change(info);

		about.pack();
		info.pack();

		setDialogCentered(about);
		setDialogCentered(info);

		mainframe.pack();

		setWindowCentered(mainframe);
	}
}//end class GUI