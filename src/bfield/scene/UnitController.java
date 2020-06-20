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
package bfield.scene;

import bfield.Application;
import bfield.RulesUtilities;
import bfield.Utilities;
import bfield.event.UnitChangeEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import bfield.data.Unit;
import bfield.data.Unit2nd;
import bfield.rules.UnitRules;
import java.util.Optional;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;

import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author root
 */
public class UnitController {
  private bfield.data.Unit unit;

  @FXML
  private Text lblName;
  @FXML
  private Text lblClass;
  @FXML
  private Label lblDef;
  @FXML
  private Label lblMelee;
  @FXML
  private Label lblMissile;
  @FXML
  private Label lblCharge;
  @FXML
  private Region rPictureBackground;
  @FXML
  private Region rPictureIcon;
  private Region gCharge;
  @FXML
  private ToggleButton tgVis;
  @FXML
  private ToggleButton tgWeather;
  @FXML
  private ToggleButton tgMountain;
  @FXML
  private ToggleButton tgHero;
  @FXML
  private ToggleButton tgFortification;

  
  private boolean bSilent;
  @FXML
  private Label lblEL;
  @FXML
  private Label lblHealth;
  @FXML
  private ToggleButton tgTerrain;
  @FXML
  private Button btnDamage;
  @FXML
  private Button btnHeal;
  
  private String armyID;
  
  @FXML
  private BorderPane root;
  @FXML
  private ToggleButton tbCamp;
  
  private Color armyColor;
  
  
  private UnitRules modEnvironment;
  
  @FXML
  private Label lblMorale;
  @FXML
  private Label lblMove;
  
  @FXML
  private Label lblPaddles;
  
  @FXML
  private Group grPaddles;
  
  private boolean showOrdinal;
  
  @FXML
  private Icon iMissile;
  @FXML
  private Icon iCharge;
  /**
   * TODO:
   * Army request is used for color change only at this time.
   * It may be wiser to remove any reference at all and just call
   * setColor from army controller.
   * @param name 
   */
  
  
  public void setShowOrdinal(boolean show) {
    showOrdinal = show;
    if (unit != null) {
      if (showOrdinal && unit.getOrdinal() != null) {
        lblName.setText(bfield.tools.RomanNumerals.toRoman(unit.getOrdinal()) 
        + " " + unit.getName());
      } else
        lblName.setText(unit.getName());
    }
      
  }
  
  public void setArmyColor(Color armyColor) {
    this.armyColor = armyColor;
  }

  public void setWeatherDisabled(boolean value) {
    tgWeather.setDisable(value);
  }
  
  public void setTerrainDisabled(boolean value) {
    tgTerrain.setDisable(value);
  }
  public void setMountainDisabled(boolean value) {
    tgMountain.setDisable(value);
  }
  public void setVisibilityDisabled(boolean value) {
    tgVis.setDisable(value);
  }
  
  public void setArmyID(String name) {
    armyID = name;
  }
  
  public Unit getUnit() {return unit;}
  
