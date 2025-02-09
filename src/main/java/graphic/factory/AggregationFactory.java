package graphic.factory;

import change.BufferDeepCreation;
import change.Change;
import classDiagram.relationships.Aggregation;
import classDiagram.relationships.Association.NavigateDirection;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.relations.AggregationView;
import swing.SPanelDiagramComponent;

import java.awt.*;

/**
 * AggregationFactory allows to create a new aggregation view associated with a new association UML. Give this factory
 * at the graphic view using the method initNewComponent() for initialize a new factory. Next, graphic view will use the
 * factory to allow creation of a new component, according to the specificity of the factory.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class AggregationFactory extends RelationFactory {

  /**
   * Create a new factory allowing the creation of an aggregation.
   *
   * @param parent the graphic view
   */
  public AggregationFactory(final GraphicView parent) {
    super(parent);

    GraphicView.setButtonFactory(SPanelDiagramComponent.getInstance()
                                                       .getBtnAggregation());
  }

  @Override
  public GraphicComponent create() {
    if (isFirstComponentValid() && componentMouseReleased instanceof EntityView) {
      final EntityView source = (EntityView) componentMousePressed;
      final EntityView target = (EntityView) componentMouseReleased;

      final Aggregation aggregation = new Aggregation(source.getComponent(),
                                                      target.getComponent(), NavigateDirection.BIDIRECTIONAL);
      final AggregationView a = new AggregationView(parent, source, target,
                                                    aggregation, mousePressed, mouseReleased, true);

      parent.addLineView(a);
      classDiagram.addAggregation(aggregation);

      Change.push(new BufferDeepCreation(false, aggregation));
      Change.push(new BufferDeepCreation(true, aggregation));

      parent.unselectAll();
      a.setSelected(true);

      return a;
    }

    repaint();
    return null;
  }

  @Override
  protected boolean isFirstComponentValid() {
    return componentMousePressed instanceof EntityView;
  }

  @Override
  protected void drawExtremity(Graphics2D g2) {
    Point p = points.size() < 2 ? mouseLocation : points.get(1);
    AggregationView.paintExtremity(
        g2, p, points.get(0), Color.WHITE,
        Color.DARK_GRAY);
  }

}
