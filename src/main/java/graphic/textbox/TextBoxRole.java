package graphic.textbox;

import classDiagram.IDiagramComponent;
import classDiagram.components.Visibility;
import classDiagram.relationships.Role;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.relations.AssociationView;
import graphic.relations.LineView;
import graphic.relations.MagneticGrip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Observable;

/**
 * A TextBox is a graphic component from Slyum containing a String. The particularity of a TextBox is it text can be
 * changed by double-clinking on it.
 * <p>
 * A TextBoxLabel add the possibility to be moved by user in dragging mouse on it. The TextBoxLabel's position is
 * relative to it's anchor. When the anchor moves, this label moves too to keep the dimension of the anchor always the
 * same.
 * <p>
 * A TextBox label have an associated graphic component and draw a line between the label and the component when mouse
 * hover the label. Points of the line is compute in this way:
 * <p>
 * first point : it's the middle bounds of the label second point : it's compute by calling the computeAnchor() abstract
 * method.
 * <p>
 * A TextBoxMultiplicity is associated with a MagneticGrip. The computeAnchor() method return the position of the grip
 * like a second point.
 * <p>
 * When text is edited, the role associated changed and notifiy it's listener. This TextBox parse the text into a role.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class TextBoxRole extends TextBoxLabel {

  /**
   * Convert given string into the given role. If String can't be parsing, given role will be inchanged.
   *
   * @param text the string to convert.
   * @param role the {@link Role}.
   */
  public static void convertStringToRole(final String text, final Role role) {
    if (text.length() >= 1) {
      final String visCar = text.substring(0, 1);

      final Visibility visibility = Visibility.getVisibility(visCar
                                                                 .toCharArray()[0]);

      final String name = text.substring(1, text.length());

      if (visibility == null)

        role.setName(visCar + name);

      else {
        if (name != null && !name.isEmpty()) role.setName(name.trim());

        role.setVisibility(visibility);
      }
    } else
      role.setName(null);
  }

  /**
   * Get a String from the role. (('+' | '-' | '#' | '~')' '{Role_Name})
   *
   * @param role the role to convert in String
   *
   * @return the role converting in String
   */
  private static String roleToString(Role role) {
    return role.toString();
  }

  private final MagneticGrip grip;

  private final Role role;

  private TextBoxMultiplicity tbm;

  /**
   * Create a new TextBoxRole associated with a role and draw a line through the MagneticGrip.
   *
   * @param parent the graphic view
   * @param role the role associated
   * @param grip the grip near the role
   */
  public TextBoxRole(GraphicView parent, Role role, MagneticGrip grip) {
    super(parent, roleToString(role));

    if (grip == null) throw new IllegalArgumentException("grip is null");

    if (parent.searchAssociedComponent(role) != null)
      throw new IllegalArgumentException("This role is already present in this view.");

    this.role = role;
    role.addObserver(this);

    this.grip = grip;
    grip.addObserver(this);

    parent.addOthersComponents(tbm = new TextBoxMultiplicity(parent, role
        .getMultiplicity(), grip));

    reinitializeLocation();
  }

  @Override
  public void reinitializeLocation() {

    final Rectangle classBounds = grip.getAssociedComponentView().getBounds();
    final Point gripAnchor = grip.getAnchor();

    deplacement = new Point(); // (0, 0)

    // Permet d'attendre que la taille de la textbox soit définie.
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {

        if (gripAnchor.x <= classBounds.x)
          deplacement.x -= getBounds().width + TextBox.MARGE;
        else
          deplacement.x += TextBox.MARGE;

        if (gripAnchor.y <= classBounds.y)
          deplacement.y -= getBounds().height + TextBox.MARGE;
        else
          deplacement.y += TextBox.MARGE;

        computeLabelPosition();
      }
    });

    tbm.reinitializeLocation();
  }

  @Override
  protected Point computeAnchor() {
    return grip.getAnchor();
  }

  @Override
  public void delete() {
    super.delete();

    parent.removeComponent(this);
    tbm.delete();
  }

  @Override
  public IDiagramComponent getAssociatedComponent() {
    return role;
  }

  @Override
  public String getText() {
    return role.toString();
  }

  public TextBoxMultiplicity getTextBoxMultiplicity() {
    return tbm;
  }

  public Role getRole() {
    return role;
  }

  private AssociationView getAssociationView() {
    return (AssociationView) parent.searchAssociedComponent(role
                                                                .getAssociation());
  }

  private LineView getLinkedAssociationView() {
    return grip.getRelation();
  }

  @Override
  public void gMouseClicked(MouseEvent e) {
    super.gMouseClicked(e);
    GraphicComponent c = parent.searchAssociedComponent(role.getAssociation());

    if (!GraphicView.isAddToSelection(e)) {
      parent.unselectAll();
      c.setSelected(true);
    } else {
      c.setSelected(!c.isSelected());
    }
  }

  @Override
  public void gMousePressed(MouseEvent e) {
    super.gMousePressed(e);
    maybeShowPopup(e, getLinkedAssociationView());
  }

  @Override
  public void gMouseReleased(MouseEvent e) {
    super.gMouseReleased(e);
    maybeShowPopup(e, getLinkedAssociationView());
  }

  @Override
  public void setMouseHover(boolean hover) {
    super.setMouseHover(hover);
    tbm.setMouseHover(hover);
  }

  @Override
  public void setText(String text) {
    convertStringToRole(text, role);
    super.setText(role.toString());
    setChanged();
    role.notifyObservers();
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    super.update(arg0, arg1);

    super.setText(role.toString());
  }

  @Override
  public void restore() {
    super.restore();
    tbm.restore();
  }

  @Override
  public void setSelected(boolean selected) {
    super.setSelected(selected);
    tbm.setSelected(selected);
  }

}
