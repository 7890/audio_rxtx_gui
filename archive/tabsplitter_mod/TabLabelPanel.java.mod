
package com.magelang.tabsplitter;

import com.magelang.BorderPanel;

import java.awt.Component;
import java.awt.Button;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/** <b><i>NOTE: This class is used by TabSplitter and is not intended to be used 
 *  directly.  It has several methods that are specifically related to TabSplitter.</i></b>
 *
 *  TabLabelPanel is used to provide a nice title button for
 *  a component that used to be its own tab in a TabSplitter
 *  but is now part of a panel with a SplitterLayout.
 *
 *  <p>Use this code at your own risk!  MageLang Institute is not
 *  responsible for any damage caused directly or indirectly through
 *  use of this code.
 *  <p><p>
 *  <b>SOFTWARE RIGHTS</b>
 *  <p>
 *  TabSplitter, version 2.0, Scott Stanchfield, MageLang Institute
 *  <p>
 *  We reserve no legal rights to this code--it is fully in the
 *  public domain. An individual or company may do whatever
 *  they wish with source code distributed with it, including
 *  including the incorporation of it into commerical software.
 *
 *  <p>However, this code <i>cannot</i> be sold as a standalone product.
 *  <p>
 *  We encourage users to develop software with this code. However,
 *  we do ask that credit is given to us for developing it
 *  By "credit", we mean that if you use these components or
 *  incorporate any source code into one of your programs
 *  (commercial product, research project, or otherwise) that
 *  you acknowledge this fact somewhere in the documentation,
 *  research report, etc... If you like these components and have
 *  developed a nice tool with the output, please mention that
 *  you developed it using these components. In addition, we ask that
 *  the headers remain intact in our source code. As long as these
 *  guidelines are kept, we expect to continue enhancing this
 *  system and expect to make other tools available as they are
 *  completed.
 *  <p>
 *  The MageLang Support Classes Gang:
 *  @version TabSplitter 2.0, MageLang Insitute, Jan 18, 1998
 *  @author <a href="http:www.scruz.net/~thetick">Scott Stanchfield</a>, <a href=http://www.MageLang.com>MageLang Institute</a>
 */
class TabLabelPanel extends Panel implements ActionListener {
	private Component comp;      // component to display
	private Button nameButton;   // the "separate me" button 
	private int position;        // the original position
	private String explicitText; // the explicit tab text (if any)

	/** Constructor for TabLabelPanel
	 */
	public TabLabelPanel(String text, Component c, int position, String explicitText) {
		// keep track of the component
		comp = c;
		this.explicitText = explicitText;
		this.position = position;
		
		// Create the title button
		nameButton = new Button(text);
	
		// Listen for the button to be pressed
		nameButton.addActionListener(this);
	
		// Set BorderLayout, lightGray bg
		setLayout(new java.awt.BorderLayout());

		// Create a nice looking border panel and contain the button in it	
		BorderPanel b = new BorderPanel();
		//b.add("Center", nameButton);
	
		// Add the title button and the component to the panel
		add("North", b);
		add("Center", c);
	}
	
	public void separate()
	{
		((SplitterPanel)getParent()).separateTabs(this);
	}

	/** When the button is pressed, separate the tab from the SplitterPanel */
	public void actionPerformed(ActionEvent e) {
		separate();
	}
	
	public void decrPosition(int pos) {
		if (pos < position) position--;
	}	
	/** return the component that we're housing */
	public Component getComponent() {
		return comp;
	}
	
	public String getExplicitText() {
		return explicitText;
	}	
	/** return the original position of the component */
	public int getPosition() {
		return position;
	}	
	
	/** return the original tab name of the component */
	public String getTabName() {
		return nameButton.getLabel();
	}
	
	/** remove the component from the panel */
	public Component removeComponent() {
		remove(comp);
		return comp;
	}	
	
}