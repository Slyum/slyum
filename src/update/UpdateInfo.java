package update;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import swing.FlatButton;
import swing.PanelClassDiagram;
import swing.PropertyLoader;
import swing.SScrollPane;
import swing.Slyum;
import utility.SMessageDialog;
import utility.TagDownload;
import utility.Utility;

/**
 *
 * @author MiserezDavid
 */
public class UpdateInfo extends JDialog {
  
  public static boolean isUpdateAvailable() {
    try {
      return getIntVersion(Updater.getLatestVersion()) > getIntVersion(Slyum.VERSION);
    } catch (Exception ex) {
      Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }
  
  public static void getNewUpdate() {
    getNewUpdate(false);
  }
  
  public static void getNewUpdate(boolean askForDisableCheckingUpdate) {
    try {
      if (isUpdateAvailable())
        new UpdateInfo(Updater.getWhatsNew(), askForDisableCheckingUpdate);
    } catch (Exception ex) {
      Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public static void getPatchNote() {
    try {
      new UpdateInfo(Updater.getWhatsNew());
    } catch (Exception ex) {
      Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static final String updaterDirectory = Slyum.getPathAppDir();
  private static final String updaterFile = 
     updaterDirectory + Slyum.FILE_SEPARATOR +"SlyumUpdater.jar";
  private static final String tagUpdater = "[updater]";
    
  private JEditorPane infoPane;
  private JScrollPane scp;
  private JButton ok;
  private JButton cancel;
  private JPanel pan1;
  private JPanel pan2;
  private Boolean askForDisableCheckingUpdate = false;
  private Boolean displayOkButton = true;

  private UpdateInfo(String info) {
    super(Slyum.getInstance(), true);
    displayOkButton = false;
    initComponents();
    infoPane.setText(info);
    setVisible(true);
  }

  private UpdateInfo(String info, boolean askForDisableCheckingUpdate) {
    super(Slyum.getInstance(), true);
    initComponents();
    infoPane.setText(info);
    this.askForDisableCheckingUpdate = askForDisableCheckingUpdate;
    setVisible(true);
  }

  private void initComponents() {

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Slyum - New update available");
    JComponent glassPane = new JComponent() {
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        final URL imageLogoURL = Slyum.class.getResource(Slyum.ICON_PATH
                + "logo148.png");
        Rectangle bounds = getBounds();
        BufferedImage imgLogo;
        Utility.setRenderQuality(g2);
        try {
          imgLogo = ImageIO.read(imageLogoURL);
          Point imgLocation = new Point(bounds.x + bounds.width
                  - imgLogo.getWidth() - 40, bounds.y + 20);
          g2.drawImage(imgLogo, imgLocation.x, imgLocation.y, this);
        } catch (final IOException e) {
          System.err.println("Unable to get Slyum's logo for updater.");
        }
      }
    };
    setGlassPane(glassPane);
    glassPane.setVisible(true);
    
    pan1 = new JPanel();
    pan1.setLayout(new BorderLayout());

    pan2 = new JPanel();
    pan2.setLayout(new FlowLayout());
    
    HTMLEditorKit kit = new HTMLEditorKit();
    infoPane = new JEditorPane();
    infoPane.setEditable(false);
    infoPane.setEditorKit(kit);
    infoPane.setDocument(kit.createDefaultDocument());
    infoPane.setContentType("text/html; charset=utf-8");
    
    StyleSheet s = kit.getStyleSheet();
    s.addRule("body {color:#444444; margin: 10px 20px; font-family: Ubuntu;}");
    s.addRule("h1 {color:#2A60FF; margin-left: 40px; font-size: 1.5em;}");
    s.addRule("h2 {color:#444444; font-size: 1.2em;}");
    

    scp = new SScrollPane(infoPane);
    scp.setBorder(null);

    ok = new FlatButton("Update");
    ok.addActionListener( new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        update();
      }
    });

    cancel = new FlatButton("Close");
    cancel.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        if (askForDisableCheckingUpdate && 
            JOptionPane.showConfirmDialog(
                UpdateInfo.this, 
                "Would you continue to check for updates at launch of Slyum?", 
                "Slyum",
                JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
          setUpdateCheckedAtLaunch(false);
        UpdateInfo.this.dispose();
      }
    });
    
    if (displayOkButton)
      pan2.add(ok);
    pan2.add(cancel);
    pan1.add(pan2, BorderLayout.SOUTH);
    pan1.add(scp, BorderLayout.CENTER);
    add(pan1);
    pack();
    setSize(750, 600);
    setLocationRelativeTo(Slyum.getInstance());
  }
  
  private void initializeUpdater() throws MalformedURLException, Exception {
    File f = new File(updaterFile);
    if (f.exists())
      return;
    
    if (!new File(updaterDirectory).exists())
      new File(updaterDirectory).mkdirs();
    
    URL website = new URL(TagDownload.getContentTag(tagUpdater));
    try (ReadableByteChannel rbc = Channels.newChannel(website.openStream()); 
         FileOutputStream fos = new FileOutputStream(updaterFile)) {
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }
  }
  
  private void update() {
    // Check if a current project is open and unsaved.
    if (Slyum.getInstance() != null && Slyum.getInstance().isVisible()) {
      if (JOptionPane.showConfirmDialog(
          UpdateInfo.this, 
          "Slyum need to be closed before installing new update.\n"+
          "Would you like to continue? Your project will be saved.", 
          "Slyum",
          JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
        return;
      PanelClassDiagram.getInstance().saveToXML(false);
    }
    
    try {
      initializeUpdater();
    } catch (Exception ex) {
      Logger.getLogger(UpdateInfo.class.getName()).log(Level.SEVERE, null, ex);
      SMessageDialog.showErrorMessage("Unable to get the updater.");
    }
    String[] run = {"java","-jar", updaterFile};
    try {
      Runtime.getRuntime().exec(run);
    } catch (Exception ex) {
      ex.printStackTrace();
      SMessageDialog.showErrorMessage(
          "An error occured when trying to update Slyum.");
    }
    System.exit(0);
  }
  
  private static int getIntVersion(String version) {
    return Integer.parseInt(version.replace(".", ""));
  }

  public static boolean isUpdateCheckedAtLaunch() {
    final String prop = PropertyLoader.getInstance().getProperties()
            .getProperty(PropertyLoader.CHECK_UPDATE_AT_LAUNCH);
    boolean enable = true;

    if (prop != null) 
      enable = Boolean.parseBoolean(prop);
    return enable;
  }
  
  public static void setUpdateCheckedAtLaunch(boolean enable) {
    Properties properties = PropertyLoader.getInstance().getProperties();
    properties.put(
        PropertyLoader.CHECK_UPDATE_AT_LAUNCH, 
        String.valueOf(enable));
  }
}

