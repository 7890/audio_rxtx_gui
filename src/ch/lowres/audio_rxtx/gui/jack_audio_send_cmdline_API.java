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

import java.io.File;

//========================================================================
public class jack_audio_send_cmdline_API
{
	static jack_audio_send_GUI g;

	static String command_name="jack_audio_send";

	//path prefix to find jack_audio_send binary
	//empty if in search path
	//on windows set to dir where binaries were extracted
	static String prefixPath="";

	//helper to determine operating system
	static OSTest os;

	//variables starting with _ are 1:1 options of jack_audio_send
	//initially set at startup, read from properties file
	static boolean	lport_random=false;
	static int 	_lport=-1;
	static int 	_in=-1;
	static boolean 	_connect=false;
	static boolean 	_16=false;
	static String 	_name="";
	static String 	_sname="";
	static int 	_update=-1;
	static boolean test_mode=false;
	static int 	_limit=-1;
	static boolean 	_nopause=false;
	static int 	_drop=-1;
	static String 	_target_host="";
	static int 	_target_port=-1;

	//other vars

	//passthrough jack_audio_send std output
	static boolean verbose=false;

	//filled by config_dump received from jack_audio_send

	//jack_audio_send versions
	static float version=-1;
	static float format_version=-1;

	//local jack properties
	static int jack_sample_rate=-1;
	static int jack_period_size=-1;

	static int msg_size=0;
	static int transfer_size=0;
	static float expected_network_data_rate=0;

	//increment for every /autoconnect
	static int total_connected_ports=0;

//========================================================================
	public jack_audio_send_cmdline_API()
	{
		os=new OSTest();
	}

//========================================================================
	public void setPrefixPath(String prefix)
	{
		prefixPath=prefix+File.separator;
	}

//========================================================================
	public String getCommandLineString()
	{
		String command="";
		if(prefixPath.length()>0)
		{
			command=prefixPath;
		}

		command+=command_name+" --in "+_in+" --name \""+_name+"\" --sname \""+_sname+"\" ";
		command+="--update "+_update+" --io --iohost localhost ";

		if(_16){command+="--16 ";}
		if(_connect){command+="--connect ";}
		if(_nopause){command+="--nopause ";}
		if(test_mode){command+="--limit "+_limit+" ";}
		if(_drop>0){command+="--drop "+_drop+" ";}
		if(lport_random){command+="--lport 0 ";}else{command+="--lport "+_lport+" ";}
		command+="--ioport "+g.gui_osc_port+" ";

		command+=_target_host+" "+_target_port;
		return command;
	}
}//end class jack_audio_send_cmdline_API

/*
$ jack_audio_send --help

jack_audio_receive v0.84 (format v1.10)
(C) 2013 - 2014 Thomas Brand  <tom@trellis.ch>
Usage: jack_audio_send [Options] target_host target_port.
Options:
  Display this text and quit          --help
  Show program version and quit       --version
  Show liblo properties and quit      --loinfo
  Local port                   (9990) --lport  <integer>
  Number of capture channels      (2) --in     <integer>
  Autoconnect ports                   --connect
  Send 16 bit samples  (32 bit float) --16
  JACK client name             (send) --name   <string>
  JACK server name          (default) --sname  <string>
  Update info every nth cycle    (99) --update <integer>
  Limit totally sent messages         --limit  <integer>
  Don't display running info          --quiet
  Don't output anything on std*       --shutup
  Enable Remote Control / GUI         --io
     Disable push to GUI              --nopush
     GUI host             (localhost) --iohost <string>
     GUI port(UDP)            (20220) --ioport <string>
  Immediate send, ignore /pause       --nopause
  (Use with multiple receivers. Ignore /pause, /deny)
  Drop every nth message (test)   (0) --drop   <integer>
target_host:   <string>
target_port:   <integer>

If target_port==0 and/or --lport 0: use random port(s)
Example: jack_audio_send --in 8 10.10.10.3 1234
One message corresponds to one multi-channel (mc) period.
See http://github.com/7890/jack_tools/
*/
