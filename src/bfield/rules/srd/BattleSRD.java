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
    final Army home = battle.getArmies().get(Battle.ID_HOME),
         away = battle.getArmies().get(Battle.ID_AWAY);
    java.util.Map<String,String> res = new java.util.HashMap();
    StringBuilder homeStr = new StringBuilder(), 
                  awayStr = new StringBuilder(),
                  mainStr = new StringBuilder();
    
    final int VALUE = getFixedNumber(rules),
              MIN = rules.getMinimalDamage();
    
    final double MOD = rules.getBaseAttackMultiplier();
 
    final ArmyRules rHome = battle.getArmyRules(home.getID()),
            rAway = battle.getArmyRules(away.getID());
 
    double multiplier = VALUE + getAttackValue(home, battle) - rAway.getDefense();
           multiplier *= MOD * home.getActiveUnitsSize();
           
    final int resultHome = (int)Math.max(Math.floor(multiplier), MIN);
    
    
    //away attack
    multiplier = VALUE + getAttackValue(away, battle) - rHome.getDefense();
    multiplier *= MOD * home.getActiveUnitsSize();
    final int resultAway = (int)Math.max(Math.floor(multiplier), MIN);
    
    
    if (battle.getTerrain().getHighGroundRule().contains("hill") &&
            battle.getRound() == 1) {
      if (home.hasHighGround()) {
        mainStr.append("<p>").append(home.getName()).append(" charged downhill, losing the high ground.");
        home.setHighGround(false);
      } else if (away.hasHighGround()) {
        mainStr.append("<p>").append(home.getName()).append(" charged downhill, losing the high ground.");
        away.setHighGround(false);
      }
    }
    
    if (resultHome == resultAway && (homeStr.length()+awayStr.length()==0)) {
      mainStr.append("<p>The armies held their ground.<strong>Both</strong> receive ")
              .append(resultHome).append(" point(s) of damage.</p>");
    } else {
      res.put(home.getID(), elaborateResult(homeStr, home, resultHome, resultAway).toString());
      res.put(away.getID(), elaborateResult(awayStr, away, resultAway, resultHome).toString());
    }
    
    res.put("main", mainStr.toString());
    return res;
  }
  
  /**
   * Utility to write more nice results. all results are under <p>.
   * @param builder a valid stringbuilder
   * @param a valid army
   * @param result army's damage dealt
   * @param damage army's damage suffered
   * @return builder
   */
  private StringBuilder elaborateResult(StringBuilder builder, Army a, int result, int damage) {
    builder.append("<p>");
    if (result == 0)
      builder.append(a.getName()).append(" was overwhelmed and scored no damage point.");
    else
      builder.append(a.getName()).append(" scored ").append(result).append(" damage point(s).");
    builder.append("</p>");
    return builder;
  }
}
