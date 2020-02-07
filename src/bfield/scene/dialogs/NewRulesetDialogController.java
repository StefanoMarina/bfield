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
import bfield.data.Rules;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * FXML Controller class
 *
 * @author ste
 */
public class NewRulesetDialogController {

  @FXML
  private TextField txtName;
  @FXML
  private CheckBox shouldClone;
  @FXML
  private ComboBox<String> cbRuleset;
  @FXML
  private TextArea txtDesc;
  @FXML
  private TextField txtFolder;

    
  public void initialize() {
    cbRuleset.getItems()
            .addAll(FXCollections.observableArrayList(
            Application.getApp().getFactories().keySet()));
    
    cbRuleset.getSelectionModel().select(0);
    cbRuleset.setDisable(true);
    
    shouldClone.setSelected(false);
    shouldClone.setOnAction( (eh) -> {
      cbRuleset.setDisable(!shouldClone.isSelected());
    });
    
    //folder field
    
    txtFolder.addEventFilter(KeyEvent.KEY_TYPED, (el) ->{
      String character = el.getCharacter();
      if (character.matches("[\\\\!\"#$%&()*+,./:;<=>?@\\[\\]^_{|}~]+"))
        el.consume();
      if (txtFolder.getText().length()+character.length() >= 128)
        el.consume();
    });
  }
  
  public String getNewRulesetName() {
    return txtName.getText();
  }
  
  public String getNewRulesetDesc() {
    return txtDesc.getText();
  }
  
  public String getRequestedClone() {
    if (shouldClone.isSelected())
      return cbRuleset.getSelectionModel().getSelectedItem();
    else
      return null;
  }
  
  /**
   * Returns the user selected folder name
   * @return the folder's name or null if empty
   */
  public String getFolder() {
    return (txtFolder.getText().isEmpty()) ? null : txtFolder.getText();
  }
}
