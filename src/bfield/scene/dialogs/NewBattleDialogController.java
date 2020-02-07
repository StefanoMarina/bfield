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
package bfield.scene.dialogs;

import bfield.Application;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author ste
 */
public class NewBattleDialogController  {

  @FXML
  private TextField tBattleName;
  @FXML
  private TextField tHome;
  @FXML
  private TextField tAway;
  @FXML
  private ComboBox<String> cbRules;
  @FXML
  private TextArea taRules;

  
  public void initialize() {
    cbRules.getItems().addAll(Application.getApp().getFactories().keySet());
    
    cbRules.setOnAction( (a) -> {
      taRules.setText(
        Application.getApp().getFactories().get(cbRules.getValue())
                .getDescription() );
    });
    
    cbRules.getSelectionModel().select(0);
    taRules.setText(
        Application.getApp().getFactories().get(cbRules.getValue())
                .getDescription() );
  }
  
  public String getHomeName() {
    return tHome.getText();
  }
  
  public String getAwayName() {
    return tAway.getText();
  }
  
  public String getBattleName() {
    return tBattleName.getText();
  }
  
  public String getRulesName() {
    return cbRules.getValue();
  }
}
