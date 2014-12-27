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

import javax.swing.plaf.basic.BasicTabbedPaneUI;

import org.xnap.commons.i18n.*;

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
	//multilanguage
	public static I18n i18n;

	public final static String progName="audio_rxtx GUI";
	public final static String progVersion="0.2";
	public final static String progNameSymbol="audio_rxtx_gui_v"+progVersion+"_"+141030;

	public final static String defaultPropertiesFileName="audio_rxtx_gui.properties";

	public final static String reportIssueUrl="https://github.com/7890/audio_rxtx_gui/issues";
	//dummy
	public final static String newestVersionFileUrl="https://raw.githubusercontent.com/7890/audio_rxtx_gui/master/README.md";

	public static int panelWidth=10;
	public static int panelHeight=10;

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

	//dalogs
	public static ConfigureDialog configure;
	public static AboutDialog about;

	//holding several config values, construct command line for jack_audio_send
	public static jack_audio_send_cmdline_API apis;
	public static jack_audio_receive_cmdline_API apir;

	public static Image appIcon;

	//the main window
	public static JFrame mainframe;

	public static AppMenu applicationMenu;

	public static JScrollPane scrollerTabSend;
	public static JScrollPane scrollerTabReceive;

	public static JScrollBar scrollbarSend;
	public static JScrollBar scrollbarReceive;

	public static int scrollbarIncrement=11;

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

	public static Font fontNormal;
	public static Font fontLarge;

	//platform specific system tmp directory
	public static String tmpDir;

	//thread to run binaries
	public static RunCmd cmdSend;
	public static RunCmd cmdReceive;

	//watching RunCmd
	public static Watchdog dogSend;
	public static Watchdog dogReceive;

	public static OSTest os;

	//map containing all global key actions
	public static HashMap<KeyStroke, Action> actionMap = new HashMap<KeyStroke, Action>();

//========================================================================
	public static void main(String[] args) 
	{
		//header
		p("");
		p(progName+" v"+progVersion);
		p("(c) 2014 Thomas Brand <tom@trellis.ch>");

		Main m=new Main(args);
	}//end main

