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

//http://stackoverflow.com/questions/19577893/custom-scrollbar-arrows

package ch.lowres.audio_rxtx.gui.widgets;
import ch.lowres.audio_rxtx.gui.*;
import ch.lowres.audio_rxtx.gui.helpers.*;


import java.awt.*;
import javax.swing.*;

import javax.swing.plaf.basic.BasicScrollBarUI;

/**
* Custom Scrollbar
*/
//========================================================================
public class AScrollbarUI extends BasicScrollBarUI 
{
//========================================================================
	@Override
	protected JButton createDecreaseButton(int orientation)
	{
		JButton decreaseButton = new JButton(getAppropriateIcon(orientation))
		{
			@Override
			public Dimension getPreferredSize()
			{
				return new Dimension(32, 32);
			}
		};
		decreaseButton.setFocusable(false);
		return decreaseButton;
	}

//========================================================================
	@Override
	protected JButton createIncreaseButton(int orientation)
	{
		JButton increaseButton = new JButton(getAppropriateIcon(orientation))
		{
			@Override
			public Dimension getPreferredSize()
			{
				return new Dimension(32, 32);
			}
		};
		increaseButton.setFocusable(false);
		return increaseButton;
	}

//========================================================================
	private ImageIcon getAppropriateIcon(int orientation)
	{
		switch(orientation)
		{
			case SwingConstants.SOUTH: return Images.arrowDown;
			case SwingConstants.NORTH: return Images.arrowUp;
			case SwingConstants.EAST: return Images.arrowRight;
			default: return Images.arrowLeft;
		}
	}
}//end class AScrollbarUI
