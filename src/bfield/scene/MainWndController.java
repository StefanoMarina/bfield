/*
 * Copyright (C) 2019 ste
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
package bfield.scene;

import bfield.Application;
import bfield.Utilities;
import bfield.data.BField;
import bfield.data.Unit;
import bfield.data.UnitBuilder;
import bfield.data.UnitFactory;
import bfield.data.XMLFactory;
import bfield.event.BattleEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * FXML Controller class
 *
 * @author ste
 */
public class MainWndController  {

  @FXML
  private TabPane tabMain;

  private final java.util.Map<Tab, BattleController> battles;

  private final EventHandler<BattleEvent> ehBattleChange;
  @FXML
  private Menu mBattle;
  
  public MainWndController() {
    battles = new java.util.HashMap();
    
    ehBattleChange = (eh) -> {
      Tab t = tabMain.getSelectionModel().getSelectedItem();
      BattleController bc = battles.get(t);
      if (bc == null) {
        throw new RuntimeException("Could not retrieve battle controller.");
      }
      
      t.setText("*"+bc.getBattlefield().getBattle().getName());
      bc.getBattlefield().setUpToDate(false);
    };
  }
  /**
   * Returns current bfield. This does NOT check for open tabs -- if no battle
   * tab is selected, a RuntimeException is thrown.
   * @return the BField associated with the current opened tab.
   */
  private BField getSelectedBattle() {
    Tab t = tabMain.getSelectionModel().getSelectedItem();
    BattleController bc = battles.get(t);
    if (bc == null) {
      throw new RuntimeException("Could not retrieve battle controller.");
    }
    return bc.getBattlefield();
  }
  
  public void initialize() {
    tabMain.getSelectionModel().selectedItemProperty().addListener(
            (ov, t, t1)-> {
              onTabSelectionChange(t, t1);
            }
    );
    
  }  
  
  public void addNewBattleTab(BField b) {
    
    if (battles.containsKey(b)) {
      //What to do?
    }
    
    FXMLLoader loader = new FXMLLoader(getClass().getResource("battle.fxml"));
    Pane battlePane;
    try {
      battlePane = loader.load();
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
    
    BattleController bc = loader.getController();
    bc.setBattle(b);
    
    Tab newTab = new Tab(b.getBattle().getName(),battlePane);
    newTab.setTooltip(new Tooltip("Unsaved battle."));
    
    newTab.setOnCloseRequest( (eh) ->{
       if (!onCloseBattleTab((Tab)eh.getSource()))
         eh.consume();
    });
    
    
    battles.put(newTab, bc);
    battlePane.addEventHandler(BattleEvent.BATTLE_GENERAL_CHANGE, ehBattleChange);
    tabMain.getTabs().add(newTab);
    //tabMain.getSelectionModel().select(newTab);
  }

  private boolean onCloseBattleTab(Tab t) {
    BField b = battles.get(t).getBattlefield();
    if (!b.isUpToDate()) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Unsaved battle");
      alert.setHeaderText("Save battle " + b.getBattle().getName() + "?");
      if (b.getFile() == null)
        alert.setContentText("Battle was not saved.");
      else
        alert.setContentText("Saved as " + b.getFile());
      
      ButtonType btSave = new ButtonType("Save");
      ButtonType btNoSave = new ButtonType("Don't save");
      //ButtonType btNoSave = new ButtonType("Don't save");
      alert.getButtonTypes().setAll(btSave, btNoSave, ButtonType.CANCEL);
      
      Optional<ButtonType> result = alert.showAndWait();
      if (result.get() == btSave) {
        onMenuSave(null);
        battles.remove(t);
        return true;
      } if (result.get() == btNoSave) {
        battles.remove(t);
        return true;
      } else {
        return false;
      }
    } else {
      battles.remove(t);
      return true;
    }
  }
  
  @FXML
  private void onMenuNew(ActionEvent event) {
    BField newFile = Application.getApp().actionNewBattle();
    if (newFile != null)
      addNewBattleTab(newFile);
  }

  @FXML
  private void onMenuSave(ActionEvent event) {
    BField bf = getSelectedBattle();
    Tab t = tabMain.getSelectionModel().getSelectedItem();
      
    if (bf.getFile() == null) {
      FileChooser fc = createFileChooser("Save battle", "bfield");
    
      File result = fc.showSaveDialog(Application.getApp().getStage());
    
      if (result == null)
          return;
      
      if (!result.getAbsolutePath().endsWith(".bfield"))
        result = new File (result.getAbsolutePath()+".bfield");
      
      bf.setFile(result);
      t.setTooltip(new Tooltip(result.toString()));
      Application.getApp().getPreferences().put(Application.P_LASTFOLDER, 
            result.getParent());
    }
    
    javax.xml.bind.JAXB.marshal(bf, bf.getFile());
    bf.setUpToDate(true);
    t.setTooltip(new Tooltip(bf.getFile().getAbsolutePath()));
    t.setText(bf.getBattle().getName());
  }