//========================================================================
	public Main(String[] args)
	{
		///test
		i18n = I18nFactory.getI18n(getClass(), "ch.lowres.audio_rxtx.gui.i18n.Messages", java.util.Locale.GERMAN);

		if(args.length>0 && (args[0].equals("--help") || args[0].equals("-h")))
		{
			p("");
			p(Main.tr("First argument: <URI of .properties file to use>"));
			p(Main.tr("A file called '")+defaultPropertiesFileName+Main.tr("' in the current directory will be loaded if no argument was given and the file is available."));
			p("");
			System.exit(0);
		}

		IOTools iot=new IOTools();
		p("");
		p(Main.tr("MD5 Sum of jar")+": "+iot.getJarMd5Sum()+"\n");
		p_undl(Main.tr("Build Information"));
		p(BuildInfo.get()+"\n");

		os=new OSTest();
		p_undl(Main.tr("Runtime Information"));
		p("Host OS: "+os.getOSName());
		p("JVM: "+os.getVMName()+"\n");

		//os specific / or \ path separator
		tmpDir=System.getProperty("java.io.tmpdir")+File.separator+progNameSymbol;
		p(Main.tr("Temporary Cache Directory: '")+tmpDir+"'");

		apis=new jack_audio_send_cmdline_API();
		apir=new jack_audio_receive_cmdline_API();

		if(os.isWindows())
		{
			apis.setPrefixPath(tmpDir+File.separator+"resources"+File.separator+"win");
			apir.setPrefixPath(tmpDir+File.separator+"resources"+File.separator+"win");
		}
		else if(os.isMac())
		{
			apis.setPrefixPath(tmpDir+File.separator+"resources"+File.separator+"mac");
			apir.setPrefixPath(tmpDir+File.separator+"resources"+File.separator+"mac");
		}
		else if(os.isLinux())
		{
			String dir="lin32";
			if(os.is64Bits())
			{
				dir="lin64";
			}
			apis.setPrefixPath(tmpDir+File.separator+"resources"+File.separator+dir);
			apir.setPrefixPath(tmpDir+File.separator+"resources"+File.separator+dir);
		}

		if(args.length>0)
		{
			//load default settings from properties file in jar then try overload with given file
			IOTools.loadSettings(args[0]);
		}
		else
		{
			//load default settings from properties file in jar then try overload from current dir
			IOTools.loadSettings(defaultPropertiesFileName);
		}

		File fTest=new File(tmpDir);
		if(fTest!=null && fTest.exists() && fTest.canRead() && fTest.isDirectory())
		{
			p(Main.tr("Using resources from cache"));
		}
		else if(!fTest.exists())
		{
			//extract
			iot.copyJarContent("/resources/README.txt",tmpDir);
			iot.copyJarContent("/resources/COPYING.txt",tmpDir);
			iot.copyJarContent("/resources/doc",tmpDir);

			if(os.isWindows())
			{
				iot.copyJarContent("/resources/win",tmpDir);
			}
			else if(os.isMac())
			{
				iot.copyJarContent("/resources/mac",tmpDir);

				p(Main.tr("Setting permissions of binaries"));
				RunCmd setPerms=new RunCmd("chmod 750 "+tmpDir+"/resources/mac/*");
				setPerms.start();
			}
			else if(os.isLinux())
			{
				String dir="lin32";
				if(os.is64Bits())
				{
					dir="lin64";
				}
				iot.copyJarContent("/resources/"+dir,tmpDir);
				p(Main.tr("Setting permissions of binaries"));
				RunCmd setPerms=new RunCmd("chmod 750 "+tmpDir+"/resources/"+dir+"/*");
				setPerms.start();
			}
		}
		else
		{
			e("error creating tmp directory to extract jar contents: '"+tmpDir+"'");
		}

		//will remove tmpdir on program exit if keep_cache==false
		createShutDownHook();

		setCrossPlatformLAF();

		appIcon=iot.createImageFromJar("/resources/audio_rxtx_icon.png");

		screenDimension=Toolkit.getDefaultToolkit().getScreenSize();

		fontNormal=new ALabel().getFont();
		fontLarge=iot.createFontFromJar("/resources/AudioMono.ttf",18f);

		formUtility=new FormUtility();

		//dialogs

		configure=new ConfigureDialog(mainframe,"Configure "+progName, true);
		about=new AboutDialog(mainframe, "About "+progName, true);

		createForm();

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

/**
* @see <a href="http://stackoverflow.com/questions/11116386/java-gtk-native-look-and-feel-looks-bad-and-bold"></a>
*/
//========================================================================
	public static void setNativeLAF()
	{
		// Native L&F
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			w(Main.tr("Unable to set native look and feel")+": " + e);
		}
	}

/**
* @see <a href="http://stackoverflow.com/questions/1065691/how-to-set-the-background-color-of-a-jbutton-on-the-mac-os"></a>
*/
//========================================================================
	public static void setCrossPlatformLAF()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e)
		{
			w(Main.tr("Unable to set cross platform look and feel")+": " + e);
		}
	}

