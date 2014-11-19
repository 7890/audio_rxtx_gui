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

class FormHelper
{
//========================================================================
	static void validate(Container container)
	{
		Component c[]=container.getComponents();
		for(int i=0;i<c.length;i++)
		{
		        if(c[i] instanceof TextFieldWithLimit)
		        {
				((TextFieldWithLimit)c[i]).validate_();
		        }
		}
	}//end validate

//========================================================================
	static void defaultCardAction(Component comp)
	{
		Component c=comp.getParent();

		while(c!=null)
		{
			if(c instanceof Card)
			{
				((Card)c).defaultAction();
			}
			c=c.getParent();
		}
	}
}//end class FormHelper
