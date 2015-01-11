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

package ch.lowres.audio_rxtx.gui.widgets;
import ch.lowres.audio_rxtx.gui.*;
import ch.lowres.audio_rxtx.gui.helpers.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.swing.plaf.basic.BasicScrollBarUI;

import java.util.*;

import javax.swing.border.*;

/**
* 
*/
//half-baked, only for fixed/non-changing lists atm
//========================================================================
public class ListDialog extends JDialog implements MouseListener, ComponentListener
{
	private static Main m;
	private static GUI g;
	private static Fonts f;

	private JList list;

	private JPanel north=new JPanel();

	private JButton button_expand=new JButton("^");//expand
	private JButton button_close=new JButton("X");//close

/*
where to display on the screen, set via showDialog(..) by caller
 ______________________
| implicit close & shrink buttons above
.------------------
^ mouse click position
| items in list
| ...
*/	

	JScrollPane listScrollPane;

	private int xOffset=0;
	private int yOffset=0;

	//inside list -> for item hover highlighting
	private boolean mouseInside=false;

	private boolean mouseInsideScroller=false;

	//mouse pressed on scrollbar -> suppress hover highlighting
	private boolean mouseDownOnScroller=false;

	//hold index of item in the list ist currently hovered, -1 if none
	private int hoveredIndex=-1;

	//holding the items, given at creation time
	public final Vector items=new Vector();

	//remember slected index for cancel
	private int indexOnOpen=0;

	//remember x, y pos for shrink after expand
	private int xOnOpen=0;
	private int yOnOpen=0;

	private boolean isExpanded=false;

