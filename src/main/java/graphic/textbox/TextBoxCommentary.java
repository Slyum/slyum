package graphic.textbox;

import change.BufferCreation;
import change.BufferNote;
import change.Change;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import graphic.ColoredComponent;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.MovableComponent;
import graphic.entity.EntityView;
import graphic.relations.LineCommentary;
import graphic.relations.LineView;
import graphic.relations.RelationGrip;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import swing.Slyum;
import swing.UserInputDialog;
import swing.propretiesView.NoteProperties;
import utility.PersonalizedIcon;
import utility.Utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * A TextBoxCommentary is not a subclass of TextBox becauseit is not on signle line. A TextBoxCommentary display a note
 * on multi-line and can be etited by double-click on it. The size of the TextBoxCommentary is automatically compute
 * according to its content.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class TextBoxCommentary extends MovableComponent implements ColoredComponent {
  public static final String DEFAULT_TEXT = "Double-click to edit note.";
  public static final Point MINIMUM_SIZE = new Point(50, 50);

  /**
   * Draw a representation of a note (with creased corner) with the specified bounds and color.
   *
   * @param g2 the graphic context
   * @param bounds the bounds of the note
   * @param cornerSize the size of the corner
   * @param color the color of the note
   */
  public static void drawNote(Graphics2D g2, Rectangle bounds, int cornerSize,
                              Color color) {
    final int cornerX = bounds.x + bounds.width - cornerSize;
    final int cornerY = bounds.y + cornerSize;

    final int x2 = bounds.x + bounds.width;
    final int y2 = bounds.y + bounds.height;

    final int[] pointsX = new int[] {bounds.x, cornerX, x2, x2, bounds.x};
    final int[] pointsY = new int[] {bounds.y, bounds.y, cornerY, y2, y2};

    g2.setStroke(new BasicStroke(1.2f));

    g2.setColor(color);
    g2.fillPolygon(pointsX, pointsY, pointsX.length);

    g2.setColor(Color.DARK_GRAY);
    g2.drawPolygon(pointsX, pointsY, pointsX.length);

    g2.drawLine(cornerX, bounds.y, cornerX, cornerY);
    g2.drawLine(cornerX, cornerY, x2, cornerY);
  }

  private Rectangle bounds;

  private final Font font = new Font("Ubuntu", Font.PLAIN, 12);

  private String text;

  /**
   * Create a new TextBoxCommentary with the given text.
   *
   * @param parent the graphic view
   * @param text the default text include in the note
   */
  public TextBoxCommentary(final GraphicView parent, String text) {
    super(parent);
    init(text);
  }

  /**
   * Create a new TextBoxCommentaray with the givent text associated with anoth component. A TextBoxCommentary
   * associated with a graphic component create a new line between the note and the component. This line can be edited.
   *
   * @param parent the graphic component
   * @param text the default text include in the note
   * @param component the component associated with the not
   */
  public TextBoxCommentary(final GraphicView parent, String text,
                           GraphicComponent component) {
    super(parent);

    if (component == null)
      throw new IllegalArgumentException("component is null");

    final Point middleTextBox = new Point(bounds.x + bounds.width / 2, bounds.y
                                                                       + bounds.height / 2);

    final Point middleComponent = new Point(bounds.x + bounds.width / 2,
                                            bounds.y + bounds.height / 2);

    if (LineCommentary.checkCreate(this, component, true))
      parent.addLineView(new LineCommentary(parent, this, component,
                                            middleTextBox, middleComponent, true));

    init(text);
  }

  @Override
  public Point computeAnchorLocation(Point first, Point next) {
    return EntityView.searchNearestEgde(getBounds(), first, next);
  }

  /**
   * Compute the width of the note according to it content. If a word is too long for current width, the width is
   * automatically enlarged the size of the word.
   *
   * @param g2
   */
  private void computeWidth(Graphics2D g2) {
    final int PADDING = 5;
    final Rectangle bounds = getBounds();
    final String[] texts = getText().split("\\ ");

    final Font effectiveFont = font.deriveFont(font.getSize()
                                               * parent.getZoom());

    final FontMetrics metrics = g2.getFontMetrics(effectiveFont);

    final int hgt = metrics.getHeight();
    int adv, offsetY = bounds.y + PADDING + hgt, offsetX = PADDING;
    int nbLines = 1;

    int newWidth = bounds.width;

    for (final String text2 : texts) {
      final String currentText = text2 + " ";
      adv = metrics.stringWidth(currentText);

      if (offsetX + adv > bounds.width - PADDING * 2) {
        offsetY += hgt; // new line
        offsetX = PADDING;
        nbLines++;

        if (offsetX + adv > newWidth - PADDING)

          newWidth = adv + PADDING * 4;

      }

      offsetX += adv;
    }

    final int newHeight = offsetY + PADDING * 2 - bounds.y;

    if (nbLines == 1)

      newWidth = offsetX + PADDING * 4;

    if (bounds.width != newWidth || bounds.height != newHeight)

      setBounds(new Rectangle(bounds.x, bounds.y, newWidth, newHeight));
  }

  @Override
  public void delete() {
    super.delete();

    parent.removeComponent(leftMovableSquare);
    parent.removeComponent(rightMovableSquare);
  }

  @Override
  public void drawSelectedEffect(Graphics2D g2) {
    if (pictureMode) return;

    Color backColor = parent.getColor();
    Color fill = getColor();
    fill = new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 100);

    final Color border = backColor.darker();
    final BasicStroke borderStroke = new BasicStroke(1.0f,
                                                     BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
                                                     new float[] {2.0f}, 0.0f);

    g2.setColor(fill);
    g2.fillRect(ghost.x, ghost.y, ghost.width, ghost.height);

    g2.setColor(border);
    g2.setStroke(borderStroke);
    g2.drawRect(ghost.x, ghost.y, ghost.width - 1, ghost.height - 1);
  }

  /**
   * Draw the text of the note. When a word exceeds the note width, a new line is created. The height is dynamically
   * compute according to the number of lines in the note.
   *
   * @param g2 the graphic context
   */
  private void drawText(Graphics2D g2) {
    final int PADDING = 5;
    final Rectangle bounds = getBounds();
    final String[] texts = getText().split("\\ ");

    final Font effectiveFont = font.deriveFont(font.getSize()
                                               * parent.getZoom());

    final FontMetrics metrics = g2.getFontMetrics(effectiveFont);

    final int hgt = metrics.getHeight();
    int adv, offsetY = bounds.y + PADDING + hgt, offsetX = PADDING;
    g2.setFont(effectiveFont);

    for (final String text2 : texts) {
      final String currentText = text2 + " ";
      adv = metrics.stringWidth(currentText);

      if (offsetX + adv > bounds.width - PADDING * 2) {
        offsetY += hgt; // new line
        offsetX = PADDING;
      }
      g2.drawString(currentText, bounds.x + offsetX, offsetY);
      offsetX += adv;
    }
  }

  @Override
  public Rectangle getBounds() {
    if (bounds == null) bounds = new Rectangle();

    return new Rectangle(bounds);
  }

  /**
   * Get the text in the note.
   *
   * @return the text in the note
   */
  public String getText() {
    return text;
  }

  @Override
  public String getFullString() {
    return getText();
  }

  @Override
  public void gMouseClicked(MouseEvent e) {
    super.gMouseClicked(e);

    if (e.getClickCount() == 2) {
      final UserInputDialog ecd = new UserInputDialog(text.equals(DEFAULT_TEXT) ? "" : text,
                                                      "Slyum - Commentary editor", "Edit commentary : ");
      ecd.setVisible(true);

      if (ecd.isAccepted())

        setText(ecd.getText());
    }
  }

  /**
   * Calls by constructor for init the note.
   *
   * @param text the text of the note
   */
  private void init(String text) {
    this.text = text;

    setColor(EntityView.getBasicColor());
    popupMenu.addSeparator();
    final JMenuItem item = new JMenuItem("Delete commentary",
                                         PersonalizedIcon.createImageIcon("delete.png"));
    item.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        delete();
      }
    });

    parent.selectOnly(this);
    popupMenu.add(item);

    pushBufferCreation();
  }

  @Override
  protected void pushBufferCreation() {

    Change.push(new BufferCreation(false, this));
    Change.push(new BufferCreation(true, this));
  }

  @Override
  public boolean isAtPosition(Point position) {
    return getBounds().contains(position);
  }

  @Override
  public void paintComponent(Graphics2D g2) {
    if (ghost.isEmpty()) computeWidth(g2);

    drawNote(g2, getBounds(), 15, getColor());
    drawText(g2);
    drawSelectedEffect(g2);

    final Rectangle bounds = getBounds();

    if (!pictureMode && isSelected()) {
      final BasicStroke borderStroke = new BasicStroke(1.0f,
                                                       BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
                                                       new float[] {2.0f}, 0.0f);

      g2.setColor(Color.DARK_GRAY);
      g2.setStroke(borderStroke);

      g2.drawRect(bounds.x - 2, bounds.y - 2, bounds.width + 4,
                  bounds.height + 4);
      g2.drawRect(bounds.x + 2, bounds.y + 2, bounds.width - 4,
                  bounds.height - 4);
    }

    if (!pictureMode && isHighlight()) {

      g2.setStroke(new BasicStroke());

      g2.setColor(new Color(76, 175, 80, 150));
      g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

      g2.setColor(new Color(76, 175, 80));
      g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
  }

  @Override
  public void repaint() {
    parent.getScene().repaint(getBounds());
  }

  @Override
  public void restore() {
    super.restore();
    parent.addNotes(this);
    repaint();
  }

  @Override
  public void setBounds(Rectangle bounds) {
    this.bounds = bounds;

    // Move graphics elements associed with this component
    leftMovableSquare.setBounds(computeLocationResizer(0));
    rightMovableSquare.setBounds(computeLocationResizer(bounds.width));

    setChanged();
    notifyObservers();

    repaint();
  }

  /**
   * Set the text of the note.
   *
   * @param text the text of the note
   */
  public void setText(String text) {
    Change.push(new BufferNote(this));
    this.text = text;
    Change.push(new BufferNote(this));

    repaint();
  }

  @Override
  public void setSelected(boolean selected) {
    super.setSelected(selected);

    NoteProperties.getInstance().update(this,
                                        selected ? UpdateMessage.SELECT : UpdateMessage.UNSELECT);
  }

  @Override
  public String getXmlTagName() {
    return "note";
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element note = doc.createElement(getXmlTagName());
    note.setAttribute("content", text);
    note.setAttribute("color", String.valueOf(getColor().getRGB()));
    note.appendChild(Utility.boundsToXmlElement(doc, getBounds(),
                                                "noteGeometry"));

    for (LineView lv : parent.getLinesViewAssociedWith(this)) {
      Element noteLine;
      int id = -1;

      // Recherche du composant UML associé.
      IDiagramComponent associedComponent = lv.getLastPoint()
                                              .getAssociedComponentView().getAssociatedComponent();
      if (associedComponent != null) {
        id = associedComponent.getId();
      } else {
        associedComponent = lv.getFirstPoint().getAssociedComponentView()
                              .getAssociatedComponent();
        if (associedComponent != null) id = associedComponent.getId();
      }

      noteLine = doc.createElement("noteLine");
      noteLine.setAttribute("relationId", String.valueOf(id));
      noteLine.setAttribute("color", String.valueOf(lv.getColor().getRGB()));

      for (RelationGrip grip : lv.getPoints()) {
        Point pt = grip.getAnchor();
        pt.translate(1, 1);
        noteLine.appendChild(Utility.pointToXmlElement(pt, "point", doc));
      }

      note.appendChild(noteLine);
    }

    return note;
  }

  @Override
  public Color getDefaultColor() {
    return EntityView.getBasicColor();
  }

}
