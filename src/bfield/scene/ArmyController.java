/*
 * Copyright (C) 2019 Stefano
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

import bfield.event.UnitChangeEvent;
import bfield.data.Army;
import bfield.data.Battle;
import bfield.data.Unit;
import bfield.event.ArmyEvent;
import bfield.rules.ArmyRules;
import bfield.rules.UnitRules;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Stefano
 */
public class ArmyController  {

  @FXML
  private ToggleButton tbFortified;
  private Army army;

  @FXML
  private Button btnWeather;
  
  @FXML
  private ColorPicker btnColorChange;

  @FXML
  private ToggleButton tbHighGround;
  @FXML
  private ComboBox cbUnits;
  @FXML
  private Button btnAdd;
  @FXML
  private VBox boxList;
  
  private java.util.WeakHashMap<Pane, UnitController> mUnits;
  
  //Check out what units are doing
  private final EventHandler<UnitChangeEvent> uehUnitChange;
  private final EventHandler<UnitChangeEvent> uehUnitRemove;
  private final EventHandler<UnitChangeEvent> uehUnitTerrain;
  
  private String newUnitName;
  @FXML
  private Label lblDef;
 
  @FXML
  private Label lblMelee;
  @FXML
  private Label lblCharge;
  @FXML
  private Spinner sDefMod;
  @FXML
  private Spinner sAttMod;
  
  private boolean bSilent;

  @FXML
  private Button lblName;
  @FXML
  private BorderPane root;
  
  private boolean bShowOrdinal;
  
  /*
  TODO:
  Find a way to update army stats without adding Battle to all controllers
  */
  private Battle battle;
  
  private UnitRules unitMechanic;
  
  public void updateOrdinals(boolean value) {
    bShowOrdinal = value;
    for (UnitController uc : mUnits.values()) {
      uc.setShowOrdinal(value);
    }
  }
  
  public void setWeatherDisabled(boolean value) {
    btnWeather.setDisable(value);
    getUnits().values().forEach( (x) -> {x.setWeatherDisabled(value); x.refreshUnit();});
  }
  
  public void setGroundDisabled(boolean value) {
    tbHighGround.setDisable(value);
    getUnits().values().forEach( (x) -> {x.setMountainDisabled(value); x.refreshUnit();});
  }
  
   public void setVisibilityDisabled(boolean value) {
    getUnits().values().forEach( (x) -> {x.setVisibilityDisabled(value); x.refreshUnit();});
  }
   
  public ArmyController() {
    this.uehUnitChange = (event) -> {    
      refreshArmy();
    };
    
    this.uehUnitTerrain = (event) -> {
      root.fireEvent(new ArmyEvent(army,ArmyEvent.ARMY_CHANGED));
    };
    
    this.uehUnitRemove = (event) -> {
      Pane source = (Pane) event.getSource();
      Unit unit = event.getUnit();
      
      if (!army.getUnits().contains(unit)) {
        throw new RuntimeException("Requested unit is not part of the army!");
      }
      
      army.getUnits().remove(unit);
      boxList.getChildren().remove(source);
      getUnits().remove(source);
      refreshArmy();
      root.fireEvent(new ArmyEvent(army,ArmyEvent.ARMY_CHANGED));
    };
  }
  
  @FXML
  public void setName() {
    TextInputDialog dlg = new TextInputDialog(army.getName());
    dlg.setTitle("Set Army name");
    dlg.setContentText("New name:");
    dlg.setHeaderText("Input army's new name");
    
    Optional<String> result = dlg.showAndWait();
    if (result.isPresent()) {
      army.setName(result.get());
      lblName.setText(result.get());
      //Send armychange
    }
  }
  
  public void setDatabaseList(List<String> names) {
    cbUnits.getSelectionModel().clearSelection();
    cbUnits.setItems(FXCollections.observableArrayList(names));
  }
  
