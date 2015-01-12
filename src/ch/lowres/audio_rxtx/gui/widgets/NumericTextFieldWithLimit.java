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

import java.awt.event.*;

/**
* Extended {@link TextFieldWithLimit} allowing only digits.
* The minimum / maximum values can be configured.
* Arrow keys up / down allow increment / decrement of natural number.
* Validation takes place on focus lost or when incrementing / decrementing.
* Text is selected, caret position at end when focused.
*/
//========================================================================
public class NumericTextFieldWithLimit extends TextFieldWithLimit
{
	Integer minInclusive=null;
	Integer maxInclusive=null;

//========================================================================
	public NumericTextFieldWithLimit (String initialStr, int col, int maxLength) 
	{
		super(initialStr, col, maxLength);
	}

//========================================================================
	public void setMinInclusive(int i)
	{
		minInclusive=i;
	}

//========================================================================
	public void setMaxInclusive(int i)
	{
		maxInclusive=i;
	}

//========================================================================
	public void setMinInclusive()
	{
		setText(""+minInclusive);
	}

//========================================================================
	public void setMaxInclusive()
	{
		setText(""+maxInclusive);
	}

//========================================================================
	@Override
	public void validate_()
	{
		try
		{
			int number=Integer.parseInt(getText());

			if(minInclusive!=null && number < minInclusive)
			{
				setMinInclusive();
			}
			else if(maxInclusive!=null && number > maxInclusive)
			{
				setMaxInclusive();
			}

		}catch(Exception ign)
		{
			//should have setDefault() too
			setMinInclusive();
		}
	}

//========================================================================
	@Override
	public boolean disallowedChar(char c)
	{
		//blacklist, true if not allowed
		return (!Character.isDigit(c));
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
	public void keyPressed(KeyEvent e)
	{
		//repaint();
/*
http://stackoverflow.com/questions/11380406/how-to-use-vk-up-or-vk-down-to-move-to-the-previous-or-next-textfield
The code you quoted won't work because you should use getKeyCode instead of getKeyChar. 
The former corresponds to those VK_ constants, whereas the latter will result in the character for a 
"normal" (i.e. printing) key, and only during the KEY_TYPED event. For non-printing keys, the KEY_TYPED 
event will never be generated, and during all other events, the key character will be CHAR_UNDEFINED instead.
*/
		if(e.getKeyCode()==KeyEvent.VK_UP)
		{
			try
			{
				int number=Integer.parseInt(getText());
				number+=getNumericStep(e);
				setText(""+number);
				validate_();
			}catch(Exception ign){}
			select(0,getText().length());
			e.consume();
		}
		else if(e.getKeyCode()==KeyEvent.VK_DOWN)
		{
			try
			{
				int number=Integer.parseInt(getText());
				number-=getNumericStep(e);
				setText(""+number);
				validate_();
			}catch(Exception ign){}
			select(0,getText().length());
			e.consume();
		}
	}//end keyPressed

//========================================================================
	@Override
	public void focusLost(FocusEvent fe)
	{
		validate_();
		super.focusLost(fe);
	}
}//end HostTextFieldWithLimit
