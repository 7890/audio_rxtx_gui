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
import java.awt.event.*;

import javax.swing.JButton;

/**
* Extended JButton with focus paint.
*/
//========================================================================
public class AButton extends JButton implements KeyListener, FocusListener
{
	static Main m;
	static Fonts f;

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
		setFont(f.fontLarge);
		setBackground(Colors.button_background);
		setForeground(Colors.button_foreground);

		setCursor(new Cursor(Cursor.HAND_CURSOR));
		addKeyListener(this);
		addFocusListener(this);
	}

//========================================================================
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension((int)super.getPreferredSize().getWidth(),
			(int)(f.fontDefaultSize * f.fontLargeFactor * m.buttonHeightScale));
	}

//========================================================================
	@Override
	public void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		FocusPaint.paint(g,this);
	}

//========================================================================
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {repaint();}
	public void focusLost(FocusEvent fe) {repaint();}
	public void focusGained(FocusEvent fe) {repaint();}
}//end class AButton
