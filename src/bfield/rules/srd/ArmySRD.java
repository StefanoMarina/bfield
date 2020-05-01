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
package bfield.rules.srd;

import bfield.data.*;
import bfield.event.UnitChangeEvent;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
/**
 *
 * @author ste
 */
public class ArmySRD extends bfield.rules.ArmyRules {

  public ArmySRD(Battle b, String armyID) {
    super(b, armyID);
  }


  /**
   * SRD Attack
   * Average attack bonus
   * Hill high ground applied
   * custom bonus applied
   * @return 
   */
  @Override
  public double getAttack() {
    double value = 0.0;
    
    Unit[] aliveUnits = army.getActiveUnits();
    
    int shipsValue = 0, shipsCount = 0;
    
    for (Unit u : aliveUnits) {
      Unit modUnit = modEnvironment.mod(u, army.getID());
      if (modUnit.getClassName().contains("Ship")) {
       shipsValue+= modUnit.getMelee();
       shipsCount++;
      }
      else
        value += modUnit.getMelee();
    }
    
    value = (value / aliveUnits.length);
    if (shipsCount > 0 && shipsValue > 0)
      value += (shipsValue / shipsCount);
    
    /**
     * Hill high ground - 1st attack only, but whole army
    */
    if (modEnvironment.getContext().getTerrain().getHighGroundRule().contains("hill")) {
      value += UnitSRD.getHighGroundModifier(army.getEnemy(), 
              modEnvironment.getContext().getRules());
    }
    
    return value  + army.getAttackMod();
  }

  @Override
  public double getDefense() {
   double value = 0.0;
    
    Unit[] aliveUnits = army.getActiveUnits();
    
    for (Unit u : aliveUnits) {
      Unit modUnit = modEnvironment.mod(u, army.getID());
      value += modUnit.getDef();
    }
    
    value = (value / aliveUnits.length);
    /**
     * Hill high ground - 1st attack only, but whole army
    */
    if (modEnvironment.getContext().getTerrain().getHighGroundRule().contains("hill")) {
      value += UnitSRD.getHighGroundModifier(army.getEnemy(), 
              modEnvironment.getContext().getRules());
    }
    
    return value  + army.getDefenseMod();
  }

  /**
   * Special attack
   * adds the best between charge, melee or missile
   * @return 
   */
  @Override
  public double getSpecial() {
    double value = 0.0;
    
    int shipsValue = 0, shipsCount= 0;
        
    Unit[] aliveUnits = army.getActiveUnits();
    
    for (Unit u : aliveUnits) {
      Unit modUnit = modEnvironment.mod(u, army.getID());
      
      if (modUnit.getClassName().contains("Ship")) {
          shipsValue+= modUnit.getMissile();
          shipsCount++;
      }
    
      value += Math.max(
              modUnit.getMelee(), Math.max(
              modUnit.getMissile(), modUnit.getCharge()));
    }
    
    value = (value / aliveUnits.length);
    if (shipsValue > 0 && shipsCount > 0)
      value += (shipsValue / shipsCount);
    
    /**
     * Hill high ground - 1st attack only, but whole army
    */
    if (modEnvironment.getContext().getTerrain().getHighGroundRule().contains("hill")) {
      value += UnitSRD.getHighGroundModifier(army.getEnemy(), 
              modEnvironment.getContext().getRules());
    }
    
    return value  + army.getAttackMod();
  }

  @Override
  public boolean embedHero(Unit unitToEmbed) {
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
         unitToEmbed.attachHeroUnit(Integer.parseInt(result.get()));
         return true;
       } catch (NumberFormatException e) {
         Alert alert = new Alert (Alert.AlertType.INFORMATION);
         alert.setTitle("Could not parse number");
         alert.setHeaderText(null);
         alert.setContentText(result.get() + " is not a valid integer number.");
         return false;
       }
     } else
       return false;
  }
}
