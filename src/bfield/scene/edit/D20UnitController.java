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
package bfield.scene.edit;

import bfield.Utilities;
import bfield.data.Unit;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class
 *
 * @author ste
 */
public class D20UnitController implements EditionData  {

  @FXML
  private ComboBox<Integer> cbMorale;
  @FXML
  private ComboBox<Integer> cbMelee;
  @FXML
  private ComboBox<Integer> cbCharge;
  @FXML
  private ComboBox<Integer> cbMissile;
  @FXML
  private CheckBox chkCharge;
  @FXML
  private CheckBox chkMissile;
  @FXML
  private ComboBox<Integer> cbMove;

  public void initialize() {
     Integer[] baseValueArray = Utilities.generatePositiveLinearArray(-10, 30);
     
     cbMorale.getItems().addAll(Utilities.generatePositiveLinearArray(0, 30));
     cbMove.getItems().addAll(Utilities.generatePositiveLinearArray(0, 30));
     cbMove.getSelectionModel().select(2);
     
     cbMelee.getItems().addAll(baseValueArray);
     cbCharge.getItems().addAll(baseValueArray);
     cbMissile.getItems().addAll(baseValueArray);
     
     chkCharge.setOnAction( (el) ->{
       cbCharge.setDisable(!chkCharge.isSelected());
     });
     chkMissile.setOnAction( (el) ->{
       cbMissile.setDisable(!chkMissile.isSelected());
     });
  }
  
  @Override
  public void setData(Unit u) {
    cbMove.getSelectionModel().select((Integer)u.getMovement());
    cbMelee.getSelectionModel().select((Integer)u.getMelee());
    cbMorale.getSelectionModel().select((Integer)u.getMorale());
    
    if (u.getChargeBonus() != Unit.NA) {
      chkCharge.setSelected(true);
      cbCharge.getSelectionModel().select((Integer)u.getChargeBonus());
    } else 
      chkCharge.setSelected(false);
    
    chkCharge.getOnAction().handle(null);
    
    
    if (u.getMissile() != Unit.NA) {
      chkMissile.setSelected(true);
      cbMissile.getSelectionModel().select((Integer)u.getMissile());
    } else
      chkMissile.setSelected(false);
    
    chkMissile.getOnAction().handle(null);
  }

  @Override
  public Map<String, String> getData() {
    java.util.Map<String,String> result = new
          java.util.HashMap();
    
    result.put("movement", cbMove.getSelectionModel().getSelectedItem().toString());
    result.put("melee", cbMelee.getSelectionModel().getSelectedItem().toString());
    result.put("morale", cbMorale.getSelectionModel().getSelectedItem().toString());
    result.put("missile", !chkMissile.isSelected()
            ? String.valueOf(Unit.NA)
            : cbMissile.getSelectionModel().getSelectedItem().toString());
    result.put("charge", !chkCharge.isSelected()
            ? String.valueOf(Unit.NA)
            : cbCharge.getSelectionModel().getSelectedItem().toString());
    return result;
  }
}
