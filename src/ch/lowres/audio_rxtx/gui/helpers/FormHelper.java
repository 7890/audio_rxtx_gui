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

package ch.lowres.audio_rxtx.gui.helpers;
import ch.lowres.audio_rxtx.gui.*;
import ch.lowres.audio_rxtx.gui.widgets.*;

import java.awt.*;

/**
* Helper to validate input widgets in a Container, trigger default action.
* Helper to switch or combine send and receive panels.
*/
//========================================================================
public class FormHelper
{
	private static Main m;

//========================================================================
	public static void validate(Container container)
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
	public static void defaultCardAction(Component comp)
	{
		Component c=comp;
		while(c!=null)
		{
			if(c instanceof Card)
			{
				((Card)c).defaultAction();
			}
			c=c.getParent();
		}
	}

//========================================================================
	public static void viewSendPanel()
	{
		m.mainGrid.removeAll();
		m.tabPanel.removeAll();

		m.tabPanel.add(m.tr("Send"), m.scrollerTabSend);
		m.tabPanel.add(m.tr("Receive"), m.scrollerTabReceive);

		m.mainGrid.add(m.tabPanel);
		m.mainGrid.validate();

		m.mainframe.pack();

		m.tabPanel.setSelectedIndex(0);
		m.tabPanel.requestFocus();
	}

//========================================================================
	public static void viewReceivePanel()
	{
		m.mainGrid.removeAll();
		m.tabPanel.removeAll();

		m.tabPanel.add(m.tr("Send"), m.scrollerTabSend);
		m.tabPanel.add(m.tr("Receive"), m.scrollerTabReceive);

		m.mainGrid.add(m.tabPanel);
		m.mainGrid.validate();

		m.mainframe.pack();

		m.tabPanel.setSelectedIndex(1);
		m.tabPanel.requestFocus();
	}

//========================================================================
	public static void viewBothPanels()
	{
		m.mainGrid.removeAll();
		m.tabPanel.removeAll();

		m.mainGrid.add(m.scrollerTabSend);
		m.mainGrid.add(m.scrollerTabReceive);
		m.mainGrid.validate();

		m.mainframe.pack();
//		m.setWindowCentered(m.mainframe);

		m.frontSend.focusFirstInputWidget();
	}
}//end class FormHelper
