/*
 * Copyright (C) 2019 root
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bfield;

import bfield.data.*;

import bfield.scene.MainWndController;
import bfield.scene.dialogs.NewBattleDialogController;
import bfield.scene.dialogs.UnitSelectorController;
import bfield.scene.edit.EditUnitController;
import javafx.scene.image.Image;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.xml.bind.JAXB;

/**
 *
 * @author root
 */
public class Application extends javafx.application.Application {

  /**
   * Preferences static values
   */
  public static final String P_LASTFOLDER = "lastFolder";
  
  private java.util.Properties unitIconDatabase;
  private java.util.Map<String, XMLFactory> rulesCache;
  private MainWndController rootController;
  private Stage stage;
  private static Application app;
  
  private Properties preferences;
  
  public static Application getApp() {
    return app;
  }

  public void quit() {
    rootController.closeAllTabs();
    try {
      getPreferences().storeToXML(new FileOutputStream(new File
      (System.getProperty("user.dir")+File.separator+"preferences.xml")),
              "Battlefield preferences");
    } catch (IOException ioe) {
      System.err.println("Could not save preferences.");
    }
  }
  
  public Properties getPreferences() {
    if (preferences == null) {
      File filePref = new File (System.getProperty("user.dir")
              + File.separator+"preferences.xml");
      preferences = new Properties();
      
      try {
        preferences.loadFromXML(new FileInputStream(filePref));
      } catch(IOException fnfe) {
        //no big deal, just keep new preference file
        System.out.println("Warning: no preferences found.");
      }
    }
    return preferences;
  }
  
  public static void main(String[] args) throws Exception {
    launch(args);
  }

