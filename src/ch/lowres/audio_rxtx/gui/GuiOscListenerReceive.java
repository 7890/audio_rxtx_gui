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
//			card.setLabel(3,"Autoconnected Ports: "+g.apis.total_connected_ports+" / "+g.apis._in);
			card.setLabel(3,"Autoconnected Ports: "+g.apir.total_connected_ports+" / "+g.apir._out);
		}

		else if(path.equals("/status") && argsSize==12)
		{
			card.setStatus("Receiving Audio Message #"+args.get(0));
		}

	}//end accept
}//end GuiOscListener
