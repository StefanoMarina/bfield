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
import bfield.rules.ArmyRules;
import java.util.Properties;
import java.util.Map;
import bfield.rules.BattleRules;

/**
 * This class implemens the base formula and all his variations
 * @author ste
 */
public abstract class BattleSRD implements BattleRules {
  
  protected abstract double getAttackValue(Army army, Battle b);
  protected abstract Integer getFixedNumber(Rules r);
  
  /**
   * Base SRD Formula
   * (11+HomeAttack-EnemyDef)*0.05*ActiveUnits
   * 9/10 will be 1, which is the minimum
   * @param rules
   * @param battle
   * @return 
   */
  @Override
  public Map<String,String> doBattle(Rules rules, Battle battle) {
    Army home = battle.getArmies().get(Battle.ID_HOME),
         away = battle.getArmies().get(Battle.ID_AWAY);
    
    final int VALUE = getFixedNumber(rules),
              MIN = rules.getMinimalDamage();
    
    final double MOD = rules.getBaseAttackMultiplier();
 
    final ArmyRules rHome = battle.getArmyRules(home.getID()),
            rAway = battle.getArmyRules(away.getID());
 
    double multiplier = VALUE + getAttackValue(home, battle) - rAway.getDefense();
           multiplier *= MOD * home.getActiveUnitsSize();
           
    int resultHome = (int)Math.max(Math.floor(multiplier), MIN);
    
    
    //away attack
    multiplier = VALUE + getAttackValue(away, battle) - rHome.getDefense();
    multiplier *= MOD * home.getActiveUnitsSize();
    int resultAway = (int)Math.max(Math.floor(multiplier), MIN);
    
    java.util.Map<String,String> res = new java.util.HashMap();
    
    if (battle.getTerrain().getHighGroundRule().contains("hill") &&
            battle.getRound() == 1) {
      if (home.hasHighGround()) {
        res.put(home.getID(), home.getName() + " lost the high ground.");
        home.setHighGround(false);
      } else if (away.hasHighGround()) {
        res.put(away.getID(), away.getName() + " lost the high ground.");
        away.setHighGround(false);
      }
    }
    
    res.put(home.getID(), home.getName() + " scored " + String.valueOf(resultHome) + " hits.");
    res.put(away.getID(), away.getName() + " scored " + String.valueOf(resultAway) + " hits.");
    
    return res;
  }
}
