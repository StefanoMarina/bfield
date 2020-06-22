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
import bfield.RulesUtilities;
import bfield.data.Army;
import bfield.data.BField;
import bfield.data.Battle;
import bfield.data.Condition;
import bfield.data.ConditionFactory;
import bfield.data.Unit;
import bfield.event.ArmyEvent;
import bfield.event.BattleEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import bfield.rules.BattleRules;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author ste
 */
public class BattleController  {

  @FXML
  private ComboBox<Condition> cbTerrain;
  @FXML
  private ComboBox<Condition> cbWeather;
  @FXML
  private ComboBox<Condition> cbVisibility;
  @FXML
  private ToggleButton tbEffect;
  
  private BField battle;
  private ArmyController acHome, acAway;
  private Pane pnlHome, pnlAway;
  @FXML
  private BorderPane root;
  @FXML
  private GridPane pnlContent;
  @FXML
  private ComboBox<BattleRules> cbFormula;
  @FXML
  private StackPane stack;
  
  
  public BField getBattlefield() {return battle;}
  
  private boolean bSilent;
  
  public void initialize() {  
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("army.fxml"));
      pnlHome = loader.load();
      acHome = loader.<ArmyController>getController();
      
      
      loader = new FXMLLoader(getClass().getResource("army.fxml"));
      pnlAway = loader.load();
      acAway = loader.<ArmyController>getController();
      
      GridPane.setVgrow(pnlHome, Priority.ALWAYS);
      GridPane.setVgrow(pnlAway, Priority.ALWAYS);
      
      pnlContent.add(pnlHome, 0, 0);
      pnlContent.add(pnlAway, 1, 0);
      
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
    
    
    cbTerrain.setOnAction( (a) ->{
      if (bSilent) return;
      battle.getBattle().setTerrain(cbTerrain.getValue());
      refreshArmies();
    });
    cbWeather.setOnAction( (a) ->{
      if (bSilent) return;
      battle.getBattle().setWeather(cbWeather.getValue());
      refreshArmies();
    });
    cbVisibility.setOnAction( (a) ->{
      if (bSilent) return;
      battle.getBattle().setVisibility(cbVisibility.getValue());
      refreshArmies();
    });
    cbFormula.setOnAction( (a) ->{
      if (bSilent) return;
      //battle.getBattle().getRules().setAttackMethod(cbFormula.getValue());
      battle.setSelectedBattleMechanic(cbFormula.getSelectionModel().getSelectedItem());
    });
    
    final EventHandler<ArmyEvent> armyHGChanged = (event) -> {
      ArmyController ucEnemy = (event.getArmy().getEnemy().getID()
              .equals(Battle.ID_HOME))
              ? acHome : acAway;
      event.getArmy().getEnemy().setHighGround(false);
      ucEnemy.refreshArmyAndUnits();
    };
    
    final EventHandler<ArmyEvent> armyRefreshRequest = (event) -> {
      refreshArmies();
    };
    
    final EventHandler<ArmyEvent> armyDisembarkUnit = (event) -> {
      Unit unit = event.getTargetUnit();
      ArmyController acRequest = (event.getArmy().getID()
          .equals(Battle.ID_HOME)) ? acHome : acAway;
      event.getArmy().addUnit(event.getTargetUnit());
      acRequest.addUnit(event.getTargetUnit());
      refreshArmies();
    };
            
