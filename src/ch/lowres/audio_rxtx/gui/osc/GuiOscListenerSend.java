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
* Extended {@link GuiOscListener}, handling incoming OSC messages specific to jack_osc_send.
*/
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
			m.apis._target_port=(Integer)args.get(21);
			//22 nopause
			//23 drop every nth message
			m.apis.msg_size=(Integer)args.get(24);
			m.apis.transfer_size=(Integer)args.get(25);
			m.apis.expected_network_data_rate=(Float)args.get(26);
		}

		else if(path.equals("/autoconnect") && argsSize==2)
		{
			card.setLabel(3,l.tr("Autoconnected ports")+": "+m.apis.total_connected_ports+" / "+m.apis._in);
		}

		//else 
		else if(path.equals("/offering") && argsSize==1)
		{
			card.setStatus(l.tr("Offering audio message #")+args.get(0));

			card.setLabel(4,":"+m.apis._lport+" -> "+m.apis._target_host+":"+m.apis._target_port);

//remember sender host here
//..

			card.setLabel(5,l.tr("Offering..."));
			card.setLabel(6,"");
		}

		else if(path.equals("/receiver_denied_transmission") && argsSize==3)
		{
			//receiver props:
			//0: format_version
			//1: sample_rate
			//2: bytes per sample

			card.setLabel(4,l.tr("Transmission denied (")
				+args.get(0)+", "+args.get(1)+", "
				+((Integer)args.get(2)==2 ? "16" : "32")+")");

			card.setStatusError(l.tr("Receiver denied transmission"));

			card.button_default.setLabel(l.tr("Back"));
		}

		else if(path.equals("/receiver_accepted_transmission"))
		{
			card.setStatus(l.tr("Receiver accepted transmission"));
		}

		else if(path.equals("/sending") && argsSize==7)
		{
			card.setLabel(4,":"+m.apis._lport+" -> "+m.apis._target_host+":"+m.apis._target_port);

			card.setLabel(5, 
				String.format(new DecimalFormat("0.00").format(m.apis.expected_network_data_rate))
				+" kbit/s "
				+String.format(new DecimalFormat("0.00").format(m.apis.expected_network_data_rate/1000/8))
				+" MB/s");

			//hms, transferred mb
			card.setLabel(6,(String)args.get(1)
				+" "+String.format(new DecimalFormat("0.00").format((Float)args.get(4)))
				+" "+(String)args.get(5)
				+" ("+m.apis._in+" "+l.tr("ch.")+")");

			//msg #
			card.setStatus(l.tr("Sending audio message #")+args.get(0));
		}

		else if(path.equals("/receiver_requested_pause"))
		{
			card.setStatus(l.tr("Receiver requested pause"));
		}
	}//end accept
}//end GuiOscListener