//========================================================================
	private static void createForm()
	{
		//setCrossPlatformLAF();
		//setNativeLAF();

		mainframe=new JFrame(progName);
		mainframe.setBackground(Colors.form_background);
		mainframe.setForeground(Colors.form_foreground);
		mainframe.setLayout(new BorderLayout());
		mainframe.setIconImage(appIcon);

		//"cards" / for switching views / all in one window
		cardPanelSend=new JPanel();
		cardLaySend=new CardLayout();
		cardPanelSend.setLayout(cardLaySend);

		cardPanelReceive=new JPanel();
		cardLayReceive=new CardLayout();
		cardPanelReceive.setLayout(cardLayReceive);

		tabSend=new JPanel(new BorderLayout());
		tabSend.setBackground(Colors.form_background);
		tabSend.add(cardPanelSend,BorderLayout.CENTER);

		scrollerTabSend=new JScrollPane (tabSend, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabSend.getViewport().setBackground(Colors.form_background);
		scrollerTabSend.setWheelScrollingEnabled(true);

		scrollbarSend=scrollerTabSend.getVerticalScrollBar();
		scrollbarSend.setUnitIncrement(scrollbarIncrement);

		tabReceive=new JPanel(new BorderLayout());
		tabReceive.setBackground(Colors.form_background);
		tabReceive.add(cardPanelReceive,BorderLayout.CENTER);
		scrollerTabReceive=new JScrollPane (tabReceive, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabReceive.getViewport().setBackground(Colors.form_background);
		scrollerTabReceive.setWheelScrollingEnabled(true);

		scrollbarReceive=scrollerTabReceive.getVerticalScrollBar();
		scrollbarReceive.setUnitIncrement(scrollbarIncrement);

		//start tabbed
		tabPanel.add(Main.tr("Send"), scrollerTabSend);
		tabPanel.add(Main.tr("Receive"), scrollerTabReceive);
		tabPanel.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				setFocusedWidget();
			}
		});

/**
* @see <a href="http://stackoverflow.com/questions/5183687/java-remove-margin-padding-on-a-jtabbedpane"></a>
*/
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
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

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

		labelStatus=new StatusLabel(Main.tr("Ready"));
		labelStatus.setBackground(Colors.status_background);
		labelStatus.setForeground(Colors.status_foreground);

		mainframe.add(labelStatus,BorderLayout.SOUTH);

		frontSend.checkbox_format_16.requestFocus();

		addWindowListeners();

		addGlobalKeyListeners();

		mainframe.pack();
		mainframe.setResizable(false);
		setWindowCentered(mainframe);

		//"run" GUI
		mainframe.setVisible(true);

/*
p("cardPanelSend "+cardPanelSend.getPreferredSize().getWidth()+" "+cardPanelSend.getPreferredSize().getHeight());
p("tabSend "+tabSend.getPreferredSize().getWidth()+" "+tabSend.getPreferredSize().getHeight());
p("frontSend "+frontSend.getPreferredSize().getWidth()+" "+frontSend.getPreferredSize().getHeight());
p("insets "+mainframe.getInsets().left+" "+mainframe.getInsets().right+" "+mainframe.getInsets().top+" "+mainframe.getInsets().bottom);
p("application_menu "+applicationMenu.getPreferredSize().getWidth()+" "+applicationMenu.getPreferredSize().getHeight());
p("label_status "+labelStatus.getPreferredSize().getWidth()+" "+labelStatus.getPreferredSize().getHeight());
p("button_default "+frontSend.button_default.getPreferredSize().getWidth()+" "+frontSend.button_default.getPreferredSize().getHeight());
*/
		panelHeight=(int)(
			cardPanelSend.getPreferredSize().getHeight()
			+applicationMenu.getPreferredSize().getHeight()
			+labelStatus.getPreferredSize().getHeight()
			+frontSend.button_default.getPreferredSize().getHeight()
		);

		panelWidth=(int)(mainGrid.getPreferredSize().getWidth());

/*
		mainframe.setSize(
			panelWidth+mainframe.getInsets().left+mainframe.getInsets().right,
			panelHeight+mainframe.getInsets().top+mainframe.getInsets().bottom
		);
*/
	}//end createForm

//========================================================================
	public static void setStatus(String message)
	{
		if(labelStatus!=null)
		{
			labelStatus.setStatus(message,2000);
		}
		//p("MAIN STATUS "+s);
	}

