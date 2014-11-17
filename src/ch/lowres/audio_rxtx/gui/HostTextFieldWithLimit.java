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
class HostTextFieldWithLimit extends TextFieldWithLimit implements KeyListener 
{
//========================================================================
	public HostTextFieldWithLimit (String initialStr, int col, int maxLength) 
	{
		super(initialStr, col, maxLength);
	}

//========================================================================
	@Override
	public boolean disallowedChar(char c)
	{
		//blacklist, true if not allowed
		return (!Character.isLetterOrDigit(c) && c!='.' && c!='-' && c!='_');
	}
}//end HostTextFieldWithLimit