	//"large" number: means expand to bottom (...)
	//other value: preferred pixelsize of ~drowdown list
	//private int shrinkedHeight=30000;
	private int shrinkedHeight=300;
	//the list dialog should never cross screen edges
	//dialog window size should respect all insets (docks etc)

//========================================================================
	public ListDialog(Dialog d, String title, boolean modality)
	{
		super(d,title,modality);
///////////
		//init(vector v) must be called manually!
	}

//========================================================================
	public void initCellDimensions()
	{
		//http://stackoverflow.com/questions/9408409/revalidating-jlist-custom-elements
		list.setFixedCellHeight(0);
		list.setFixedCellWidth(0);
		list.setFixedCellHeight(-1);
		list.setFixedCellWidth(-1);
	}

//========================================================================
	public int showDialog(int selectedIndex, int x, int y)
	{
		if(list==null)
		{
			return 0;
		}
		indexOnOpen=selectedIndex;
		xOnOpen=x;
		yOnOpen=y;

		xOffset=x;
		yOffset=y;

		list.setSelectedIndex(selectedIndex);

		if(isExpanded)
		{
			expandList();
			initVisible();
			setVisible(true);
			list.repaint();
		}
		else
		{
			setLocation(xOffset, yOffset-button_close.getHeight());
			pack();
			initVisible();
			setVisible(true);
			list.repaint();
		}

		//http://stackoverflow.com/questions/4089311/how-can-i-return-a-value-from-a-jdialog-box-to-the-parent-jframe
		return list.getSelectedIndex();
	}

//========================================================================
	public void init(Vector v)
	{
		setIconImage(Images.appIcon);
		setLayout(new BorderLayout());
		setCursor(new Cursor(Cursor.HAND_CURSOR));

		//http://docs.oracle.com/javase/7/docs/api/java/awt/Window.html#setAlwaysOnTop%28boolean%29
		//on osx: prevents parent dialog to ever be on top of this dialog
		//half-baked. windows can be in-between this and the parent dialog
		if(Toolkit.getDefaultToolkit().isAlwaysOnTopSupported() && m.os.isMac())
		{
			setAlwaysOnTop(true);
		}

		items.clear();

		//copy to final vector
		items.addAll(v);

		//m.p("v "+v.size());
		//m.p("items "+items.size());

		list = new JList(items);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		//show all by default
		//this will be restricted through screensize, preferred shrinked size, open position
		list.setVisibleRowCount(items.size());
		//http://docs.oracle.com/javase/8/docs/api/javax/swing/JList.html#setLayoutOrientation-int-
		//list.setVisibleRowCount(-1);

		//custom renderer, allowing hover
		list.setCellRenderer(new Renderer());

		//list.setLayoutOrientation(JList.VERTICAL_WRAP);
		//list.setSelectedIndex(selectedIndex);

		addComponentListener(this);

		addWindowListeners();

		list.addMouseListener(this);

		//http://objectmix.com/java/73071-highlight-itemin-jlist-mouseenter-mouse-over.html
		list.addMouseMotionListener(new MouseAdapter()
		{
			public void mouseMoved(MouseEvent me)
			{
				Point mousePos = me.getPoint();

				int hovered=list.locationToIndex(mousePos);
				if(hovered!=hoveredIndex)
				{
					//repaint only old hovered and newly hovered
					if(hoveredIndex>=0)
					{
						list.repaint(list.getCellBounds(hoveredIndex,hoveredIndex+1));
					}
					hoveredIndex=hovered;
					list.repaint(list.getCellBounds(hoveredIndex,hoveredIndex+1));
				}
			}
		});

		//http://www.rgagnon.com/javadetails/java-0201.html
		list.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)//Released(KeyEvent e)
			{
				boolean isCtrlOrCmdDown=e.isControlDown();
				if(m.os.isMac())
				{
					isCtrlOrCmdDown=e.isMetaDown();
				}

				if (e.getKeyCode() == KeyEvent.VK_ENTER
					|| e.getKeyCode() == KeyEvent.VK_SPACE
				)
				{
					e.consume();
					confirmDialog();
				}

				else if (e.getKeyCode() == KeyEvent.VK_RIGHT && !isCtrlOrCmdDown)
				{
					e.consume();
					expandList();
				}
				else if (e.getKeyCode() == KeyEvent.VK_LEFT && !isCtrlOrCmdDown)
				{
					e.consume();
					if(isExpanded)
					{
						shrinkList();
					}
					else
					{
						confirmDialog();
					}
				}
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					e.consume();
					cancelDialog();
				}
			}//end keyReleased
	 	});//end list.addKeyListener

		listScrollPane = new JScrollPane(list);

		listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		listScrollPane.getVerticalScrollBar().setUI(new AScrollbarUI());
		listScrollPane.getHorizontalScrollBar().setUI(new AScrollbarUI());

		listScrollPane.getVerticalScrollBar().addMouseListener(new MouseListener()
		{
			//remember mouse status
			public void mousePressed(MouseEvent e) 
			{
				mouseDownOnScroller=true;
			}
			public void mouseReleased(MouseEvent e)
			{
				mouseDownOnScroller=false;
				//make sure item is hovered when mouse released on item
				if(mouseInside)
				{
					handleHover();
				}
			}
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) 
			{
				mouseInsideScroller=true;
			}
			public void mouseExited(MouseEvent e) 
			{
				mouseInsideScroller=false;
			}
		});

		listScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
		{
			public void adjustmentValueChanged(AdjustmentEvent e)
			{
				//m.p("adjusted: "+e.getAdjustable().getValue());
				if(mouseInside)
				{
					handleHover();
				}
			}
		});

		north.setLayout(new GridLayout(1,2));
		north.setFocusable(false);
		Border border = BorderFactory.createLineBorder(Colors.black, 2);

		button_close.setHorizontalAlignment(SwingConstants.CENTER);
		button_close.setBorder(border);
		button_close.setFocusable(false);
		north.add(button_close);

		add(north,BorderLayout.NORTH);

		button_close.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cancelDialog();
				//confirmDialog();
			}
		});

		button_expand.setHorizontalAlignment(SwingConstants.CENTER);
		button_expand.setBorder(border);
		button_expand.setFocusable(false);
		north.add(button_expand);

		add(north,BorderLayout.NORTH);

		button_expand.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(!isExpanded)
				{
					expandList();
				}
				else
				{
					shrinkList();
				}

			}
		});

		add(listScrollPane, BorderLayout.CENTER);

		setUndecorated(true);
		pack();
		//setResizable(false);
	}//end init

