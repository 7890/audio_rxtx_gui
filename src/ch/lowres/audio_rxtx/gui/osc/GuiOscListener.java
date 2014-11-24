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

package ch.lowres.audio_rxtx.gui.osc;
import ch.lowres.audio_rxtx.gui.*;
import ch.lowres.audio_rxtx.gui.api.*;

import java.awt.Panel;

import com.illposed.osc.*;
import java.net.InetAddress;

import java.util.Date;
import java.util.List;

import java.text.DecimalFormat;

/**
* Abstract class handling common incoming OSC messages.
*/
//========================================================================
public abstract class GuiOscListener implements OSCListener
{
	static Main g;

	Card card;
	CmdlineAPI api;

//========================================================================
	public GuiOscListener(Card c, CmdlineAPI a)
	{
		card=c;
		api=a;
	}

//========================================================================
	public void acceptMessage(Date time,OSCMessage msg) 
	{
		commonAccept(msg);
		accept(msg);
	}

//========================================================================
	public abstract void accept(OSCMessage msg);


//========================================================================
	public void commonAccept(OSCMessage msg) 
	{

		if(card==null || api==null)
		{
			return;
		}

		String path=msg.getAddress();
		List<Object> args=msg.getArguments();
		int argsSize=args.size();

		//println("osc msg received: "+path+" ("+argsSize+" args)");
		if(path.equals("/startup") && argsSize==2)
		{
			card.setStatus(api.command_name+" Started");
			api.version=(Float)args.get(0);
			api.format_version=(Float)args.get(1);

			card.setLabel(1,api.command_name+" v"+api.version+" Format v"+api.format_version);
		}

		else if(path.equals("/client_name_changed"))
		{
			card.setStatus("JACK Client Name Changed");
		}

//issiiiiiiiiiiiiiiiiisiiiiif
//12345678901234567890
		else if(path.equals("/config_dump") && argsSize>=20)
		{
			//maybe changed (sender only / receiver random port suppressed by form/config)
			if(api._lport!=(Integer)args.get(0))
			{
				api._lport=(Integer)args.get(0);

				//if of type send
				if(api.getType()==0)
				{
					//reconfigure sender
					try
					{
						g.portOutSend.setTarget(InetAddress.getLocalHost(),api._lport);
					}
					catch(Exception ex)
					{///
					}
				}
			}

			api._name=(String)args.get(1);
			api._sname=(String)args.get(2);

			api.jack_sample_rate=(Integer)args.get(3);
			api.jack_period_size=(Integer)args.get(4);

			api.test_mode=( (Integer)args.get(7)==0 ? false : true );
			api._limit=(Integer)args.get(8);

//////////
			g.p(card.getName()+" JACK: "+api.jack_sample_rate+" / "+api.jack_period_size
				+"    TRF: "+ (api._16 ? "16 bit Integer" : "32 bit Float")
			);

			card.setLabel(2,"JACK: "+api.jack_sample_rate+" / "+api.jack_period_size
				+"    TRF: "+ (api._16 ? "16 bit Integer" : "32 bit Float")
			);

card.setLabel(3, "Initializing...");


		}//end /config_dump

		else if(path.equals("/autoconnect") && argsSize==2)
		{
			//card.setStatus("Autoconnecting JACK Ports");
			//0: from 1: to
			api.total_connected_ports++;
		}

		else if(path.equals("/start_main_loop"))
		{
			card.setStatus("Main Loop Started");
		}

		else if(path.equals("/test_finished") && argsSize==1)
		{
			//0: # of cycles elapsed
			card.setStatus("Test finished");
		}

		else if(path.equals("/quit") && argsSize==1)
		{
			//tell quit reason

			card.setLabel(3,"Process Terminated");

			if(args.get(0).equals("nolibjack"))
			{
				card.setLabel(4,"No libjack Found. Is JACK Installed?");
				card.setLabel(5,"See http://www.jackaudio.org");

			}
			else if(args.get(0).equals("nojack"))
			{
				card.setLabel(4,"JACK Not Running (Server '"+api._sname+"')");
				card.setLabel(5,"Please Start JACK Manually.");
			}

			card.button_default.setLabel("OK");

/////////
			card.setStatus("jack_audio_send Quit: "+args.get(0));
		}

	}//end acceptMessage
}//end GuiOscListener