package ch.lowres.audio_rxtx.gui;

//tb/1410

import com.illposed.osc.*;
import java.net.InetAddress;

import java.util.Date;
import java.util.List;

import java.text.DecimalFormat;

public class GuiOscListener implements OSCListener 
{
	static jack_audio_send_GUI g;
	static jack_audio_send_cmdline_API api;

	public static void println(String s)
	{
		System.out.println(s);
	}

	@Override
	public void acceptMessage(Date time,OSCMessage msg) 
	{
		String path=msg.getAddress();
		List<Object> args=msg.getArguments();
		int argsSize=args.size();

//		println("osc msg received: "+path+" ("+argsSize+" args)");

		if(path.equals("/startup") && argsSize==2)
		{
			g.setStatus("jack_audio_send Started");
			api.version=(Float)args.get(0);
			api.format_version=(Float)args.get(1);

			g.running.label_1.setText("jack_audio_send v"+api.version+" Format v"+api.format_version);
		}

		else if(path.equals("/client_name_changed"))
		{
			g.setStatus("JACK Client Name Changed");
		}

		else if(path.equals("/config_dump") && argsSize==16)
		{
			g.setStatus("config dump received");

			if(api._lport!=(Integer)args.get(0))
			{
				api._lport=(Integer)args.get(0);

				//reconfigure sender
				try
				{
					g.sender.close();
					g.sender=new OSCPortOut(InetAddress.getLocalHost(), api._lport);
				}
				catch(Exception ex)
				{/////
				}
			}

			//could have changed
			api._target_port=(Integer)args.get(2);
			api._name=(String)args.get(3);

			api.jack_sample_rate=(Integer)args.get(5);
			api.jack_period_size=(Integer)args.get(6);

			api.msg_size=(Integer)args.get(10);
			api.transfer_size=(Integer)args.get(11);
			api.expected_network_data_rate=(Float)args.get(12);

			g.running.label_2.setText("JACK: "+api.jack_sample_rate+" / "+api.jack_period_size
				+"    TRF: "+ (api._16 ? "16 bit Integer" : "32 bit Float")
			);
		}

		else if(path.equals("/autoconnect") && argsSize==2)
		{
			g.setStatus("Autoconnecting JACK Ports");
			//0: from 1: to
			api.total_connected_ports++;

			g.running.label_3.setText("Autoconnected Ports: "+api.total_connected_ports+" / "+api._in);
		}

		else if(path.equals("/start_main_loop"))
		{
			g.setStatus("autoconnecting JACK ports");
		}

		else if(path.equals("/offering") && argsSize==1)
		{
			g.setStatus("Offering Audio Message #"+args.get(0));

			g.running.label_4.setText(":"+api._lport+" -> "+api._target_host+":"+api._target_port);
		}

		else if(path.equals("/receiver_denied_transmission") && argsSize==3)
		{
			//receiver props:
			//0: format_version
			//1: sample_rate
			//2: bytes per sample

			g.running.label_4.setText("Transmission denied ("
				+args.get(0)+", "+args.get(1)+", "
				+((Integer)args.get(2)==2 ? "16" : "32")+")");

			g.setStatus("Receiver denied Transmission");

			g.running.button_stop_transmission.setLabel("OK");
		}

		else if(path.equals("/receiver_accepted_transmission"))
		{
			g.setStatus("Receiver accpeted Transmission");
		}

		else if(path.equals("/sending") && argsSize==7)
		{

			g.running.label_4.setText(":"+api._lport+" -> "+api._target_host+":"+api._target_port);

			g.running.label_5.setText( 
				String.format(new DecimalFormat("0.00").format(api.expected_network_data_rate))
				+" kbit/s   "
				+String.format(new DecimalFormat("0.00").format(api.expected_network_data_rate/1000/8))
				+" MB/s");

			//hms, transferred mb
			g.running.label_6.setText((String)args.get(1)
				+"   "+String.format(new DecimalFormat("0.00").format((Float)args.get(4)))
				+" "+(String)args.get(5)
				+"   ("+api._in+" CH)");

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

			g.running.label_3.setText("Process Terminated");

			if(args.get(0).equals("nolibjack"))
			{
				g.running.label_4.setText("No libjack Found. Is JACK Installed?");
				g.running.label_5.setText("See http://www.jackaudio.org");

			}
			else if(args.get(0).equals("nojack"))
			{
				g.running.label_4.setText("JACK Not Running (Server '"+api._sname+"')");
			}

			g.running.button_stop_transmission.setLabel("OK");

			g.setStatus("jack_audio_send Quit: "+args.get(0));
		}
	}//end acceptMessage
}//end GuiOscListener
