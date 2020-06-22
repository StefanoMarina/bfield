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

import bfield.Application;
import bfield.event.UnitChangeEvent;
import bfield.data.Army;
import bfield.data.Battle;
import bfield.data.Unit;
import bfield.event.ArmyEvent;
import bfield.rules.ArmyRules;
import bfield.rules.UnitRules;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

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
  private FlowPane boxList;
  
  private java.util.WeakHashMap<Pane, UnitCellController> mUnits;
  
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
  
  private EventHandler<javafx.scene.input.MouseEvent> ehOnUnitClick;
  
  private UnitController ucSelection;
  private Pane pSelection;
  
  /*
  TODO:
  Find a way to update army stats without adding Battle to all controllers
  */
  private Battle battle;
  
  private UnitRules unitMechanic;
  @FXML
  private Button btnEmbark;
  @FXML
  private StackPane stack;
  
  
  public void setWeatherDisabled(boolean value, boolean update) {
    btnWeather.setDisable(value);
    getUnits().values().forEach( (x) -> {
      x.setStatusEnabled("weather", !value); 
      if (update)x.refreshUnit();
    });
  }
  
  public void setGroundDisabled(boolean value, boolean update) {
    tbHighGround.setDisable(value);
    getUnits().values().forEach( (x) -> {
        x.setStatusEnabled("mountain", !value); 
        if (update)x.refreshUnit();
      });
  }
  
   public void setVisibilityDisabled(boolean value, boolean update) {
    getUnits().values().forEach( (x) -> {
      x.setStatusEnabled("visibility", !value); 
        if (update)x.refreshUnit();
      });
  }
  
   /**
    * Utility to get the currently selected controller.
    * @param u a unit (may be null)
    * @return a UnitCellController with isSelected() == true
    * or null if no element is selected
    */
  private java.util.Map.Entry<Pane, UnitCellController> getSelectedCell(Unit u) {
     if (u == null)
      return mUnits.entrySet().stream().filter(en -> (en.getValue().isSelected()))
             .findFirst().orElse(null);
     else
       return mUnits.entrySet().stream().filter(en -> (en.getValue().isSelected() &&
               en.getValue().getUnit() == u)).findFirst().orElse(null);
   }
   
  
  public ArmyController() {
    //Unit controller has been used.
    this.uehUnitChange = (event) -> {    
      refreshArmy();
      
      //if the change occourred on selected unit, update both controllers
      if (ucSelection.getUnit() == event.getUnit()) {
        if (ucSelection == event.getSource())
          ucSelection.refreshUnit();
        else
          mUnits.values().stream().filter( uc -> (uc.isSelected() &&
                  event.getUnit() == uc.getUnit()))
                  .forEach( uc -> {uc.refreshUnit();});
      }
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
      
      Pane selectedPane = source;
      
      if (ucSelection.getUnit() == unit) {
        if (pSelection == event.getSource()) {
          ucSelection.setUnit("",null,null);
          //pSelection.setVisible(false);
          stack.getChildren().remove(pSelection);
          selectedPane = this.getSelectedCell(unit).getKey();
        }
      }
      
      army.getUnits().remove(unit);
      boxList.getChildren().remove(selectedPane);
      getUnits().remove(selectedPane);
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
  
    try {
      FXMLLoader unitLoader = new FXMLLoader(getClass().getResource("unit.fxml"));
      pSelection = (Pane) unitLoader.load();
      ucSelection = unitLoader.getController();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
     pSelection.addEventHandler(UnitChangeEvent.UNIT_CHANGE, uehUnitChange);
     pSelection.addEventHandler(UnitChangeEvent.UNIT_REMOVE, uehUnitRemove);
     pSelection.addEventFilter(UnitChangeEvent.UNIT_CHANGE_TERRAIN, uehUnitTerrain);
     
    //BorderPane.setAlignment(pSelection, Pos.BOTTOM_CENTER);
    //BorderPane.setMargin(pSelection, new Insets(20,0,10,0));
    
    /*
      root.setBottom(pSelection);
      pSelection.setVisible(false);
    */
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
    
    //Unit selection
    this.ehOnUnitClick = (MouseEvent e) -> {
      e.consume();
      if (!(e.getSource() instanceof Pane)) {
        System.err.println(e.getSource().getClass().getSimpleName());
        return;
      }
      
      Pane pane = (Pane) e.getSource();
      if (!getUnits().containsKey(pane)) {
        return;
      }
      
      onUnitSelection(pane);  
    };
    
    root.setOnMouseClicked( (MouseEvent e) ->{
      e.consume();
      if (stack.getChildren().contains(pSelection)) {
        
        UnitCellController uc = getUnits().values().stream().filter(
          controller -> (controller.isSelected())).findFirst().orElse(null);
        
        if (uc != null)
          uc.setSelected(false);
        
        stack.getChildren().remove(pSelection);
      }
    });
  }
  
  public void onUnitSelection(Pane clickedNode) {
    
    UnitCellController selectedController = mUnits.get(clickedNode);
    if (selectedController.isSelected())
      return;
    
    getUnits().values().forEach( controller -> { controller.setSelected(
            mUnits.get(clickedNode) == controller); });
    
    ucSelection.setArmyColor(army.getColor());
    ucSelection.setUnit(army.getID(), selectedController.getUnit(), unitMechanic);

    if (!stack.getChildren().contains(pSelection)) {
      StackPane.setAlignment(pSelection, Pos.BOTTOM_CENTER);
      StackPane.setMargin(pSelection,new Insets(20));
      pSelection.setVisible(true);
      stack.getChildren().add(pSelection);
    }
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
    for (UnitCellController uc : mUnits.values() )
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
    
    UnitController.styleData(attack, attack, lblMelee);
    UnitController.styleData(defense, defense, lblDef);
    UnitController.styleData(special, special, lblCharge);
    /*
    lblMelee.setText( (attack != Unit.NA) ? String.valueOf(attack) : "--");
    lblDef.setText( (defense != Unit.NA) ? String.valueOf(defense) : "--");
    lblCharge.setText( (special != Unit.NA) ? String.valueOf(special) : "--");
    
    lblMelee.setStyle((attack < -9) ? "-fx-font-size: 1.0em" : "");
    lblDef.setStyle((defense < -9) ? "-fx-font-size: 1.0em" : "");
    lblCharge.setStyle((special < -9) ? "-fx-font-size: 1.0em" : "");
    */
    
    bSilent = false;
  }
  
  public void refreshArmyAndUnits() {
    refreshArmy();
    refreshUnits();
  }
  
  public void refreshUnits() {
    getUnits().values().forEach((uc) -> {
      uc.refreshUnit();
    });
    if (stack.getChildren().contains(pSelection))
      ucSelection.refreshUnit();
  }

  public void addUnit(Unit u) {
    Pane newPane;
    
    try {
      FXMLLoader unitLoader = new FXMLLoader(getClass().getResource("unitcell.fxml"));
      newPane = unitLoader.load();
      UnitCellController uc = unitLoader.getController();
      uc.setArmyColor(army.getColor());
      //uc.setShowOrdinal(bShowOrdinal);
      getUnits().put(newPane, uc);
      
      uc.setUnit(army.getID(), u, unitMechanic);
      newPane.addEventHandler(UnitChangeEvent.UNIT_CHANGE, uehUnitChange);
      newPane.addEventHandler(UnitChangeEvent.UNIT_REMOVE, uehUnitRemove);
      newPane.addEventFilter(UnitChangeEvent.UNIT_CHANGE_TERRAIN, uehUnitTerrain);
      
      if (u.getClassName().contains("Ship")) {
        //disembark action is added
        newPane.addEventHandler(ArmyEvent.ARMY_DISEMBARK_UNIT, (eh) ->{
          root.fireEvent(new ArmyEvent(army, ArmyEvent.ARMY_DISEMBARK_UNIT, null, 
                  eh.getTargetUnit()));
          eh.consume();
        });
      }
      
      uc.setStatusEnabled("weather", btnWeather.isDisable());
      //uc.setWeatherDisabled(btnWeather.isDisable());
      
      newPane.addEventHandler(MouseEvent.MOUSE_CLICKED, ehOnUnitClick);
      
      boxList.getChildren().add(newPane);
    } catch (IOException ex) {
      System.err.println("Could not load unit pane " + u.getName()
              + ": " + ex.getMessage());
      return;
    }
    
  }

  
  private java.util.Map<Pane,UnitCellController> getUnits() {
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

  @FXML
  private void onEmbarkUnit(ActionEvent event) {
    if (newUnitName == null)
      return;
    
    Unit ship = ucSelection.getUnit();
    if (ship == null || !ship.getClassName().contains("Ship"))
      onAddNewUnit();
    else if (ship.getCargo().size() == ship.getBunks()) {
      Application.showMessage("Cap reached", "Cannot add new crew",
              ship.getName() + " has already reached full crew", null);
      return;
    }
    else
    //sends request to battlecontroller
    root.fireEvent(new ArmyEvent(army, ArmyEvent.ARMY_EMBARK_UNIT, newUnitName,
            ship));
  }
  
  public void setFortificationVisible(boolean visible) {
    tbFortified.setVisible(visible);
  }
  
  public void setHighGroundVisible(boolean visible) {
    tbHighGround.setVisible(visible);
  }
  
  public void setStormModifierVisible(boolean visible) {
    btnWeather.setVisible(visible);
  }
}
