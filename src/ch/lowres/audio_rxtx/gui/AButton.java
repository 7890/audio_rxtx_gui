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

import javax.swing.JButton;

//========================================================================
public class AButton extends JButton implements KeyListener, FocusListener
{
//========================================================================
	public AButton()
	{
		super("");
		init();
	}

//========================================================================
	public AButton(String label)
	{
		super(label);
		init();
	}

//========================================================================
	void init()
	{
		addKeyListener(this);
		addFocusListener(this);
	}

//========================================================================
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(50,30);
	}

//========================================================================
	@Override
	public void paint(Graphics g) 
	{
		super.paint(g);
		FocusPaint.paint(g,this);
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
	public void focusLost(FocusEvent fe) {repaint();}
	public void focusGained(FocusEvent fe) {repaint();}
}//end class AButton
