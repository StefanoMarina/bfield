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
package bfield.rules;

import bfield.data.Army;
import bfield.data.Battle;
import bfield.data.Condition;
import bfield.data.Unit;

/**
 * Mod Mechanics
 * Class that implements all condition rules to a single unit
 * ModMechs should be applied to use conditional rules in applying 
 * individual bonuses (i.e. everybut BUT class X gets a +1).
 * @author ste
 */
public abstract class UnitRules {
  
  protected final Battle context;
  
  public UnitRules(Battle context) {
    this.context = context;
  }
  
  public abstract Unit mod(Unit source, String ownerArmyID);
  public abstract Condition conditionFor(Unit u, Army enemy);

  public Battle getContext() {
    return context;
  }
  public abstract boolean embedHero(Unit unitToEmbed);
}
