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
import javax.swing.*;

/**
* Helper to validate input widgets in a Container, trigger default action.
* Helper to switch or combine send and receive panels.
*/
//========================================================================
public class FormHelper
{
	private static Main m;
	private static GUI g;
	private static Languages l;

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
		if(g.tabPanel.getTabCount()==2)
		{
			g.tabPanel.setSelectedIndex(0);
			g.frontSend.focusFirstInputWidget();
			return;
		}

		g.mainGrid.removeAll();
		g.tabPanel.removeAll();

		g.tabPanel.add(l.tr("Send"), g.scrollerTabSend);
		g.tabPanel.add(l.tr("Receive"), g.scrollerTabReceive);

		g.mainGrid.add(g.tabPanel);
		g.mainGrid.validate();

		SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				g.mainframe.pack();
				g.tabPanel.setSelectedIndex(0);
				g.frontSend.focusFirstInputWidget();
			}
		});
	}

//========================================================================
	public static void viewReceivePanel()
	{
		if(g.tabPanel.getTabCount()==2)
		{
			g.tabPanel.setSelectedIndex(1);
			g.frontReceive.focusFirstInputWidget();
			return;
		}

		g.mainGrid.removeAll();
		g.tabPanel.removeAll();

		g.tabPanel.add(l.tr("Send"), g.scrollerTabSend);
		g.tabPanel.add(l.tr("Receive"), g.scrollerTabReceive);

		g.mainGrid.add(g.tabPanel);
		g.mainGrid.validate();

		SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				g.mainframe.pack();
				g.tabPanel.setSelectedIndex(1);
				g.frontReceive.focusFirstInputWidget();
			}
		});
	}

//========================================================================
	public static void viewBothPanels()
	{
		g.mainGrid.removeAll();
		g.tabPanel.removeAll();

		g.mainGrid.add(g.scrollerTabSend);
		g.mainGrid.add(g.scrollerTabReceive);
		g.mainGrid.validate();

		g.mainframe.pack();
//		g.setWindowCentered(g.mainframe);

		g.frontSend.focusFirstInputWidget();
	}
}//end class FormHelper
