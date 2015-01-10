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

//import org.xnap.commons.i18n.*;

/**
* audio_rxtx GUI main class. This is the application entry point (having a main method).
* <p>
* Run: java -cp .:build/classes/ ch.lowres.audio_rxtx.gui.Main 
* <p>
* Run with splashscreen, more allocated memory: java -splash:src/gfx/audio_rxtx_splash_screen.png -Xms1024m -Xmx1024m -cp .:build/classes/ ch.lowres.audio_rxtx.gui.Main
* <p>
* Run distributable multiplatform .jar: java -jar audio_rxtx_gui_1416691911.jar
* <p>
* To dump the AWT component hierarchy, focus any component and press Ctrl+Shift+F1.
*
* @author <tom@trellis.ch>
* @see <a href="https://github.com/7890/audio_rxtx_gui">https://github.com/7890/audio_rxtx_gui</a>
* @see <a href="https://github.com/7890/jack_tools">https://github.com/7890/jack_tools</a>
* @see <a href="http://stackoverflow.com/questions/6671021/how-to-debug-java-swing-layouts/6674624#6674624">http://stackoverflow.com/questions/6671021/how-to-debug-java-swing-layouts/6674624#6674624</a>
*/
//========================================================================
public class Main
{
	public final static String progName="audio_rxtx GUI";
	public final static String progVersion="0.3";
	public final static String progNameSymbol="audio_rxtx_gui_v"+progVersion+"_"+150110;

	public final static String defaultPropertiesFileName="audio_rxtx_gui.properties";

	public final static String reportIssueUrl="https://github.com/7890/audio_rxtx_gui/issues";
	//dummy
	public final static String newestVersionFileUrl="https://raw.githubusercontent.com/7890/audio_rxtx_gui/master/README.md";

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

	//osc sender, receiver for communication gui<->jack_audio_send
	public static OSCPortOut portOutSend; //sender
	public static OSCPortIn portInSend; //receiver

	public static OSCPortOut portOutReceive; //sender
	public static OSCPortIn portInReceive; //receiver

	public static final long WAIT_FOR_SOCKET_CLOSE=3;

	//handler for osc messages
	public static GuiOscListenerSend goscs;
	public static GuiOscListenerReceive goscr;

	//holding several config values, construct command line for jack_audio_send
	public static jack_audio_send_cmdline_API apis;
	public static jack_audio_receive_cmdline_API apir;

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

	public final static int FRONT=0;
	public final static int RUNNING=1;

	public static int sendStatus=FRONT;
	public static int receiveStatus=FRONT;

	//convenience class to add widgets to form
	public static FormUtility formUtility;

	public static Dimension screenDimension;

	//platform specific system tmp directory
	public static String tmpDir;

	//thread to run binaries
	public static RunCmd cmdSend;
	public static RunCmd cmdReceive;

	//watching RunCmd
	public static Watchdog dogSend;
	public static Watchdog dogReceive;

	public static IOTools iot=new IOTools();
	public static OSTest os=new OSTest();

	//map containing all global key actions
	public static HashMap<KeyStroke, Action> actionMap = new HashMap<KeyStroke, Action>();

