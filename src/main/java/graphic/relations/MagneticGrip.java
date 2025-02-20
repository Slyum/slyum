package graphic.relations;

import change.Change;
import graphic.GraphicComponent;
import graphic.GraphicView;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

/**
 * The relationGrip is a grip who customize a LineView. The LineView uses RelationGrip for draw segments between each
 * RelationGrip.
 * <p>
 * The MagneticGrip is a RelationGrip that is associated with a GraphicComponent. The MagneticGrip can be moved but it
 * compute its location by calling the computeAnchorLocation() from the GraphicComponent that is assigned to the grip
 * each time the setAnchor() si called. A LineView have two MagneticGrip for each extremity.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class MagneticGrip extends RelationGrip implements Observer {
  private GraphicComponent component;
  private boolean magnetism = true;

  private Point preferredAnchor;

  /**
   * /** Create a new RelationGrip associate with the given LineView.
   *
   * @param parent the graphic view
   * @param relation the LineView associated.
   * @param component the graphic component associated.
   * @param first need to compute the first location of the anchor. Give the point were you want the grip is.
   * @param next need to compute the first location of the anchor. Give the point were is the next grip on the
   * relation.
   */
  public MagneticGrip(GraphicView parent, LineView relation,
                      GraphicComponent component, Point first, Point next) {
    super(parent, relation);

    if (component == null)
      throw new IllegalArgumentException("component is null");

    Rectangle boundsRect = component.getBounds();

    this.component = component;
    preferredAnchor = new Point(first.x - boundsRect.x, first.y - boundsRect.y);
    super.setAnchor(component.computeAnchorLocation(first, next));
    component.addObserver(this);
    setVisible(false);
  }

  /**
   * Get the GraphicComponent associated with this MagnieticGrip.
   *
   * @return the GraphicComponent that magnetize the grip
   */
  public GraphicComponent getAssociedComponentView() {
    return component;
  }

  public void setAssociedComponentView(GraphicComponent component) {
    this.component.deleteObserver(this);
    component.addObserver(this);
    this.component = component;
    setChanged();
    notifyObservers();
  }

  @Override
  public void gMouseDragged(MouseEvent e) {
    super.gMouseDragged(e);
    magnetism = false;
  }

  @Override
  public void gMouseReleased(MouseEvent e) {
    // Récupération de l'élément sur lequel l'utilisateur a "lâché" le grip.
    GraphicComponent onMouseComponent = parent.getDiagramElementAtPosition(
        e.getPoint(), relation);

    magnetism = true;

    // L'utilisateur a "lâché" le grip sur le GraphicView.
    if (onMouseComponent == null) onMouseComponent = parent;

    // Est-ce que le composant cible a changé?
    if (!component.equals(onMouseComponent))
      relation.relationChanged(this, onMouseComponent);

    setAnchor(e.getPoint());
    relation.smoothLines();
    relation.searchUselessAnchor(this);
    pushBufferChangeMouseReleased(e);
    Change.stopRecord();
    maybeShowPopup(e, relation);
    notifyObservers();
    relation.reinitializeTextBoxesLocation();
  }

  @Override
  public void setAnchor(Point anchor) {
    if (magnetism) {
      Rectangle bounds = component.getBounds();
      RelationGrip nearGrip = relation.getNearestGrip(this);
      preferredAnchor = new Point(anchor.x - bounds.x, anchor.y - bounds.y);
      super.setAnchor(component.computeAnchorLocation(anchor, nearGrip.getAnchor()));
    } else {
      super.setAnchor(anchor);
    }
  }

  @Override
  protected Point adjustOnGrid(Point pt) {
    if (magnetism) return pt;
    return super.adjustOnGrid(pt);
  }

  @Override
  public void restore() {
    parent.addOthersComponents(this);
  }

  @Override
  public void delete() {
    parent.removeComponent(this);
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    if (!magnetism) return;

    Rectangle boundsRect = component.getBounds();
    Point absolutePrefLoc = new Point(preferredAnchor.x + boundsRect.x,
                                      preferredAnchor.y + boundsRect.y);
    setAnchor(absolutePrefLoc);

    arg0.deleteObserver(this);
    notifyObservers();
    arg0.addObserver(this);
  }

  public boolean isMagnetism() {
    return magnetism;
  }

  public void setMagnetism(boolean magnetism) {
    this.magnetism = magnetism;
    setChanged();
  }

  public Point getPreferredAnchor() {
    return preferredAnchor;
  }

  public void setPreferredAnchor(Point preferredAnchor) {
    this.preferredAnchor = preferredAnchor;
    setChanged();
  }

}
