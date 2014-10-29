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
public class OSTest
{
	private String os;
	private boolean isUnix;
	private boolean isWindows;
	private boolean isMac;

//========================================================================
	public OSTest()
	{
		determineOS();
	}

//========================================================================
	public void determineOS()
	{
		os = System.getProperty("os.name").toLowerCase();
		isUnix = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0;
		isWindows = os.indexOf("win") >= 0;
		isMac = os.indexOf("mac") >= 0;
	}

//========================================================================
	public String getName()
	{
		return System.getProperty("os.name");
	}

//========================================================================
	boolean isUnix()
	{
		return isUnix;
	}

//========================================================================
	boolean isLinux()
	{
		return isUnix;
	}

//========================================================================
	boolean isMac()
	{
		return isMac;
	}

//========================================================================
	boolean isWindows()
	{
		return isWindows;
	}
} //end class OSTest
