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
import ch.lowres.audio_rxtx.gui.helpers.*;
import ch.lowres.audio_rxtx.gui.widgets.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import java.net.*;

/**
* InfoDialog showing build and runtime infos
*/
//========================================================================
public class InfoDialog extends ADialog
{
	private static Main m;

//========================================================================
	public InfoDialog(Frame f, String title, boolean modality) 
	{
		super(f,title,modality);
	}

//========================================================================
	public String getHtml()
	{
		return getHtml(m.tr("unknown"));
	}

//========================================================================
	public void updateHtml()
	{
		InetAddress localMachine=m.iot.getLocalIPAddress();
		if(localMachine!=null)
		{
			getTextPane().setText(getHtml(localMachine.getHostAddress()));
		}
		else
		{
			getTextPane().setText(getHtml());
		}
	}

//========================================================================
	public String getHtml(String ipAddress)
	{
		StringBuffer sb=new StringBuffer();
		sb.append("<html><body>");

		sb.append("<h2>"+m.tr("Build Information")+"</h2>");
		sb.append("<p>"+BuildInfo.get().replace("\n", "<br>")+"</p>");

		sb.append("<h2>"+m.tr("Runtime Information")+"</h2>");
		sb.append("<p>java.version: "+m.os.getJavaVersion()+"<br>");
		sb.append("java.vm.name: "+m.os.getVMName()+"<br>");
		sb.append("java.vm.version: "+m.os.getVMVersion()+"<br>");
		sb.append("os.name: "+m.os.getOSName()+"<br>");
		sb.append("64bit: "+m.os.is64Bits()+"<br>");
		sb.append("DPI: "+m.os.getDPI()+"<br>");
		sb.append(m.tr("Temporary cache dir")+":<br>"+m.tmpDir+"<br>");
		sb.append(m.tr("MD5 Sum of jar")+": "+m.iot.getJarMd5Sum()+"<br>");
		sb.append(m.tr("IP address of default NIC")+": "+ipAddress+"</p>");

		sb.append("</body></html>");
		return sb.toString();
		//getTextPane().setText(sb.toString());
	}//end setHtml
}//end class InfoDialog
