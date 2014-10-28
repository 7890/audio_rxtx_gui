package ch.lowres.audio_rxtx.gui;

//tb/141016

//http://www.rgagnon.com/javadetails/java-0227.html

import java.awt.TextField;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

/*
TextField methods
getSelectedText, getSelectionEnd, getSelectionStart, getText
select, selectAll, setBackground, setCaretPosition, setEditable, setSelectionEnd, setSelectionStart
*/

class TextFieldWithLimit extends TextField implements KeyListener, FocusListener
{
	private int maxLength;

	public TextFieldWithLimit (String initialStr, int col, int maxLength) 
	{
		super(initialStr, col);
		this.maxLength=maxLength;
		addKeyListener(this);
		addFocusListener(this);
	}

	public TextFieldWithLimit (int col,int maxLength) 
	{
		this("", col, maxLength);
	}

	public void setMaxLenght(int i)
	{
		maxLength=i;
	}

	public int getMaxLenght(int i)
	{
		return maxLength;
	}

	public void keyTyped(KeyEvent e) 
	{
		char c = e.getKeyChar();
		int len = getText().length();
/*
		if(c==KeyEvent.VK_ENTER)
		{
			System.out.println("enter typed");
		}
*/
		//use navig/edit events
		if((c==KeyEvent.VK_BACK_SPACE)||
			(c==KeyEvent.VK_DELETE) ||
			(c==KeyEvent.VK_ENTER)||
			(c==KeyEvent.VK_TAB)||
			e.isActionKey())
		{
			return;
		}

		//if more chars/digits allowed
		if(len < maxLength) 
		{
			if(disallowedChar(c))
			{
				//absorb (not add to textfield)
				e.consume();
			}
			else
			{
				//use
				return;
			}
		}//end < maxlength
		//>= maxlength
		else if(getSelectionStart()==0 && getSelectionEnd()==getText().length())
		{
			//prevent that key gets ignored when ==maxlength && all selected
			return;
		}
		else
		{
			//absorb
		 	e.consume(); 
		}
	 }//end keytyped

	public void keyReleased(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {}
	public void focusLost(FocusEvent fe) {}

	public void focusGained(FocusEvent fe) 
	{
		setCaretPosition(0);
		if (getText()!=null) 
		{
			//selectAll();			
			setCaretPosition(getText().length());
		}
	}

	//default: all input allowed
	//override in subclass for another filter
	//blacklist, if not allowed -> return true
	//is c *outside* of the valid class? true. else false
	public boolean disallowedChar(char c) {return false;}

}//end TextFieldWithLimit
