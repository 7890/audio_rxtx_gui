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
public class GuiOscListenerReceive extends GuiOscListener
{
//========================================================================
	public GuiOscListenerReceive(Card c, CmdlineAPI a)
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

		if(path.equals("/config_dump") && argsSize==25)
		{

		}

		else if(path.equals("/autoconnect") && argsSize==2)
		{
			card.setLabel(3,"Autoconnected Ports: "+g.apir.total_connected_ports+" / "+g.apir._out);
		}

		else if(path.equals("/wait_for_input") && argsSize==0)
		{
			card.setStatus("Waiting For Input...");
		}

		else if(path.equals("/buffering") && argsSize==2)
		{
			card.setStatus("Buffering #"+args.get(0)+"  ("+args.get(1)+" To Go)");
			card.setLabel(4,"Buffering...");
			card.setLabel(5,"");
			card.setLabel(6,"");
			card.setLabel(7,"");

		}

		else if(path.equals("/sender_restarted") && argsSize==0)
		{
			card.setStatus("Sender Was Restarted");
			card.setLabel(4,"Restarted");
		}

		else if(path.equals("/status") && argsSize==12)
		{

/*
//message_number
(Long)args.get(0)

//input_port_count
(Integer)args.get(1)

//channel_offset
(Integer)args.get(2)

//buffer fill level
(Float)args.get(3)

//can_read_count / buffer bytes
(Long))args.get(4)

//interval
(Float)args.get(5)

//avg interval
(Float)args.get(6)

//remote_xrun_counter
(Long)args.get(7)

//local_xrun_counter
(Long)args.get(8)

//multi_channel_drop_counter
(Long)args.get(9)

//buffer_overflow_counter
(Long)args.get(10)

//cycle business
(Float)args.get(11)
*/

			card.setStatus("Receiving Audio Message #"+args.get(0));

			String s="Receiving Channels: ";
			if( (Integer)args.get(2) > 0 )
			{
				s+="("+(Integer)args.get(2)+"+)";
			}
			card.setLabel(4,s+args.get(1));

			card.setLabel(5,"Fill: "
				+String.format(new DecimalFormat("0.00").format((Float)args.get(3)))
				+" MC Periods, "+args.get(4)+" Bytes");

			card.setLabel(6,"Interval ms: "
				+String.format(new DecimalFormat("0.00").format((Float)args.get(6)))
				+" XRuns R: "+(Long)args.get(7)+" L: "+(Long)args.get(8));

			card.setLabel(7,"Drops: "+(Long)args.get(9)+" Overflows: "+(Long)args.get(10)+" Busy: "
				+String.format(new DecimalFormat("0.00").format((Float)args.get(11))) );
		}

	}//end accept
}//end GuiOscListener
