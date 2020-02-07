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
import bfield.data.Unit2nd;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author ste
 */
public class EditUnitController  implements EditionData {

  @FXML
  private ComboBox<String> cbType;
  @FXML
  private ComboBox<String> cbIcon;
  @FXML
  private ComboBox<String> cbClass;
  @FXML
  private ComboBox<String> cbSpeed;
  @FXML
  private ComboBox<String> cbLevel;
  @FXML
  private ComboBox<Integer> cbDefense;
  
  @FXML
  private ComboBox<Integer> cbHits;

  
  
  @FXML
  private VBox root;
  @FXML
  private ToggleButton tgVis;
  @FXML
  private ToggleButton tgWeather;
  @FXML
  private ToggleButton tgMountain;
  @FXML
  private ToggleButton tgFortification;
  @FXML
  private ToggleButton tgTerrain;
  
  private EditionData controller;
  @FXML
  private GridPane grid;
  @FXML
  private TextField txtName;
  
  public void initialize() {
    //Icon controller
    File path = new File(System.getProperty("user.dir")
            + File.separator + "svg");
    
    String[] files = path.list((File dir, String name) 
            -> (name.contains(".svg")));
    
    cbIcon.getItems().add("(Class default)");
    cbIcon.getItems().addAll(files);
    cbIcon.setValue("-Select SVG icon-");
    
    final Integer[] a1to30 = bfield.Utilities.generatePositiveLinearArray(1, 30);
    cbDefense.getItems().addAll( a1to30 );
    cbDefense.setValue(10);
  //  cbMove.getItems().addAll(a1to30);
  //  cbMove.setValue(2);
    
    //loadController("d20Unit.fxml");
  }
  
  private void loadController(String resource) {
   
    FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
    try {
      loader.load();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
    
    controller = loader.getController();
    //if (grid.getChildren().size() > 1)
    //  grid.getChildren().remove(root.getChildren().size()-1);
    
    GridPane child = loader.<GridPane>getRoot();
    /*GridPane.setRowIndex(child, 6);
    GridPane.setColumnIndex(child, 0);
    GridPane.setColumnSpan(child, 5);
    GridPane.setRowSpan(child, 2);*/
    
    root.getChildren().add(child);
  }

  
  @Override
  public void setData(Unit u) {
    txtName.setText(u.getName());
    
    if (u instanceof Unit2nd) {
      cbType.getSelectionModel().select(1);
    } else 
      cbType.getSelectionModel().select(0);
    
    cbClass.getSelectionModel().select(Utilities.classGetFullName(u.getClassName()));
    cbDefense.getSelectionModel().select((Integer)u.getDef());
    cbHits.getSelectionModel().select((Integer)u.getHits());
    if (u.getIcon()==null)
      cbIcon.getSelectionModel().select(0);
    else if (cbIcon.getItems().contains(u.getIcon()))
      cbIcon.getSelectionModel().select(u.getIcon());
    else {
      System.err.println("editUnit: list did not contain " + u.getIcon());
      cbIcon.getSelectionModel().clearSelection();
      cbIcon.setValue(u.getIcon());
    }
   // if (u.getMovement() instanceof Integer)
   //   cbMove.getSelectionModel().select((Integer)u.getMovement());
    
    if (u.getClassName().contains("+"))
      cbLevel.getSelectionModel().select("Veteran");
    else if (u.getClassName().contains("-"))
      cbLevel.getSelectionModel().select("Green");
    else
      cbLevel.getSelectionModel().select("Regular");
    
    String speed = u.getClassName().substring(0,2);
    switch (speed) {
      case "Hv": cbSpeed.getSelectionModel().select("Heavy");
      case "Lt": cbSpeed.getSelectionModel().select("Light");
      default : cbSpeed.getSelectionModel().select("Medium");
    }
    
    if (u instanceof Unit) {
      loadController("d20Unit.fxml");
    } else
      loadController("adndUnit.fxml");
    
    controller.setData(u);
  }

  @Override
  public Map<String, String> getData() {
    java.util.Map<String,String> result = controller.getData();
    
    result.put("name", txtName.getText());
    result.put("className", cbClass.getSelectionModel().getSelectedItem());
    result.put("defense", cbDefense.getSelectionModel().getSelectedItem().toString());
    result.put("hits", cbHits.getSelectionModel().getSelectedItem().toString());
    
    if (cbIcon.getSelectionModel().getSelectedIndex() == 0)
      result.put("icon", null);
    else
      result.put("icon", cbIcon.getSelectionModel().getSelectedItem());
    
    result.put("level", cbLevel.getSelectionModel().getSelectedItem());
//    result.put("move", cbMove.getSelectionModel().getSelectedItem().toString());
    result.put("speed", cbSpeed.getSelectionModel().getSelectedItem());
    
    return result;
  }
}
