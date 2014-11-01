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

import com.illposed.osc.*;
import java.net.InetAddress;

import java.util.Date;
import java.util.List;

import java.text.DecimalFormat;

//========================================================================
public class GuiOscListener implements OSCListener 
{
	static Main g;

//========================================================================
	public static void println(String s)
	{
		System.out.println(s);
	}

//========================================================================
	@Override
	public void acceptMessage(Date time,OSCMessage msg) 
	{
		String path=msg.getAddress();
		List<Object> args=msg.getArguments();
		int argsSize=args.size();

		//println("osc msg received: "+path+" ("+argsSize+" args)");
		if(path.equals("/startup") && argsSize==2)
		{
			g.setStatus("jack_audio_send Started");
			g.apis.version=(Float)args.get(0);
			g.apis.format_version=(Float)args.get(1);

			g.runningSend.label_1.setText("jack_audio_send v"+g.apis.version+" Format v"+g.apis.format_version);
		}

		else if(path.equals("/client_name_changed"))
		{
			g.setStatus("JACK Client Name Changed");
		}

		else if(path.equals("/config_dump") && argsSize==16)
		{
			g.setStatus("config dump received");

			if(g.apis._lport!=(Integer)args.get(0))
			{
				g.apis._lport=(Integer)args.get(0);

				//reconfigure sender
				try
				{
					g.sender.close();
					g.sender=new OSCPortOut(InetAddress.getLocalHost(), g.apis._lport);
				}
				catch(Exception ex)
				{///
				}
			}

			//could have changed
			g.apis._target_port=(Integer)args.get(2);
			g.apis._name=(String)args.get(3);

			g.apis.jack_sample_rate=(Integer)args.get(5);
			g.apis.jack_period_size=(Integer)args.get(6);

			g.apis.msg_size=(Integer)args.get(10);
			g.apis.transfer_size=(Integer)args.get(11);
			g.apis.expected_network_data_rate=(Float)args.get(12);

			g.runningSend.label_2.setText("JACK: "+g.apis.jack_sample_rate+" / "+g.apis.jack_period_size
				+"    TRF: "+ (g.apis._16 ? "16 bit Integer" : "32 bit Float")
			);
		}

		else if(path.equals("/autoconnect") && argsSize==2)
		{
			g.setStatus("Autoconnecting JACK Ports");
			//0: from 1: to
			g.apis.total_connected_ports++;

			g.runningSend.label_3.setText("Autoconnected Ports: "+g.apis.total_connected_ports+" / "+g.apis._in);
		}

		else if(path.equals("/start_main_loop"))
		{
			g.setStatus("autoconnecting JACK ports");
		}

		else if(path.equals("/offering") && argsSize==1)
		{
			g.setStatus("Offering Audio Message #"+args.get(0));

			g.runningSend.label_4.setText(":"+g.apis._lport+" -> "+g.apis._target_host+":"+g.apis._target_port);
		}

		else if(path.equals("/receiver_denied_transmission") && argsSize==3)
		{
			//receiver props:
			//0: format_version
			//1: sample_rate
			//2: bytes per sample

			g.runningSend.label_4.setText("Transmission denied ("
				+args.get(0)+", "+args.get(1)+", "
				+((Integer)args.get(2)==2 ? "16" : "32")+")");

			g.setStatus("Receiver denied Transmission");

			g.runningSend.button_stop_transmission.setLabel("OK");
		}

		else if(path.equals("/receiver_accepted_transmission"))
		{
			g.setStatus("Receiver accpeted Transmission");
		}

		else if(path.equals("/sending") && argsSize==7)
		{

			g.runningSend.label_4.setText(":"+g.apis._lport+" -> "+g.apis._target_host+":"+g.apis._target_port);

			g.runningSend.label_5.setText( 
				String.format(new DecimalFormat("0.00").format(g.apis.expected_network_data_rate))
				+" kbit/s   "
				+String.format(new DecimalFormat("0.00").format(g.apis.expected_network_data_rate/1000/8))
				+" MB/s");

			//hms, transferred mb
			g.runningSend.label_6.setText((String)args.get(1)
				+"   "+String.format(new DecimalFormat("0.00").format((Float)args.get(4)))
				+" "+(String)args.get(5)
				+"   ("+g.apis._in+" CH)");

			//msg #
			g.setStatus("Sending Audio Message #"+args.get(0));
		}

		else if(path.equals("/receiver_requested_pause"))
		{
			g.setStatus("Receiver requested Pause");
		}

		else if(path.equals("/test_finished") && argsSize==1)
		{
			//0: # of cycles elapsed
			g.setStatus("Test finished");
		}

		else if(path.equals("/quit") && argsSize==1)
		{
			//tell quit reason

			g.runningSend.label_3.setText("Process Terminated");

			if(args.get(0).equals("nolibjack"))
			{
				g.runningSend.label_4.setText("No libjack Found. Is JACK Installed?");
				g.runningSend.label_5.setText("See http://www.jackaudio.org");

			}
			else if(args.get(0).equals("nojack"))
			{
				g.runningSend.label_4.setText("JACK Not Running (Server '"+g.apis._sname+"')");
			}

			g.runningSend.button_stop_transmission.setLabel("OK");

			g.setStatus("jack_audio_send Quit: "+args.get(0));
		}
	}//end acceptMessage
}//end GuiOscListener
