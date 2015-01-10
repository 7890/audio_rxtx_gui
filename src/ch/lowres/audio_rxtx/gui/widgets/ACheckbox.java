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
* Extended JCheckBox with gradient, focus and mouse hover paint.
*/
//========================================================================
public class ACheckbox extends JCheckBox implements KeyListener, FocusListener, MouseListener
{
	private static Main m;
	private static Fonts f;

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
	public void init()
	{
		setOpaque(false);
		setFont(f.fontNormal);
		setCursor(new Cursor(Cursor.HAND_CURSOR));

		setIconTextGap(m.commonWidgetHeight);

		//http://www.java2s.com/Code/Java/Swing-JFC/CustomizeJCheckBoxicons.htm
		//"remove" standard icons
		setIcon(new ImageIcon(""));
		setSelectedIcon(new ImageIcon(""));
		setDisabledIcon(new ImageIcon(""));
		setDisabledSelectedIcon(new ImageIcon(""));
		setPressedIcon(new ImageIcon(""));
		setRolloverIcon(new ImageIcon(""));
		setRolloverSelectedIcon(new ImageIcon(""));

		addKeyListener(this);
		addFocusListener(this);
		addMouseListener(this);
	}

//========================================================================
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension((int)super.getPreferredSize().getWidth()+30,m.commonWidgetHeight);
	}

//========================================================================
	@Override
	public void paintComponent(Graphics g) 
	{
		FocusPaint.gradient(g,this);
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		//hover
		if(alpha!=0 && !hasFocus())
		{
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2.setPaint(Colors.hovered_overlay);
			g2.fill( new Rectangle2D.Float(0, 0, getBounds().width, getBounds().height) );
		}

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

		FocusPaint.paint(g,this);

		//draw the checkbox
		g2.setPaint(Colors.black);
		float padding=m.commonWidgetHeight*.15f;
		g2.setStroke(new BasicStroke( (float)(Math.max(1,(m.commonWidgetHeight*0.05)) )));
		g2.draw( new Rectangle2D.Float(
			padding,
			padding,
			m.commonWidgetHeight-2*padding,
			m.commonWidgetHeight-2*padding
		));

		if(isSelected())
		{
			padding=m.commonWidgetHeight*.25f;
			g2.fill( new Rectangle2D.Float(
				padding,
				padding,
				m.commonWidgetHeight-2*padding,
				m.commonWidgetHeight-2*padding
			));
		}
	}//end paintComponent

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

//========================================================================
	public void focusGained(FocusEvent fe) 
	{
		repaint();
		Component c=getParent();

		//makes sure widget is visible in scrollpane if got focus
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
