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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author ste
 */
public class SrdRollController {

  @FXML
  private Label lblHomeRoll;
  @FXML
  private TextField txtHomeRoll;
  @FXML
  private Label lblAway;
  @FXML
  private TextField txtAwayRoll;

  public void initialize() {
    txtHomeRoll.setText(String.valueOf(bfield.RulesUtilities.roll(20, 0)));
    txtAwayRoll.setText(String.valueOf(bfield.RulesUtilities.roll(20, 0)));
  }
  
  public void setUnits(String home, String away) {
    lblHomeRoll.setText(home);
    lblAway.setText(away);
  }
  
  public java.util.Map<String,Integer> getRolls() {
    java.util.Map<String,Integer> result = new java.util.HashMap();
    
    try {
      result.put(lblHomeRoll.getText(), Integer.parseInt(txtHomeRoll.getText()));
      result.put(lblAway.getText(), Integer.parseInt(txtAwayRoll.getText()));
      return result;
    } catch (Exception e) {
      return null;
    }
  }
}
