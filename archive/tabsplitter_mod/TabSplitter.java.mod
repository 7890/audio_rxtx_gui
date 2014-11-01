
package com.magelang.tabsplitter;

import java.awt.Panel; 
import java.awt.PopupMenu; 
import java.awt.MenuItem; 
import java.awt.Graphics; 
import java.awt.Color; 
import java.awt.Dimension; 
import java.awt.FontMetrics; 
import java.awt.Font; 
import java.awt.Component; 
import java.awt.Polygon; 
import java.util.Vector; 
import java.util.Hashtable; 
import java.util.Enumeration; 
import java.awt.Insets; 
import java.awt.event.MouseEvent; 
import java.awt.event.MouseListener; 
import java.awt.event.MouseMotionListener; 
import java.awt.event.ActionListener; 
import java.awt.CardLayout; 
import java.awt.Container; 
import java.awt.Image; 
import java.awt.Rectangle; 
import java.awt.Cursor;
import com.magelang.splitter.SplitterLayout;
import com.magelang.splitter.ReOrientingSplitterBar;
/**
 * TabSplitter -- allows a user to select from several interface components
 * by clicking a tab at the top of the panel.
 *
 * In addition, it allows the user to merge any tabs together into a
 * a single tab using a SplitterLayout.
 *
 * <p>Each contained component is represented by a tab at the top of the
 * TabSplitter, much like file folders in a file cabinet.  When a
 * tab is clicked, it becomes the "selected" tab and its associated
 * component will be displayed.
 * <p>There are two types of navigational aids provided with the
 * TabSplitter.  If there are more tabs than can be displayed in the
 * current window, two triangle buttons will appear.  These buttons
 * will scroll the set of tabs left and right.
 * <p>There are also two buttons marked "+" and "-".  These
 * buttons move the user through each tab in succession.
 * <p>To properly set up a tab panel, you need to do two things:
 * <ul>
 *    <li>add components to the TabSplitter, using an "add" method.
 *        <br>The order in which panels are added is the 
 *            order in which their tabs will appear.
 *    <li>set a tabText string array to represent the text that
 *        is displayed on each tab.
 * </ul>
 *
 * <p>The thing that sets this tabbed panel apart is how it reacts
 * to mouse drags on its tabs.  Click on a tab and drag it to
 * any other tab.  The "source" tab will be merged into the
 * "target" tab, combined via a panel with a SplitterLayout.
 * The target tab's text will be modified to list both previous
 * tab texts.  In addition, each sub panel that has been merged will
 * have a Button placed above it with the old tab text for that
 * component.  Clicking on this button will separate out the subpanel
 * on its own tab once again.
 *
 * <p><b>Note:</b> It is extremely important that the user of this
 * tab panel not try to directly use the layoutmanger (via getLayout()
 * and setLayout() ).  These two methods could not be overridden
 * to prevent modification, as many GUI builders expect to use it.
 * If you want to switch between tabs, use the "next()" and "previous()"
 * methods provided by TabSplitter, <i>not</i> those of CardLayout.
 * <p>Use this code at your own risk!  MageLang Institute is not
 * responsible for any damage caused directly or indirctly through
 * use of this code.
 
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
 * @see SplitterLayout
 * @see TabPanel
 */
public class TabSplitter extends TabPanel implements ActionListener, MouseListener, MouseMotionListener {
	// Cursors to use when selecting a tab
	private static Cursor DEF_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

	// The "swap orientation" item in the popup menu
	private MenuItem swapItem = null;
	
	// The original positions of the components
	private Hashtable position = new Hashtable();
	
	// Which tab (if any) is currently being dragged
	private int tabBeingDragged = -1;	
	
	private Hashtable compsInSplitters = new Hashtable();

	/** Constructor */
	public TabSplitter() {
		super();
		addMouseMotionListener(this);
		PopupMenu popupMenu = getPopupMenu();
		popupMenu.setLabel("TabSplitter");
		swapItem = new MenuItem("Swap Orientation");
		popupMenu.add(swapItem);
		popupMenu.add(new MenuItem("-"));
		swapItem.addActionListener(this);
	}
	
