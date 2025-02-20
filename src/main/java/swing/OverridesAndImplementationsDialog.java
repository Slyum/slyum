package swing;

import classDiagram.components.Entity;
import classDiagram.components.Method;
import classDiagram.components.SimpleEntity;
import swing.slyumCustomizedComponents.SList;
import utility.Utility;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

public class OverridesAndImplementationsDialog extends JDialog {

  public class CheckableItem {
    private boolean isSelected;

    private final Method m;

    public CheckableItem(Method m) {
      this.m = m;
      isSelected = false;
    }

    public Method getMethod() {
      return m;
    }

    public boolean isSelected() {
      return isSelected;
    }

    public void setSelected(boolean b) {
      isSelected = b;
    }

    @Override
    public String toString() {
      return m.getStringFromMethod(Method.ParametersViewStyle.TYPE_AND_NAME);
    }

  }

  class CheckListRenderer extends JCheckBox implements ListCellRenderer<Object> {

    public CheckListRenderer() {
      setBackground(UIManager.getColor("List.textBackground"));
      setForeground(UIManager.getColor("List.textForeground"));
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index, boolean isSelected, boolean hasFocus) {
      setEnabled(list.isEnabled());
      setSelected(((CheckableItem) value).isSelected());
      setFont(list.getFont());
      setText(value.toString());
      return this;
    }

  }

  private static final long serialVersionUID = 2174601193484988913L;
  private boolean accepted = false;
  private final JPanel contentPanel = new JPanel();

  Vector<CheckableItem> items = new Vector<>();

  private final SimpleEntity parent, child;

  /**
   * Create the dialog.
   *
   * @param parent The parent entity of inheritance.
   * @param child The child entity of inheritance.
   */
  public OverridesAndImplementationsDialog(SimpleEntity parent,
                                           SimpleEntity child) {
    Utility.setRootPaneActionOnEsc(getRootPane(), new AbstractAction() {

      private static final long serialVersionUID = -9137055482704631902L;

      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });

    this.parent = parent;
    this.child = child;
    setTitle("Overrides & Implementations");
    setModalityType(ModalityType.APPLICATION_MODAL);
    setBounds(100, 100, 244, 332);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    final GridBagLayout gbl_contentPanel = new GridBagLayout();
    gbl_contentPanel.columnWidths = new int[] {0, 0};
    gbl_contentPanel.rowHeights = new int[] {0, 0};
    gbl_contentPanel.columnWeights = new double[] {1.0, Double.MIN_VALUE};
    gbl_contentPanel.rowWeights = new double[] {1.0, Double.MIN_VALUE};
    contentPanel.setLayout(gbl_contentPanel);
    {
      final SList<?> list = new SList<>(createData());
      final GridBagConstraints gbc_scrollPane = new GridBagConstraints();
      gbc_scrollPane.fill = GridBagConstraints.BOTH;
      gbc_scrollPane.gridx = 0;
      gbc_scrollPane.gridy = 0;
      contentPanel.add(list.getScrollPane(), gbc_scrollPane);
      {
        list.setBorder(null);
        list.setCellRenderer(new CheckListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            final int index = list.locationToIndex(e.getPoint());
            final CheckableItem item = (CheckableItem) list.getModel()
                                                           .getElementAt(index);
            item.setSelected(!item.isSelected());
            final Rectangle rect = list.getCellBounds(index, index);
            list.repaint(rect);
          }
        });
      }
    }
    {
      final JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            accepted = true;
            setVisible(false);
          }
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            accepted = false;
            setVisible(false);
          }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }

    setLocationRelativeTo(Slyum.getInstance());
    setVisible(true);
  }

  private Vector<CheckableItem> createData() {
    items = new Vector<CheckableItem>();

    for (final Entity e : parent.getAllParents()) {

      SimpleEntity se = (SimpleEntity) e;

      out:
      for (final Method m : se.getMethods()) {
        for (final CheckableItem c : items)
          if (c.getMethod().equals(m)) continue out;

        if (m.isStatic()) continue;

        final CheckableItem ci = new CheckableItem(m);
        ci.setSelected(child.getMethods().contains(m));
        items.add(ci);
      }
    }

    return items;
  }

  public Vector<CheckableItem> getCheckableItems() {
    return items;
  }

  public boolean isAccepted() {
    return accepted;
  }

}
