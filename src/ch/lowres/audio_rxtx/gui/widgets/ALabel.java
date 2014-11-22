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

import java.awt.*;

import javax.swing.*;

//========================================================================
public class ALabel extends JLabel
{
//========================================================================
	public ALabel()
	{
		super("");
		init();
	}

//========================================================================
	public ALabel(String label)
	{
		super(label);
		init();
	}

//========================================================================
	public ALabel(String label, int align)
	{
		super(label,align);
		init();
	}

//========================================================================
	void init()
	{
		setOpaque(false);
	}

//========================================================================
	@Override
	public int getHeight()
	{
		return 30;
	}

//========================================================================
/*	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(200,30);
	}
*/
}//end class ALabel
