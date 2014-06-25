package swing;

import swing.slyumCustomizedComponents.SSeparator;
import swing.slyumCustomizedComponents.SToolBar;
import swing.slyumCustomizedComponents.SToolBarButton;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import swing.PanelClassDiagram;
import swing.slyumCustomizedComponents.SButton;
import swing.Slyum;
import swing.slyumCustomizedComponents.SSeparator;
import swing.slyumCustomizedComponents.SToolBar;
import swing.slyumCustomizedComponents.SToolBarButton;

import utility.PersonalizedIcon;
import utility.Utility;

public class SPanelFileComponent extends SToolBar implements ActionListener {
  private static final String TT_NEW_PROJECT = "New project "
          + Utility.keystrokeToString(Slyum.KEY_NEW_PROJECT);
  private static final String TT_OPEN = "Open "
          + Utility.keystrokeToString(Slyum.KEY_OPEN_PROJECT);
  private static final String TT_SAVE = "Save "
          + Utility.keystrokeToString(Slyum.KEY_SAVE);
  private static final String TT_EXPORT = "Export image "
          + Utility.keystrokeToString(Slyum.KEY_EXPORT);
  private static final String TT_CLIPBOARD = "Clipboard "
          + Utility.keystrokeToString(Slyum.KEY_KLIPPER);
  private static final String TT_PRINT = "Print "
          + Utility.keystrokeToString(Slyum.KEY_PRINT);

  private SButton newProject, open, save, export, klipper, print;

  private static SPanelFileComponent instance;

  public static SPanelFileComponent getInstance() {
    if (instance == null) instance = new SPanelFileComponent();

    return instance;
  }

  private SPanelFileComponent() {
    add(newProject = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "new.png"),
            Slyum.ACTION_NEW_PROJECT, Color.BLUE, TT_NEW_PROJECT));
    add(open = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "open.png"),
            Slyum.ACTION_OPEN, Color.BLUE, TT_OPEN));
    add(save = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "save.png"),
            Slyum.ACTION_SAVE, Color.BLUE, TT_SAVE));
    add(new SSeparator());
    add(export = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "export.png"),
            Slyum.ACTION_EXPORT, Color.BLUE, TT_EXPORT));
    add(klipper = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "klipper.png"),
            Slyum.ACTION_KLIPPER, Color.BLUE, TT_CLIPBOARD));
    add(print = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "print.png"),
            Slyum.ACTION_PRINT, Color.BLUE, TT_PRINT));
  }

  private SButton createSButton(ImageIcon ii, String a, Color c, String tt) {
    return new SToolBarButton(ii, a, c, tt, this);
  }

  public SButton getBtnNewProject() {
    return newProject;
  }

  public SButton getBtnOpen() {
    return open;
  }

  public SButton getBtnSave() {
    return save;
  }

  public SButton getBtnExport() {
    return export;
  }

  public SButton getBtnKlipper() {
    return klipper;
  }

  public SButton getBtnPrint() {
    return print;
  }
}
