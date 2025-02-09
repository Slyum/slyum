package graphic.relations;

import classDiagram.relationships.Binary;
import classDiagram.relationships.Role;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.textbox.TextBoxRole;

import java.awt.*;
import java.util.LinkedList;

/**
 * The LineView class represent a collection of lines making a link between two GraphicComponent. When it creates, the
 * LineView have one single line between the two GraphicComponent. By clicking on the line, the user can personnalize
 * the LineView by adding new grips. When drawing, the LineView will draw a segment between each grips. Grips are
 * movable and a LineView have two special grips; MagneticGrip. These grips are associated with a GraphicComponent and
 * can't be placed elsewhere.
 * <p>
 * A RelationView have an associated UML component.
 * <p>
 * An AssociationView is associated with an association UML component.
 * <p>
 * A BinaryView is associated with an Binary UML component.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class BinaryView extends AssociationView {
  /**
   * Create a new BinaryView between source and target.
   *
   * @param parent the graphic view
   * @param source the entity source
   * @param target the entity target
   * @param binary the binary UML
   * @param posSource the position for put the first MagneticGrip
   * @param posTarget the position for put the last MagneticGrip
   * @param checkRecursivity check if the relation is on itself
   */
  public BinaryView(GraphicView parent, EntityView source, EntityView target,
                    Binary binary, Point posSource, Point posTarget,
                    boolean checkRecursivity) {
    super(parent, source, target, binary, posSource, posTarget,
          checkRecursivity);

    final LinkedList<Role> roles = binary.getRoles();

    TextBoxRole tb = new TextBoxRole(parent, roles.getFirst(), getFirstPoint());
    tbRoles.add(tb);
    parent.addOthersComponents(tb);

    tb = new TextBoxRole(parent, roles.getLast(), getLastPoint());
    tbRoles.add(tb);
    parent.addOthersComponents(tb);
  }

  @Override
  public void restore() {
    super.restore();

    if (this.getClass().equals(BinaryView.class))
      parent.getClassDiagram().addBinary((Binary) getAssociatedComponent(), false);

    repaint();
  }

}
