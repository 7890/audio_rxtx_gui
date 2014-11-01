
package com.magelang.tabsplitter;

/** <b><i>NOTE: This class is used by TabSplitter and is not intended to be used 
 *  directly.  It has several methods that are specifically related to TabSplitter.</i></b>
 *
 *  <p>SplitterPanel is a class that is used to provide a panel with a splitter bar
 *  between each component contained within it.  TabSplitter creates one of these
 *  panels anytime a user merges two tabs together.
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
import com.magelang.splitter.ReOrientingSplitterBar;
import com.magelang.splitter.SplitterBar;
import com.magelang.splitter.SplitterLayout;

import java.awt.Component;
import java.awt.Panel;
import java.util.Hashtable;

public class SplitterPanel extends Panel implements TabNamedComponent {
	private String tabName; // the name to display on the tab in TabSplitter

	/** Constructor */
	public SplitterPanel() {
		super();
		setLayout(new SplitterLayout(SplitterLayout.HORIZONTAL));
	}
	
	/** Merge another SplitterPanel with this one 
	 */
	void add(SplitterPanel p) {
		Component cs[] = p.getComponents();
		int count = p.getComponentCount();
		for (int i=0;i<count;i++)
			if (cs[i] instanceof SplitterBar)
				p.remove(cs[i]);
			else
				add(((TabLabelPanel)cs[i]).getTabName(), cs[i], 
				    ((TabLabelPanel)cs[i]).getPosition(),
				    ((TabLabelPanel)cs[i]).getExplicitText());
	}
	
	/** Add a component to this SplitterPanel */
	Component add(String name, Component comp, int position, String explicitText) {
		comp.setVisible(true);
		if (!(comp instanceof TabLabelPanel))
			comp = new TabLabelPanel(name, comp, position, explicitText);

		// find the proper position to add the new component
		int count = getComponentCount();
				
		if (count == 0)
			super.add("1", comp);

		else {		
			Component comps[] = getComponents();
			int i = 0;
			while(i < count && ((comps[i] instanceof SplitterBar) || ((TabLabelPanel)comps[i]).getPosition() < position))
				i++;
			
			// possible positions:
			//   i == 0:        insert as first component followed by SplitterBar
			//   i == count-1 : inset as last component preceded by SplitterBar
			//   other:         insert before i followed by SplitterBar
			if (i == count) {
				super.add(new ReOrientingSplitterBar());
				super.add("1", comp);
			}
			
			else {
				super.add(new ReOrientingSplitterBar(), i);
				super.add(comp, "1", i);
			}
		}	
		doNames();
		return comp;
	}	
	
	/** Tell if this SplitterPanel contains the requested component */
	boolean contains(Component c) {
		Component comp[] = getComponents();
		int count = getComponentCount();
		for(int i = 0; i<count; i++)
			if (comp[i] instanceof TabLabelPanel && 
				 ((TabLabelPanel)comp[i]).getComponent() == c) return true;
		return false;
	}	
	
	/** Tell if this SplitterPanel contains the requested component */
	boolean contains(String name) {
		Component comp[] = getComponents();
		int count = getComponentCount();
		for(int i = 0; i<count; i++)
			if (comp[i] instanceof TabLabelPanel &&
			   ((TabLabelPanel)comp[i]).getTabName().equals(name))
				return true;
		return false;
	}	
	public void decrPositions(int pos) {
		int count = getComponentCount();
		Component comp[] = getComponents();
		for(int i=0; i<count; i++)
			if (comp[i] instanceof TabLabelPanel)
				((TabLabelPanel)comp[i]).decrPosition(pos);
	}	
	/** Re-determine the bean name (getName()) and tab text */
	protected void doNames() {
		String newTabName  = null;
		String newBeanName = null;
		
		// re-build the tab name and bean name
		Component c[] = getComponents();
		int count = getComponentCount();
		for(int i = 0; i < count; i++)
			if (c[i] instanceof TabLabelPanel)
				if (newTabName == null)
					newTabName  = newBeanName = ((TabLabelPanel)c[i]).getTabName();
				else {
					newTabName  += " / " + ((TabLabelPanel)c[i]).getTabName();
					newBeanName += "/" + ((TabLabelPanel)c[i]).getTabName();
				}	
		tabName = newTabName;
		setName(newBeanName);
	}
	
	/** tell what our current orientation is */
	int getOrientation() {
		return ((SplitterLayout)getLayout()).getOrientation();
	}	
	
	/** return the name to be used on the tab */
	public String getTabName() {
		return tabName;
	}
	
	/** return an array of components that reside in this SplitterPanel */
	Object getVisibleComponent() {
		int count = getComponentCount();
		Component comp[] = getComponents();
		Object o[] = new Object[(count+1)/2];
		for(int i = 0,j=0; i < count; i++)
			if (comp[i] instanceof TabLabelPanel)
				o[j++] = ((TabLabelPanel)comp[i]).getComponent();
		return o;
	}	
	
	/** return an array of the position numbers of the components inside us */
	int[] getVisibleComponentNum() {
		int count = getComponentCount();
		Component comp[] = getComponents();
		int o[] = new int[(count+1)/2];
		for(int i = 0,j=0; i < count; i++)
			if (comp[i] instanceof TabLabelPanel)
				o[j++] = ((TabLabelPanel)comp[i]).getPosition();
		return o;
	}	
	
	/** Remove a component from us 
	 *  Overridden to recompute the bean name and tab text
	 */
	public void remove(int n) {
		Component comp = getComponent(n);
		super.remove(n);
		if (!(comp instanceof SplitterBar))
			doNames();
	}	
	
	/** separate the named component from this SplitterPanel */
	public void separate(Component c) {
		Component comp[] = getComponents();
		int count = getComponentCount();
		for(int i = 0; i<count; i++)
			if (comp[i] instanceof TabLabelPanel &&
				 ((TabLabelPanel)comp[i]).getComponent() == c) {
				separateTabs((TabLabelPanel)comp[i]);
				break;
			}	
	}	
	
	/** Separate the named component from this SplitterPanel */
	void separateTabs(TabLabelPanel p) {
		int count = getComponentCount();
		Component c[] = getComponents();
		int i = 0;
		while(c[i] != p) i++;
		remove(p);
		if (i > 0)
			remove(i-1);
		else
			remove(0);
		
		Component comp1 = p.getComponent();
		String    name1 = p.getTabName();
		String    exp1  = p.getExplicitText();
		Component comp2 = null;
		String    name2 = null;
		String    exp2  = null;
		if (count == 3) {
			p = (TabLabelPanel)getComponent(0);
			comp2 = p.getComponent();
			name2 = p.getTabName();
			exp2  = p.getExplicitText();
		}	
			
		((TabSplitter)getParent()).separateTabs(name1, comp1, exp1,
		                                        name2, comp2, exp2, this);
		invalidate();
	}
	
	/** Change the orientation of the splitter layout */
	void swapOrientation() {
		((SplitterLayout)getLayout()).swapOrientation(this);
	}	
	
}