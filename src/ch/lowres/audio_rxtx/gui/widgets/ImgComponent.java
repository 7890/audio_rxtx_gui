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

package ch.lowres.audio_rxtx.gui.widgets;
import ch.lowres.audio_rxtx.gui.*;
import ch.lowres.audio_rxtx.gui.helpers.*;

import java.awt.*;

import javax.swing.ImageIcon;

import java.net.MalformedURLException;
import java.net.URL;

/**
* Used in {@link AboutDialog}, image with version paint.
*/
//========================================================================
public class ImgComponent extends Component
{
	private static Main g;

	private ImageIcon audio_rxtx_logo;
	private int imageTop=130;
	private int preferredWidth=290;

//========================================================================
	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(preferredWidth,imageTop);
	}

//========================================================================
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(preferredWidth,imageTop);
	}

//========================================================================
	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(preferredWidth,imageTop);
	}

//========================================================================
	public ImgComponent()
	{
		try
		{
			//audio_rxtx_logo = new ImageIcon(new URL("http://..."));
			//audio_rxtx_logo = new ImageIcon(new URL("file:///..."));
			audio_rxtx_logo=new IOTools().createImageIconFromJar("/resources/audio_rxtx_about_screen.png");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

//========================================================================
	public void paint(Graphics gfx)
	{
		gfx.setColor(Color.WHITE);
		gfx.fillRect(0, 0, getWidth(),getHeight());

		int x = (getWidth() - audio_rxtx_logo.getIconWidth()) / 2;
		int y=0;

		gfx.drawImage(audio_rxtx_logo.getImage(), x, y, audio_rxtx_logo.getIconWidth(), audio_rxtx_logo.getIconHeight(), this);
		gfx.setColor(Color.BLACK);
//		gfx.drawString("v"+g.progVersion, getWidth()-75, 25);




	}//end paint
}//end class ImgComponent
