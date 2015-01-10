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

package ch.lowres.audio_rxtx.gui.helpers;
import ch.lowres.audio_rxtx.gui.*;
import ch.lowres.audio_rxtx.gui.widgets.*;

import java.awt.*;
import javax.swing.*;

import java.awt.geom.Line2D;
import java.awt.geom.GeneralPath;

/**
* Helper to overlay indication on focused input widgets, paint gradients.
*/
//========================================================================
public class FocusPaint
{
	private static Main m;

//========================================================================
	public static void paint(Graphics g, JComponent c) 
	{
		Dimension size = c.getSize();

		Graphics2D g2 = (Graphics2D) g;
/*
		//indicate disabled
		if((c instanceof TextFieldWithLimit) && !c.isEnabled() )
		{
		}
*/
		if(c.hasFocus())
		{
			//m.p("getvisible rect "+((JComponent)c).getVisibleRect().toString());
			int visibleWidth=(int)((JComponent)c).getVisibleRect().getWidth();
			int height=(int)size.height;

			if(c instanceof JTabbedPane)
			{
				int index=((JTabbedPane)c).getSelectedIndex();
				Rectangle r=((JTabbedPane)c).getBoundsAt(index);
				//m.p("bounds: "+ r.toString());

				//derived from UIManager.put("TabbedPane.selectedTabPadInsets",new Insets(5,0,0,0));
				height=r.height+5;
			}

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

			if(!(c instanceof JTabbedPane))
			{
				//~underline
				g2.setColor(Colors.status_focused_outline);
				g2.setStroke(new BasicStroke(4));
				g2.draw(new Line2D.Float(0,height-1,visibleWidth,height-1));
			}

			//draw minimal if small visible area
			if(visibleWidth<60)
			{
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				g2.setColor(Colors.white);
				g2.setStroke(new BasicStroke(12));
				g2.draw(new Line2D.Float(-3+visibleWidth,0,-3+visibleWidth,height));

				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				g2.setColor(Colors.black);
				g2.setStroke(new BasicStroke(12));
				g2.draw(new Line2D.Float(visibleWidth,0,visibleWidth,height));

				return;	
			}

			//cover text near focus indication
			if(visibleWidth<c.getWidth())
			{
				g2.setColor(c.getBackground());

				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));

				g2.setStroke(new BasicStroke(30));//!
				g2.draw(new Line2D.Float(-15+visibleWidth,0,-15+visibleWidth,height));
				g2.draw(new Line2D.Float(-10+visibleWidth,0,-10+visibleWidth,height));
				g2.draw(new Line2D.Float(-6+visibleWidth,0,-6+visibleWidth,height));

				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
				g2.draw(new Line2D.Float(-3+visibleWidth,0,-3+visibleWidth,height));

			}

			//reset to opaque
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

			//right side block
			g2.setColor(Colors.black);
			g2.setStroke(new BasicStroke(30));//!
			g2.draw(new Line2D.Float(visibleWidth,0,visibleWidth,height));

//filled triangle
/*
       p2

 p1

       p3
*/

			//http://docstore.mik.ua/orelly/java-ent/jfc/ch04_05.htm
			g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			Point p1 = new Point(visibleWidth-20,height/2);
			Point p2 = new Point(visibleWidth-12,0);
			Point p3 = new Point(visibleWidth-12,height);

			GeneralPath gp=new GeneralPath();
			gp.moveTo((float)p1.x,(float)p1.y);
			gp.lineTo(p2.x,p2.y);
			gp.lineTo(p3.x,p3.y);
			gp.closePath();

			g2.fill(gp);
		} //end if has focus
	}//end paint

//========================================================================
	//http://stackoverflow.com/questions/14364291/jpanel-gradient-background
	public static void gradient(Graphics g, Component c)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GradientPaint gp = new GradientPaint(0, 0,
			c.getBackground().brighter().brighter(), 0, c.getHeight()/3,
			c.getBackground());
		g2.setPaint(gp);
		g2.fillRect(0, 0, c.getWidth(), c.getHeight()/3);

		gp = new GradientPaint(0, c.getHeight()/3,
			c.getBackground(), 0, c.getHeight(),
			c.getBackground());
		g2.setPaint(gp);
		g2.fillRect(0, c.getHeight()/3, c.getWidth(), c.getHeight());
	}
}//end class FocusPaint
