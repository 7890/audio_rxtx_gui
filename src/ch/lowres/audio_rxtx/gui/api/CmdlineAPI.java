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
import ch.lowres.audio_rxtx.gui.*;

/**
* A {@link CmdlineAPIInterface} handling common command line parameters.
*/
//========================================================================
public abstract class CmdlineAPI implements CmdlineAPIInterface
{
	static Main m;

	public String prefixPath="";

	public String command_name="";

	//variables starting with _ are 1:1 options of jack_audio_send
	//initially set at startup, read from properties file
	public boolean lport_random=false;
	public int _lport=-1;
	public boolean _connect=false;
	public boolean _16=false;
	public String _name="";
	public String _sname="";
	public int _update=-1;
	public boolean test_mode=false;
	public int _limit=-1;

	//other vars
	//passthrough jack_audio_* std output
	public boolean verbose=false;
	public boolean autostart=false;

	//filled by config_dump received from jack_audio_send

	//jack_audio_send versions
	public float version=-1;
	public float format_version=-1;

	//local jack properties
	public int jack_sample_rate=-1;
	public int jack_period_size=-1;

	//increment for every /autoconnect
	public int total_connected_ports=0;

//========================================================================
	public void setPrefixPath(String prefix)
	{
		prefixPath=prefix+java.io.File.separator;
	}

//Implemented in send/receive specific subclass
//========================================================================
	public abstract String getCommandLineString();
	public abstract int getType();
}//end class CmdlineAPI