	public static int ctrlOrCmd=InputEvent.CTRL_MASK;

//========================================================================
	public static void main(String[] args) 
	{
		//header
		p("");
		p(progName+" v"+progVersion);
		p("(c) 2014-2015 Thomas Brand <tom@trellis.ch>");

		Main m=new Main(args);
	}//end main

//========================================================================
	public Main(String[] args)
	{
		if(args.length>0 && (args[0].equals("--help") || args[0].equals("-h")))
		{
			p("");
			p(l.tr("First argument: <URI of .properties file to use>"));
			p(l.tr("A file called '")+defaultPropertiesFileName+l.tr("' in the current directory will be loaded if no argument was given and the file is available."));
			p("");
			System.exit(0);
		}

		p("");
		p(l.tr("MD5 Sum of jar")+": "+iot.getJarMd5Sum()+"\n");
		p_undl(l.tr("Build Information"));
		p(BuildInfo.get()+"\n");

		p_undl(l.tr("Runtime Information"));
		p("java.version: "+os.getJavaVersion());
		p("java.vm.name: "+os.getVMName());
		p("java.vm.version: "+os.getVMVersion());
		p("java.io.tmpdir: "+os.getTempDir());
		p("os.name: "+os.getOSName());
		p("64bit: "+os.is64Bits());
		p("DPI: "+os.getDPI()+"\n");

		//will remove tmpdir on program exit if keep_cache==false
		createShutDownHook();

		//os specific / or \ path separator
		//tmpDir=System.getProperty("java.io.tmpdir")+File.separator+progNameSymbol;
		tmpDir=os.getTempDir()+File.separator+progNameSymbol;
		p(l.tr("Temporary Cache Directory: '")+tmpDir+"'");

		apis=new jack_audio_send_cmdline_API();
		apir=new jack_audio_receive_cmdline_API();

		String dir="lin32";

		if(os.isWindows())
		{
			dir="win";
		}
		else if(os.isMac())
		{
			dir="mac";
			ctrlOrCmd=KeyEvent.META_DOWN_MASK;
		}
		else if(os.isLinux())
		{
			if(os.is64Bits())
			{
				dir="lin64";
			}
		}

		apis.setPrefixPath(tmpDir+File.separator+"resources"+File.separator+dir);
		apir.setPrefixPath(tmpDir+File.separator+"resources"+File.separator+dir);

		if(args.length>0)
		{
			//load default settings from properties file in jar then try overload with given file
			Settings.load(args[0]);
		}
		else
		{
			//load default settings from properties file in jar then try overload from current dir
			Settings.load(defaultPropertiesFileName);
		}

		File fTest=new File(tmpDir);
		if(fTest!=null && fTest.exists() && fTest.canRead() && fTest.isDirectory())
		{
			p(l.tr("Using resources from cache"));
		}
		else if(!fTest.exists())
		{
			//extract
			iot.copyJarContent("/resources/README.txt",tmpDir);
			iot.copyJarContent("/resources/COPYING.txt",tmpDir);
			iot.copyJarContent("/resources/doc",tmpDir);
			//platform specific
			iot.copyJarContent("/resources/"+dir,tmpDir);

			//make binaries executable 750 rwxr-x---
			if(os.isMac() || os.isLinux())
			{
				p(l.tr("Setting permissions of binaries"));
				RunCmd setPerms=new RunCmd("chmod 750 "+tmpDir+"/resources/"+dir+"/*");
				setPerms.start();
			}
			//windows binaries keep executable flag
			else if(os.isWindows()){}
		}
		else
		{
			e("error creating tmp directory to extract jar contents: '"+tmpDir+"'");
		}

		setCrossPlatformLAF();

		Images.init();

		screenDimension=Toolkit.getDefaultToolkit().getScreenSize();

		//init
		Fonts.recreate();

		formUtility=new FormUtility();

		//dialogs
		configure=new ConfigureDialog(mainframe,"Configure "+progName, true);
		about=new AboutDialog(mainframe, "About "+progName, true);
		info=new InfoDialog(mainframe, "Information "+progName, true);

		createForm();

		if(os.isMac())
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

		if(apis.autostart || apir.autostart)
		{
			try{Thread.sleep(500);}catch(Exception ign){}
		}

		if(apis.autostart)
		{
			startTransmissionSend();
			if(apir.autostart)
			{
				try{Thread.sleep(100);}catch(Exception ign){}
			}
		}
		if(apir.autostart)
		{
			startTransmissionReceive();
		}

	}//end constructor

//http://stackoverflow.com/questions/1065691/how-to-set-the-background-color-of-a-jbutton-on-the-mac-os
//========================================================================
	public static void setCrossPlatformLAF()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			if(os.isMac())
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
			w(l.tr("Unable to set cross-platform look and feel")+": " + e);
		}
	}

//========================================================================
	private static void createForm()
	{
		//setCrossPlatformLAF();
		//setNativeLAF();

		mainframe=new JFrame(progName);
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

		tabPanel.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				setFocusedWidget();
			}
		});

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
	public static void startTransmissionSend()
	{
		//form already read/vaildated in front button handler

		frontSend.setStatus("Starting GUI OSC Server");

		//for gui<->jack_audio_send communication
		if(startOscServerSend()==-1)
		{
			e("the audio_rxtx osc gui server could not be started.");
			frontSend.setStatus("GUI OSC Server Could Not Be Started");
			return;
		}
		else
		{
			p(l.tr("OSC GUI server started on UDP port")+" "+gui_osc_port_s);
			frontSend.setStatus("GUI OSC Server Started");
		}

		frontSend.setStatus("Executing jack_audio_send");

		p(l.tr("Execute")+": "+apis.getCommandLineString());
		cmdSend=new RunCmd(apis.getCommandLineString());

		cmdSend.devNull(!apis.verbose);

		//cmdSend.maxPrio();
		cmdSend.start();

		if(dogSend!=null)
		{
			dogSend.cancel();
		}
		dogSend=new Watchdog(cmdSend);
		dogSend.start();

		AppMenu.setForRunningSend();

		runningSend.setValues();
		cardLaySend.show(cardPanelSend, "2");
		runningSend.button_default.requestFocus();

		sendStatus=RUNNING;

	}//end startTransmissionSend

