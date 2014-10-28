package ch.lowres.audio_rxtx.gui;

//tb/1010/1105/1410

public class RunCmd extends Thread 
{
	String sCmd="";

	//==0: normally good
	// >0 || <0: error
	int iExitStatus=-1;

	//if true, input will be consumed but absorbed / ignored
	boolean devNull=false;

	OSTest os;

	public RunCmd(String cmd)
	{
		sCmd=cmd;
		os=new OSTest();
	}

	public RunCmd()
	{
		os=new OSTest();
	}

	public void setCmd(String cmd)
	{
		sCmd=cmd;
	}

	public String getCmd()
	{
		return sCmd;
	}

	public int getExitStatus()
	{
		return iExitStatus;
	}

	public void devNull(boolean b)
	{
		devNull=b;
	}

	public void maxPrio()
	{
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	}

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

			if (os.isUnix() || os.isMac())
			{
				pb = new ProcessBuilder("/bin/sh", "-c", sCmd);
			}
			else if (os.isWindows())
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
			System.out.println("RunCmd '"+sCmd+"': "+ex.toString());
		}
		finally
		{
			try {shellIn.close();} catch (Exception ignoreMe) {}
		}

		System.out.println("RunCmd '"+sCmd+"' done. Exit was "+iExitStatus);

	}//end run
} //end class RunCmd
