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

import bfield.Application;
import bfield.scene.dialogs.SrdRollController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author ste
 */
public interface SRDUtils {
  
  public static java.util.Map<String,Integer> doubleRoll(String home, String away) {
    java.util.Map<String,Integer> result = null;
    
    FXMLLoader loader = new FXMLLoader(Application.class.getResource(
      "/bfield/scene/dialogs/srdRoll.fxml"));
      
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("User roll");
    SrdRollController controller;
    
    try {
      javafx.scene.Node obj = loader.load();
      controller = loader.<SrdRollController>getController();
      alert.getDialogPane().setContent(obj);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
    
    controller.setUnits(home, away);
    
    alert.showAndWait();
    if (alert.getResult() != ButtonType.OK)
      return null;
    else {
      return controller.getRolls();
    }
  }
}
