package ch.lowres.audio_rxtx.gui;

//tb/141016

//import java.awt.*;
import java.awt.event.KeyListener;

class NumericTextFieldWithLimit extends TextFieldWithLimit implements KeyListener 
{
	public NumericTextFieldWithLimit (String initialStr, int col, int maxLength) 
	{
		super(initialStr, col, maxLength);
	}

	@Override
	public boolean disallowedChar(char c)
	{
		//blacklist, true if not allowed
		return (!Character.isDigit(c));
	}

}//end HostTextFieldWithLimit