  public Stage getStage() {
    return stage;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Application.app = this;
    stage = primaryStage;

    primaryStage.setOnCloseRequest( (we) -> {
      quit();
    });
    
    System.out.println("Loading rules...");
    rulesCache = new java.util.HashMap();

    final Path pDir = Paths.get(System.getProperty("user.dir")
            + File.separator + "rules");

    try {
      Files.walk(pDir, 1).filter((p) -> p.toFile().isDirectory()
              && p != pDir).forEach((subDir) -> {
        System.out.print("Loading " + subDir.toString() + " ...");
        try {
          XMLFactory factory = XMLFactory.loadDataSet(subDir.toFile());
          rulesCache.put(factory.getName(), factory);
          System.out.println(factory.getUnitFactory().getUnits().size() + " units found.");
        } catch (IOException ex) {
          showMessage("Loading error", "Failure in rules loading.",
                  "Ruleset " + subDir.getFileName() + " failed.", ex);
          System.out.println("failed.");
        }

      });
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }

    if (rulesCache.isEmpty()) {
      showMessage("No rules found!", "Could not find any rules.", "You need a rules"
              + " pack inside the \"rules\" subfolder.", null);
    }

    try {
      unitIconDatabase = new Properties();
      unitIconDatabase.loadFromXML(new java.io.FileInputStream(new File("./res/icons.xml")));
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.exit(2);
    }
    
    
   
    //Main scene
    FXMLLoader loader = new FXMLLoader(getClass().getResource("scene/MainWnd.fxml"));
    //FXMLLoader loader = new FXMLLoader(getClass().getResource("scene/dialogs/NewRulesetDialog.fxml"));
    javafx.scene.Parent obj = loader.load();
    this.rootController = loader.<MainWndController>getController();
    javafx.scene.Scene scene = new javafx.scene.Scene(obj);
    
    //Css & Launch
    scene.getStylesheets().add(getClass().getResource("scene/content.css").toExternalForm());
    scene.getStylesheets().add(getClass().getResource("scene/icons.css").toExternalForm());
    scene.getStylesheets().add(getClass().getResource("scene/smallcell.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.setTitle("Battlefield!");
    primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
    primaryStage.show();
  }


  public static void showMessage(String title, String header, String content,
          Throwable stackTrace) {

    Alert alert;
    if (stackTrace == null) {
      alert = new Alert(Alert.AlertType.INFORMATION);
    } else {
      alert = new Alert(Alert.AlertType.ERROR);
    }

    alert.setTitle(title);
    alert.setHeaderText(header);
    if (content != null) {
      alert.setContentText(content);
    }
    if (stackTrace != null) {
      TextArea ta = new TextArea();
      StringWriter swr = new StringWriter();
      stackTrace.printStackTrace(new PrintWriter(swr));
      ta.setText(stackTrace.toString() + "\n" + swr.toString());
      ta.setPrefWidth(400);
      ta.setPrefHeight(400);
      alert.getDialogPane().setExpandableContent(ta);
    }
    alert.getDialogPane().setPrefWidth(400);
    alert.getDialogPane().setPrefHeight(400);
    
    alert.showAndWait();
  }

  public java.util.Properties getIconManager() {
    return unitIconDatabase;
  }
  
  public String getAppropriateIcon(String unitClass) {
    unitClass = unitClass.replaceAll("[\\+\\-]", "");
    return (String) unitIconDatabase.getOrDefault(unitClass, null);
  }

  public Map<String, XMLFactory> getFactories() {
    return rulesCache;
  }
  
  public BField actionNewBattle() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("scene/dialogs/NewBattleDialog.fxml"));
    Pane pane;
    try {
      pane = loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    NewBattleDialogController cont = loader.<NewBattleDialogController>getController();

    Alert alert = new Alert(Alert.AlertType.NONE);
    alert.getDialogPane().setContent(pane);
    alert.setTitle("New battle");
    alert.setHeaderText("New battle generation");
    alert.getButtonTypes().add(ButtonType.OK);
    alert.getButtonTypes().add(ButtonType.CANCEL);
    alert.showAndWait();

    ButtonType result = alert.getResult();
    if (result == ButtonType.CANCEL) {
      return null;
    }

    BField newBattlefield = new BField();
    newBattlefield.setFactory(getFactories().get(cont.getRulesName()));
    Battle b = newBattlefield.getFactory().newBattle(
            cont.getHomeName(), cont.getAwayName(), cont.getBattleName()
    );

    newBattlefield.setBattle(b);
    return newBattlefield;
  }
  
  public BField actionLoadBattle(File file) {
    BField result = JAXB.unmarshal(file, BField.class);
    Battle battle = result.getBattle();
    
    for (Army a : battle.getArmies().values()) {
      a.setEnemy(battle.getArmy(a.getEnemyID()));
    }
    
   return result;
  }
    
  public Unit actionEditUnit(String title, Unit u) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("scene/edit/editUnit.fxml"));
    Pane pane;
    try {
      pane = loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    EditUnitController cont = loader.<EditUnitController>getController();
    cont.setData(u);
    
    Alert alert = new Alert(Alert.AlertType.NONE);
    
    alert.getDialogPane().setContent(pane);
    alert.setTitle(title);
    alert.setHeaderText("Edit unit data");
    alert.getButtonTypes().add(ButtonType.OK);
    alert.getButtonTypes().add(ButtonType.CANCEL);
    alert.getDialogPane().getStylesheets()
            .add(getClass().getResource("scene/content.css").toExternalForm());
    alert.getDialogPane().getStylesheets()
            .add(getClass().getResource("scene/icons.css").toExternalForm());
    alert.showAndWait();

    ButtonType result = alert.getResult();
    if (result == ButtonType.CANCEL) {
      return null;
    }
    
    Map<String,String> data = cont.getData();
    /*
    * TODO: add 2e support.
    */
    Unit newUnit = UnitBuilder.createD20Unit(data);
    return newUnit;
  }
  
