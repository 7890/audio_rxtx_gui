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
import ch.lowres.audio_rxtx.gui.helpers.*;


import java.util.Vector;
import java.awt.event.*;//KeyListener;

import java.awt.*;

import java.awt.geom.GeneralPath;


/**
* Extended {@link TextFieldWithLimit} (keyboard UP, DOWN, jump to nearest entry on 0-Z).
*/
//========================================================================
public class ListTextFieldWithLimit extends TextFieldWithLimit //implements KeyListener 
{
	private Vector items=new Vector();
	private int index=0;

	//when clicked with mouse or space pressed, show list as separate dialog
	private ListDialog ld=new ListDialog((Dialog)getParent(),l.tr("Choose"),true);

//========================================================================
	public ListTextFieldWithLimit (String initialStr, int col, int maxLength) 
	{
		super(initialStr, col, maxLength);
		setEditable(false);
	}

//========================================================================
	public ListTextFieldWithLimit (String[] itemArr, int col, int maxLength) 
	{
		super( (itemArr.length>0 ? itemArr[0] : ""), col, maxLength);
		addItems(itemArr);
		setEditable(false);
		ld.init(items);
	}

//========================================================================
	public void setTitle(String s)
	{
		ld.setTitle(s);
	}

//========================================================================
	//needed after font change
	public void initLd()
	{
		ld.initCellDimensions();
	}

//========================================================================
	public void addItem(String item)
	{
		items.add(item);
	}

//========================================================================
	//callers should only add validated items
	public void addItems(String[] stringItems)
	{
		for(int i=0;i<stringItems.length;i++)
		{
			addItem(stringItems[i]);
		}
	}

//========================================================================
	public int getSelectedIndex()
	{
		return index;
	}

//========================================================================
	public void setSelectedIndex(int pos)
	{
		index=Math.min(pos,items.size()-1);
		index=Math.max(0,index);
		if(!items.isEmpty())
		{
			setText((String)items.elementAt(index));
			select(0,getText().length());
		}
	}

//========================================================================
	public int getNumericStep(KeyEvent e)
	{
		boolean isCtrlOrCmdDown=e.isControlDown();
		if(m.os.isMac())
		{
			isCtrlOrCmdDown=e.isMetaDown();
		}

		if(isCtrlOrCmdDown && e.isShiftDown())
		{
			return 1000;
		}
		else if(!isCtrlOrCmdDown && e.isShiftDown())
		{
			return 100;
		}
		else if(isCtrlOrCmdDown && !e.isShiftDown())
		{
			return 10;
		}
		else
		{
			return 1;
		}
	}

//========================================================================
	@Override
	public void keyReleased(KeyEvent e)
	{
		boolean isCtrlOrCmdDown=e.isControlDown();
		if(m.os.isMac())
		{
			isCtrlOrCmdDown=e.isMetaDown();
		}

		if(e.getKeyCode()==KeyEvent.VK_SPACE
			|| (e.getKeyCode()==KeyEvent.VK_RIGHT && !isCtrlOrCmdDown)
		)
		{

			Point p=getLocationOnScreen();
			index=ld.showDialog(index,(int)p.getX()+50,(int)p.getY());

			if(!items.isEmpty())
			{
				index=Math.min(index,items.size()-1);
				index=Math.max(index,0);
				setText((String)items.elementAt(index));
			}
		}
	}

//========================================================================
	@Override
	public void keyPressed(KeyEvent e)
	{
		boolean isCtrlOrCmdDown=e.isControlDown();
		if(m.os.isMac())
		{
			isCtrlOrCmdDown=e.isMetaDown();
		}

		if(e.getKeyCode()==KeyEvent.VK_UP)
		{
			index-=getNumericStep(e);
		}
		else if(e.getKeyCode()==KeyEvent.VK_DOWN)
		{
			index+=getNumericStep(e);
		}
		else if(e.getKeyCode()==KeyEvent.VK_LEFT && isCtrlOrCmdDown)
		{
			index=0;
		}
		else if(e.getKeyCode()==KeyEvent.VK_RIGHT && isCtrlOrCmdDown)
		{
			index=Math.max(0,items.size()-1);
		}

		else
		{
			return;
		}

		if(!items.isEmpty())
		{
			index=Math.min(index,items.size()-1);
			index=Math.max(index,0);
			setText((String)items.elementAt(index));
		}

		select(0,getText().length());
		e.consume();
	}//end keyPressed

//========================================================================
	public int firstItemEqual(String s)
	{
		for(int i=0;i<items.size();i++)
		{
			String entry=(String)items.elementAt(i);
			if(entry.toLowerCase().equals(s.toLowerCase()))
			{
				return i;
			}
		}
///////////////
		return 0;
	}

//========================================================================
	public int nextItemStartingWith(char c)
	{
		if(index<0)
		{
			index=0;
		}
		if(index>=items.size())
		{
			return items.size();
		}
		//search from current+1 pos (cycling through entries with same start character)
		for(int i=index+1;i<items.size();i++)
		{
			String entry=(String)items.elementAt(i);
			if(entry.toLowerCase().startsWith((""+c).toLowerCase()))
			{
				return i;
			}
		}
		//search from start
		for(int i=0;i<items.size();i++)
		{
			String entry=(String)items.elementAt(i);
			if(entry.toLowerCase().startsWith((""+c).toLowerCase()))
			{
				return i;
			}
		}
		return index;
	}

//========================================================================
	@Override
	public boolean disallowedChar(char c)
	{
		//find next entry in list starting with c (lower- or uppercase) && set text
		index=nextItemStartingWith(c);
		setSelectedIndex(index);

		//don't use chars / disallow all
		//blacklist, true if not allowed
		return true;
	}

//========================================================================
	@Override
	public void validate_() {}

//========================================================================
	@Override
	public void focusLost(FocusEvent fe)
	{
		validate_();
		super.focusLost(fe);
	}

//========================================================================
	@Override
	public void focusGained(FocusEvent fe)
	{
		super.focusGained(fe);
	}

//========================================================================
	@Override
	public void mousePressed(MouseEvent e) {}

//========================================================================
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if(isEnabled())
		{
			int ldGap=5;

			Point posScreen=e.getLocationOnScreen();
			Point posComponent=e.getPoint();

			//show list dialog, align to top of current component
			index=ld.showDialog(index
				,(int)(posScreen.getX()+ldGap)
				,(int)(posScreen.getY()-posComponent.getY())
			);

			if(!items.isEmpty())
			{
				index=Math.min(index,items.size()-1);
				index=Math.max(index,0);
				setText((String)items.elementAt(index));
			}
		}
	}//end mousePressed

