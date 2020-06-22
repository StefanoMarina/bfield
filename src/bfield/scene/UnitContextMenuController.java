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
package bfield.scene;

import bfield.Application;
import bfield.data.Unit;
import bfield.event.ArmyEvent;
import bfield.event.UnitChangeEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * FXML Controller class
 *
 * @author ste
 */
public class UnitContextMenuController {
  
  @FXML
  private MenuItem miHit;
  @FXML
  private MenuItem miHeal;
  @FXML
  private CheckMenuItem miRetire;
  @FXML
  private MenuItem miEmbed;

  @FXML
  private CheckMenuItem cmiVisibility;
  @FXML
  private CheckMenuItem cmiTerrain;
  @FXML
  private CheckMenuItem cmisWeather;
  @FXML
  private CheckMenuItem cmiFortification;
  @FXML
  private CheckMenuItem cmisMountain;
  
  @FXML
  private ContextMenu root;
  @FXML
  private Menu mnuBunks;
  
  private java.util.WeakHashMap<Unit, ContextMenu> wmCargo;
  private Unit unit;
  @FXML
  private MenuItem miDelete;
  private java.util.WeakHashMap<Unit,ContextMenu> getCargoMap() {
    if (wmCargo == null)
      wmCargo = new java.util.WeakHashMap();
    return wmCargo;
  }
  
  public void setUnit(Unit unit) {
    this.unit = unit;
    
    mnuBunks.setVisible(unit.getClassName().contains("Ship"));
    miHit.setDisable(unit.isDead() || unit.isRetired());
    miHeal.setDisable(unit.getCurrentHits()==unit.getHits());
  }

  
  public MenuItem addBunkMenu(Unit u, ContextMenu cmnu) {
    mnuBunks.setVisible(true);

    if (getCargoMap().containsKey(u))
      return null;
    
    Menu cItem = new Menu(u.getName());
    MenuItem miDisembark = new MenuItem("Disembark");
    
    miDisembark.setOnAction( (eh) -> {
      root.fireEvent(new UnitChangeEvent(u, null, UnitChangeEvent.UNIT_DISEMBARK));
    });
    
    cItem.getItems().add(miDisembark);
    cItem.getItems().addAll(cmnu.getItems());
    
    mnuBunks.getItems().add(cItem);
    
    return cItem.getItems().get(0);
  }
  
  public Unit getUnit() {
    return unit;
  }

  @FXML
  private void onHit(ActionEvent event) {
    unit.hit();
    miHit.setDisable(unit.isDead() || unit.isRetired());
    miHeal.setDisable(unit.getCurrentHits()==unit.getHits());
    root.fireEvent(new UnitChangeEvent(unit));
  }

  @FXML
  private void onHeal(ActionEvent event) {
    unit.recover();
    miHit.setDisable(unit.isDead() || unit.isRetired());
    miHeal.setDisable(unit.getCurrentHits()==unit.getHits());
    root.fireEvent(new UnitChangeEvent(unit));
  }

  @FXML
  private void onRetire(ActionEvent event) {
    unit.setRetired(!unit.isRetired());
    ((CheckMenuItem)event.getSource()).setSelected(unit.isRetired());
    
    root.fireEvent(new UnitChangeEvent(unit));
  }

  @FXML
  private void onEmbedHero(ActionEvent event) {
    root.fireEvent (new UnitChangeEvent(getUnit(),null,
            UnitChangeEvent.UNIT_EMBED_HERO));
  }

  @FXML
  private void onStatusChange(ActionEvent event) {
    CheckMenuItem cmi = (CheckMenuItem) event.getSource();
    String property = cmi.getId().substring(3).toLowerCase();
    
    switch (property) {
      case "visibility" : unit.setIgnoreVisibility(!unit.getIgnoreVisibility());break;
      case "weather" : unit.setIgnoreWeather(!unit.getIgnoreWeather());break;
      case "terrain" : unit.setIgnoreTerrain(!unit.getIgnoreTerrain());break;
      case "fortification" :
        unit.setIgnoreFortification(!unit.getIgnoreFortification());break;
      case "mountain": 
        unit.setIgnoreMountain(!unit.getIgnoreMountain());
      break;
    }
    
    root.fireEvent(new UnitChangeEvent(unit));
  }
  
  public void setDeleteAction(EventHandler<ActionEvent> handler) {
    miDelete.setOnAction(handler);
  }
}
