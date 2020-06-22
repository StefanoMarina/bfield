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

import bfield.data.Battle;
import bfield.data.Rules;
import bfield.rules.BattleRules;
import java.util.Map;

import bfield.data.*;
import bfield.rules.*;
import java.util.Arrays;
import java.util.Random;
/**
 *
 * @author ste
 */
public class BattleMM implements BattleRules {

  private class Results {
    int hitten;
    int destroyed;
    int promoted;
    int defeated;
  }
  
  @Override
  public Map<String, String> doBattle(Rules rules, Battle battle) {
    Army home = battle.getArmy(Battle.ID_HOME),
         away = battle.getArmy(Battle.ID_AWAY);
    
    ArmyRules arHome = battle.getArmyRules(home.getID()),
            arAway = battle.getArmyRules(away.getID());
    
    java.util.Map<String, String> result = new java.util.HashMap();
    
    double hBCR = arHome.getDefense(), aBCR = arAway.getDefense();
    if (hBCR >= 10 && ((hBCR / 2.0) >= aBCR) ) {
      result.put("main","<p>"+ home.getName() + " crushed " + away.getName() + "!</p>"
                        + "<p><small>BCR ("+hBCR+") &gt; 10 and more than half of " + arAway.getArmy().getName() + "'s BCR ("+aBCR+")</small></p>"
      );
      return result;
    } else if (aBCR >= 10 && ((aBCR / 2.0) >= hBCR) ) {
      result.put("main", "<p>"+away.getName() + " crushed " + home.getName()+"!</p>"
                                + "<p><small>BCR ("+aBCR+") &gt; 10 and more than half of " + arHome.getArmy().getName() + "'s BCR ("+hBCR+")</small></p>"
      );
      return result;
    }
    
    //Now that BCR is resolved, let's fight
    Random die = new Random();
    final StringBuilder strHome = new StringBuilder(), strAway = new StringBuilder();
    
    Arrays.asList( new ArmyRules[]{arHome, arAway} ).forEach( (ArmyRules armyRule)
            -> {
      final double baseMod = armyRule.getAttack();
      final UnitRules ur = armyRule.getModEnvironment();
      final Results results = new Results();
      final Army army = armyRule.getArmy();
      
      army.getActiveUnitsStream().forEach( (Unit u) ->{
        StringBuilder builder = (army == arHome.getArmy()) ? strHome : strAway;
        int mod = 0;
        int dieRoll = 0;
        mod = (int)baseMod+ur.mod(u, army.getID()).getMelee();
        dieRoll = die.nextInt(6) + 1 + mod;
        
        if (dieRoll <= 1)
          results.defeated += 1;
        
        switch (dieRoll) {
          case 1: case 2: case 3: case 4: {
            if (u.hit()) {
              results.destroyed += 1;
              builder.append("<p>").append(u.getOrderAndName()).append(" was destroyed.</p>");
            } else {
              results.hitten += 1;
              builder.append("<p>").append(u.getOrderAndName()).append(" was hit.</p>");
            }
          } break;
          case 5: case 6: break;
          default: {
            if (dieRoll < 1) {
              if (u.die()) {
                results.destroyed+=1;
                builder.append("<p>").append(u.getOrderAndName()).append(" was destroyed.</p>");
              }
            } else if (dieRoll >= 7 && !"LtIrr-".equals(u.getClassName())
                    && !u.getClassName().contains("+")) {
              //promote unit
              u.setDef(u.getDef()+1);
              UnitBuilder.setUnitExperience(u, "+");
              results.promoted +=1;
              builder.append("<p>").append(u.getOrderAndName()).append(" was promoted to veteran!</p>");
            }
              
          }break;
        }
      });
      /*
      if (results.defeated == 0)
        result.put(army.getID(), army.getName() + " never suffered 1 or less");
      else
        result.put(army.getID(), army.getName() + " got 1 or less " + 
                results.defeated + " times.");
      
      if (results.destroyed > 0)
        result.put(army.getID(), army.getName() + " lost " + results.destroyed 
        +  " units.");
      
      if (results.hitten > 0) 
        result.put(army.getID(), army.getName() + " suffered " + results.hitten 
        +  " attacks but survived.");
      
      if (results.promoted > 0) {
        result.put(army.getID(), army.getName() + " saw " + results.promoted
                + " units rise to veteran status!");
      }*/
    });
    
    result.put(arHome.getArmy().getID(), strHome.toString());
    result.put(arAway.getArmy().getID(), strAway.toString());
    
    return result;
  }

  @Override
  public String toString() {
    return "BCR vs BCR, then 1d6+A each";
  }
  
  
}
