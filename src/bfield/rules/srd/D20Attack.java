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
import java.util.Map;
import javafx.fxml.FXMLLoader;

/**
 * d20
 * instead of fixed + mod attack, it's 0 + (roll+attack)
 * This is useful because in standard formula fixedNumber is called only
 * once, getAttackValue is the only army-dependant value.
 * @author ste
 */
public class D20Attack extends BattleSRD {

  java.util.Map<String,Integer> rolls;

  @Override
  protected double getAttackValue(Army army, Battle b) {
    if (rolls == null || rolls.isEmpty()){
      System.out.println("called d20/getAttackValue with empty result.");
      return bfield.RulesUtilities.roll(20, 
              (int)b.getArmyRules(army.getID()).getAttack());
    } else
      return rolls.get(army.getName())+b.getArmyRules(army.getID()).getAttack();
  }
  
  @Override
  protected Integer getFixedNumber(Rules r) {return 0;}
  
  @Override
  public String toString() {
    return "1d20 + attack";
  }

  @Override
  public Map<String, String> doBattle(Rules rules, Battle battle) {
    //call dice rolls
    rolls = SRDUtils.doubleRoll(battle.getArmy(Battle.ID_HOME).getName(),
              battle.getArmy(Battle.ID_AWAY).getName());
    
    if (rolls == null)
      return null;
    
    java.util.Map<String,String> result = super.doBattle(rules, battle);
    rolls = null;
    return result;
  }
  
  
}
