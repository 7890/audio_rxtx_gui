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

package ch.lowres.audio_rxtx.gui.api;

/**
* Extended {@link CmdlineAPI}, handling command line parameters specific to jack_osc_receive.
*/
//========================================================================
public class jack_audio_receive_cmdline_API extends CmdlineAPI
{
	//variables starting with _ are 1:1 options of jack_audio_send
	//initially set at startup, read from properties file
	public static int 	_out=-1;
	
	public static int 	_offset=-1;
	public static int 	_pre=-1;
	public static int 	_max=-1;

	public static boolean 	_rere=false;
	public static boolean 	_reuf=false;
	public static boolean 	_nozero=false;
	public static boolean 	_norbc=false;
	public static boolean 	_close=false;

//...

	//other vars
//========================================================================
	public jack_audio_receive_cmdline_API()
	{
		command_name="jack_audio_receive";
	}

//========================================================================
	public String getCommandLineString()
	{
		String command=prefixPath;

		command+=command_name+" --out "+_out+" --name \""+_name+"\" --sname \""+_sname+"\" ";
		command+="--update "+_update+" --io --iohost localhost ";

		if(_16){command+="--16 ";}
		if(_connect){command+="--connect ";}
		if(test_mode){command+="--limit "+_limit+" ";}
		if(_rere){command+="--rere ";}
		if(_reuf){command+="--reuf ";}
		if(_nozero){command+="--nozero ";}
		if(_norbc){command+="--norbc ";}
		if(_close){command+="--close ";}
		command+="--offset "+_offset+" ";
		command+="--pre "+_pre+" ";
		command+="--max "+_max+" ";
//		if(lport_random){command+="--lport 0 ";}else{command+="--lport "+_lport+" ";}
		command+="--ioport "+m.gui_osc_port_r+" ";
		command+=_lport;
		return command;
	}

//========================================================================
	public int getType()
	{
		//1: receive
		return 1;
	}
}//end class jack_audio_receive_cmdline_API

/*
$ jack_audio_receive --help

jack_audio_receive v0.84 (format v1.10)
(C) 2013 - 2014 Thomas Brand  <tom@trellis.ch>
Usage: jack_audio_receive [Options] listening_port.
Options:
  Display this text and quit         --help
  Show program version and quit      --version
  Show liblo properties and quit     --loinfo
  Number of playback channels    (2) --out    <integer>
  Channel Offset                 (0) --offset <integer>
  Autoconnect ports                  --connect
  Send 16 bit samples (32 bit float) --16
  JACK client name         (receive) --name   <string>
  JACK server name         (default) --sname  <string>
  Initial buffer size (4 mc periods) --pre    <integer>
  Max buffer size (>= init)   (auto) --max    <integer>
  Rebuffer on sender restart         --rere
  Rebuffer on underflow              --reuf
  Re-use old data on underflow       --nozero
  Disallow ext. buffer control       --norbc
  Update info every nth cycle   (99) --update <integer>
  Limit processing count             --limit  <integer>
  Don't display running info         --quiet
  Don't output anything on std*      --shutup
  Enable Remote Control / GUI        --io
     Disable push to GUI             --nopush
     GUI host            (localhost) --iohost <string>
     GUI port(UDP)           (20220) --ioport <string>
  Quit on incompatibility            --close
listening_port:   <integer>

If listening_port==0: use random port
Example: jack_audio_receive --out 8 --connect --pre 200 1234
One message corresponds to one multi-channel (mc) period.
See http://github.com/7890/jack_tools
*/
