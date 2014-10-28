package ch.lowres.audio_rxtx.gui;

//1410

public class OSTest
{
	private String os;
	private boolean isUnix;
	private boolean isWindows;
	private boolean isMac;

	public OSTest()
	{
		determineOS();
	}

	public void determineOS()
	{
		os = System.getProperty("os.name").toLowerCase();
		isUnix = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0;
		isWindows = os.indexOf("win") >= 0;
		isMac = os.indexOf("mac") >= 0;
	}

	public String getName()
	{
		return System.getProperty("os.name");
	}

	boolean isUnix()
	{
		return isUnix;
	}

	boolean isLinux()
	{
		return isUnix;
	}

	boolean isMac()
	{
		return isMac;
	}

	boolean isWindows()
	{
		return isWindows;
	}
} //end class OSTest
