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

import com.illposed.osc.*;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.io.File;

import javax.swing.*;
import javax.swing.event.*;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

//java -splash:src/gfx/audio_rxtx_splash_screen.png -Xms1024m -Xmx1024m -cp .:build/classes/ ch.lowres.audio_rxtx.gui.Main

/*
//http://stackoverflow.com/questions/6671021/how-to-debug-java-swing-layouts/6674624#6674624
Take a look at page: Troubleshooting AWT

It provides the following debugging tip:
To dump the AWT component hierarchy, press Ctrl+Shift+F1.
*/

//========================================================================
public class Main implements ChangeListener
// implements TabSelectionListener
{
	static String progName="audio_rxtx GUI";
	static String progVersion="0.2";
	static String progNameSymbol="audio_rxtx_gui_v"+progVersion+"_"+141030;

	static String defaultPropertiesFileName="audio_rxtx_gui.properties";

	static String reportIssueUrl="https://github.com/7890/audio_rxtx_gui/issues";
	//dummy
	static String newestVersionFileUrl="https://raw.githubusercontent.com/7890/audio_rxtx_gui/master/README.md";

	static int panelWidth=10;
	static int panelHeight=10;

	//osc gui io
	//will be set by .properties file
	static boolean gui_osc_port_random_s=false;
	static int gui_osc_port_s=-1;

	static boolean gui_osc_port_random_r=false;
	static int gui_osc_port_r=-1;

	static boolean keep_cache=false;

	//osc sender, receiver for communication gui<->jack_audio_send
	static OSCPortOut portOutSend; //sender
	static OSCPortIn portInSend; //receiver

	static OSCPortOut portOutReceive; //sender
	static OSCPortIn portInReceive; //receiver

	final long WAIT_FOR_SOCKET_CLOSE=3;

	//handler for osc messages
	static GuiOscListenerSend goscs;
	static GuiOscListenerReceive goscr;

	//dalogs
	static ConfigureDialog configure;
	static AboutDialog about;

	//holding several config values, construct command line for jack_audio_send
	static jack_audio_send_cmdline_API apis;
	static jack_audio_receive_cmdline_API apir;

	static Image appIcon;

	//the main window
	static JFrame mainframe;

	static AppMenu applicationMenu;

	static JScrollPane scrollerTabSend;
	static JScrollPane scrollerTabReceive;

	static JPanel tabSend;
	static JPanel tabReceive;

	static JTabbedPane tabPanel = new JTabbedPane();

	static JPanel mainGrid;

	static JPanel cardPanelSend;
	static CardLayout cardLaySend;

	static JPanel cardPanelReceive;
	static CardLayout cardLayReceive;

	//cards
	static FrontCardSend frontSend;
	static RunningCardSend runningSend;

	static FrontCardReceive frontReceive;
	static RunningCardReceive runningReceive;

	static JLabel labelStatus;

	final static int FRONT=0;
	final static int RUNNING=1;

	static int sendStatus=FRONT;
	static int receiveStatus=FRONT;

	//convenience class to add widgets to form
	static FormUtility formUtility;

	static Dimension screenDimension;

	static Font fontLarge;

	//platform specific system tmp directory
	static String tmpDir;

	//thread to run binaries
	static RunCmd cmdSend;
	static RunCmd cmdReceive;

	//watching RunCmd
	static Watchdog dogSend;
	static Watchdog dogReceive;

	static OSTest os;

	//map containing all global key actions
	static HashMap<KeyStroke, Action> actionMap = new HashMap<KeyStroke, Action>();

//========================================================================
	public static void main(String[] args) 
	{
		//header
		p("");
		p(progName+" v"+progVersion);
		p("(c) 2014 Thomas Brand <tom@trellis.ch>");

		if(args.length>0 && (args[0].equals("--help") || args[0].equals("-h")))
		{
			p("");
			p("First argument: <URI af .properties file to use>");
			p("A file called '"+defaultPropertiesFileName+"' in the current directory will be loaded if no argument was given and the file is available.");
			p("");
			System.exit(0);
		}
		Main m=new Main(args);

	}//end main

//========================================================================
	public Main(String[] args)
	{
		os=new OSTest();
		p("host OS: "+os.getOSName());
		p("jvm: "+os.getVMName());

		//os specific / or \ path separator
		tmpDir=System.getProperty("java.io.tmpdir")+File.separator+progNameSymbol;
		p("temporary cache dir: '"+tmpDir+"'");

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

		IOTools iot=new IOTools();

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
			p("using resources from cache");
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

				p("setting permissions of binaries");
				RunCmd setPerms=new RunCmd("chmod 755 "+tmpDir+"/resources/mac/*");
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
				p("setting permissions of binaries");
				RunCmd setPerms=new RunCmd("chmod 755 "+tmpDir+"/resources/"+dir+"/*");
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

		//http://stackoverflow.com/questions/209812/how-do-i-change-the-default-application-icon-in-java
		java.net.URL url = ClassLoader.getSystemResource("resources/audio_rxtx_icon.png");
		Toolkit kit = Toolkit.getDefaultToolkit();
		appIcon = kit.createImage(url);
	
		screenDimension=Toolkit.getDefaultToolkit().getScreenSize();

		fontLarge=iot.createFontFromJar("/resources/AudioMono.ttf",18f);

		formUtility=new FormUtility();

		//dialogs
		configure=new ConfigureDialog(mainframe,"Configure "+progName, true);
		about=new AboutDialog(mainframe, "About "+progName, true);

		createForm();
	}




//========================================================================
	//http://stackoverflow.com/questions/11116386/java-gtk-native-look-and-feel-looks-bad-and-bold
	public static void setNativeLAF()
	{
		// Native L&F
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			w("unable to set native look and feel: " + e);
		}
	}