//========================================================================
	public static void setStatusError(String message)
	{
		if(labelStatus!=null)
		{
////
labelStatus.setBackground(Colors.black);
			labelStatus.setStatusError(message,2000);
		}
		//p("MAIN STATUS "+s);
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
			p(Main.tr("OSC GUI server started on UDP port")+" "+gui_osc_port_s);
			frontSend.setStatus("GUI OSC Server Started");
		}

		frontSend.setStatus("Executing jack_audio_send");

		p(Main.tr("Execute")+": "+apis.getCommandLineString());
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
			p(Main.tr("OSC GUI server started on UDP port")+" "+gui_osc_port_r);
			frontReceive.setStatus("GUI OSC Server Started");
		}

		frontReceive.setStatus("Executing jack_audio_receive");

		p(Main.tr("Execute")+": "+apir.getCommandLineString());
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
		p(Main.tr("Stopping transmission (send)"));

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
		frontSend.setStatus(Main.tr("Ready"));

		sendStatus=FRONT;

	}//end stopTransmissionSend

//========================================================================
	public static void stopTransmissionReceive()
	{
		p(Main.tr("Stopping transmission (receive)"));

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
		frontReceive.setStatus(Main.tr("Ready"));

		receiveStatus=FRONT;

	}//end stopTransmissionReceive

//========================================================================
	private static void addWindowListeners()
	{
		mainframe.addWindowListener(new WindowListener() 
		{
			@Override
			public void windowClosed(WindowEvent arg0)
			{
				///
				System.exit(0);
			}
			@Override
			public void windowClosing(WindowEvent arg0)
			{
				///
				System.exit(0);		
			}
			@Override
			public void windowActivated(WindowEvent arg0) {}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowOpened(WindowEvent arg0) {}
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
			e(Main.tr("Could not start OSC GUI server on UDP port")+" "+gui_osc_port_s+". "+oscex.getMessage());
			return -1;
		}

		return 0;
	}//end startOscServerSend

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
			e(Main.tr("Could not start OSC GUI server on UDP port")+" "+gui_osc_port_r+". "+oscex.getMessage());
			return -1;
		}

		return 0;
	}//end startOscServerReceive

/**
* @see <a href="http://stackoverflow.com/questions/11435533/how-does-ctrl-c-work-with-java-program"></a>
*/
//========================================================================
	private static void createShutDownHook()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				p("");
				w(Main.tr("Shutdown signal received!"));
				stopTransmissionSend();
				stopTransmissionReceive();

				if(!keep_cache)
				{
					p(Main.tr("Cleaning up..."));
					p(Main.tr("Removing temporary cache directory '")+tmpDir+"'");
					IOTools.deleteDirectory(new File(tmpDir));
				}
				//possibly more clean up tasks here 
				p(Main.tr("Done! Bye"));
			}
		}));
	}

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
		if(tabname.equals(Main.tr("Send")))
		{
		}
		else if(tabname.equals(Main.tr("Receive")))
		{
		}
*/
	}

