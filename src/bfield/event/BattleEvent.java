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

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author ste
 */
public class BattleEvent extends Event {
  
  public static final EventType<BattleEvent> BATTLE_GENERAL_CHANGE =
      new EventType<>(Event.ANY, "BATTLE_GENERAL_CHANGE");
  
  public BattleEvent(EventType<? extends Event> et) {
    super(et);
  }
  
}
