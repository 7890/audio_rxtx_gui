package ch.lowres.audio_rxtx.gui;

//tb/1410

public class Watchdog extends Thread 
{
	static jack_audio_send_GUI g;

	//just a wrapper to interrupt. let process stop cooperatively
	public void cancel()
	{
		interrupt();
	}

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
			{//////
			}
		}//end while not interrupted
		System.out.println("Watchdog finished.");
	}//end run
} //end class Watchdog