  public void initialize() {
    
    tgVis.setOnAction( (ActionEvent e) -> {
      if (bSilent) return;
      unit.setIgnoreVisibility(tgVis.isSelected());
      refreshUnit();
      root.fireEvent(new UnitChangeEvent(unit));
    });
    tgWeather.setOnAction( (ActionEvent e) -> {
      if (bSilent) return;
      unit.setIgnoreWeather(tgWeather.isSelected());
      refreshUnit();
      root.fireEvent(new UnitChangeEvent(unit));
    });
    tgFortification.setOnAction( (ActionEvent e) -> {
      if (bSilent) return;
      unit.setIgnoreFortification(tgFortification.isSelected());
      refreshUnit();
      root.fireEvent(new UnitChangeEvent(unit));
    });
    tgMountain.setOnAction( (ActionEvent e) -> {
      if (bSilent) return;
      unit.setIgnoreMountain(tgMountain.isSelected());
      refreshUnit();
      //root.fireEvent(new UnitChangeEvent(unit));
      root.fireEvent(new UnitChangeEvent(unit, root, UnitChangeEvent.UNIT_CHANGE_TERRAIN));
    });
    tgTerrain.setOnAction( (ActionEvent e) -> {
      if (bSilent) return;
      unit.setIgnoreTerrain(tgTerrain.isSelected());
      refreshUnit();
      //root.fireEvent(new UnitChangeEvent(unit));
      root.fireEvent(new UnitChangeEvent(unit, root, UnitChangeEvent.UNIT_CHANGE_TERRAIN));
    });
    tgHero.setOnAction( (ActionEvent e) -> {
      if (bSilent) return;
      if (unit.getHeroEL() != Unit.NA) {
        unit.detachHeroUnit();
        refreshUnit();
        root.fireEvent(new UnitChangeEvent(unit));
      } else {
        onHeroSelection();
      }
    });
    tbCamp.setOnAction ( (ActionEvent e) ->{
      unit.setRetired(tbCamp.isSelected());
      tbCamp.getTooltip().setText( (unit.isRetired() ? "Send unit to battle" : 
              "Send unit to camp"));
      refreshUnit();
      root.fireEvent(new UnitChangeEvent(unit));
    });
  }
  
protected void onHeroSelection() {
  TextInputDialog dialog = new TextInputDialog("8");
  dialog.setTitle("Attach hero unit");
  dialog.setHeaderText("Add the encounter level (EL) of the heroes embedded\n"
          + " on that unit. If the unit is destroyed, the heroes' fate will be\n"
          + " decided by rules in chapter 6 of the BRCS.");
  dialog.setContentText("Enter Hero EL:");
  
  //dialog.getDialogPane().getStylesheets()
  //  .add(getClass().getResource("dialog.css").toExternalForm());
  //dialog.getDialogPane().getStyleClass().add("dlg");
  
  Optional<String> result = dialog.showAndWait();
  if (result.isPresent()) {
    try {
      unit.attachHeroUnit(Integer.parseInt(result.get()));
    } catch (NumberFormatException e) {
      Alert alert = new Alert (AlertType.INFORMATION);
      alert.setTitle("Could not parse number");
      alert.setHeaderText(null);
      alert.setContentText(result.get() + " is not a valid integer number.");
    } finally {
      refreshUnit();
      root.fireEvent(new UnitChangeEvent(unit));
    }
  }
}

public void setModEnvironment(UnitRules um) {
  modEnvironment = um;
  refreshUnit();
}

public void setUnit(String team, Unit u, UnitRules um) {
   this.unit = u;
   
   if (unit == null) {
      modEnvironment = null;
      return;
   }
   
   this.armyID = team;
   modEnvironment = um;
   
   //call this as a redundant utility to apply the correct name
   setShowOrdinal(showOrdinal);
   
  
  //load and set unit symbol
  String svgFile = (u.getIcon() != null ? u.getIcon()
      : Application.getApp().getAppropriateIcon(u.getClassName()));
  try {
    String svg = Utilities.loadSVGFile(svgFile);
    rPictureIcon.setStyle("-fx-shape: '"+svg+"'");
  } catch (Exception e) {
      System.out.println("::setUnit: Could not load " + svgFile);  
  }
  
  rPictureIcon.getStyleClass().clear();
  rPictureIcon.getStyleClass().add("picture-front");   
  rPictureBackground.setStyle("-fx-background-color: red");
  
  /**
   * 2nd edition units have the "rowing" capability
   * TODO: add show_rowing to rules instead of ugly class ref check
   */ 
    if (unit instanceof Unit2nd) {
      lblPaddles.setVisible(true);
      grPaddles.setVisible(true);
      Unit2nd u2 = (Unit2nd) unit;
      styleData(u2.getRowing(), u2.getRowing(), lblPaddles);
   } else {
      lblPaddles.setVisible(false);
      grPaddles.setVisible(false);
   }
   
    //movement can be number or string (movement class for ships in 2e)
    if (unit.getMovement() != null)
      lblMove.setText(unit.getMovement().toString());
    else
      lblMove.setText("--");
    
    refreshUnit();
 }
 