//========================================================================
	public void initVisible()
	{
		int index=list.getSelectedIndex();
		list.ensureIndexIsVisible(index);

		//m.p(list.getFirstVisibleIndex());
		//m.p(list.getLastVisibleIndex());

		final int first=list.getFirstVisibleIndex();
		final int last=	list.getLastVisibleIndex();
		final int min=	list.getMinSelectionIndex();
/*
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				list.scrollRectToVisible(
					list.getCellBounds(
						min, min+last-first
						//min, min+(int)((last-first)/2)
					)
				);
			}
		});
*/
	}//end initVisible

	//componentlistener
	public void componentHidden(ComponentEvent e)
	{
		//m.p(e.getComponent().getClass().getName() + " --- Hidden");
	}

	public void componentMoved(ComponentEvent e)
	{
		//m.p(e.getComponent().getClass().getName() + " --- Moved");
	}

	public void componentResized(ComponentEvent e)
	{
		//m.p(e.getComponent().getClass().getName() + " --- Resized ");
	}

	public void componentShown(ComponentEvent e)
	{
		//m.p(e.getComponent().getClass().getName() + " --- Shown");

		//set mouse props when "mouseEntered" was missing, mouse not moved
		Point loc=MouseInfo.getPointerInfo().getLocation();

		Point p1=MouseInfo.getPointerInfo().getLocation();
		Point p2=list.getLocationOnScreen();
/*
0---------screen
|
|   x--------component (p2)    getSize()
|   |
|   |   .mouse (p1)  
|   |
|   ---------
|________

*/

/*
		m.p("mouse: "+p1.getX()+" "+p1.getY());
		m.p("list: "+p2.getX()+" "+p2.getY());
		m.p("list size: "+list.getSize().getWidth()+" "+list.getSize().getHeight());
		m.p("scrollpane size: "+listScrollPane.getSize().getWidth()+" "+listScrollPane.getSize().getHeight());
		m.p("pref. size: "+getPreferredSize().getWidth()+" "+getPreferredSize().getHeight());
*/


		if(
			p1.getX() >= p2.getX()
			&&
			p1.getX() < p2.getX()+list.getPreferredSize().getWidth()
			&&
			p1.getY() >= p2.getY()
			&&
			p1.getY() < p2.getY()+getPreferredSize().getHeight()
		)
		{
			//m.p("mouse inside on show");
			mouseInside=true;
			handleHover();
		}
	}//end componentShown

//========================================================================
	public void handleHover()
	{
		if(!isVisible())
		{
			return;
		}
/////////////
//		if(mouseInside && (
		if(!mouseDownOnScroller && !mouseInsideScroller)
		{
			//m.p("handle hover "+hoveredIndex);
			try
			{
				Point p1=MouseInfo.getPointerInfo().getLocation();
				Point p2=list.getLocationOnScreen();
				Point p3=new Point(p1.x-p2.x,p1.y-p2.y);

				int hovered=list.locationToIndex(p3);

				if(hovered!=hoveredIndex)
				{
					//repaint old hovered and newly hovered
					if(hoveredIndex>=0)
					{
						//list.repaint(list.getCellBounds(list.getFirstVisibleIndex(),list.getLastVisibleIndex()));
						//old hovered
						list.repaint(list.getCellBounds(hoveredIndex,hoveredIndex+1));
					}
					//new hovered
					hoveredIndex=hovered;
					list.repaint(list.getCellBounds(hoveredIndex,hoveredIndex+1));
				}
			}
			catch(Exception ex){ex.printStackTrace();}
		}
	}//end handleHover

//========================================================================
	public void mousePressed(MouseEvent e) {}

//========================================================================
	public void mouseReleased(MouseEvent e){}

//========================================================================
	public void mouseClicked(MouseEvent e) 
	{
		confirmDialog();
	}

//========================================================================
	public void mouseEntered(MouseEvent e)
	{
		mouseInside=true;
		handleHover();
	}

//========================================================================
	public void mouseExited(MouseEvent e) 
	{
		mouseInside=false;
		if(hoveredIndex>=0)
		list.repaint(list.getCellBounds(hoveredIndex,hoveredIndex+1));
		hoveredIndex=-1;
	}

//========================================================================
	public void expandList()
	{
		isExpanded=true;
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
		button_expand.setLabel("-");

		Dimension d=super.getPreferredSize();

		if(d.getWidth() > g.screenDimension.getWidth()-insets.right-xOnOpen
		)
		{
			xOffset=(int)(g.screenDimension.getWidth()-d.getWidth()-insets.right);
		}

		yOffset=(int)Math.max(0,insets.top);
		setLocation(xOffset, yOffset);
		pack();
	}

