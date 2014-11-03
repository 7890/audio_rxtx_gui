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

import java.awt.Panel;

import com.illposed.osc.*;
import java.net.InetAddress;

import java.util.Date;
import java.util.List;

import java.text.DecimalFormat;

//========================================================================
public abstract class GuiOscListener implements OSCListener
{
	static Main g;

	public Card card;
	public CmdlineAPI api;

//========================================================================
	public GuiOscListener(Card c, CmdlineAPI a)
	{
		card=c;
		api=a;
	}

//========================================================================
	public static void println(String s)
	{
		System.out.println(s);
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

		else if(path.equals("/config_dump") && argsSize>=20)
		{
			card.setStatus("config dump received");

			//maybe changed
			if(api._lport!=(Integer)args.get(0))
			{
				api._lport=(Integer)args.get(0);

				//reconfigure sender
				try
				{
///////////////////////!!!needs correction
					g.OscOutSend.close();
					g.OscOutSend=new OSCPortOut(InetAddress.getLocalHost(), api._lport);
				}
				catch(Exception ex)
				{///
				}
			}

			api._name=(String)args.get(1);
			api._sname=(String)args.get(2);

			api.jack_sample_rate=(Integer)args.get(3);
			api.jack_period_size=(Integer)args.get(4);

			api.test_mode=( (Integer)args.get(7)==0 ? false : true );
			api._limit=(Integer)args.get(8);

			card.setLabel(2,"JACK: "+api.jack_sample_rate+" / "+api.jack_period_size
				+"    TRF: "+ (api._16 ? "16 bit Integer" : "32 bit Float")
			);
		}//end /config_dump

		else if(path.equals("/autoconnect") && argsSize==2)
		{
			card.setStatus("Autoconnecting JACK Ports");
			//0: from 1: to
			api.total_connected_ports++;

		}

		else if(path.equals("/start_main_loop"))
		{
			card.setStatus("autoconnecting JACK ports");
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
			}

			card.button_default.setLabel("OK");

			card.setStatus("jack_audio_send Quit: "+args.get(0));
		}

	}//end acceptMessage
}//end GuiOscListener