    final EventHandler<ArmyEvent> armyAddNewUnit = (event) -> {
      String newUnitName = event.getData();
      ArmyController ucRequest = (event.getArmy().getID()
              .equals(Battle.ID_HOME)) ? acHome : acAway;
      
      Unit newUnit;

      newUnit = battle.getFactory().getUnitFactory().createUnit(newUnitName,
                event.getArmy().nextOrdinal(newUnitName));
      
      
      if (event.getEventType() == ArmyEvent.ARMY_EMBARK_UNIT) {
        if (newUnit.getClassName().contains("Ship")) {
          Application.showMessage("Cannot add ship", "Unit is a ship", 
                  "you cannot embark a ship inside another ship.", null);
          event.consume();
          return;
        }
        event.getTargetUnit().getCargo().add(newUnit);
      } else {
        event.getArmy().addUnit(newUnit);
        ucRequest.addUnit(newUnit);
      }
      
      refreshArmies();
    };
    final Pane[] loop = new Pane[] {pnlHome, pnlAway};
    for (Pane p : loop ){
      p.addEventHandler(ArmyEvent.ARMY_HIGHGROUND_CHANGED, armyHGChanged);
      p.addEventHandler(ArmyEvent.ARMY_CHANGED, armyRefreshRequest);
      p.addEventHandler(ArmyEvent.ARMY_ADD_UNIT, armyAddNewUnit);
      p.addEventHandler(ArmyEvent.ARMY_EMBARK_UNIT, armyAddNewUnit);
      p.addEventHandler(ArmyEvent.ARMY_DISEMBARK_UNIT, armyDisembarkUnit);
    }
  }

  @FXML
  private void setEffectiveness(ActionEvent event) {
    battle.getBattle().getRules().setEffectivenessOn(tbEffect.isSelected());
    refreshArmies();
  }
 
  
  public void refreshUnitsLists() {
    final List<String> units = battle.getFactory().getUnitFactory().getAllNames();
    
    acHome.setArmy(battle.getBattle().getArmies().get(Battle.ID_HOME), battle.getBattle());
    acHome.setDatabaseList(units);
    
    acAway.setArmy(battle.getBattle().getArmies().get(Battle.ID_AWAY), battle.getBattle());
    acAway.setDatabaseList(units);
  }
  
  public void setBattle(BField b) {
    battle = b;
    bSilent = true;
    refreshUnitsLists();
    
    ConditionFactory cf = b.getFactory().getConditionFactory();
    
    cbTerrain.setItems(FXCollections.observableArrayList(cf.getTerrain()));
    cbWeather.setItems(FXCollections.observableArrayList(cf.getWeather()));
    cbVisibility.setItems(FXCollections.observableArrayList(cf.getVisibility()));
   // cbFormula.setItems(FXCollections.observableArrayList(Rules.AttackMethod.values()));
    cbFormula.setItems(FXCollections.observableArrayList(
              battle.getFactory().getRules().getBattleMechanics()));
    
    refreshBattle();
    refreshArmies();
    bSilent = false;
  }
  
  public void refreshArmies() {
    List<ArmyController> la = Arrays.<ArmyController>asList(acHome, acAway);
    
    la.forEach( (ac) -> {
      ac.setGroundDisabled(!cbTerrain.getValue().isHighGroundAllowed(), false);
      ac.setWeatherDisabled("Normal".equals(cbWeather.getValue().getName()), false);
      ac.setVisibilityDisabled("Full".equals(cbVisibility.getValue().getName()),false);
      ac.refreshArmyAndUnits();
    });
    
    root.fireEvent(new BattleEvent(BattleEvent.BATTLE_GENERAL_CHANGE));
  }
  
  public void refreshBattleAndArmies() {
    refreshBattle();
    refreshArmies();
  }
  
  public void refreshBattle() {
    cbTerrain.getSelectionModel().select(battle.getBattle().getTerrain());
    cbWeather.getSelectionModel().select(battle.getBattle().getWeather());
    cbVisibility.getSelectionModel().select(battle.getBattle().getVisibility());
    tbEffect.setSelected(battle.getBattle().getRules().isEffectivenessOn()); 
    cbFormula.getSelectionModel().select(battle.getSelectedBattleMechanic());
  }

  @FXML
  private void onWeatherRoll(ActionEvent event) {
    TextInputDialog dlg = new TextInputDialog("0");
    dlg.setTitle("Random weather");
    dlg.setContentText("d100 modifier:");
    dlg.setHeaderText("Set d100 roll modifier. weather will be decided using\n"
           +"d20 SRD rules for weather determination.");
    
    Optional<String> res =dlg.showAndWait();
    if (!res.isPresent())
      return;
    
    int value;
    try {
      value = Integer.parseInt(res.get());
    } catch (NumberFormatException e) {
      value = 0;
    }
    
    String result = RulesUtilities.rollWeather(value);
    
    if ("Major Storm".equals(result)) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setContentText("A Major storm is happening. Combat is impossibile.");  
    } else {
      for (Condition c : battle.getFactory().getConditionFactory().getWeather()) {
        if (result.equals(c.getName())) {
          battle.getBattle().setWeather(c);
          refreshBattleAndArmies();
          break;
        }
      }
    }
  }
 
 @FXML
 private void onAttack() {
   
  for (Army a : battle.getBattle().getArmies().values()) {
     if (a.getActiveUnitsSize() <= 0) {
       Application.showMessage("Cannot attack", "Units are missing.", "You cannot "
               + " attack with an empty army.", null);
       return;
     }
  }
   
  Map<String,String> result;
  result = battle.getSelectedBattleMechanic().doBattle(battle.getFactory().getRules(),
          battle.getBattle());
   
  if (result == null) {
    //attack aborted.
    return;
  }
  
  bfield.scene.BattleResultFormatter formatter = new
            BattleResultFormatter();
  String html = formatter.toHTML(getBattlefield().getBattle(), result);
  
  Application.getApp().actionshowHTMLContent("Attack results", html, true);
  battle.getBattle().nextRound();
  refreshBattleAndArmies();
 }
}
