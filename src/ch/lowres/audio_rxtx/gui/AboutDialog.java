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
import java.util.*;

/**
* Dialog showing version of audio_rxtx_gui, used libs, project url etc.
*/
//========================================================================
public class AboutDialog extends ADialog
{
//========================================================================
	public AboutDialog(Frame f, String title, boolean modality) 
	{
		super(f,title,modality);
		getTextPane().setHighlighter(null);
	}

//========================================================================
	public String getHtml()
	{
		StringBuffer sb=new StringBuffer();
		sb.append("<html><body>");

		sb.append("<p><strong>"+l.tr("About  -- THIS PROGRAM IS NOT YET READY FOR ACTION")+"</strong><br>");
		sb.append("<br>");
		//http://stackoverflow.com/questions/9117814/jtextpane-with-html-local-image-wont-load
		sb.append("<img src=\""
			+this.getClass().getClassLoader().getResource(
			"resources/images/audio_rxtx_about_screen.png"
			).toString()+"\"/>");
		sb.append("<br>");
		sb.append(BuildInfo.getGitCommit()+" (V "+m.progVersion+")</p>");

		sb.append("<p><strong>"+l.tr("audio_rxtx GUI for jack_audio_{send, receive}")+"</strong></p>");
		sb.append("<h2>"+l.tr("Live JACK Audio Data Transmission")+"</h2>");

		sb.append("<p><strong>"+l.tr("Credits & Program Libraries")+"</strong></p>");
		sb.append("<table>");// width=\"400px\">");
		sb.append("<tr>");

		sb.append("<td>JACK</td><td>"+ahref("http://www.jackaudio.org")+"</td>");
		sb.append("</tr><tr>");

		sb.append("<td>liblo</td><td>"+ahref("http://liblo.sourceforge.net")+"</td>");
		sb.append("</tr><tr>");

		sb.append("<td>JavaOSC</td><td>"+ahref("https://github.com/hoijui/JavaOSC")+"</td>");
		sb.append("</tr><tr>");

		sb.append("<td>gettext</td><td>"+ahref("https://code.google.com/p/gettext-commons")+"</td>");
		sb.append("</tr><tr>");

		sb.append("<td>OpenJDK</td><td>"+ahref("http://openjdk.java.net")+"</td>");
/*
http://stackoverflow.com/
http://javatechniques.com/
*/
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<p><strong>"+ahref("https://github.com/7890/audio_rxtx_gui")+"</strong></p>");
		sb.append("<p>© 2014-2015 Thomas Brand &lt;tom@trellis.ch&gt;</p>");
		sb.append("<p><small>This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License version 2 or later.</small></p>");
		sb.append("</body></html>");
		return sb.toString();
	}//end getAboutHtml

/*
	public Dimension getPreferedSize()
	{
		return new Dimension(getTextPane().getWidth(),100);
	}
*/
}//end class AboutDialog
