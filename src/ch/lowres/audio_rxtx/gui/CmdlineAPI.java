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

//Common for jack_audio_send, jack_audio_receive
//========================================================================
public abstract class CmdlineAPI implements CmdlineAPIInterface
{
	static Main g;

	String prefixPath="";

	String command_name="";

	//variables starting with _ are 1:1 options of jack_audio_send
	//initially set at startup, read from properties file
	boolean lport_random=false;
	int _lport=-1;
	boolean _connect=false;
	boolean _16=false;
	String _name="";
	String _sname="";
	int _update=-1;
	boolean test_mode=false;
	int _limit=-1;

	//other vars

	//passthrough jack_audio_* std output
	boolean verbose=false;

	//filled by config_dump received from jack_audio_send

	//jack_audio_send versions
	float version=-1;
	float format_version=-1;

	//local jack properties
	int jack_sample_rate=-1;
	int jack_period_size=-1;

	//increment for every /autoconnect
	int total_connected_ports=0;

//========================================================================
	public void setPrefixPath(String prefix)
	{
		prefixPath=prefix+java.io.File.separator;
	}

//Implemented in send/receive specific subclass
//========================================================================
	public abstract String getCommandLineString();
}//end class CmdlineAPI
