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

import bfield.Application;
import bfield.data.XMLFactory;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

/**
 * FXML Controller class
 *
 * @author ste
 */
public class UnitSelectorController {

  @FXML
  private ComboBox<String> cbRuleset;
  @FXML
  private ListView<String> lsView;

  
  public void initialize() {
    cbRuleset.getItems().addAll(Application.getApp().getFactories().keySet());
    
    cbRuleset.setOnAction ( (eh) ->{
      if (lsView.getItems().size() > 0)
        lsView.getItems().remove(0, lsView.getItems().size());
      
      XMLFactory xf = Application.getApp().getFactories().get(cbRuleset.
              getSelectionModel().getSelectedItem());
      
      lsView.getItems().addAll(FXCollections.observableArrayList(
        xf.getUnitFactory().getAllNames()));
    });
    
    cbRuleset.getSelectionModel().select(0);
    cbRuleset.getOnAction().handle(null);
  }
  
  public String getSelection() {
    return lsView.getSelectionModel().getSelectedItem();
  }

  public String getRuleset() {
    return cbRuleset.getSelectionModel().getSelectedItem();
  }
}
