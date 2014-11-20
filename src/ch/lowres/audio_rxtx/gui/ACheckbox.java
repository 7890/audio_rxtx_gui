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

import java.awt.geom.Line2D;

import javax.swing.*;

//========================================================================
public class ACheckbox extends Checkbox implements KeyListener, FocusListener, MouseListener
{
	private FocusPaint fpaint;

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
	public ACheckbox(String label, boolean state, CheckboxGroup group)
	{
		super(label, state, group);
		init();
	}

//========================================================================
	public ACheckbox(String label, CheckboxGroup group, boolean state)
	{
		super(label, group, state);
		init();
	}

//========================================================================
	void init()
	{
		fpaint=new FocusPaint();
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
	public void paint(Graphics g) 
	{
		fpaint.paint(g,this);
		super.paint(g);
	}

//========================================================================
	@Override
	public void update(Graphics g) 
	{
		paint(g);
	}

//========================================================================
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {repaint();}
	public void focusLost(FocusEvent fe)
	{
		repaint();
	}

//========================================================================
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {repaint();}
	public void mouseReleased(MouseEvent e) {repaint();}
	public void mouseClicked(MouseEvent e) {}

//========================================================================
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
}//end class ACheckbox
