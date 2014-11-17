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
import java.awt.event.*;

import java.awt.geom.Line2D;

import javax.swing.JTextField;

/*
Declared in javax.swing.text.JTextComponent

    caret <javax.swing.text.Caret> the caret used to select/navigate
    caretColor <Color> the color used to render the caret
    caretPosition <int> the caret position
    disabledTextColor <Color> color used to render disabled text
    document <javax.swing.text.Document> the text document model
    editable <boolean> specifies if the text can be edited
    focusAccelerator <char> accelerator character used to grab focus
    highlighter <javax.swing.text.Highlighter> object responsible for background highlights
    margin <Insets> desired space between the border and text area
    selectedText <String> selectedText
    selectedTextColor <Color> color used to render selected text
    selectionColor <Color> color used to render selection background
    selectionEnd <int> ending location of the selection.
    selectionStart <int> starting location of the selection.
    text <String> the text of this component
*/

//========================================================================
class TextFieldWithLimit extends JTextField implements KeyListener, FocusListener
{
	private int maxLength;

//========================================================================
	public TextFieldWithLimit (String initialStr, int col, int maxLength) 
	{
		super(initialStr, col);
		this.maxLength=maxLength;

		//remove border from textfield (this is not possible with java.awt.TextField)
		setBorder(javax.swing.BorderFactory.createEmptyBorder());

		addKeyListener(this);
		addFocusListener(this);
	}

//========================================================================
	public TextFieldWithLimit (int col,int maxLength) 
	{
		this("", col, maxLength);
	}

//========================================================================
	public void setMaxLenght(int i)
	{
		maxLength=i;
	}

//========================================================================
	public int getMaxLenght(int i)
	{
		return maxLength;
	}

//========================================================================
        @Override
        public Dimension getPreferredSize()
        {
		return new Dimension(200,30);
        }


//========================================================================
	@Override
	public void paint(Graphics g) 
	{
		super.paint(g);

		Dimension size = getSize();	 

		if(hasFocus())
		{
			g.setColor(Colors.status_focused_outline);
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(30));
			g2.draw(new Line2D.Float(size.width,0,size.width,size.height));
		}
	}

//========================================================================
	@Override
	public void update(Graphics g) 
	{
		paint(g);
	}

//========================================================================
	public void keyTyped(KeyEvent e) 
	{
		char c = e.getKeyChar();
		int len = getText().length();

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

//========================================================================
	public void keyReleased(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {repaint();}

	public void focusLost(FocusEvent fe)
	{
		repaint();
		select(0,0);
	}

//========================================================================
	public void focusGained(FocusEvent fe) 
	{
		repaint();
		setCaretPosition(0);
		if (getText()!=null) 
		{
			select(0,getText().length());
			//setCaretPosition(getText().length());
		}
/*

--------start   scroller.getMinimum / 0

  f1
---viewport     here: navigate up: relocate      viewport~ scroller.getValue
| f2
|
| f3  
---	        here: navigate down: relocate
  f4

  f5

----------end   scroller.getMaximum

*/
		Component c=getParent();

		while(c!=null)
		{
			System.out.println(c);
			c=c.getParent();

			//look for java.awt.ScrollPane
			if(c instanceof ScrollPane)
			{
				Adjustable vadjust=((ScrollPane)c).getVAdjustable();

				int vp_height=(int)((ScrollPane)c).getViewportSize().getHeight();

				if(getBounds().y + getBounds().height > vadjust.getValue()+vp_height
					|| getBounds().y < vadjust.getValue())
				{
					vadjust.setValue(getBounds().y);
				}
				break;
			}
		}
	}//end focusGained

//========================================================================
	//default: all input allowed
	//override in subclass for another filter
	//blacklist, if not allowed -> return true
	//is c *outside* of the valid class? true. else false
	public boolean disallowedChar(char c) {return false;}

}//end TextFieldWithLimit
