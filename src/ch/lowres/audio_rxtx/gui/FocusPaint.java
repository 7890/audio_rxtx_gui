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

import java.awt.geom.Line2D;
import java.awt.geom.GeneralPath;

//========================================================================
public class FocusPaint
{

//========================================================================
	public FocusPaint()
	{
	}

//========================================================================
	void paint(Graphics g, Component c) 
	{
		Dimension size = c.getSize();

		if(c.hasFocus())
		{
			g.setColor(Colors.status_focused_outline);
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(30));
			g2.draw(new Line2D.Float(size.width,0,size.width,size.height));

/*
       p2

 p1

       p3
*/


			//http://docstore.mik.ua/orelly/java-ent/jfc/ch04_05.htm
			g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			Point p1 = new Point(size.width-20,size.height/2);
			Point p2 = new Point(size.width-12,0);
			Point p3 = new Point(size.width-12,size.height);

			GeneralPath gp=new GeneralPath();
			gp.moveTo((float)p1.x,(float)p1.y);
			gp.lineTo(p2.x,p2.y);
			gp.lineTo(p3.x,p3.y);
			gp.closePath();

			g2.fill(gp);
		}
	}//end paoint
}//end class FocusPaint