//========================================================================
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if(this.hasFocus())
		{

			Dimension size = this.getSize();
			int visibleWidth=(int)getVisibleRect().getWidth();

			int rightOffsetBase=20;

			if(visibleWidth<60)
			{
				rightOffsetBase=5;
			}

			if(items.size()>1)
			{
				g.setColor(Colors.status_focused_outline);
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(Colors.black);
				g2.setStroke(new BasicStroke(5));
				if(index > 0)
				{
/*
      p1
    /   \
  p2-----p3
*/
					g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
					Point p1 = new Point(visibleWidth-rightOffsetBase-5,0);
					Point p2 = new Point(visibleWidth-rightOffsetBase,5);
					Point p3 = new Point(visibleWidth-rightOffsetBase-10,5);

					GeneralPath gp=new GeneralPath();
					gp.moveTo((float)p1.x,(float)p1.y);
					gp.lineTo(p2.x,p2.y);
					gp.lineTo(p3.x,p3.y);
					gp.closePath();

					g2.fill(gp);

				}
				if(index < items.size()-1)
				{
/*
  p1-----p2
    \   /
      p3
*/
					g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
					Point p1 = new Point(visibleWidth-rightOffsetBase,size.height-5);
					Point p2 = new Point(visibleWidth-rightOffsetBase-10,size.height-5);
					Point p3 = new Point(visibleWidth-rightOffsetBase-5,size.height);

					GeneralPath gp=new GeneralPath();
					gp.moveTo((float)p1.x,(float)p1.y);
					gp.lineTo(p2.x,p2.y);
					gp.lineTo(p3.x,p3.y);
					gp.closePath();

					g2.fill(gp);
				}
			}//end if more than 1 items
		}//end if hasFocus
	}//end paintComponent
}//end HistorifiedHostTextFieldWithLimit
