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

import bfield.data.Unit;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author ste
 */
public class CargoCellController extends ListCell<Unit>{
 /**
   * Background color. if null the unit will be drawn
   * with black front and transparent background
   */
  protected Color armyColor = null;
  @FXML
  private Region rPictureBackground;
  @FXML
  private Region rPictureFront;
  @FXML
  private Label lblUnitData;

  private FXMLLoader loader;
  
  /**
   * sets the new background color.
   * @param armyColor null for transparent
   */
  public void setArmyColor(Color armyColor) {
    this.armyColor = armyColor;
  }

  /**
   * Returns the current background color.
   * @return null if transparent or set color.
   */
  public Color getArmyColor() {
    return armyColor;
  }

  @Override
  protected void updateItem(Unit item, boolean empty) {
    super.updateItem(item, empty);
    if (empty || item == null) {
      setText(null);
      setGraphic(null);
      return;
    }
    
    if (loader == null) {
      loader = new FXMLLoader(getClass().getResource("cargo_cell.fxml"));
      loader.setController(this);
      try {
        loader.load();
      } catch (Exception e) {
        throw new RuntimeException("Failure in loading cargocell fxml.");
      }
    }
    
    if (armyColor == null) {
      rPictureBackground.setVisible(false);
      rPictureFront.setStyle("-fx-background-color: black");
    } else if (item.isDead())  {
      rPictureBackground.setVisible(true);
      rPictureBackground.setStyle("-fx-background-color: darkgray");
      rPictureFront.setStyle("-fx-background-color: white");
    } else {
      rPictureBackground.setVisible(true);
      rPictureFront.setStyle("-fx-background-color: white");
      rPictureBackground.setStyle("-fx-background-color: " +
              armyColor.toString().replaceAll("0x","#"));
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(item.getOrdinal()).append(" ").append(item.getName())
            .append(item.getHits()-item.getCurrentHits())
            .append("/")
            .append(item.getHits());
    lblUnitData.setText(builder.toString());
    
    setGraphic(loader.<Node>getRoot());
  }
}
