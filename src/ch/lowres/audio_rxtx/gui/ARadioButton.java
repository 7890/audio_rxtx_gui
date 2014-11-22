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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.awt.geom.*;

//========================================================================
public class ARadioButton extends JRadioButton implements MouseListener
{
	//0: invisible overlay
	private float alpha = 0.0f;

//========================================================================
	public ARadioButton()
	{
		super("");
		init();
	}

//========================================================================
	public ARadioButton(String label)
	{
		super(label);
		init();
	}

//========================================================================
	void init()
	{
		setOpaque(false);
		addMouseListener(this);
	}

//========================================================================
	@Override
	public void paintComponent(Graphics g) 
	{
		FocusPaint.gradient(g,this);
		super.paintComponent(g);

		//hover
		Graphics2D g2 = (Graphics2D) g;
		if(alpha!=0)
		{
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2.setPaint(Colors.hovered_overlay);
			g2.fill( new Rectangle2D.Float(0, 0, getBounds().width, getBounds().height) );
		}

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

		FocusPaint.paint(g,this);
	}

//========================================================================
	public void mouseEntered(MouseEvent e) 
	{
		alpha=0.2f;
		repaint();
	}
	public void mouseExited(MouseEvent e) 
	{
		alpha=0f;
		repaint();
	}
	public void mousePressed(MouseEvent e) {repaint();}
	public void mouseReleased(MouseEvent e) {repaint();}
	public void mouseClicked(MouseEvent e) {}

//========================================================================
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(200,30);
	}

//legacy wrappers
//========================================================================
	public void setState(boolean b)
	{
		setSelected(b);
	}

//========================================================================
	public boolean getState()
	{
		return isSelected();
	}

//========================================================================
/*	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(200,30);
	}
*/
}//end class ARadioButton