  @FXML
  private void onMenuOpen(ActionEvent event) {
    FileChooser fc = createFileChooser("Save battle", "bfield");
    File result = fc.showOpenDialog(Application.getApp().getStage());
    
    if (result == null)
      return;
    Application.getApp().getPreferences().put(Application.P_LASTFOLDER, 
            result.getParent());
    BField b = Application.getApp().actionLoadBattle(result);
    
    if (b == null) {
      Application.showMessage("Fail", "File loading failed", "could not load file"
              + result.toString(), null);
      return;
    }
    
    b.setFile(result);
    b.setUpToDate(false);
    addNewBattleTab(b);
    
  }

  public void closeAllTabs() {
    for (Tab t : tabMain.getTabs()) {
      BField bf = battles.get(t).getBattlefield(); 
    }
  }
  
  @FXML
  public void onMenuQuit(ActionEvent event) {
    Application.getApp().getStage().close();
    //kill me
  }

  @FXML
  private void onMenuEditUnit(ActionEvent event) {
    java.util.Map.Entry<String,Unit> result =
            Application.getApp().actionShowUnitList(false);
    
    if (result == null)
      return;
    
    Unit u = Application.getApp().actionEditUnit("Change unit", 
              result.getValue());  
    if (u == null)
      return;
    
    bfield.data.XMLFactory xmlFactory = Application.getApp().getFactories()
            .get(result.getKey());
    UnitFactory uf = xmlFactory.getUnitFactory();
    
    if (uf.getAllNames().contains(u.getName())) {
      Alert alert = new Alert(Alert.AlertType.NONE);
      alert.setHeaderText("Replace unit \""+u.getName()+"\"?");
      alert.setContentText("This will replace the existing unit.\n"
              + "Units already deployed will NOT be affected.");
      
      final ButtonType BT_ADD= new ButtonType("Just add"),
              BT_REP=new ButtonType("Replace");
      alert.getButtonTypes().addAll(BT_ADD,BT_REP,ButtonType.CANCEL);
      alert.showAndWait();
      
      if (alert.getResult() == ButtonType.CANCEL)
        return;
      else if (alert.getResult() == BT_REP) {
        uf.setUnit(u);
      } else if (alert.getResult() == BT_ADD){
        UnitBuilder.setNameMark(u);
        uf.appendUnit(u);
      }
    } else
        uf.appendUnit(u);
    
    try {
      xmlFactory.save();
    } catch (java.io.IOException ioe) {
      Application.showMessage("Failure", "Could not save ruleset.", "An exception"
              + " was thrown during the attemp to save the ruleset files.", ioe);
    }
    refreshBattleData();
  }
  
  private void refreshBattleData() {
    for (BattleController bc : battles.values()){
      //bc.setBattle(bc.getBattlefield());
      bc.refreshUnitsLists();
    }
  }

  private void onTabSelectionChange(Tab oldTab, Tab newTab) {
    //check if it is a battle tab
    if (battles.containsKey(newTab)) {
      mBattle.setDisable(false);
    } else {
      mBattle.setDisable(true);
    }
  }
  
  @FXML
  private void onUnitDelete(ActionEvent event) {
    java.util.Map.Entry<String,Unit> result = 
            Application.getApp().actionShowUnitList(true);
    
    if (result == null)
      return;
    
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete unit from " + result.getKey());
    alert.setHeaderText("Delete unit " + result.getValue().getName() + "?");
    alert.setContentText("This change cannot be undone!");
    alert.showAndWait();
    
    if (alert.getResult() == ButtonType.OK) {
      XMLFactory xmlf = Application.getApp().getFactories().get(result.getKey());
      xmlf.getUnitFactory().deleteUnit(result.getValue());
      
      try {
        xmlf.save();
      } catch (java.io.IOException ioe) {
        Application.showMessage("Failure", "Could not save ruleset.", "An exception"
                + " was thrown during the attemp to save the ruleset files.", ioe);
      }
      refreshBattleData();
    }
  }

