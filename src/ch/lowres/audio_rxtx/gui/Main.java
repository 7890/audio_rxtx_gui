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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.File;

//java -splash:src/gfx/audio_rxtx_splash_screen.png -Xms1024m -Xmx1024m -cp .:build/classes/ ch.lowres.audio_rxtx.gui.Main

//========================================================================
public class Main 
{
	static String progName="audio_rxtx GUI";
	static String progVersion="0.2";
	static String progNameSymbol="audio_rxtx_gui_v"+progVersion+"_"+141030;

	static String defaultPropertiesFileName="audio_rxtx_gui.properties";

	//osc gui io
	//will be set by .properties file
	static boolean gui_osc_port_random=false;
	static int gui_osc_port=-1;
	static boolean keep_cache=false;

	//osc sender, receiver for communication gui<->jack_audio_send
	static OSCPortOut sender;
	static OSCPortIn receiver;
	final long WAIT_FOR_SOCKET_CLOSE=3;

	//handler for osc messages
	static GuiOscListener gosc;

	//dalogs
	static ConfigureDialog configure;
	static AboutDialog about;

	//holding several config values, construct command line for jack_audio_send
	static jack_audio_send_cmdline_API api;

	static Image appIcon;

	//the main window
	static Frame mainframe;
	static Panel cardPanel;
	static CardLayout cardLay;

	//cards
	static FrontCardSend front;
	static RunningCardSend running;

	static Label label_status;

	//convenience class to add widgets to form
	static FormUtility formUtility;

	static Dimension screenDimension;

	static Font fontLarge;

	//platform specific system tmp directory
	static String tmpDir;

	//thread to run binaries
	static RunCmd cmd;

	//watching RunCmd
	static Watchdog dog;

//========================================================================
	public static void main(String[] args) 
	{
		//header
		println("");
		println(progName+" v"+progVersion);
		println("(c) 2014 Thomas Brand <tom@trellis.ch>");

		if(args.length>0 && (args[0].equals("--help") || args[0].equals("-h")))
		{
			println("");
			println("First argument: <URI af .properties file to use>");
			println("A file called '"+defaultPropertiesFileName+"' in the current directory will be loaded if no argument was given and the file is available.");
			println("");
			System.exit(0);
		}

		OSTest os=new OSTest();
		println("host OS: "+os.getOSName());
		println("jvm: "+os.getVMName());

		//os specific / or \ path separator
		tmpDir=System.getProperty("java.io.tmpdir")+File.separator+progNameSymbol;
		println("temporary cache dir: '"+tmpDir+"'");

		api=new jack_audio_send_cmdline_API();

		if(os.isWindows())
		{
			api.setPrefixPath(tmpDir+File.separator+"resources"+File.separator+"win");
		}
		else if(os.isMac())
		{
			api.setPrefixPath(tmpDir+File.separator+"resources"+File.separator+"mac");
		}
		else if(os.isLinux())
		{
			String dir="lin32";
			if(os.is64Bits())
			{
				dir="lin64";
			}
			api.setPrefixPath(tmpDir+File.separator+"resources"+File.separator+dir);
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
			println("using resources from cache");
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

				println("setting permissions of binaries");
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
				println("setting permissions of binaries");
				RunCmd setPerms=new RunCmd("chmod 755 "+tmpDir+"/resources/"+dir+"/*");
				setPerms.start();
			}
		}
		else
		{
			println("/!\\ error creating tmp directory to extract jar contents: '"+tmpDir+"'");
		}

		//will remove tmpdir on program exit if keep_cache==false
		createShutDownHook();

		//http://stackoverflow.com/questions/209812/how-do-i-change-the-default-application-icon-in-java
		java.net.URL url = ClassLoader.getSystemResource("resources/icon.png");
		Toolkit kit = Toolkit.getDefaultToolkit();
		appIcon = kit.createImage(url);
	
		screenDimension=Toolkit.getDefaultToolkit().getScreenSize();

		fontLarge=iot.createFontFromJar("/resources/AudioMono.ttf",18f);

		formUtility=new FormUtility();

		//dialogs
		configure=new ConfigureDialog(mainframe,"Configure audio_rxtx jack_audio_send", true);
		about=new AboutDialog(mainframe, "About audio_rxtx", true);

		createForm();
	}//end main

