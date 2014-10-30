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
	private boolean isUnix;
	private boolean isWindows;
	private boolean isMac;
	private boolean is32Bits;
	private boolean is64Bits;

//========================================================================
	public OSTest()
	{
		determineOS();
		determineArch();
	}

//========================================================================
	public void determineOS()
	{
		String os = System.getProperty("os.name").toLowerCase();
		isUnix = os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0;
		isWindows = os.indexOf("win") >= 0;
		isMac = os.indexOf("mac") >= 0;
	}

//========================================================================
	//https://community.oracle.com/thread/2086185?start=0&tstart=0
	public void determineArch()
	{
		String bits = System.getProperty("sun.arch.data.model", "?");
		if(bits.equals("64"))
		{
			is64Bits=true;
			is32Bits=false;
			return;
		}
		else if(bits.equals("?"))
		{
			// probably sun.arch.data.model isn't available
			// maybe not a Sun JVM?
			// try with the vm.name property
			is64Bits=System.getProperty("java.vm.name").toLowerCase().indexOf("64") >= 0;
			is32Bits=!is64Bits;
			return;
		}
		// probably 32bit
		is32Bits=true;
		is32Bits=false;
	}

//========================================================================
	public String getOSName()
	{
		return System.getProperty("os.name");
	}

//========================================================================
	public String getVMName()
	{
		return System.getProperty("java.vm.name");
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

//========================================================================
	boolean is32Bits()
	{
		return is32Bits;
	}

//========================================================================
	boolean is64Bits()
	{
		return is64Bits;
	}
} //end class OSTest