//========================================================================
	public void shrinkList()
	{
		isExpanded=false;
		button_expand.setLabel("^");
		xOffset=xOnOpen;
		yOffset=yOnOpen;
		setLocation(xOffset, yOffset-button_close.getHeight());
		pack();
		initVisible();
	}

//========================================================================
	public void confirmDialog()
	{
		//m.p("selected index: "+list.getSelectedIndex());
		//String value = (String)items.elementAt(list.getSelectedIndex());
		//m.p("value: "+value);

		mouseInside=false;
		mouseInsideScroller=false;
		mouseDownOnScroller=false;
		hoveredIndex=-1;

		this.setVisible(false);
	}

//========================================================================
	public void cancelDialog()
	{
		list.setSelectedIndex(indexOnOpen);

		mouseInside=false;
		mouseInsideScroller=false;
		mouseDownOnScroller=false;
		hoveredIndex=-1;

		this.setVisible(false);
	}

//========================================================================
/*
--------------------
|insets   |        |
|  -------|------  |
|  |      |yoff |  |
|  xoff ----    |  |
|-------|  |    |  |
|  |    .--.    |  |
|  .------------.  |
|                  |
.------------------.

--------------------
|insets   |        |
|  -------|------  |  ^
|  |      |yoff |  |  |
|  xoff ---------------
|-------|           
|  |    |       |   
|  .----|     --.   
|       |          |
.-------|        --.
        |
    <---


*/
	@Override
	public Dimension getPreferredSize()
	{
		Dimension d=super.getPreferredSize();

		//DisplayMode mode = this.getGraphicsConfiguration().getDevice().getDisplayMode();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());

		if(isExpanded)
		{
			return new Dimension(
				Math.min(
					(int)d.getWidth()
					,(int)g.screenDimension.getWidth()-insets.right-xOffset
				)
				//use top and bottom
				,(int)g.screenDimension.getHeight()-insets.top-insets.bottom
			);
		}
		//else
		return new Dimension(
			Math.min(
				(int)d.getWidth()
				,(int)g.screenDimension.getWidth()-insets.right-xOffset
			)
			,Math.min(
				(int)d.getHeight()
				//only bottom, plus button		
				,Math.min(
					(int)g.screenDimension.getHeight()-insets.bottom-yOffset+button_close.getHeight()
					,(int)shrinkedHeight-insets.bottom+button_close.getHeight()
				)
			)
		);
	}//end getPreferredSize

//========================================================================
	public class Renderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(
			JList list, Object value, int index, boolean selected, boolean focused)
		{
			JTextArea area = new JTextArea();

			area.setText((String)items.elementAt(index));

			area.setFont(f.fontNormal);

			//top, left, bottom, right
			area.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 10, 3, 10));

			area.setBackground(selected ? Colors.list_selected_background : Colors.list_background);
			area.setForeground(selected ? Colors.list_selected_foreground : Colors.list_foreground);

			if(index==hoveredIndex && hoveredIndex!=list.getSelectedIndex())
			{
				area.setBackground(Colors.list_hovered_background);
				area.setForeground(Colors.list_hovered_foreground);
			}
			return area;
		}
	}//end class Renderer

//========================================================================
	private void addWindowListeners()
	{
		addWindowListener(new WindowListener()
		{
			@Override
			public void windowClosed(WindowEvent arg0) {}

			@Override
			public void windowClosing(WindowEvent arg0) {}
			@Override

			public void windowActivated(WindowEvent arg0) {/*m.p("---activated");*/}
			@Override

			public void windowDeactivated(WindowEvent arg0) 
			{
				//m.p("---deactivated");
				if(isVisible())
				{
					cancelDialog();
				}
			}
			@Override
			public void windowDeiconified(WindowEvent arg0) {/*m.p("---deiconified");*/}

			@Override
			public void windowIconified(WindowEvent arg0) {/*m.p("---iconified");*/}

			@Override
			public void windowOpened(WindowEvent arg0) {/*m.p("---opened");*/}

		});
	}//end addWindowListeners
}//end class ListDialog