  public void initialize() {
    cbUnits.setPromptText("-- Select unit --");
    cbUnits.valueProperty().addListener( (ov, t, t1) ->{
      if (t1 != null && t1.equals(cbUnits.getPromptText()))
        newUnitName = null;
      else
        newUnitName = (String) t1;
    });
    
    sDefMod.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-99,99,0));
    sAttMod.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-99,99,0));
    
    ChangeListener lst = (ov, t, t1) -> {
      army.setAttackMod((int)sAttMod.getValueFactory().getValue());
      army.setDefenseMod((int)sDefMod.getValueFactory().getValue());
      
      refreshArmy();
    };
    sDefMod.getValueFactory().valueProperty().addListener(lst);
    sAttMod.getValueFactory().valueProperty().addListener(lst);
    
    btnColorChange.setOnAction((e) ->{
      onSetColor();
    });
    
  
  }
  
  @FXML
  public void onFortified() {
    if(bSilent)
      return;
    army.setFortified(tbFortified.isSelected());
    root.fireEvent(new ArmyEvent(army, ArmyEvent.ARMY_CHANGED));
    refreshArmyAndUnits();
  }
  
  @FXML
  public void onHighGround() {
    army.setHighGround(tbHighGround.isSelected());
    root.fireEvent(new ArmyEvent(army, ArmyEvent.ARMY_HIGHGROUND_CHANGED));
    refreshArmyAndUnits();
  }
  
  public void onSetColor() {
    army.setColor(btnColorChange.getValue());
    for (UnitController uc : mUnits.values() )
      uc.setArmyColor(btnColorChange.getValue());
    
    refreshUnits();
  }
  
  @FXML
  public void onHostileWeather() {
    ButtonType btnEnable = new ButtonType("Enable accustom");
    ButtonType btnDisable = new ButtonType("Disableaccustom");
    ButtonType btnCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
    
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Abnomarl weather suffering");
    alert.setHeaderText("Enable abnormal weather suffering?");
    alert.getButtonTypes().setAll(btnEnable, btnDisable, btnCancel);
    
    Label content = new Label("By enabling weather suffering, the whole army will"
            + "suffer -2 to all attacks.\n Individual resistance can be changed "
            + " inside the war card control panel.");
    content.setWrapText(true);

    alert.getDialogPane().setContent(content);
    
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == btnDisable){
      army.getUnits().forEach((u) -> {u.setIgnoreWeather(false);}); 
      refreshUnits();
    } else if (result.get() == btnEnable){
      army.getUnits().forEach((u) -> {u.setIgnoreWeather(true);}); 
      refreshUnits();
    }
  }
  
  /**
   * 
   * @param a army to show
   * @param b 
   * @note assumes army list is clean
   */
  public void setArmy(Army a, Battle b) {
    bSilent = true;
    
    army = a;
    battle = b;
    unitMechanic = b.getUnitRules();
    
    lblName.setText(army.getName());
    
    //lose all children
    boxList.getChildren().clear();
    
    a.getUnits().forEach((u) -> {addUnit(u);});
    refreshArmy();
    
    
  }
  
  public void refreshArmy() {
    bSilent = true;
    tbFortified.setSelected(army.isFortified());
    tbHighGround.setSelected(army.hasHighGround());
    btnColorChange.setValue(army.getColor());
    //rShield.setStyle("-fx-background-color:"+army.getColor().toString().replaceAll("0x", "#"));
    
    ArmyRules armyRules = battle.getArmyRules(army.getID());
    int attack = (int)armyRules.getAttack();
    int defense = (int)armyRules.getDefense();
    int special = (int)armyRules.getSpecial();
    
    sAttMod.getValueFactory().setValue(army.getAttackMod());
    sDefMod.getValueFactory().setValue(army.getDefenseMod());
    
    lblMelee.setText( (attack != Unit.NA) ? String.valueOf(attack) : "--");
    lblDef.setText( (defense != Unit.NA) ? String.valueOf(defense) : "--");
    lblCharge.setText( (special != Unit.NA) ? String.valueOf(special) : "--");
    
    lblMelee.setStyle((attack < -9) ? "-fx-font-size: 1.0em" : "");
    lblDef.setStyle((defense < -9) ? "-fx-font-size: 1.0em" : "");
    lblCharge.setStyle((special < -9) ? "-fx-font-size: 1.0em" : "");
    
    bSilent = false;
  }
  
  public void refreshArmyAndUnits() {
    refreshArmy();
    refreshUnits();
  }
  
  public void refreshUnits() {
    getUnits().values().forEach((uc) -> {uc.refreshUnit();});
  }
  
  
  public void addUnit(Unit u) {
    try {
      FXMLLoader unitLoader = new FXMLLoader(getClass().getResource("unit.fxml"));
      Pane newPane = unitLoader.load();
      UnitController uc = unitLoader.getController();
      uc.setArmyColor(army.getColor());
      uc.setShowOrdinal(bShowOrdinal);
      getUnits().put(newPane, uc);
      
      uc.setUnit(army.getID(), u, unitMechanic);
      newPane.addEventHandler(UnitChangeEvent.UNIT_CHANGE, uehUnitChange);
      newPane.addEventHandler(UnitChangeEvent.UNIT_REMOVE, uehUnitRemove);
      newPane.addEventFilter(UnitChangeEvent.UNIT_CHANGE_TERRAIN, uehUnitTerrain);
      
      uc.setWeatherDisabled(btnWeather.isDisable());
      
      boxList.getChildren().add(newPane);
    } catch (IOException ex) {
      System.err.println("Could not load unit pane " + u.getName());
    }
  }

  
  private java.util.Map<Pane,UnitController> getUnits() {
    if (mUnits == null)
      mUnits = new java.util.WeakHashMap();
    return mUnits;
  }
  
  @FXML
  public void onAddNewUnit() {
    if (newUnitName == null)
      return;
    
    //sends request to battlecontroller
    root.fireEvent(new ArmyEvent(army, ArmyEvent.ARMY_ADD_UNIT, newUnitName));
  }
}