  public java.util.Map.Entry<String,Unit> actionShowUnitList(boolean getOriginal) {
    
    FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "scene/dialogs/UnitSelectorDialog.fxml"));
    
    try {
      loader.load();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    
    Node root = loader.<Node>getRoot();
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Edit unit");
    alert.setHeaderText("Select unit to edit");
    alert.getDialogPane().setContent(root);
    alert.showAndWait();
    
    if (!alert.getResult().equals(ButtonType.OK))
      return null;  
    
    UnitSelectorController usdc = 
          loader.<UnitSelectorController> getController();

    final String RULESET = usdc.getRuleset(),
            SELECTION = usdc.getSelection();



    if (RULESET == null || SELECTION == null)
      return null;
    
    UnitFactory uf = Application.getApp().getFactories().get(RULESET)
                    .getUnitFactory();
    Unit unit = null;
    
    if (getOriginal) {
      for (Unit un : uf.getUnits())
        if (un.getName().equals(SELECTION)) {
          unit = un;
          break;
      }
      if (unit == null)
        throw new RuntimeException("could not retrieve unit " + SELECTION + " from data");
    } else {
      unit = uf.createUnit(SELECTION);
    }
      
    return java.util.Collections.<String,Unit>singletonMap(RULESET, 
              unit).entrySet().iterator().next();
  }
  
  /**
   * New Ruleset action
   * Creates a new rules directory with a new ruleset name
   * @param name name of the new ruleset - will be the directory's name
   * @param desc Description of the new ruleset
   * @param folder folder name of the new ruleset, if empty will be created
   * @param reference if null, wil be created from template dir, otherwise
   *  it will be created copying the #reference directory
   * @return String with the folder name
   * @throws java.io.IOException if something goes wrong while copying files
   */
  public String actionNewRuleset(String name, String desc, String folder, String reference) throws 
          IOException {
    
    if (getFactories().containsKey(name))
      throw new IOException("Ruleset named " + name + "already exists.");
    
    if (folder == null) {
      folder = name.replaceAll("[\\\\!\"#$%&()*+,./:;<=>?@\\[\\]^_{|}~]+","");
      folder = folder.substring(1, Math.min(127, folder.length()));
    }
    
    final String USERPATH = System.getProperty("user.dir");
    final String RULESPATH = USERPATH + File.separator + "rules";
    
    //Copy the new ruleset
    File targetDir = new File (RULESPATH + File.separator+folder);
    if (targetDir.exists())
      throw new IOException("Ruleset " + name + " already exist. Delete folder from "
              + " rules subfolder.");
    
    //Retrieve source Folder
    File sourceDir = (reference == null)
            ? new File(USERPATH + File.separator+"template")
            : getFactories().get(reference).getPath()
    ;
    
    //Copy all files
    /**
     * TODO:
     * As of now rules directories do not have subfolders.
     * If one day they get to do, another function must be
     * called to allow recursive copy.
     */
    Files.walk(sourceDir.toPath())
      .forEach(source -> {
              //System.out.println(source.toString());
      try {
            Files.copy(source, targetDir.toPath().
                      resolve(sourceDir.toPath().relativize(source))
            );
          } catch (IOException ioe) {
            System.err.println(ioe);
        }
      });
    
    /*
    Name and description must be changed. the easies tway is to load and save
    */
    XMLFactory f = XMLFactory.loadDataSet(targetDir);
    f.setName(name);
    f.setDescription(desc);
    f.save();
    getFactories().put(name, f);
    return folder;
  }
  
  public boolean actionExportArmy(BField battle, File toFile) throws IOException {
    String exportID = actionArmySelection("Select army to export:", battle);
    if (exportID == null)
      return false;
    
    
    JAXB.marshal(battle.getBattle().getArmies().get(exportID), toFile);
    return true;
  }
  
  public boolean actionImportArmy(File source, BField battle) {
    Army a = (Army) JAXB.unmarshal(source, Army.class);
    
    String replaceID = actionArmySelection("Select army to replace", battle);
    if (replaceID == null)
      return false;
    
    Army oldArmy = battle.getBattle().getArmy(replaceID);
    
    a.setID(replaceID);
    
    //Enemy reference replacement
    a.setEnemy(oldArmy.getEnemy());
    oldArmy.getEnemy().setEnemy(a);
    
    battle.getBattle().getArmies().put(replaceID, a);
    return true;
  }
  
  public static String actionArmySelection(String selectionText, BField battle) {
    
    RadioButton home = new RadioButton(battle.getBattle().getArmy(Battle.ID_HOME)
    .getName()),
            away = new RadioButton(battle.getBattle().getArmy(Battle.ID_AWAY)
            .getName());
    javafx.scene.control.ToggleGroup group = new javafx.scene.control
            .ToggleGroup();
    group.getToggles().add(home);
    group.getToggles().add(away);
    
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    VBox vbox = new VBox();
    vbox.getChildren().add(new javafx.scene.control.Label(selectionText));
    vbox.getChildren().add(home);
    vbox.getChildren().add(away);
    group.selectToggle(home);
    alert.getDialogPane().setContent(vbox);
    
    alert.showAndWait();
    if (alert.getResult() != ButtonType.OK)
      return null;
    
    return (group.getSelectedToggle().equals(home))
            ? Battle.ID_HOME : Battle.ID_AWAY;
  }
  
  /**
   * Shows a window with html content on it, for customized long-size
   * dialogs such as tutorials.It is not intended to be a full browser,
 only to show extended, formatted, text.
   * @param title
   * @param htmlData valid html source from wich read HTML text.
   * @param isModal
   */
  public void actionshowHTMLContent(String title, String htmlData, boolean 
          isModal) {
    WebView browser = new WebView();
    browser.getEngine().loadContent(htmlData, "text/html");
   
    Alert alert = new Alert(Alert.AlertType.NONE);
    alert.setTitle(title);
    alert.getDialogPane().setContent(browser);
    alert.getDialogPane().setPrefSize(800.0, 600.0);
    alert.getButtonTypes().add(ButtonType.OK);
    
    if (!isModal)
      alert.initModality(Modality.NONE);
    
    alert.show();
  }
}