 public void refreshUnit() {
   if (unit == null)
     return;
   
   Unit mod = modEnvironment.mod(unit, armyID);
   
   bSilent = true;
  
   lblClass.setText(RulesUtilities.parseClassName(unit.getClassName()));
   
   lblDef.setText(String.valueOf(mod.getDef()));
   lblMelee.setText(String.format("%d", mod.getMelee()));
   lblMorale.setText(String.valueOf(mod.getMorale()));
   
   iMissile.setGray(unit.getMissile() == Unit.NA);
   styleData(unit.getMissile(), mod.getMissile(), lblMissile);
           
   iCharge.setGray(unit.getChargeBonus() == Unit.NA);
   styleData(unit.getCharge(), mod.getCharge(), lblCharge);
  
   if (tgFortification.getTooltip() == null) {
     tgFortification.setTooltip(new Tooltip());
     tgWeather.setTooltip(new Tooltip());
     tgVis.setTooltip(new Tooltip());
     tgMountain.setTooltip(new Tooltip());
     tgTerrain.setTooltip(new Tooltip());
     tgHero.setTooltip(new Tooltip());
   }
   tgFortification.setSelected(mod.getIgnoreFortification());
   tgFortification.getTooltip().setText(
           "Ignore fortification defense is " 
           + (tgFortification.isSelected() ? "ON" : "OFF")
   );
   tgWeather.setSelected(mod.getIgnoreWeather());
   tgWeather.getTooltip().setText(
           "Unit accustomed to weather is " 
           + (tgWeather.isSelected() ? "ON" : "OFF")
   );
   tgVis.setSelected(mod.getIgnoreVisibility());
   tgVis.getTooltip().setText(
           "Ignore bad visibility is " 
           + (tgVis.isSelected() ? "ON" : "OFF")
   );
   tgMountain.setSelected(mod.getIgnoreMountain());
   tgMountain.getTooltip().setText(
           "Unit ignore high ground is " 
           + (tgMountain.isSelected() ? "ON" : "OFF")
   );
   tgTerrain.setSelected(mod.getIgnoreTerrain());
   tgTerrain.getTooltip().setText(
           "Ignore terrain is " 
           + (tgTerrain.isSelected() ? "ON" : "OFF")
   );
   tgHero.setSelected((mod.getHeroEL() != Unit.NA));
   tgHero.getTooltip().setText((mod.getHeroEL() == Unit.NA)
   ? "Click to embed a hero unit to this mod."
           : "Hero unit embedded. click to remove.");
  
   lblEL.setVisible(mod.getHeroEL() != Unit.NA);
   lblEL.setText(String.valueOf(mod.getHeroEL() != Unit.NA? mod.getHeroEL() : "--"));
   
   lblHealth.setText(String.valueOf(mod.getCurrentHits()));
   lblHealth.setStyle("-fx-text-fill:" +
           ((mod.getHits() != mod.getCurrentHits())
              ? "red" : "black" ));
   
   btnDamage.setDisable(mod.isDead());
   tgHero.setDisable(btnDamage.isDisabled());
   btnHeal.setDisable(mod.getCurrentHits() >= mod.getHits());
   
   //Colors
    
   styleData(unit.getDef(), mod.getDef(), lblDef);
   styleData(unit.getMelee(), mod.getMelee(), lblMelee);
   styleData(unit.getMissile(), mod.getMissile(), lblMissile);
   styleData(unit.getMelee(), mod.getMelee(), lblMelee);
   styleData(unit.getMorale(), mod.getMorale(), lblMorale);
   styleData(unit.getCharge(), mod.getCharge(), lblCharge);
   styleData(unit.getMorale(), mod.getMorale(), lblMorale);
   
   rPictureBackground.setStyle("-fx-background-color:"
    + (unit.isDead() ? "darkgray" : armyColor.toString().replaceAll("0x","#")));
   
   bSilent = false;
 }
 
 /**
  * Utility that will restyle score labels adjacent to icons.
  * Gray parameter will be applied if original value is Unit.NA,
  * otherwise a red X will be printed out.
  * l will be using .t-better-dark css class to show improvements
  * from value, .t-worse if worse, or plain black.
  * If text is 3 digits or more (i.e. -10), font will be reduced.
  * @param value the original value
  * @param modvalue the final value that will be shown
  * @param l label to be changed
  */
 public static void styleData(int value, int modvalue, Label l) {
   
  final String better = "t-better-dark", worse ="t-worse";
  l.getStyleClass().remove(better);
  l.getStyleClass().remove(worse);
  if (value == Unit.NA)
     l.setText("--");
  else if (modvalue == Unit.NA)
    l.setText("X");
  else
    l.setText(String.valueOf(modvalue));
  
  if (l.getText().length() > 2)
    l.setStyle(l.getStyle()+";-fx-font-size:1.1em");
  else
    l.setStyle(l.getStyle().replaceAll(";\\-fx\\-font\\-size\\:1\\.1em",""));
  
  if (value != modvalue)
    l.getStyleClass().add((value < modvalue) ? better : worse);
 }
     
  @FXML
  public void onHealUnit() {
    if (unit.getCurrentHits() == unit.getHits())
      return;
    
    unit.recover();
    refreshUnit();
    root.fireEvent(new UnitChangeEvent(unit));
  }
  
  @FXML
  public void onDamageUnit() {
    if (unit.isDead())
      return;
    
    if (unit.hit()) { //Unit is defeated
      if (unit.getHeroEL() != Unit.NA) {
        //What about the heroes?
        String message= "Unit was defeated.\n"+RulesUtilities.rollHeroDefeated(0);
        
        Application.showMessage("Hero unit defeated", "Unit was defeated. Heroes were in it.",
            message, null);
        
        unit.detachHeroUnit();
      }   
    }
    
    refreshUnit();
    root.fireEvent(new UnitChangeEvent(unit));
  }
  
  @FXML
  public void onUnitRemove() {
    root.fireEvent(new UnitChangeEvent(unit, root, UnitChangeEvent.UNIT_REMOVE));
  }


 
}