//========================================================================
	static void createForm()
	{
		mainframe=new Frame(progName);
		mainframe.setBackground(Colors.form_background);
		mainframe.setForeground(Colors.form_foreground);
		mainframe.setLayout(new BorderLayout());

		mainframe.setIconImage(appIcon);

		//"cards" / for switching views / all in one window
		cardPanel=new Panel();
		cardLay=new CardLayout();
		cardPanel.setLayout(cardLay);
		mainframe.add(cardPanel,BorderLayout.NORTH);

		mainframe.setMenuBar(new AppMenu());

		front=new FrontCardSend();
		front.setValues();
		cardPanel.add(front, "1");

		running=new RunningCardSend();
		cardPanel.add(running, "2");

		label_status=new Label("Ready");
		label_status.setBackground(Colors.status_background);
		label_status.setForeground(Colors.status_foreground);

		mainframe.add(label_status,BorderLayout.SOUTH);

		front.checkbox_format_16.requestFocus();

		addWindowListeners();

		mainframe.pack();
		mainframe.setResizable(false);
		setWindowCentered(mainframe);

		//"run" GUI
		mainframe.setVisible(true);
	}//end createForm

//========================================================================
	static void setStatus(String s)
	{
		if(label_status!=null)
		{
			label_status.setText(s);
		}
	}

//========================================================================
	static void startTransmission()
	{
		//form already read/vaildated in front button handler

		setStatus("Starting GUI OSC Server");

		//for gui<->jack_audio_send communication
		if(startOscServer()==-1)
		{
			println("/!\\ the audio_rxtx osc gui server could not be started.");
			setStatus("GUI OSC Server Could Not Be Started");
			return;
		}
		else
		{
			println("osc gui server started on port "+gui_osc_port);
			setStatus("GUI OSC Server Started");
		}

		setStatus("Executing jack_audio_send");

		println("execute: "+api.getCommandLineString());
		cmd=new RunCmd(api.getCommandLineString());

		cmd.devNull(!api.verbose);

		cmd.maxPrio();
		cmd.start();

		if(dog!=null)
		{
			dog.cancel();
		}
		dog=new Watchdog();
		dog.start();

		AppMenu.setForRunning();
		running.clearLabels();
		cardLay.show(cardPanel, "2");
		running.button_stop_transmission.requestFocus();
	}//end startTransmission

//========================================================================
	static void stopTransmission()
	{
		//terminate jack_audio_send running in thread
		OSCMessage msg=new OSCMessage("/quit");

		try
		{
			sender.send(msg);
		}
		catch(Exception sndex)
		{///
		};

		if(dog!=null)
		{
			dog.cancel();
		}

		cmd=null;

		AppMenu.setForFrontScreen();

		api.total_connected_ports=0;
		api.jack_sample_rate=0;
		api.jack_period_size=0;
		api.msg_size=0;
		api.transfer_size=0;
		api.expected_network_data_rate=0;

		//show main panel again
		cardLay.show(cardPanel, "1");
		front.button_start_transmission.requestFocus();
		setStatus("Ready");
	}//end stopTransmission

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
	static int calcRandPort()
	{
		return new Random().nextInt(40000)+1024;
	}

//========================================================================
	static int startOscServer()
	{
		try
		{
			if(gui_osc_port_random)
			{
				gui_osc_port=calcRandPort();
				println("random UDP port "+gui_osc_port);
			}

			if(receiver!=null)
			{
				receiver.stopListening();
				receiver.close();
			}
			if(sender!=null)
			{
				sender.close();
			}

			receiver=new OSCPortIn(gui_osc_port);
			sender=new OSCPortOut(InetAddress.getLocalHost(), api._lport);

			gosc=new GuiOscListener();

			//catch every message
			receiver.addListener("/*", gosc);
			receiver.startListening();
		}
		catch(Exception oscex)
		{
			System.out.println("/!\\ could not start osc gui server on port "+gui_osc_port+". "+oscex.getMessage());
			return -1;
		}

		return 0;
	}//end startOscServer

//========================================================================
	//http://stackoverflow.com/questions/11435533/how-does-ctrl-c-work-with-java-program
	private static void createShutDownHook()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				println("shutdown signal received!");
				if(!keep_cache)
				{
					println("cleaning up...");
					println("removing tmp dir '"+tmpDir+"'");
					IOTools.deleteDirectory(new File(tmpDir));
				}
				//possibly more clean up tasks here 
				println("done! bye");
			}
		}));
	}

//========================================================================
	static void println(String s)
	{
		System.out.println(s);
	}

//========================================================================
	static void setWindowCentered(Frame f)
	{
		f.setLocation(
			(int)((screenDimension.getWidth()-f.getWidth()) / 2),
			(int)((screenDimension.getHeight()-f.getHeight()) / 2)
		);
	}
}//end class Main