	/** Handle the menu selection events -- just adds in our processing
	 *  for the swap orientation menu item
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		if ((e.getSource() == swapItem) )
			swapOrientation();
		else
			super.actionPerformed(e);
	}
	
	/** adds a component to the TabSplitter.
	 *  Performs the extra processing to keep track of the original position
	 *  of the component within the TabSplitter
 	 * @param comp (java.awt.Component) -- the component to be added
 	 * @param constraints (java.lang.Object) -- constraints on the component
 	 * @param index (int) -- at which position will the component be added
 	 */
	protected void addImpl(Component comp, Object constraints, int index) {
		int count = getComponentCount();
		Component comps[] = getComponents();

		// find out where it should go
		if (compsInSplitters.get(comp) != null)
			index = getPosition(comp);
		else if (index == -1)
			index = getMaxPos()+1;

		int target=0;
		boolean found = false;
		for(int i=0; i<count; i++) {
			// if it's already here, abort!
			if (comps[i] == comp) return;
			if (getPosition(comps[i]) < index)
				target = i+1;
		}

		// add it to the container (as a tab)
		super.addImpl(comp, constraints, target);		
				
		// if it's a brand-spankin' new component (not hiding in a splitter panel and
		//   being brought back in), we need to adjust the positions of components after it
		if ((!(comp instanceof SplitterPanel)) && 
		    compsInSplitters.get(comp) == null) { // if not in splitter
			// we need to adjust the positions of all components after the one
			// being added...
			Enumeration e = position.keys();
			while(e.hasMoreElements()) {
				Object o = e.nextElement();
				int i = getPosition(o);
				if (i >= index)
					position.put(o, new Integer(i+1));
			}	
			position.put(comp, new Integer(index));
		}
		
		tabBeingDragged = -1;
	}
	
	/** Determine where the component should be placed based
	 *  on its position when added to the TabSplitter
	 */
	protected void findWhereToAdd(String name, Component comp) {
		int count = getComponentCount();
		Component comps[] = getComponents();
		int i=0;
		int pos = getPosition(comp);
		while((i < count) && (getPosition(comps[i]) < pos)) i++;
		if (i == count)
			add(name, comp);
		else
			add(comp, name, i);
	}
		
	protected int getMaxPos() {
		int max = -1;
		Enumeration e = position.keys();
		while(e.hasMoreElements()) {
			int temp = ((Integer)position.get(e.nextElement())).intValue();
			if (temp > max)
				max = temp;
		}
		return max;	
	}	
	/* Determine where the component thinks it belongs */
	protected int getPosition(Object comp) {
		Integer i = (Integer)position.get(comp);
		if (i == null) return -1;
		return i.intValue();
	}	
	
	/** Get the component that is currently visible
	 *  @return the component that is on the currently-selected tab (note that this
	 *  method returns an Object because its subclass returns an array of visible comps)
	 */
	public Object getVisibleComponent() {
		if (getComponentCount() == 0) return null;
		Component comp = getComponent(getSelectedTabNum());
		if (comp instanceof SplitterPanel)
			return ((SplitterPanel)comp).getVisibleComponent();

		return comp;	
	}	
	
	/** Get an array of selected component numbers.  These numbers will match the
	 *  positions of the components when they were originally added to the panel.
	 *  Each visible component in the currently-selected tab will have its
	 *    original position returned.
	 *  @return array of original positions of visible components.
	 */
	public int[] getVisibleComponentNum() {
		if (getComponentCount() == 0) return new int[] {-1};
		Component comp = getComponent(getSelectedTabNum());
		if (comp instanceof SplitterPanel)
			return ((SplitterPanel)comp).getVisibleComponentNum();

		int i = getPosition(comp);
		return new int[] {i};
	}	
	
	/** Determine if we want to merge a tab or just select one */
	protected void mergeOrShow(int n) {
		// if they released on the same tab they pressed, or nothing was being dragged
		if (tabBeingDragged == n || tabBeingDragged == -1)
			// select the target tab
			showPhysicalTab(n);
			
		// otherwise, they were dragging a tab and released on a different tab -- merge them
		else
			mergeTabs(n, tabBeingDragged);
	}	

	//set in mergeTabs, nulled in separateTabs
	SplitterPanel splitter;
	public SplitterPanel getSplitterPanel()
	{
		return splitter;
	}

