package ch.lowres.audio_rxtx.gui;

//tb/141016

import java.awt.event.KeyListener;

class HostTextFieldWithLimit extends TextFieldWithLimit implements KeyListener 
{
	public HostTextFieldWithLimit (String initialStr, int col, int maxLength) 
	{
		super(initialStr, col, maxLength);
	}

	@Override
	public boolean disallowedChar(char c)
	{
		//blacklist, true if not allowed
		return 	(!Character.isLetterOrDigit(c) && c!='.' && c!='-' && c!='_');
	}

}//end HostTextFieldWithLimit
