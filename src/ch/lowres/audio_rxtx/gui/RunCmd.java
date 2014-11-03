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
public class RunCmd extends Thread 
{
	Main g;

	String sCmd="";

	//==0: normally good
	// >0 || <0: error
	int iExitStatus=-1;

	//if true, input will be consumed but absorbed / ignored
	boolean devNull=false;

//========================================================================
	public RunCmd(String cmd)
	{
		sCmd=cmd;
	}

//========================================================================
	public RunCmd()
	{
	}

//========================================================================
	public void setCmd(String cmd)
	{
		sCmd=cmd;
	}

//========================================================================
	public String getCmd()
	{
		return sCmd;
	}

//========================================================================
	public int getExitStatus()
	{
		return iExitStatus;
	}

//========================================================================
	public void devNull(boolean b)
	{
		devNull=b;
	}

//========================================================================
	public void maxPrio()
	{
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
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
		//System.out.println("running "+sCmd);

		//http://forums.devx.com/showthread.php?t=147403
		//create a process for the shell
		ProcessBuilder pb=null;
		Process shell=null;
		java.io.InputStream shellIn=null;
		try
		{
			//http://stackoverflow.com/questions/671049/how-do-you-kill-a-thread-in-java
			//while(!Thread.currentThread().isInterrupted()) 

			if (g.os.isUnix() || g.os.isMac())
			{
				pb = new ProcessBuilder("/bin/sh", "-c", sCmd);
			}
			else if (g.os.isWindows())
			{
				pb = new ProcessBuilder("cmd.exe", "/C", sCmd);
			}
			//what else?

			//~ 2>&1
			pb.redirectErrorStream(true);

			//run command
			shell = pb.start();

			//output from command is input for us
			shellIn = shell.getInputStream();

			//consume output but possibly absorb
			int c;
			while ((c = shellIn.read())!=-1)
			{
				if(!devNull)
				{
					System.out.write(c);
				}
			}
			//close stream
			try {shellIn.close();} catch (Exception ignoreMe) {}

			iExitStatus = shell.waitFor();
		}//end try
		catch (Exception ex) //i.e. interrupted
		{
			g.p("RunCmd '"+sCmd+"': "+ex.toString());
		}
		finally
		{
			try {shellIn.close();} catch (Exception ignoreMe) {}
		}

		if(iExitStatus==0)
		{
			g.p("RunCmd '"+sCmd+"' done. Exit was "+iExitStatus);
		}
		else
		{
			g.e("RunCmd '"+sCmd+"' done. Exit was "+iExitStatus);
		}

	}//end run
} //end class RunCmd