	/** Merges two tabs' components onto one tab by putting them both on a splitter panel */
	public void mergeTabs(int target, int source) {

		// first things first -- we're no longer dragging a tab...
		tabBeingDragged = -1;
	
		// get the text that is displayed on the tabs
		String names[] = determineTabText();

		Component c[]=getComponents();
		if(c.length<2)
		{
			return;
		}

		// get the source and target components
		Component t = getComponent(target);
		Component s = getComponent(source);

		if (!(t instanceof SplitterPanel))
			compsInSplitters.put(t,t);
		if (!(s instanceof SplitterPanel))
			compsInSplitters.put(s,s);

		String sText = getExplicitTabText(s);
		String tText = getExplicitTabText(t);
		
		int tpos = getPosition(t);
		int spos = getPosition(s);
		
		// remove the components from the TabSplitter
		remove(s);
		remove(t);
	
		if (t instanceof SplitterPanel) {
			if (s instanceof SplitterPanel) 
				((SplitterPanel)t).add((SplitterPanel)s);
			else 
				((SplitterPanel)t).add(names[source], s, spos, sText);
			position.put(t, new Integer(tpos));
			add(t, t.getName(), tpos);
		}
		else if (s instanceof SplitterPanel) {
			((SplitterPanel)s).add(names[target], t, tpos, tText);			
			position.put(s, new Integer(tpos));
			add(s, s.getName(), tpos);
		}	
	
		else {	
			// Neither are splitters
			// Create a new splitter panel
			// Create two new TabLabelPanels
			// Add the target/source to the TabLabelPanels
			// Add the TabLabelPanels to the splitter panel w/ a splitterbar
			splitter = new SplitterPanel();
			splitter.add(names[target], t, tpos, tText);
			splitter.add(names[source], s, spos, sText);
			position.put(splitter, new Integer(tpos));
			add(splitter, splitter.getName(), tpos);
		}	
	
		// Make sure we get repainted/layed out
		showPhysicalTab(target>source?target-1:target);
		s.setVisible(true);
		t.setVisible(true);
	}
	
	public void mouseDragged(MouseEvent e) {
		// if the mouse is dragged over the left or right arrows, shift!
		if (leftEnabled && leftArrow.contains(e.getX(), e.getY()))
			shiftLeft();
		else if (rightEnabled && rightArrow.contains(e.getX(), e.getY()))
			shiftRight();
	}
	/** Handle the mouse exiting the TabSplitter */
	public void mouseExited(java.awt.event.MouseEvent e) {
		// user moved mouse out of the tab panel -- abort a drag
		tabBeingDragged = -1;
		setCursor(DEF_CURSOR);
		super.mouseExited(e);
	}
	
	public void mouseMoved(MouseEvent e) {
	}	
	/** Handle the mouse being pressed on a TabSplitter */
	public void mousePressed(java.awt.event.MouseEvent e) {
		int selected = getSelectedTabNum();
		int firstVisible = getFirstVisible();
		
		if (!e.isMetaDown()) { // we deal with left click only...
			if ((selected > firstVisible) && tabContains(selected-firstVisible, e.getX(), e.getY()))
				tabBeingDragged = selected;
			else {
				for(int i=firstVisible; i < getComponentCount(); i++)
					if (tabContains(i-firstVisible, e.getX(), e.getY())) {
						tabBeingDragged = i;
						break;
						}
				}	
			if (tabBeingDragged != -1) {
				setCursor(HAND_CURSOR);
			}	
		}	
	
		super.mousePressed(e);
	}
	
	/** Handle the mouse being released on a tabsplitter */
	public void mouseReleased(java.awt.event.MouseEvent e) {
		setCursor(DEF_CURSOR);
		super.mouseReleased(e);
	}
	
	/** Remove a component from the container.
	 *  Need to determine a good way to do this!
	 *  This method is overridden to force a repaint in design mode.
	 * @param index (int) Which component to remove.
	 */
	public void remove(int index) {
		// find the component in our position list
		Component targetComponent = null;
		Enumeration e = position.keys();
		while(e.hasMoreElements()) {
			Object o = e.nextElement();
			if ((!(o instanceof SplitterPanel)) && getPosition(o) == index) {
				removeBody((Component)o);
				return;
			}	
		}	
		
		throw new IllegalArgumentException("tab position not found");
	}
	
	public void remove(Component comp) {
		Integer oldI = (Integer)position.get(comp);
		if (oldI == null)
			throw new IllegalArgumentException("tab not found");
		removeBody(comp);
	}	
	
	/** Remove all components from the container.
	 *  This method is overridden to force a repaint in design mode.
	 */
	public void removeAll() {
		super.removeAll();
		position = new Hashtable();
		tabBeingDragged = -1;
	}
	
	/** Support code for remove()s */
	private void removeBody(Component c) {
		tabBeingDragged = -1;
		boolean found=false;
		boolean special = false;
				
		// check if the component is in a splitter or is directly a component
		int count = getComponentCount();
		Component comp[] = getComponents();
		for(int i = 0; !found && i < count; i++)
			found = (comp[i] == c);

		for(int i = 0; !found && i < count; i++) {
			if (comp[i] instanceof SplitterPanel &&
				 ((SplitterPanel)comp[i]).contains(c)) {
				 // separate c from the splitter panel
				 ((SplitterPanel)comp[i]).separate(c);
				 comp = getComponents();
				 count = getComponentCount();
				 compsInSplitters.remove(c); //hack
				 found = special = true;
			} 
		}	

		if (found) {
			// adjust/remove the positions...
			if ((!(c instanceof SplitterPanel)) && 
			    compsInSplitters.get(c) == null) { // if NOT in a splitter
				int posIndex = getPosition(c);
				position.remove(c);
				for(int i=0;i<count;i++)
					if (comp[i] instanceof SplitterPanel)
						((SplitterPanel)comp[i]).decrPositions(posIndex);
				
				Enumeration e = position.keys();
				while(e.hasMoreElements()) {
					Object o = e.nextElement();
					int p = getPosition(o);
					if (p > posIndex)
						position.put(o, new Integer(p-1));
				}	
			}

			// figure out where the component really is...
			for(int index=0; index<count; index++)
				if (comp[index] == c)
					super.remove(index);
		}
		
		if (special)
			showPhysicalTab(getSelectedTabNum());
	}	
	
