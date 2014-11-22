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
* Running a shell command in a Thread using {@link ProcessBuilder}
*/
//========================================================================
public class RunCmd extends Thread 
{
	private Main g;
	private String commandLineString="";
	private int exitStatus=-1;
	private boolean devNull=false;

/**
 * Creates a RunCmd object with a command line string
 *
 * @param	cmd	the command line string that will be executed.
 */
//========================================================================
	public RunCmd(String cmd)
	{
		commandLineString=cmd;
	}

//========================================================================
	public RunCmd()
	{
	}

/**
 * (Re-)sets command line string
 *
 * @param	cmd	the command line string that will be executed.
 */
//========================================================================
	public void setCmd(String cmd)
	{
		commandLineString=cmd;
	}

/**
 * Returns currently set command line string
 *
 * @return	command line string (null if not yet set)
 */
//========================================================================
	public String getCmd()
	{
		return commandLineString;
	}

/**
* Returns shell exit status. Normally a value of 0 indicates no errors.
* <p>
* 0: normally good
* <p>
* >0 || <0: error
*
* @return	exit status received from shell process
*/
//========================================================================
	public int getExitStatus()
	{
		return exitStatus;
	}

/**
* Sets whether or not output from running process on std:out and std:err 
* will be printed or dropped (consumed silently)
*
* @param	devNull		true: drop, false: print
*/
//========================================================================
	public void devNull(boolean b)
	{
		devNull=b;
	}

/**
 * Sets the thread's priority to {@link Thread#MAX_PRIORITY}
 */
//========================================================================
	public void maxPrio()
	{
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	}


/**
* A wrapper to call {@link Thread#interrupt}. Let process thread stop cooperatively.
*/
//========================================================================
	public void cancel()
	{
		interrupt();
	}

/**
* Starts thread, executes command line string {@link #commandLineString}.
* <p>
* On Unix and Mac: '/bin/sh -c' is used to start a process.
* <p>
* On Windows: 'cmd.exe /C' is used to start a process.
* <p>
* Finishes immediately if {@link #commandLineString} is null. 
*/
//========================================================================
	@Override
	public void run() 
	{
		if(commandLineString==null)
		{
			g.w("RunCmd: command was not set");
			return;
		}

		//System.out.println("running "+commandLineString);

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
				pb = new ProcessBuilder("/bin/sh", "-c", commandLineString);
			}
			else if (g.os.isWindows())
			{
				pb = new ProcessBuilder("cmd.exe", "/C", commandLineString);
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

			exitStatus = shell.waitFor();
		}//end try
		catch (Exception ex) //i.e. interrupted
		{
			g.p("RunCmd '"+commandLineString+"': "+ex.toString());
		}
		finally
		{
			try {shellIn.close();} catch (Exception ignoreMe) {}
		}

		if(exitStatus==0)
		{
			g.p("RunCmd '"+commandLineString+"' done. Exit was "+exitStatus);
		}
		else
		{
			g.e("RunCmd '"+commandLineString+"' done. Exit was "+exitStatus);
		}
	}//end run
} //end class RunCmd
