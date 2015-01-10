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

import java.awt.Image;
import javax.swing.ImageIcon;

/**
* Holding Images to be accessed statically by any component.
*/
//========================================================================
public class Images
{
	private static Main m;

	public static Image appIcon;
	public static ImageIcon arrowUp;
	public static ImageIcon arrowDown;
	public static ImageIcon arrowLeft;
	public static ImageIcon arrowRight;

	private static boolean initialized=false;

//========================================================================
	public static void init()
	{
		if(!initialized)
		{
			appIcon=m.iot.createImageFromJar("/resources/images/audio_rxtx_icon.png");

			arrowUp=m.iot.createImageIconFromJar("/resources/images/arrow-up.png");
			arrowDown=m.iot.createImageIconFromJar("/resources/images/arrow-down.png");
			arrowLeft=m.iot.createImageIconFromJar("/resources/images/arrow-left.png");
			arrowRight=m.iot.createImageIconFromJar("/resources/images/arrow-right.png");
		}
	}
}//end class Images
