package ch.lowres.audio_rxtx.gui;

import java.awt.*;
import java.awt.event.*;

import com.illposed.osc.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.File;

//tb/1410
//java -Xms1024m -Xmx1024m -cp .:build/classes/ jack_audio_send_GUI

//jack_audio_send_GUI

//========================================================================
public class jack_audio_send_GUI 
{
	static String progName="jack_audio_send GUI";
	static String progVersion="0.1";
	static String progNameSymbol="jack_audio_send_gui_v"+progVersion+"_"+141020;

	static String defaultPropertiesFileName="audio_rxtx_gui.properties";

	//default settings for osc gui io
	//will be overridden by .properties file
	static boolean gui_osc_port_random=false;
	static int gui_osc_port=20220;
	static boolean keep_cache=false;

	//osc sender, receiver for communication gui<->jack_audio_send
	static OSCPortOut sender;
	static OSCPortIn receiver;
	final long WAIT_FOR_SOCKET_CLOSE=3;

	static GuiOscListener gosc;

	//thread to run binaries
	static RunCmd cmd;

	//dalog
	static ConfigureDialog configure;
	static AboutDialog about;

	//holding several config values, construct command line for jack_audio_send
	static jack_audio_send_cmdline_API api;

	static Image appIcon;

	static Frame mainframe;
	static Panel cardPanel;
	static CardLayout cardLay;

	//cards
	static FrontCard front;
	static RunningCard running;

	static Label label_status;

	//convenience class to add widgets to form
	static FormUtility formUtility;

	static Dimension screenDimension;

	static Font fontLarge;

	//platform specific system tmp directory
	static String tmpDir;

	static Watchdog dog;

//========================================================================
	public static void main(String[] args) 
	{
		//header
		println("audio_rxtx - "+progName+" v"+progVersion);
		println("(c) 2014 Thomas Brand <tom@trellis.ch>");

///////////////////
//		if(args.length>0)
//		{
//			gui_osc_port=Integer.parseInt(args[0]);
//		}

		OSTest os=new OSTest();
		println("host OS: "+os.getName());

		//os specific / or \ path separator
		tmpDir=System.getProperty("java.io.tmpdir")+File.separator+progNameSymbol;
		println("temporary cache dir: '"+tmpDir+"'");

		api=new jack_audio_send_cmdline_API();

		if(os.isWindows())
		{
			api.setPrefixPath(tmpDir+File.separator+"resources"+File.separator+"bin");
		}

		IOTools iot=new IOTools();

		//load default settings from properties file in jar then try overload from current dir
		IOTools.loadSettings(defaultPropertiesFileName);

		File fTest=new File(tmpDir);
		if(fTest!=null && fTest.exists() && fTest.canRead() && fTest.isDirectory())
		{
			println("using resources from cache");
		}
		else if(!fTest.exists())
		{
			//extract
			iot.copyJarContentToDisk("/resources/README.txt",tmpDir);
			iot.copyJarContentToDisk("/resources/COPYING.txt",tmpDir);
			iot.copyJarContentToDisk("/resources/doc",tmpDir);

			if(os.isWindows())
			{
				iot.copyJarContentToDisk("/resources/bin",tmpDir);
			}
		}
		else
		{
			println("/!\\ error creating tmp directory to extract jar contents: '"+tmpDir+"'");
		}

		//will remove tmpdir on program exit
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

//========================================================================[1;5B
	static void createForm()
	{
		//GUI======================================
		mainframe=new Frame("audio_rxtx - "+progName);
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

		front=new FrontCard();
		front.setValues();
		cardPanel.add(front, "1");

		running=new RunningCard();
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
		//else
		//{
		//	println("*** label_status was null! "+s);
		//}
	}

//========================================================================
	static void startTransmission()
	{
		//form already read/vaildated in front button handler

		setStatus("Starting GUI OSC Server");

		//for gui/jack_audio{send,return} comm
		if(startOscServer()==-1)
		{
			println("/!\\ the audio_rxtx osc gui server could not be started.");
/////////////
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
			{///////
			};

			if(dog!=null)
			{
				dog.cancel();
			}

			AppMenu.setForFrontScreen();

			api.total_connected_ports=0;
			api.jack_sample_rate=0;
			api.jack_period_size=0;
			api.msg_size=0;
			api.transfer_size=0;
			api.expected_network_data_rate=0;

			cmd=null;

			//show main panel again
			cardLay.show(cardPanel, "1");
			front.button_start_transmission.requestFocus();
			setStatus("Ready");
	}//end stopTransmission

//========================================================================
///////////////////////////
//need to call call programExit
	static void addWindowListeners()
	{
		//window events =====================================
		mainframe.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent arg0) { /*println("window activated");*/}
			@Override
			public void windowClosed(WindowEvent arg0) { /*println("window close suppressed");*/
				System.exit(-1);
			}
			@Override
			public void windowClosing(WindowEvent arg0) { /*println("window close suppressed");*/
				System.exit(-1);				
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) { /*println("window deactivated");*/ }
			@Override
			public void windowDeiconified(WindowEvent arg0) { /*println("window deiconified")*/;}
			@Override
			public void windowIconified(WindowEvent arg0) { /*println("window iconified")*/;}
			@Override
			public void windowOpened(WindowEvent arg0) { /*println("window opened")*/;}
		});
	}//end addWindowListeners

//========================================================================
	static int calcRandPort()
	{
		return new Random().nextInt(40000)+1024;
	}

//========================================================================
	static int startOscServer()//int port)
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
	}

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

					//possibly more clean up tasks here 

					println("removing tmp dir '"+tmpDir+"'");
					IOTools.deleteDirectory(new File(tmpDir));
				}
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
}//end class jack_audio_send_GUI
