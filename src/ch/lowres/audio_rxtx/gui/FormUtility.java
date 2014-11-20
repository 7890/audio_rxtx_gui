/**
 * Simple utility class for creating forms that have a column
 * of labels and a column of fields. All of the labels have the
 * same width, determined by the width of the widest label
 * component.
 * 
 * Philip Isenhour - 060628 - http://javatechniques.com/
 */
//http://javatechniques.com/blog/gridbaglayout-example-a-simple-form-layout/
//adopted to use with audio_rxtx_gui
//tb/1410

package ch.lowres.audio_rxtx.gui;

import java.awt.*;
import javax.swing.*;

//========================================================================
public class FormUtility 
{
	/**
	 * Grid bag constraints for fields and labels
	 */
	private GridBagConstraints labelConstraints=null;
	private GridBagConstraints middleConstraints=null;
	private GridBagConstraints lastConstraints=null;

//========================================================================
	//http://docs.oracle.com/javase/6/docs/api/java/awt/GridBagConstraints.html
	public FormUtility()
	{
		//Set up the constraints for the "last" field in each 
		//row first, then copy and modify those constraints.

		//weightx is 1.0 for fields, 0.0 for labels
		//gridwidth is REMAINDER for fields, 1 for labels
		lastConstraints=new GridBagConstraints();

		//Stretch components horizontally (but not vertically)
		lastConstraints.fill=GridBagConstraints.HORIZONTAL;

		//Components that are too short or narrow for their space
		//Should be pinned to the northwest (upper left) corner
		lastConstraints.anchor=GridBagConstraints.NORTHWEST;

		//Give the "last" component as much space as possible
		lastConstraints.weightx=1.0;

		//Give the "last" component the remainder of the row
		lastConstraints.gridwidth=GridBagConstraints.REMAINDER;

		//Add a little padding
		lastConstraints.insets=new Insets(1, 1, 1, 1);

		//Now for the "middle" field components
		middleConstraints=(GridBagConstraints)lastConstraints.clone();

		//These still get as much space as possible, but do
		//not close out a row
		middleConstraints.gridwidth=GridBagConstraints.RELATIVE;

		//And finally the "label" constrains, typically to be
		//used for the first component on each row
		labelConstraints=(GridBagConstraints)lastConstraints.clone();

		//Give these as little space as necessary
		labelConstraints.weightx=0.2;
		labelConstraints.gridwidth=5;
	}//end constructor FormUtility()

//========================================================================
	/**
	 * Adds a Label with the given string to the label column
	 */
	public JLabel addLabel(String s, Container parent)
	{
		JLabel c=new JLabel(s);
		c.setForeground(Colors.form_foreground);
		c.setBackground(Colors.form_background);
		addLabel(c, parent);
		return c;
	}

//========================================================================
	/**
	 * Adds an arbitrary label component, starting a new row
	 * if appropriate. The width of the component will be set
	 * to the minimum width of the widest component on the
	 * form.
	 */
	public void addLabel(Component c, Container parent)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		c.setForeground(Colors.form_foreground);
		c.setBackground(Colors.form_background);
		gbl.setConstraints(c, labelConstraints);
		parent.add(c);
	}

//========================================================================
	public void addMiddleLabel(Component c, Container parent)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		c.setForeground(Colors.form_foreground);
		c.setBackground(Colors.form_background);
		gbl.setConstraints(c, middleConstraints);
		parent.add(c);
	}

//========================================================================
	public void addLastLabel(String text, Container parent)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		JLabel c=new JLabel(text);
		c.setForeground(Colors.form_foreground);
		c.setBackground(Colors.form_background);
		gbl.setConstraints(c, lastConstraints);
		parent.add(c);
	}

//========================================================================
	public void addLastLabel(Component c, Container parent)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		c.setForeground(Colors.form_foreground);
		c.setBackground(Colors.form_background);
		gbl.setConstraints(c, lastConstraints);
		parent.add(c);
	}

//========================================================================
	//special label
	public void addTitle(String title,Container parent)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		JLabel c=new JLabel(title,JLabel.CENTER);
		c.setForeground(Colors.form_foreground);
		c.setBackground(Colors.form_background);
		//c.setAlignment(JLabel.CENTER);
		gbl.setConstraints(c, lastConstraints);
		parent.add(c);
	}

//========================================================================
	//header
	public void addHeader(String header, Container parent, Font f)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		JLabel c=new JLabel(header,JLabel.CENTER);
		c.setForeground(Colors.form_foreground);
		c.setBackground(Colors.form_background);
		//c.setAlignment(JLabel.CENTER);
		c.setFont(f);
		gbl.setConstraints(c, lastConstraints);
		parent.add(c);
	}

//========================================================================
	/**
	 * Adds a "middle" field component. Any component may be 
	 * used. The component will be stretched to take all of
	 * the space between the label and the "last" field. All
	 * "middle" fields in the layout will be the same width.
	 */
	public void addMiddleField(Component c, Container parent)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		gbl.setConstraints(c, middleConstraints);
		c.setForeground(Colors.input_foreground);
		c.setBackground(Colors.input_background);
		parent.add(c);
	}

//========================================================================
	/**
	 * Adds a field component. Any component may be used. The 
	 * component will be stretched to take the remainder of 
	 * the current row.
	 */
	public void addLastField(Component c, Container parent)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		gbl.setConstraints(c, lastConstraints);
		c.setForeground(Colors.input_foreground);
		c.setBackground(Colors.input_background);
		parent.add(c);
	}
	
//========================================================================
	public void addList(java.awt.List list, Container parent)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		gbl.setConstraints(list, lastConstraints);
		list.setForeground(Colors.input_foreground);
		list.setBackground(Colors.input_background);
		parent.add(list);
	}

//========================================================================
	//vertical spacer (fill width)
	public void addSpacer(Container parent)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		JPanel c=new JPanel();
		gbl.setConstraints(c, lastConstraints);
		c.setBackground(Colors.form_background);
		parent.add(c);
	}

//========================================================================
	//canvas, fill width
	public void addImage(Component c, Container parent)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		gbl.setConstraints(c, lastConstraints);
		parent.add(c);
	}

//========================================================================
	//'full' button
	public void addFullButton(Component c, Container parent, Font f)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		gbl.setConstraints(c, lastConstraints);
		c.setBackground(Colors.button_background);
		c.setForeground(Colors.button_foreground);
		c.setFont(f);
		parent.add(c);
	}

//========================================================================
	//buttons
	public void addButtons(Component c, Container parent, Font f)
	{
		GridBagLayout gbl=(GridBagLayout)parent.getLayout();
		c.setBackground(Colors.button_background);
		c.setForeground(Colors.button_foreground);
		c.setFont(f);
		gbl.setConstraints(c, lastConstraints);
		parent.add(c);
	}
}//end class FormUtility
