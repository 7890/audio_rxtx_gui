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

import javax.swing.*;

import java.awt.geom.*;

/**
* Extended JRadioButton with gradient, focus and mouse hover paint.
*/
//========================================================================
public class ARadioButton extends JRadioButton implements MouseListener
{
	private static GUI g;
	private static Fonts f;

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
	public void init()
	{
		setOpaque(false);
		setFont(f.fontNormal);

		setCursor(new Cursor(Cursor.HAND_CURSOR));

//http://stackoverflow.com/questions/4274606/how-to-change-cursor-icon-in-java
/*
//Standard Cursor Image:
//setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//User defined Image:
Toolkit toolkit = Toolkit.getDefaultToolkit();
Image image = toolkit.getImage("grabber.png");
Cursor c = toolkit.createCustomCursor(image, new Point(getX(), getY()), "img");
setCursor (c);
*/
		setIconTextGap(g.commonWidgetHeight);

		//http://www.java2s.com/Code/Java/Swing-JFC/CustomizeJCheckBoxicons.htm
		//"remove" standard icons
		setIcon(new ImageIcon(""));
		setSelectedIcon(new ImageIcon(""));
		setDisabledIcon(new ImageIcon(""));
		setDisabledSelectedIcon(new ImageIcon(""));
		setPressedIcon(new ImageIcon(""));
		setRolloverIcon(new ImageIcon(""));
		setRolloverSelectedIcon(new ImageIcon(""));

		addMouseListener(this);
	}

//========================================================================
	@Override
	public void paintComponent(Graphics gfx) 
	{
		FocusPaint.gradient(gfx,this);
		super.paintComponent(gfx);

		Graphics2D g2 = (Graphics2D) gfx;

		//hover
		if(alpha!=0 && !hasFocus())
		{
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2.setPaint(Colors.hovered_overlay);
			g2.fill( new Rectangle2D.Float(0, 0, getBounds().width, getBounds().height) );
		}

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

		FocusPaint.paint(gfx,this);

		//draw the radiobutton
		g2.setPaint(Colors.black);
		float padding=g.commonWidgetHeight*.15f;
		g2.setStroke(new BasicStroke( (float) (Math.max(1,(g.commonWidgetHeight*0.05)) )));
		g2.draw( new Ellipse2D.Float(
			padding,
			padding,
			g.commonWidgetHeight-2*padding,
			g.commonWidgetHeight-2*padding
		));

		if(isSelected())
		{
			padding=g.commonWidgetHeight*.35f;
			g2.fill( new Ellipse2D.Float(
				padding,
				padding,
				g.commonWidgetHeight-2*padding,
				g.commonWidgetHeight-2*padding
			));
		}
	}//end paintComponent

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
		return new Dimension((int)super.getPreferredSize().getWidth()+30,g.commonWidgetHeight);
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