//========================================================================
	public static void startTransmissionReceive()
	{
		//form already read/vaildated in front button handler

		frontReceive.setStatus("Starting GUI OSC Server");

		//for gui<->jack_audio_send communication
		if(startOscServerReceive()==-1)
		{
			e("the audio_rxtx osc gui server could not be started.");
			frontReceive.setStatus("GUI OSC Server Could Not Be Started");
			return;
		}
		else
		{
			p(l.tr("OSC GUI server started on UDP port")+" "+gui_osc_port_r);
			frontReceive.setStatus("GUI OSC Server Started");
		}

		frontReceive.setStatus("Executing jack_audio_receive");

		p(l.tr("Execute")+": "+apir.getCommandLineString());
		cmdReceive=new RunCmd(apir.getCommandLineString());

		cmdReceive.devNull(!apir.verbose);

		//cmdReceive.maxPrio();
		cmdReceive.start();

		if(dogReceive!=null)
		{
			dogReceive.cancel();
		}
		dogReceive=new Watchdog(cmdReceive);
		dogReceive.start();

		AppMenu.setForRunningReceive();
		runningReceive.setValues();
		cardLayReceive.show(cardPanelReceive, "2");
		runningReceive.button_default.requestFocus();

		receiveStatus=RUNNING;

	}//end startTransmissionReceive

//========================================================================
	public static void stopTransmissionSend()
	{
		p(l.tr("Stopping transmission (send)"));

		//terminate jack_audio_send running in thread
		OSCMessage msg=new OSCMessage("/quit");

		try
		{
			portOutSend.send(msg);
		}
		catch(Exception sndex)
		{///
		};

		if(dogSend!=null)
		{
			dogSend.cancel();
		}

		if(cmdSend!=null)
		{
			cmdSend.cancel();
		}
		cmdSend=null;

		if(portInSend!=null)
		{
			portInSend.stopListening();
			portInSend.close();
			portInSend=null;
		}
		if(portOutSend!=null)
		{
			portOutSend.close();
			portOutSend=null;
		}

		AppMenu.setForFrontScreenSend();

		apis.total_connected_ports=0;
		apis.jack_sample_rate=0;
		apis.jack_period_size=0;
		apis.msg_size=0;
		apis.transfer_size=0;
		apis.expected_network_data_rate=0;

		//show main panel again
		cardLaySend.show(cardPanelSend, "1");

		frontSend.button_default.requestFocus();
		frontSend.setStatus(l.tr("Ready"));

		mainframe.pack();

		sendStatus=FRONT;

	}//end stopTransmissionSend

//========================================================================
	public static void stopTransmissionReceive()
	{
		p(l.tr("Stopping transmission (receive)"));

		//terminate jack_audio_receive running in thread
		OSCMessage msg=new OSCMessage("/quit");

		try
		{
			portOutReceive.send(msg);
		}
		catch(Exception sndex)
		{///
		};

		if(dogReceive!=null)
		{
			dogReceive.cancel();
		}

		if(cmdReceive!=null)
		{
			cmdReceive.cancel();
		}
		cmdReceive=null;

		if(portInReceive!=null)
		{
			portInReceive.stopListening();
			portInReceive.close();
			portInReceive=null;
		}
		if(portOutReceive!=null)
		{
			portOutReceive.close();
			portOutReceive=null;
		}

		AppMenu.setForFrontScreenReceive();

		apir.total_connected_ports=0;
		apir.jack_sample_rate=0;
		apir.jack_period_size=0;

		//show main panel again
		cardLayReceive.show(cardPanelReceive, "1");
		frontReceive.button_default.requestFocus();
		frontReceive.setStatus(l.tr("Ready"));

		receiveStatus=FRONT;

	}//end stopTransmissionReceive

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
	public static int startOscServerSend()
	{
		try
		{
			DatagramSocket ds;
			if(gui_osc_port_random_s)
			{
				ds=new DatagramSocket();
				gui_osc_port_s=ds.getLocalPort();
			}
			else
			{
				ds=new DatagramSocket(gui_osc_port_s);
			}

			if(portInSend!=null)
			{
				portInSend.stopListening();
				portInSend.close();
			}
			if(portOutSend!=null)
			{
				portOutSend.close();
			}

			portInSend=new OSCPortIn(ds);
			portOutSend=new OSCPortOut(InetAddress.getLocalHost(), apis._lport, ds);

			goscs=new GuiOscListenerSend(runningSend, apis);

			//catch every message
			portInSend.addListener("/*", goscs);
			portInSend.startListening();
		}
		catch(Exception oscex)
		{
			e(l.tr("Could not start OSC GUI server on UDP port")+" "+gui_osc_port_s+". "+oscex.getMessage());
			return -1;
		}

		return 0;
	}//end startOscServerSend

