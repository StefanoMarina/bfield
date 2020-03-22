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
package bfield.scene.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import bfield.data.Unit;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author ste
 */
public class CargoController {

  
  @FXML
  private ListView<Unit> lvCargo;

  private Color color;
  
  public void initialize() {
    lvCargo.setCellFactory(new Callback<ListView<Unit>, ListCell<Unit>>() {
      @Override
      public ListCell<Unit> call(ListView<Unit> param) {
        CargoCellController ccc = new CargoCellController();
        ccc.setArmyColor(color);
        return ccc;
      }
    });
  }
  
  public void setShip(Unit ship, Color color) {
    this.color = color;
    //get all items
    lvCargo.setItems(FXCollections.observableArrayList(ship.getCargo()));
  }
}