//========================================================================
	private static void addGlobalKeyListeners()
	{
		KeyStroke keyPageUp = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP,0);
		actionMap.put(keyPageUp, new AbstractAction("pgup") 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(configure.isVisible())
				{
					configure.prevTab();
				}
				else if(!about.isVisible())
				{
					prevTab();
				}
			}
		});

		KeyStroke keyPageDown = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN,0);
		actionMap.put(keyPageDown, new AbstractAction("pgdown") 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(configure.isVisible())
				{
					configure.nextTab();
				}
				else if(!about.isVisible())
				{
					nextTab();
				}
			}
		});

		KeyStroke keyAltUp = KeyStroke.getKeyStroke(KeyEvent.VK_UP,InputEvent.ALT_MASK);
		actionMap.put(keyAltUp, new AbstractAction("alt_up") 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(configure.isVisible())
				{
					configure.scrollUp();
				}
				else if(!about.isVisible())
				{
				}
			}
		});

		KeyStroke keyAltDown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,InputEvent.ALT_MASK);
		actionMap.put(keyAltDown, new AbstractAction("alt_down") 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(configure.isVisible())
				{
					configure.scrollDown();
				}
				else if(!about.isVisible())
				{
				}
			}
		});

		KeyStroke keyAltHome = KeyStroke.getKeyStroke(KeyEvent.VK_HOME,InputEvent.ALT_MASK);
		actionMap.put(keyAltHome, new AbstractAction("alt_home") 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(configure.isVisible())
				{
					configure.scrollTop();

				}
				else if(!about.isVisible())
				{
				}
			}
		});

		KeyStroke keyAltEnd = KeyStroke.getKeyStroke(KeyEvent.VK_END,InputEvent.ALT_MASK);
		actionMap.put(keyAltEnd, new AbstractAction("alt_end") 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(configure.isVisible())
				{
					configure.scrollBottom();
				}
				else if(!about.isVisible())
				{
				}
			}
		});

		//if menu is disabled, the associated keyboard shortcuts (ctrl+_) won't work
		KeyStroke keyCtrlM = KeyStroke.getKeyStroke(KeyEvent.VK_M,InputEvent.CTRL_MASK);
		actionMap.put(keyCtrlM, new AbstractAction("ctrl_m") 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(!configure.isVisible() && !about.isVisible())
				{
					if(applicationMenu.isVisible())
					{
						applicationMenu.setVisible(false);
					}
					else
					{
						applicationMenu.setVisible(true);
					}
				}
			}
		});

		KeyStroke keyEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0);
		actionMap.put(keyEnter, new AbstractAction("enter") 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(configure.isVisible())
				{
					configure.dialogConfirmed();
				}
				else if(about.isVisible())
				{
					about.setVisible(false);
				}
				else
				{
					FormHelper.defaultCardAction((Component)e.getSource());
				}

				//check if a menu is selected, enter will act like a click or space
				SingleSelectionModel ssm=mainframe.getJMenuBar().getSelectionModel();
				//p("is selected: "+ssm.isSelected()+" index: "+ssm.getSelectedIndex());

				JMenu m=null;
				if(ssm.isSelected())
				{
					m=mainframe.getJMenuBar().getMenu(ssm.getSelectedIndex());

/**
* @see <a href="http://www.java2s.com/Tutorial/Java/0240__Swing/GettingtheCurrentlySelectedMenuorMenuItem.htm"></a>
*/
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
				}
			}
		});

		KeyStroke keyEsc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0);
		actionMap.put(keyEsc, new AbstractAction("esc") 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(configure.isVisible())
				{
					configure.dialogCancelled();
				}
				else if(about.isVisible())
				{
					about.setVisible(false);
				}

				SingleSelectionModel ssm=mainframe.getJMenuBar().getSelectionModel();
				//p("is selected: "+ssm.isSelected()+" index: "+ssm.getSelectedIndex());
				if(ssm.isSelected())
				{
					JMenu m=mainframe.getJMenuBar().getMenu(ssm.getSelectedIndex());
					MenuSelectionManager.defaultManager().clearSelectedPath();
				}
			}
		});

		KeyStroke keyCtrlD = KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.CTRL_MASK);
		actionMap.put(keyCtrlD, new AbstractAction("ctrl_d") 
		{
////
//same as esc -> should merge
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(configure.isVisible())
				{
					configure.dialogCancelled();
				}
				else if(about.isVisible())
				{
					about.setVisible(false);
				}

				SingleSelectionModel ssm=mainframe.getJMenuBar().getSelectionModel();
				//p("is selected: "+ssm.isSelected()+" index: "+ssm.getSelectedIndex());
				if(ssm.isSelected())
				{
					JMenu m=mainframe.getJMenuBar().getMenu(ssm.getSelectedIndex());
					MenuSelectionManager.defaultManager().clearSelectedPath();
				}
			}
		});

/**
* @see <a href="http://stackoverflow.com/questions/100123/application-wide-keyboard-shortcut-java-swing"></a>
*/
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
		return i18n.tr(text);
	}

}//end class Main

//should be in a table
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
