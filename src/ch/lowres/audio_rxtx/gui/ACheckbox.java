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
public class ACheckbox extends JCheckBox implements KeyListener, FocusListener, MouseListener
{
	//0: invisible overlay
	private float alpha = 0.0f;

//========================================================================
	public ACheckbox()
	{
		super("");
		init();
	}

//========================================================================
	public ACheckbox(String label)
	{
		super(label);
		init();
	}

//========================================================================
	public ACheckbox(String label, boolean state)
	{
		super(label, state);
		init();
	}

//========================================================================
	void init()
	{
		addKeyListener(this);
		addFocusListener(this);
		addMouseListener(this);
	}

//========================================================================
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(200,30);
	}

//========================================================================
	@Override
	public void paintComponent(Graphics g) 
	{
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
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {repaint();}

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
	public void focusLost(FocusEvent fe) {repaint();}

	public void focusGained(FocusEvent fe) 
	{
		repaint();
		Component c=getParent();

		while(c!=null)
		{
			//System.out.println(c);
			if(c instanceof JScrollPane)
			{
				JScrollBar sb=((JScrollPane)c).getVerticalScrollBar();

				int vp_height=(int)((JScrollPane)c).getViewport().getHeight();

				if(getBounds().y + getBounds().height > sb.getValue()+vp_height
					|| getBounds().y < sb.getValue())
				{
					sb.setValue(getBounds().y);
				}
				break;
			}
			c=c.getParent();
		}
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
}//end class ACheckbox
