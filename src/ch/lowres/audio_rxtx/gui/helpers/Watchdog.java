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

package ch.lowres.audio_rxtx.gui.helpers;
import ch.lowres.audio_rxtx.gui.*;

/**
* Thread to watch if another thread is running or terminated.
*/
//========================================================================
public class Watchdog extends Thread 
{
	private static Main g;

	static RunCmd cmd;

//========================================================================
	public Watchdog()
	{
	}

//========================================================================
	public Watchdog(RunCmd c)
	{
		cmd=c;
	}

//========================================================================
	//just a wrapper to interrupt. let process stop cooperatively
	public void cancel()
	{
		interrupt();
	}

//========================================================================
	public void run() 
	{
		while(!Thread.currentThread().isInterrupted())
		{
			if(cmd!=null)
			{
				if(cmd.getState()!=Thread.State.TERMINATED)
				{ 
					//System.out.println("cmd still running");
					//g.running.label_9.setText("proc running");
				}
				else
				{
					//System.out.println("cmd terminated, exit status was "+cmd.getExitStatus());
					//g.running.label_9.setText("proc terminated");
				}
			}

			try
			{
				Thread.sleep(500);
			}
			catch (Exception ex) //i.e. interrupted
			{///
			}
		}//end while not interrupted
		g.p("Watchdog finished.");
	}//end run
} //end class Watchdog