  @FXML
  private void onNewRuleset(ActionEvent event) {
    FXMLLoader loader = 
     new FXMLLoader(getClass().getResource("dialogs/NewRulesetDialog.fxml"));
    
    javafx.scene.Node root;
    
    try {
       root = loader.load();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("New Ruleset");
    alert.setHeaderText("Create new ruleset:");
    alert.getDialogPane().setContent(root);
    alert.showAndWait();
    
    if (alert.getResult() != ButtonType.OK)
      return;
    
    bfield.scene.dialogs.NewRulesetDialogController controller =
            loader.getController();
    
    try {
    String folder = Application.getApp().actionNewRuleset(
            controller.getNewRulesetName(),
            controller.getNewRulesetDesc(), 
            controller.getFolder(),
            controller.getRequestedClone());
    
    Application.showMessage("Success!", "New ruleset created.", 
            "Ruleset folder: " + folder + ".Please restart Battlefield!", null);
    } catch (IOException ioe) {
      Application.showMessage("IO Error", "Could not create ruleset", 
              "An error occourred while copying", ioe);
    }
    
  }

  @FXML
  private void onImportArmy(ActionEvent event) {
    FileChooser fc = createFileChooser("Import army", "xarmy");          
    File result = fc.showOpenDialog(Application.getApp().getStage());
    
    if (result == null)
      return;
    
    //store last path
    if (!Application.getApp().actionImportArmy(result, getSelectedBattle()))
      return;
    
    Application.getApp().getPreferences().put(Application.P_LASTFOLDER, 
            result.getParent());
    
    BattleController bc = battles.get(tabMain.getSelectionModel().getSelectedItem());
    //force army restoration and set
    bc.setBattle(bc.getBattlefield());
  }

  @FXML
  private void onExportArmy(ActionEvent event) {
    FileChooser fc = createFileChooser("Export army", "xarmy");
    File result = fc.showSaveDialog(Application.getApp().getStage());
    
    if (result == null)
      return;
    if (!result.getAbsolutePath().endsWith(".xarmy"))
        result = new File (result.getAbsolutePath()+".xarmy");
    try {
      Application.getApp().actionExportArmy(getSelectedBattle(), result);
    } catch (IOException ex) {
      Application.showMessage("Error", "Exporting error.", "Something went"
              + " wrong while exporting the army.", ex);
    } finally {
      Application.getApp().getPreferences().setProperty(Application.P_LASTFOLDER,
              result.getParentFile().getAbsolutePath());
    }
  }

  /**
   * shows the 'acknowledgements' menu
   * @param event 
   */
  @FXML
  private void onMenuAcknowledgemens(ActionEvent event) {
    try {
      String data = new String(Files.readAllBytes(
              new File(Utilities.getResourceFile("acknowledgements.html")).toPath()
      ));
      
      Application.getApp().actionshowHTMLContent("Acknowledgements",data, true);
      
    } catch (IOException ex) {
      Application.showMessage("Error", "Could not show document.", "Cannot load"
              + "acknowledgements.html", ex);
    }
          
  }

  /**
   * Takes the tutorial filename from the currently opened tab and
   * shows the html file. Error is raised if no tutorial or battle is selected.
   * @param event ignored
   */
  @FXML
  private void onTutorial(ActionEvent event) {
    BField bf = getSelectedBattle();
    if (bf == null) {
      Application.showMessage("No selection", "No battle selected.", 
              "Please select a battle.", null);
      return;
    }
    
    final String FNAME = bf.getFactory()
            .getRules().getTutorialFilename();
    
    if (FNAME == null || FNAME.isEmpty()) {
      Application.showMessage("No tutorial", "No tutorial found.", 
              "This ruleset has no tutorial attached.", null);
      return;
    }
      
    File tutorialFile = new File(String.format("%s%sres%s%s",
            System.getProperty("user.dir"), File.separator,
            File.separator, FNAME
    ));
    
    try {
      String data = new String(Files.readAllBytes(tutorialFile.toPath()));
      if (data == null || data.isEmpty())
        throw new IOException("Could not read file or empty file.");
      
      Application.getApp().actionshowHTMLContent("Tutorial", data, false);
    }catch(IOException ioe) {
      Application.showMessage("Error","An error occourred", 
              "please chek exception info.", ioe);
    }
  }
  
  /**
   * Creates a filechooser by putting default values, including start folder
   * based on P_LASTFOLDER preference
   * @param title Title to be shown
   * @param extension string with the starting extension (bfield, xarmy, ecc)
   * @return a newly created file chooser
   */
  private FileChooser createFileChooser(String title, String extension) {
    FileChooser chooser = new FileChooser();
    chooser.setTitle(title);
    String lastFolder = 
      Application.getApp().getPreferences().getProperty(Application.P_LASTFOLDER);
    if (lastFolder != null) {
      chooser.setInitialDirectory(new File(lastFolder));
    }
    ExtensionFilter filter;
    switch (extension) {
      case "bfield" : filter = new ExtensionFilter("Battlefield! xml files",
              "*.bfield"); break;
      case "xarmy" : filter = new ExtensionFilter("Battlefield! xml army",
              "*.xarmy"); break;
      default: throw new UnsupportedOperationException("Unknown file extension");
    }
    chooser.getExtensionFilters().add(filter);
    return chooser;
  }
}