	/** separateTabs -- split the selected tab out from its parent Splitterlayout panel */
	public void separateTabs(String name1, Component comp1, String exp1, 
	                         String name2, Component comp2, String exp2,
	                         SplitterPanel p) {
		int ppos = 0;
		Component comps[] = getComponents();
		int count = getComponentCount();
		while(ppos < count && comps[ppos] != p) ppos++;

		remove(p);
		
		// if keeping the splitter, re-add it to the CardLayout.  This is necessary to
		//   update the card name. grr.
		if (comp2 == null)
			add(p, p.getTabName(), ppos);
		else
			position.remove(p);
			
		// re-add the components to the TabSplitter
		if (exp1 != null)
			setExplicitTabText(comp1, exp1);
		findWhereToAdd(name1, comp1);
		compsInSplitters.remove(comp1);
		if (comp2 != null) {
			if (exp2 != null)
				setExplicitTabText(comp2, exp2);
			findWhereToAdd(name2, comp2);
			compsInSplitters.remove(comp2);
		}	
		
		show(comp1);
		
		invalidate();
		validate();
		repaint();

		splitter=null;
	}	
	/** Set the font to use when writing the tab text */
	public void setFont(Font f) {
		super.setFont(f);
		invalidate();
		validate();
		// the following two lines are a hack to get the box to redraw properly
		// I'll figure out a better way sometime...
		((CardLayout)getLayout()).next(this);
		((CardLayout)getLayout()).previous(this);
		repaint();
	}
	
	/** Show the nth tab (starting at 0)
	 *  The number refers to the tab added in the nth position to the 
	 *  tab splitter, not the actual nth tab on the panel.  This method
	 *  will find where component number n is located and display it,
	 *  even if it is in a SplitterPanel
	 */
	public void show(int n) {
		// find the component that was in that position
		Component comp[] = getComponents();
		int count = getComponentCount();
		for(int i = 0; i<count; i++) {
			Integer pos = (Integer)position.get(comp[i]);
			if (pos != null && pos.intValue()==n) {
				show(comp[n]);
				return;
			}	
		}
		throw new IllegalArgumentException("Tab not found");
	}
	
	/** Show a component.
	* This method will find where the component is located and display it,
	 *  even if it is in a SplitterPanel
	 */
	public void show(Component comp) {
		// find the component -- note that it could be in
		//   a SplitterPanel...
		
		// simple find first...
		try {
			super.show(comp);
		}
		catch (IllegalArgumentException e) {
			Component comps[] = getComponents();
			int count = getComponentCount();
			for(int i = 0; i< count; i++) {
				if (comps[i] instanceof SplitterPanel) {
					if (((SplitterPanel)comps[i]).contains(comp)) {
						showPhysicalTab(i);
						return;
					}
				}	
			}	
			throw new IllegalArgumentException("Tab not found");
		}	
	}
	
	/** Show a component based on tab name
	 *  This method will find where the component with the specified text 
	 *  is located and display it, even if it is in a SplitterPanel
	 */
	public void show(String tabName) {
		try {
			super.show(tabName);
		}
		catch (IllegalArgumentException e) {
			// find the component -- note that it could be in a SplitterPanel.
			Component comp[] = getComponents();
			int count = getComponentCount();
			for(int i = 0; i< count; i++) {
				if (comp[i] instanceof SplitterPanel) {
					if (((SplitterPanel)comp[i]).contains(tabName)) {
						showPhysicalTab(i);
						return;
					}
				}	
			}	
			throw new IllegalArgumentException("No tab found with text \""+tabName+"\"");
		}	
	}	
	
	/** Change the orientation (horizontal/vertical) of all SplitterPanels */
	public void swapOrientation() {
		Component comps[] = getComponents();
		for(int i = getComponentCount()-1; i>-1; i--)
			if (comps[i] instanceof Container &&
				 ((Container)comps[i]).getLayout() instanceof SplitterLayout) {
				 Container cont = (Container)comps[i];
				((SplitterLayout)cont.getLayout()).swapOrientation(cont);
				}	
	}	
	
}