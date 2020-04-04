/*
 * Copyright (C) 2020 ste
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
import bfield.IconFont;
import bfield.Utilities;
import bfield.data.Unit;
import bfield.rules.UnitRules;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author ste
 */
public class UnitCellController {

  @FXML
  private Label lblName;
  @FXML
  private Region rPictureBackground;
  @FXML
  private Region rPictureIcon;
  
  /**
   * Unit rules to mod original unit.
   */
  private UnitRules modEnvironment;
  
  /**
   * Army id, to reference unit. Army IDs are final
   */
  private String armyId;
  
  /**
   * Army color, to reference army color
   */
  private Color armyColor;
  
  @FXML
  private Label lDef;
  
  @FXML
  private Label lMel;
  @FXML
  private Label lMis;
  @FXML
  private Label lSpc;
  
  @FXML
  private ProgressBar pBar;
  @FXML
  private GridPane gpStatuses;
  
  private Unit sourceUnit;
  
  private java.util.Map<String,Boolean> showStatus;
  
  /**
   * Allows change in army color
   * @param armyColor if null, item will be transparent
   */
  public void setArmyColor(Color armyColor) {
    this.armyColor = armyColor;
  }

  public void setArmyId(String armyId) {
    this.armyId = armyId;
  }
  
  public Unit getUnit() {return sourceUnit;}
  
  public void initialize() {
   
  }

  
  public void setStatusEnabled(String status, Boolean value) {
    if (showStatus == null)
      showStatus = new java.util.HashMap();
    
    showStatus.put(status, value);
  }
  
  public void setModEnvironment(UnitRules modEnvironment) {
    this.modEnvironment = modEnvironment;
  }


  public void setUnit(String armyID, Unit u, UnitRules environment) {
    setArmyId(armyID);
    sourceUnit = u;
    modEnvironment = environment;
    refreshUnit();
  }
  
  public void refreshUnit() {
    Unit unit = (modEnvironment != null) 
            ? (modEnvironment.mod(sourceUnit, armyId))
            : sourceUnit;

      //load and set unit symbol
    String svgFile = (unit.getIcon() != null ? unit.getIcon()
        : Application.getApp().getAppropriateIcon(unit.getClassName()));
    try {
      String svg = Utilities.loadSVGFile(svgFile);
      rPictureIcon.setStyle("-fx-shape: '"+svg+"'");
    } catch (Exception e) {
        System.out.println("::setUnit: Could not load " + svgFile);  
    }

    /*rPictureIcon.getStyleClass().clear();
    rPictureIcon.getStyleClass().add("picture-front");*/
  
    //Background color
    if (armyColor == null) {
      rPictureBackground.setVisible(false);
      rPictureIcon.setStyle("-fx-background-color: black");
    } else if (unit.isDead())  {
      rPictureBackground.setVisible(true);
      rPictureBackground.setStyle("-fx-background-color: darkgray");
    } else {
      rPictureBackground.setVisible(true);
      rPictureBackground.setStyle("-fx-background-color: " +
              armyColor.toString().replaceAll("0x","#"));
    }
 
    lblName.setFont(Application.getApp().getStatusFont());
    
    if (unit.getOrdinal() != null) {
    lblName.setText(
            bfield.tools.RomanNumerals.toRoman(unit.getOrdinal()) 
        + " " + sanitizeName(unit.getName()));
    } else {
      lblName.setText(sanitizeName(unit.getName()));
    }
    
    style(unit.getDef(),     lDef, "b-def");
    style(unit.getMelee(),   lMel, "b-melee");
    style(unit.getMissile(), lMis, "b-missile");
    style(unit.getChargeBonus(),  lSpc, "b-special");
    
    //health bar
    if (!unit.isDead()) {
      pBar.setVisible(true);
      pBar.setProgress(unit.getCurrentHits()/unit.getHits());
    } else {
      pBar.setVisible(false);
    }
    
    if (showStatus == null)
      showStatus = new java.util.HashMap();
    
    //sttuses
    gpStatuses.getChildren().clear();
    if (showStatus.getOrDefault("visibility", true) && unit.getIgnoreVisibility())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_VISIBILITY,"bonus"));
    if (showStatus.getOrDefault("terrain", true) && unit.getIgnoreTerrain())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_TERRAIN,"bonus"));
    if (showStatus.getOrDefault("mountain", true) && unit.getIgnoreMountain())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_MOUNTAIN,"bonus"));
    if (showStatus.getOrDefault("weather", false) && unit.getIgnoreWeather())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_WEATHER,"bonus"));
    
    
    if (showStatus.getOrDefault("visibility", true) && !unit.getIgnoreVisibility() &&
            modEnvironment.getContext().getVisibility().isBad())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_VISIBILITY,"malus"));
    if ( showStatus.getOrDefault("weather", true) && !unit.getIgnoreWeather() &&
            modEnvironment.getContext().getWeather().isBad())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_WEATHER,"malus"));
    if (showStatus.getOrDefault("terrain", true) && !unit.getIgnoreTerrain() &&
            modEnvironment.getContext().getTerrain().isBad())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_TERRAIN,"malus"));
    if (showStatus.getOrDefault("mountain", true) && !unit.getIgnoreMountain()&&
            modEnvironment.getContext().getTerrain().isBad())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_MOUNTAIN,"malus"));
  }
 
  /**
  * Utility to create a status label
  * @param shape name of the icon
  * @param type bonus or malus
  * @return a label
  */ 
  private Label createStatusLabel(String shape, String type) {
    Label label = new Label();
    //label.setFont(Application.getApp().getStatusFont());
    label.setText(shape);
    label.getStyleClass().add("status-label");
    label.getStyleClass().add(type);
    return label;
  }
  
   
  /**
   * Adds a status to the grid
   * @param newStatus 
   */
  private void enqueueStatusDisplay(Node newStatus) {
    int nextR, nextC;
    if (!gpStatuses.getChildren().isEmpty()) {
      //int nextSlot = (int)(Math.ceil((double)gpStatuses.getChildren().size()/12.0));
      nextR = gpStatuses.getChildren().size() / 4;
      nextC = gpStatuses.getChildren().size() - (nextR*4);
    } else {
      nextR = 0;
      nextC = 0;
    }
    
    gpStatuses.add(newStatus, nextC, nextR);
  }
  
  /**
   * Utility to style text. If an attribute is not set, "--" will be print
   * and background class will be dark grey.
   * @param value current value
   * @param text attribute label
   * @param okClass which background class represents a valid value
   */
  private void style (int value, Label text, String okClass) {
    text.getStyleClass().remove(okClass);
    text.getStyleClass().remove("b-disabled");
    if (value == Unit.NA) {
      text.getStyleClass().add("b-disabled");
      text.setText("-");
    } else {
      text.getStyleClass().add(okClass);
      text.setText(String.valueOf(value));
    }
    text.setTooltip(new Tooltip(String.valueOf(value)));
  }
  
  private String sanitizeName(String unitName) {
    if (unitName.length() > 20) {
      int iSpace = unitName.indexOf(' ');
      if (iSpace == -1)
        return unitName.substring(0, 20)+".";
      
      return unitName.charAt(0) + ". " + unitName.substring(iSpace+1);
    }
    
    return unitName;
  }
}
