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

import java.awt.event.KeyListener;

//========================================================================
class NumericTextFieldWithLimit extends TextFieldWithLimit implements KeyListener 
{
//========================================================================
	public NumericTextFieldWithLimit (String initialStr, int col, int maxLength) 
	{
		super(initialStr, col, maxLength);
	}

//========================================================================
	@Override
	public boolean disallowedChar(char c)
	{
		//blacklist, true if not allowed
		return (!Character.isDigit(c));
	}

//========================================================================
	@Override
	public void keyPressed(KeyEvent e)
	{
		repaint();
/*
http://stackoverflow.com/questions/11380406/how-to-use-vk-up-or-vk-down-to-move-to-the-previous-or-next-textfield
The code you quoted won't work because you should use getKeyCode instead of getKeyChar. 
The former corresponds to those VK_ constants, whereas the latter will result in the character for a 
“normal” (i.e. printing) key, and only during the KEY_TYPED event. For non-printing keys, the KEY_TYPED 
event will never be generated, and during all other events, the key character will be CHAR_UNDEFINED instead.
*/
		if(e.getKeyCode()==KeyEvent.VK_UP)
		{
			try
			{
				int number=Integer.parseInt(getText());
				number++;
				setText(""+number);

			}catch(Exception ign){}
			e.consume();
		}
		else if(e.getKeyCode()==KeyEvent.VK_DOWN)
		{
			try
			{
				int number=Integer.parseInt(getText());
				number--;
				setText(""+number);
			}catch(Exception ign){}
			e.consume();
		}
	}//end keyPressed
}//end HostTextFieldWithLimit
