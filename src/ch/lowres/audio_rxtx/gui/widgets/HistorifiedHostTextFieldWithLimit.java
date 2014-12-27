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


import java.util.Vector;
import java.awt.event.*;//KeyListener;

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
}//end HistorifiedHostTextFieldWithLimit
