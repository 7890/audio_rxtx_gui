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
public class APanel extends JPanel
{
//========================================================================
	public APanel()
	{
		super();
		init();
	}

//========================================================================
	public APanel(LayoutManager lm)
	{
		super(lm);
		init();
	}

//========================================================================
	void init()
	{
		setOpaque(false);
	}

//========================================================================
//http://stackoverflow.com/questions/14364291/jpanel-gradient-background
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		GradientPaint gp = new GradientPaint(0, 0,
			getBackground().brighter(), 0, getHeight(),
			getBackground().darker().darker());
		g2d.setPaint(gp);
		g2d.fillRect(0, 0, getWidth(), getHeight()); 
	}
}//end class APanel
