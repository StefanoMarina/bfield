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
package bfield.rules.mm;

import bfield.data.Army;
import bfield.data.Battle;
import bfield.data.Condition;
import bfield.data.Unit;
import bfield.rules.ArmyRules;
import bfield.rules.UnitRules;

/**
 *
 * @author ste
 */
public class UnitMM extends UnitRules {
  
  public UnitMM(Battle context) {
    super(context);  
  }

  @Override
  public Unit mod(Unit source, String ownerArmyID) {
    //apparently, you don't mod units
    Unit result = source.clone();
    
    if (result.getMelee() == Unit.NA)
      result.setMelee(0);
    
    if (result.getIgnoreTerrain())
      result.setMelee(1);
    
    if (result.getHeroEL() > 0)
      result.setMelee(result.getMelee()+1);
    
    return result;    
  }

  @Override
  public Condition conditionFor(Unit u, Army enemy) {
    return null;
    
    
    //boolean bYouHaveKnights = enemy.
            
  }
  
}
