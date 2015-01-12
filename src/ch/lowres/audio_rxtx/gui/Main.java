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

import java.awt.event.*;

import com.illposed.osc.*;
import java.net.*;
import java.util.*;

import java.io.File;

//import javax.swing.plaf.basic.BasicTabbedPaneUI;

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

	public static GUI g;
	public static Fonts f;
	public static Languages l;

	//platform specific system tmp directory
	public static String tmpDir;

	public static IOTools iot=new IOTools();
	public static OSTest os=new OSTest();

	public static int ctrlOrCmd=InputEvent.CTRL_MASK;

	//holding several config values, construct command line for jack_audio_send
	public static jack_audio_send_cmdline_API apis;
	public static jack_audio_receive_cmdline_API apir;

	//thread to run binaries
	public static RunCmd cmdSend;
	public static RunCmd cmdReceive;

	//watching RunCmd
	public static Watchdog dogSend;
	public static Watchdog dogReceive;

	public final static int FRONT=0;
	public final static int RUNNING=1;
	public static int sendStatus=FRONT;
	public static int receiveStatus=FRONT;

	//osc sender, receiver for communication gui<->jack_audio_send
	public static OSCPortOut portOutSend; //sender
	public static OSCPortIn portInSend; //receiver

	public static OSCPortOut portOutReceive; //sender
	public static OSCPortIn portInReceive; //receiver
	public static final long WAIT_FOR_SOCKET_CLOSE=3;

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

		//init gui
		g.init();
	}//end constructor

//========================================================================
	public static void startTransmissionSend()
	{
		//form already read/vaildated in front button handler

		g.frontSend.setStatus("Starting GUI OSC Server");

		//for gui<->jack_audio_send communication
		if(startOscServerSend()==-1)
		{
			e("the audio_rxtx osc gui server could not be started.");
			g.frontSend.setStatus("GUI OSC Server Could Not Be Started");
			return;
		}
		else
		{
			p(l.tr("OSC GUI server started on UDP port")+" "+g.gui_osc_port_s);
			g.frontSend.setStatus("GUI OSC Server Started");
		}

		g.frontSend.setStatus("Executing jack_audio_send");

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

		g.runningSend.setValues();
		g.cardLaySend.show(g.cardPanelSend, "2");
		g.runningSend.button_default.requestFocus();

		sendStatus=RUNNING;

	}//end startTransmissionSend

//========================================================================
	public static void startTransmissionReceive()
	{
		//form already read/vaildated in front button handler

		g.frontReceive.setStatus("Starting GUI OSC Server");

		//for gui<->jack_audio_send communication
		if(startOscServerReceive()==-1)
		{
			e("the audio_rxtx osc gui server could not be started.");
			g.frontReceive.setStatus("GUI OSC Server Could Not Be Started");
			return;
		}
		else
		{
			p(l.tr("OSC GUI server started on UDP port")+" "+g.gui_osc_port_r);
			g.frontReceive.setStatus("GUI OSC Server Started");
		}

		g.frontReceive.setStatus("Executing jack_audio_receive");

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
		g.runningReceive.setValues();
		g.cardLayReceive.show(g.cardPanelReceive, "2");
		g.runningReceive.button_default.requestFocus();

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
		g.cardLaySend.show(g.cardPanelSend, "1");

		g.frontSend.button_default.requestFocus();
		g.frontSend.setStatus(l.tr("Ready"));

		g.mainframe.pack();

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
		g.cardLayReceive.show(g.cardPanelReceive, "1");
		g.frontReceive.button_default.requestFocus();
		g.frontReceive.setStatus(l.tr("Ready"));

		receiveStatus=FRONT;

	}//end stopTransmissionReceive


//========================================================================
	public static int startOscServerSend()
	{
		try
		{
			DatagramSocket ds;
			if(g.gui_osc_port_random_s)
			{
				ds=new DatagramSocket();
				g.gui_osc_port_s=ds.getLocalPort();
			}
			else
			{
				ds=new DatagramSocket(g.gui_osc_port_s);
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

			g.goscs=new GuiOscListenerSend(g.runningSend, apis);

			//catch every message
			portInSend.addListener("/*", g.goscs);
			portInSend.startListening();
		}
		catch(Exception oscex)
		{
			e(l.tr("Could not start OSC GUI server on UDP port")+" "+g.gui_osc_port_s+". "+oscex.getMessage());
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
			if(g.gui_osc_port_random_r)
			{
				ds=new DatagramSocket();
				g.gui_osc_port_r=ds.getLocalPort();
			}
			else
			{
				ds=new DatagramSocket(g.gui_osc_port_r);
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

			g.goscr=new GuiOscListenerReceive(g.runningReceive, apir);

			//catch every message
			portInReceive.addListener("/*", g.goscr);
			portInReceive.startListening();
		}
		catch(Exception oscex)
		{
			e(l.tr("Could not start OSC GUI server on UDP port")+" "+g.gui_osc_port_r+". "+oscex.getMessage());
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
			public void run()
			{
				p("");
				w(l.tr("Shutdown signal received!"));
				stopTransmissionSend();
				stopTransmissionReceive();

				if(!g.keep_cache)
				{
					p(l.tr("Cleaning up..."));
					p(l.tr("Removing temporary cache directory '")+tmpDir+"'");
					iot.deleteDirectory(new File(tmpDir));
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
