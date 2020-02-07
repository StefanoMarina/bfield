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

import bfield.data.Army;
import bfield.data.Battle;
import bfield.data.Rules;

/**
 *
 * @author ste
 */
public class FixedSpecial extends FixedAttack {

  @Override
  protected double getAttackValue(Army army, Battle b) {
   bfield.rules.ArmyRules ar = b.getArmyRules(army.getID());
    
      return ar.getSpecial();
    
  }

  @Override
  public String toString() {
    return "First attack, fixed";
  }
  
  
}