//========================================================================
	public static void setCrossPlatformLAF()
	{
		//http://stackoverflow.com/questions/1065691/how-to-set-the-background-color-of-a-jbutton-on-the-mac-os
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e)
		{
			w("unable to set native look and feel: " + e);
		}
	}

//========================================================================
	void createForm()
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

		tabReceive=new JPanel(new BorderLayout());
		tabReceive.setBackground(Colors.form_background);
		tabReceive.add(cardPanelReceive,BorderLayout.CENTER);
		scrollerTabReceive=new JScrollPane (tabReceive, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollerTabReceive.getViewport().setBackground(Colors.form_background);

//start tabbed
		tabPanel.add("Send", scrollerTabSend);
		tabPanel.add("Receive", scrollerTabReceive);

		tabPanel.addChangeListener(this);

		//http://stackoverflow.com/questions/5183687/java-remove-margin-padding-on-a-jtabbedpane

		tabPanel.setUI(new BasicTabbedPaneUI()
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

		mainGrid=new JPanel(new GridLayout(1,2)); //y,x

//		mainGrid.add(scrollerTabSend);
//		mainGrid.add(scrollerTabReceive);

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

		labelStatus=new JLabel("Ready");
		labelStatus.setBackground(Colors.status_background);
		labelStatus.setForeground(Colors.status_foreground);

		mainframe.add(labelStatus,BorderLayout.SOUTH);

		frontSend.checkbox_format_16.requestFocus();

		addWindowListeners();

		addGlobalKeyListeners();

		mainframe.pack();
//		mainframe.setResizable(false);
		setWindowCentered(mainframe);

		//"run" GUI
		mainframe.setVisible(true);

//tmp
p("cardPanelSend "+cardPanelSend.getPreferredSize().getWidth()+" "+cardPanelSend.getPreferredSize().getHeight());
p("tabSend "+tabSend.getPreferredSize().getWidth()+" "+tabSend.getPreferredSize().getHeight());
p("frontSend "+frontSend.getPreferredSize().getWidth()+" "+frontSend.getPreferredSize().getHeight());
p("insets "+mainframe.getInsets().left+" "+mainframe.getInsets().right+" "+mainframe.getInsets().top+" "+mainframe.getInsets().bottom);
p("application_menu "+applicationMenu.getPreferredSize().getWidth()+" "+applicationMenu.getPreferredSize().getHeight());
p("label_status "+labelStatus.getPreferredSize().getWidth()+" "+labelStatus.getPreferredSize().getHeight());
p("button_default "+frontSend.button_default.getPreferredSize().getWidth()+" "+frontSend.button_default.getPreferredSize().getHeight());

		panelHeight=(int)(
			cardPanelSend.getPreferredSize().getHeight()
/*	+mainframe.getInsets().top+mainframe.getInsets().bottom*/
			+applicationMenu.getPreferredSize().getHeight()
			+labelStatus.getPreferredSize().getHeight()
			+frontSend.button_default.getPreferredSize().getHeight()
			/*+10*/);

		panelWidth=(int)(mainGrid.getPreferredSize().getWidth());

		mainframe.setSize(
			panelWidth+mainframe.getInsets().left+mainframe.getInsets().right,
			panelHeight+mainframe.getInsets().top+mainframe.getInsets().bottom
		);
	}//end createForm

//========================================================================
	static void setStatus(String s)
	{
		if(labelStatus!=null)
		{
			labelStatus.setText(s);
		}
		//p("MAIN STATUS "+s);
	}

//========================================================================
	static void startTransmissionSend()
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
			p("osc gui server started on port "+gui_osc_port_s);
			frontSend.setStatus("GUI OSC Server Started");
		}

		frontSend.setStatus("Executing jack_audio_send");

		p("execute: "+apis.getCommandLineString());
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
	static void startTransmissionReceive()
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
			p("osc gui server started on port "+gui_osc_port_r);
			frontReceive.setStatus("GUI OSC Server Started");
		}

		frontReceive.setStatus("Executing jack_audio_receive");

		p("execute: "+apir.getCommandLineString());
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
	static void stopTransmissionSend()
	{
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
		frontSend.setStatus("Ready");

		sendStatus=FRONT;

	}//end stopTransmissionSend

