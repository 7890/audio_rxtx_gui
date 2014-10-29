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

//========================================================================
public class Watchdog extends Thread 
{
	static jack_audio_send_GUI g;

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
			if(g.cmd!=null)
			{
				if(g.cmd.getState()!=Thread.State.TERMINATED)
				{ 
					//System.out.println("cmd still running");
					//g.running.label_9.setText("proc running");
				}
				else
				{
					//System.out.println("cmd terminated, exit status was "+g.cmd.getExitStatus());
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
		System.out.println("Watchdog finished.");
	}//end run
} //end class Watchdog
