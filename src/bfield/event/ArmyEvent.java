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
package bfield.event;

import bfield.data.Unit;
import bfield.data.Army;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
/**
 *
 * @author ste
 */
public class ArmyEvent extends Event {
   
  public static final EventType<ArmyEvent> ARMY_HIGHGROUND_CHANGED =
      new EventType<>(Event.ANY, "ARMY_HIGHGROUND_CHANGED");
  public static final EventType<ArmyEvent> ARMY_CHANGED =
      new EventType<>(Event.ANY, "ARMY_CHANGED");
  public static final EventType<ArmyEvent> ARMY_ADD_UNIT =
      new EventType<>(Event.ANY, "ARMY_ADD_UNIT");
  
  
  private final Army army;
  private String data;
  
  public final Army getArmy() {return army;}
  
  public ArmyEvent(Army a, EventType<ArmyEvent> type) {
    super(type);
    army = a;
  }
  
  public ArmyEvent(Army a, EventType<ArmyEvent> type, String data) {
    super(type);
    army = a;
    this.data = data;
  }

  public String getData() {
    return data;
  }
}
