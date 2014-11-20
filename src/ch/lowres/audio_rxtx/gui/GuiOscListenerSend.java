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
public class GuiOscListenerSend extends GuiOscListener
{
//========================================================================
	public GuiOscListenerSend(Card c, CmdlineAPI a)
	{
		super(c,a);
	}

//========================================================================
	public void accept(OSCMessage msg)
	{
		if(card==null)
		{
			return;
		}

		String path=msg.getAddress();
		List<Object> args=msg.getArguments();
		int argsSize=args.size();

		if(path.equals("/config_dump") && argsSize==27)
		{
			//20 target host
			//maybe changed
			g.apis._target_port=(Integer)args.get(21);
			//22 nopause
			//23 drop every nth message
			g.apis.msg_size=(Integer)args.get(24);
			g.apis.transfer_size=(Integer)args.get(25);
			g.apis.expected_network_data_rate=(Float)args.get(26);
		}

		else if(path.equals("/autoconnect") && argsSize==2)
		{
			card.setLabel(3,"Autoconnected Ports: "+g.apis.total_connected_ports+" / "+g.apis._in);
		}

		//else 
		else if(path.equals("/offering") && argsSize==1)
		{
			card.setStatus("Offering Audio Message #"+args.get(0));

			card.setLabel(4,":"+g.apis._lport+" -> "+g.apis._target_host+":"+g.apis._target_port);
			card.setLabel(5,"Offering...");
			card.setLabel(6,"");
		}

		else if(path.equals("/receiver_denied_transmission") && argsSize==3)
		{
			//receiver props:
			//0: format_version
			//1: sample_rate
			//2: bytes per sample

			card.setLabel(4,"Transmission denied ("
				+args.get(0)+", "+args.get(1)+", "
				+((Integer)args.get(2)==2 ? "16" : "32")+")");

			card.setStatus("Receiver denied Transmission");

			card.button_default.setLabel("OK");
		}

		else if(path.equals("/receiver_accepted_transmission"))
		{
			card.setStatus("Receiver Accepted Transmission");
		}

		else if(path.equals("/sending") && argsSize==7)
		{
			card.setLabel(4,":"+g.apis._lport+" -> "+g.apis._target_host+":"+g.apis._target_port);

			card.setLabel(5, 
				String.format(new DecimalFormat("0.00").format(g.apis.expected_network_data_rate))
				+" kbit/s   "
				+String.format(new DecimalFormat("0.00").format(g.apis.expected_network_data_rate/1000/8))
				+" MB/s");

			//hms, transferred mb
			card.setLabel(6,(String)args.get(1)
				+"   "+String.format(new DecimalFormat("0.00").format((Float)args.get(4)))
				+" "+(String)args.get(5)
				+"   ("+g.apis._in+" CH)");

			//msg #
			card.setStatus("Sending Audio Message #"+args.get(0));
		}

		else if(path.equals("/receiver_requested_pause"))
		{
			card.setStatus("Receiver requested Pause");
		}
	}//end accept
}//end GuiOscListener
