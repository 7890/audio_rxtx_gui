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


import java.util.Vector;
import java.awt.event.*;//KeyListener;

import java.awt.*;

import java.awt.geom.Line2D;
import java.awt.geom.GeneralPath;


/**
* Extended {@link HostTextFieldWithLimit} with consolidated history (keyboard UP, DOWN).
*/
//========================================================================
public class HistorifiedHostTextFieldWithLimit extends HostTextFieldWithLimit //implements KeyListener 
{
	private Vector history;
	private int position=-1;

//========================================================================
	public HistorifiedHostTextFieldWithLimit (String initialStr, int col, int maxLength) 
	{
		super(initialStr, col, maxLength);
		history=new Vector();
	}

//========================================================================
	//callers should only add validated items
	public void addHistoricItem(String item)
	{
		//remove all identical older items
		while(history.removeElement(item)){}

		//add as last
		history.add(item);

		position=history.size()-1;
	}

//========================================================================
	//it can be out of scope, but triggering UP will put to range again (after invalid input)
	public int setAfterLast()
	{
		position=history.size();
		return position;
	}

//========================================================================
	public void setTextLast()
	{
		if(!history.isEmpty())
		{
			setText((String)history.lastElement());
		}
		position=history.size()-1;
	}

//========================================================================
	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode()==KeyEvent.VK_UP)
		{
			position--;
		}
		else if(e.getKeyCode()==KeyEvent.VK_DOWN)
		{
			position++;
		}
		else
		{
			return;
		}

		if(!history.isEmpty())
		{

			position=Math.min(position,history.size()-1);
			position=Math.max(position,0);
			setText((String)history.elementAt(position));
		}

		select(0,getText().length());
		e.consume();
	}//end keyPressed

//========================================================================
	@Override
	public void validate_()
	{
		if(getText().equals(""))
		{
			setTextLast();
		}
	}

//========================================================================
	@Override
	public void focusLost(FocusEvent fe)
	{
		validate_();
		super.focusLost(fe);
	}

//========================================================================
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Dimension size = this.getSize();

		if(this.hasFocus())
		{
			if(history.size()>1)
			{
				g.setColor(Colors.status_focused_outline);
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(5));
				if(position > 0)
				{
/*
      p1

  p2      p3
*/
					g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
					Point p1 = new Point(size.width-25,0);
					Point p2 = new Point(size.width-30,5);
					Point p3 = new Point(size.width-20,5);

					GeneralPath gp=new GeneralPath();
					gp.moveTo((float)p1.x,(float)p1.y);
					gp.lineTo(p2.x,p2.y);
					gp.lineTo(p3.x,p3.y);
					gp.closePath();

					g2.fill(gp);

				}
				if(position < history.size()-1)
				{
/*
  p1      p2

      p3
*/
					g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
					Point p1 = new Point(size.width-30,size.height-5);
					Point p2 = new Point(size.width-20,size.height-5);
					Point p3 = new Point(size.width-25,size.height);

					GeneralPath gp=new GeneralPath();
					gp.moveTo((float)p1.x,(float)p1.y);
					gp.lineTo(p2.x,p2.y);
					gp.lineTo(p3.x,p3.y);
					gp.closePath();

					g2.fill(gp);
				}
			}
		}
	}//end paintComponent
}//end HistorifiedHostTextFieldWithLimit