////make one for both
//vars: port random, port, portIn___ portOut___, goscr, apir
//========================================================================
	public static int startOscServerReceive()
	{
		try
		{
			DatagramSocket ds;
			if(gui_osc_port_random_r)
			{
				ds=new DatagramSocket();
				gui_osc_port_r=ds.getLocalPort();
			}
			else
			{
				ds=new DatagramSocket(gui_osc_port_r);
			}

			if(portInReceive!=null)
			{
				portInReceive.stopListening();
				portInReceive.close();
			}
			if(portOutReceive!=null)
			{
				portOutReceive.close();
			}

			portInReceive=new OSCPortIn(ds);
			portOutReceive=new OSCPortOut(InetAddress.getLocalHost(), apir._lport, ds);

			goscr=new GuiOscListenerReceive(runningReceive, apir);

			//catch every message
			portInReceive.addListener("/*", goscr);
			portInReceive.startListening();
		}
		catch(Exception oscex)
		{
			e(l.tr("Could not start OSC GUI server on UDP port")+" "+gui_osc_port_r+". "+oscex.getMessage());
			return -1;
		}

		return 0;
	}//end startOscServerReceive

//========================================================================
	//http://stackoverflow.com/questions/11435533/how-does-ctrl-c-work-with-java-program
	private static void createShutDownHook()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				p("");
				w(l.tr("Shutdown signal received!"));
				stopTransmissionSend();
				stopTransmissionReceive();

				if(!keep_cache)
				{
					p(l.tr("Cleaning up..."));
					p(l.tr("Removing temporary cache directory '")+tmpDir+"'");
					IOTools.deleteDirectory(new File(tmpDir));
				}
				//possibly more clean up tasks here 
				p(l.tr("Done! Bye"));
			}
		}));
	}//end createShutDownHook

//========================================================================
	public static String nTimes(String s, int count)
	{
		String ret="";
		for(int i=0;i<count;i++)
		{
			ret+=s;
		}
		return ret;
	}

//generic print to std out
//========================================================================
	public static void p(String s)
	{
		System.out.println(s);
	}

//generic print to std out, underlined
//========================================================================
	public static void p_undl(String s)
	{
		System.out.println(s);
		System.out.println(nTimes("-",s.length()));
	}

//styled print to std out, warning
//========================================================================
	public static void w(String s)
	{
		System.out.println("/:\\ "+s);
	}

//styled print to std err
//========================================================================
	public static void e(String s)
	{
		System.out.println("/!\\ "+s);
	}

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
	public static void setFocusedWidget()
	{
//		String tabname=tabPanel.getTitleAt(tabPanel.getSelectedIndex());
//		setFocusedWidget(tabname);
	}

//========================================================================
	public static void setFocusedWidget(String tabname)
	{
/*
		if(tabname.equals(l.tr("Send")))
		{
		}
		else if(tabname.equals(l.tr("Receive")))
		{
		}
*/
	}

//========================================================================
	private static void addGlobalKeyListeners()
	{
		JRootPane rootPane = mainframe.getRootPane();

		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		//InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		KeyStroke keyEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0);

		Action actionListenerConfirm = new AbstractAction("CONFIRM")
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
/////doesnt work
//FormHelper.defaultCardAction((Component)actionEvent.getSource());

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

//////////////
		KeyStroke keyAltRight = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,InputEvent.ALT_MASK);
		actionMap.put(keyAltRight, new AbstractAction("DEBUG_FOCUS") 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{

				p("focus: "+KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
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
	public static String tr(String text)
	{
///////////////////
//moved to languages
//needs refactoring in calling classes
		return Languages.tr(text);
	}

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
}//end class Main

/**
* @see <a href="http://tldp.org/LDP/abs/html/exitcodes.html"></a>
*
* Exit Code Number	Meaning	Example	Comments
* 1	Catchall for general errors	let "var1 = 1/0"	Miscellaneous errors, such as "divide by zero" and other impermissible operations
* 2	Misuse of shell builtins (according to Bash documentation)	empty_function() {}	Missing keyword or command, or permission problem (and diff return code on a failed binary file comparison).
* 126	Command invoked cannot execute	/dev/null	Permission problem or command is not an executable
* 127	"command not found"	illegal_command	Possible problem with $PATH or a typo
* 128	Invalid argument to exit	exit 3.14159	exit takes only integer args in the range 0 - 255 (see first footnote)
* 128+n	Fatal error signal "n"	kill -9 $PPID of script	$? returns 137 (128 + 9)
* 130	Script terminated by Control-C	Ctl-C	Control-C is fatal error signal 2, (130 = 128 + 2, see above)
* 255	Exit status out of range	exit -1	exit takes only integer args in the range 0 - 255
*/
