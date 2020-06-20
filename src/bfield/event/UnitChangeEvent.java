/*
 * Copyright (C) 2019 Stefano
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
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;

/**
 *
 * @author Stefano
 */
public class UnitChangeEvent extends javafx.event.Event {
  
  public static final EventType<UnitChangeEvent> UNIT_CHANGE =
      new EventType<>(Event.ANY, "UNIT_CHANGE");
  public static final EventType<UnitChangeEvent> UNIT_REMOVE =
      new EventType<>(Event.ANY, "UNIT_REMOVE");
  public static final EventType<UnitChangeEvent> UNIT_CHANGE_TERRAIN =
      new EventType<>(Event.ANY, "UNIT_CHANGE_TERRAIN");
  public static final EventType<UnitChangeEvent> UNIT_EMBED_HERO =
      new EventType<>(Event.ANY, "UNIT_EMBED_HERO");
  public static final EventType<UnitChangeEvent> UNIT_DISEMBARK =
      new EventType<>(Event.ANY, "UNIT_DISEMBARK");
  
  private final Unit unit;
  
  public UnitChangeEvent(Unit u) {
    super(UNIT_CHANGE);
    this.unit = u;
  }

  public UnitChangeEvent(Unit u, Node source, EventType<UnitChangeEvent> type) {
    super(source, null, type);
    this.unit = u;
  };
  
  public Unit getUnit() {
    return unit;
  }
  
  
}