//========================================================================
	static void stopTransmissionReceive()
	{
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
		frontReceive.setStatus("Ready");

		receiveStatus=FRONT;

	}//end stopTransmissionReceive

//========================================================================
	static void addWindowListeners()
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
	static int startOscServerSend()
	{
		try
		{
			DatagramSocket ds;
			if(gui_osc_port_random_s)
			{
				ds=new DatagramSocket();
				gui_osc_port_s=ds.getLocalPort();
				p("random UDP port "+gui_osc_port_s);
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
			e("could not start osc gui server on port "+gui_osc_port_s+". "+oscex.getMessage());
			return -1;
		}

		return 0;
	}//end startOscServerSend

//========================================================================
	static int startOscServerReceive()
	{
		try
		{
			DatagramSocket ds;
			if(gui_osc_port_random_r)
			{
				ds=new DatagramSocket();
				gui_osc_port_r=ds.getLocalPort();
				p("random UDP port "+gui_osc_port_r);
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
			e("could not start osc gui server on port "+gui_osc_port_r+". "+oscex.getMessage());
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
				w("shutdown signal received!");
				stopTransmissionSend();
				stopTransmissionReceive();

				if(!keep_cache)
				{
					p("cleaning up...");
					p("removing tmp dir '"+tmpDir+"'");
					IOTools.deleteDirectory(new File(tmpDir));
				}
				//possibly more clean up tasks here 
				p("done! bye");
			}
		}));
	}

//generic print to std out
//========================================================================
	static void p(String s)
	{
		System.out.println(s);
	}

//styled print to std out, warning
//========================================================================
	static void w(String s)
	{
		System.out.println("/:\\ "+s);
	}

//styled print to std err
//========================================================================
	static void e(String s)
	{
		System.out.println("/!\\ "+s);
	}

//========================================================================
	static void setWindowCentered(Frame f)
	{
		f.setLocation(
			(int)((screenDimension.getWidth()-f.getWidth()) / 2),
			(int)((screenDimension.getHeight()-f.getHeight()) / 2)
		);
	}


//========================================================================
	public void stateChanged(ChangeEvent e)
	{
		setFocusedWidget();
	}


//========================================================================
	public void setFocusedWidget()
	{
////////////////////
//		String tabname=tabPanel.getTitleAt(tabPanel.getSelectedIndex());
//		setFocusedWidget(tabname);
	}

//========================================================================
	public void nextTab()
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
	public void prevTab()
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
	public void setFocusedWidget(String tabname)
	{
		if(tabname.equals("Send"))
		{
		}
		else if(tabname.equals("Receive"))
		{
		}
	}

//========================================================================
	void addGlobalKeyListeners()
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
//same as esc -> should merge
//conflicts with ctrl+a / ctrl+d
//ctrl+a conflicts with select all in text fields

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

}//end class Main

/*
http://tldp.org/LDP/abs/html/exitcodes.html
Exit Code Number	Meaning	Example	Comments
1	Catchall for general errors	let "var1 = 1/0"	Miscellaneous errors, such as "divide by zero" and other impermissible operations
2	Misuse of shell builtins (according to Bash documentation)	empty_function() {}	Missing keyword or command, or permission problem (and diff return code on a failed binary file comparison).
126	Command invoked cannot execute	/dev/null	Permission problem or command is not an executable
127	"command not found"	illegal_command	Possible problem with $PATH or a typo
128	Invalid argument to exit	exit 3.14159	exit takes only integer args in the range 0 - 255 (see first footnote)
128+n	Fatal error signal "n"	kill -9 $PPID of script	$? returns 137 (128 + 9)
130	Script terminated by Control-C	Ctl-C	Control-C is fatal error signal 2, (130 = 128 + 2, see above)
255*	Exit status out of range	exit -1	exit takes only integer args in the range 0 - 255
*/
