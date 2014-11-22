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
	static Main g;

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

//========================================================================
	static void viewSendPanel()
	{
		g.mainGrid.removeAll();
		g.tabPanel.removeAll();

		g.tabPanel.add("Send", g.scrollerTabSend);
		g.tabPanel.add("Receive", g.scrollerTabReceive);

		g.tabPanel.setSelectedIndex(0);

		g.mainGrid.add(g.tabPanel);
		g.mainGrid.validate();

		g.mainframe.pack();
		g.mainframe.setSize(
			g.panelWidth+g.mainframe.getInsets().left+g.mainframe.getInsets().right,
			g.panelHeight+g.mainframe.getInsets().top+g.mainframe.getInsets().bottom
		);
	}

//========================================================================
	static void viewReceivePanel()
	{
		g.mainGrid.removeAll();
		g.tabPanel.removeAll();

		g.tabPanel.add("Send", g.scrollerTabSend);
		g.tabPanel.add("Receive", g.scrollerTabReceive);

		g.tabPanel.setSelectedIndex(1);

		g.mainGrid.add(g.tabPanel);
		g.mainGrid.validate();

		g.mainframe.pack();
		g.mainframe.setSize(
			g.panelWidth+g.mainframe.getInsets().left+g.mainframe.getInsets().right,
			g.panelHeight+g.mainframe.getInsets().top+g.mainframe.getInsets().bottom
		);
	}

//========================================================================
	static void viewBothPanels()
	{
		g.mainGrid.removeAll();
		g.tabPanel.removeAll();

		g.mainGrid.add(g.scrollerTabSend);
		g.mainGrid.add(g.scrollerTabReceive);
		g.mainGrid.validate();

		g.mainframe.pack();
		g.mainframe.setSize(
			2*g.panelWidth+g.mainframe.getInsets().left+g.mainframe.getInsets().right,
			g.panelHeight+g.mainframe.getInsets().top+g.mainframe.getInsets().bottom
		);
	}
}//end class FormHelper
