package swing;

import graphic.textbox.TextBox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utility.PersonalizedIcon;

public class SPanelDiagramComponent extends JPanelRounded
{
	private static final String TITLE = "Class diagram";
	private static final long serialVersionUID = -8198486630670114549L;

	public SPanelDiagramComponent()
	{
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
		setBackground(new Color(255, 0, 0, 10));
		setForeground(Color.GRAY);
		
		JPanel panelTop = new JPanel();
		panelTop.setOpaque(false);
		
		JPanel panelBottom = new JPanel();
		panelBottom.setLayout(new GridLayout(4, 3));
		panelBottom.setOpaque(false);
		
		JLabel labelTitle = new JLabel(TITLE);
		panelTop.add(labelTitle);

		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/class.png"), "newClass", Color.RED));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/interface.png"), "newInterface", Color.RED));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/classAssoc.png"), "newClassAssoc", Color.RED));
		
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/generalize.png"), "newGeneralize", Color.RED));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/dependency.png"), "newDependency", Color.RED));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/innerClass.png"), "newInnerClass", Color.RED));
		
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/association.png"), "newAssociation", Color.RED));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/aggregation.png"), "newAggregation", Color.RED));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/composition.png"), "newComposition", Color.RED));
		
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/multi.png"), "newMulti", Color.RED));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/note.png"), "newNote", Color.RED));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/linkNote.png"), "linkNote", Color.RED));
		
		setMaximumSize(new Dimension(200, 150));
		
		add(panelTop);
		add(panelBottom);
	}
}
