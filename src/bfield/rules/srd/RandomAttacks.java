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
import bfield.data.Unit;
import bfield.rules.UnitRules;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import bfield.rules.BattleRules;

/**
 * Everybody in the army randomly attacks another unit
 * @author ste
 */
public class RandomAttacks implements BattleRules {

  @Override
  public String toString() {
    return "Random 1 vs 1, using their best";
  }

  @Override
  public Map<String, String> doBattle(Rules rules, Battle battle) {
  Army home = battle.getArmies().get(Battle.ID_HOME),
         away = battle.getArmies().get(Battle.ID_AWAY);
    
    //Create clone armies
    java.util.List<Unit> homeUnits 
            = new java.util.ArrayList(
            Arrays.<Unit>asList(home.getActiveUnits()));
    java.util.List<Unit> awayUnits 
            = new java.util.ArrayList(
                    Arrays.<Unit>asList(away.getActiveUnits()));
    
    UnitRules mod = battle.getUnitRules();
       
    Random dice = new Random();
    int resultHome = 0, resultAway = 0;
    
    /*
      We need to map damage, as damage must be applied AFTER skimirsh
    */
    java.util.HashMap<Unit,Integer> damageMap = new java.util.HashMap();
    java.util.HashMap<Unit,Integer> damageAwayMap = new java.util.HashMap();
    
    resultHome = homeUnits.stream().map(
        (u) -> attack(u, awayUnits, dice, mod, home.getID(), 
                home.getEnemyID(),damageMap))
            .reduce(resultHome, Integer::sum);
    
    resultAway = awayUnits.stream()
            .map((u) -> attack(u, homeUnits, dice, mod, away.getID(), 
                    away.getEnemyID(),damageMap))
            .reduce(resultAway, Integer::sum);
    
    //Apply damage
    int damageValue;
    for (Unit u : damageMap.keySet()) {
       damageValue = damageMap.get(u);
      do { damageValue--;} while (!u.hit() && damageValue > 0);
    }
    
    java.util.HashMap<String, String> res = new java.util.HashMap();
    res.put(home.getID(), String.valueOf(resultHome));
    res.put(away.getID(), String.valueOf(resultAway));
    
    return res;    
  }
  
  private int attack(Unit attacker, java.util.List<Unit> targets, Random die,
          UnitRules modEnvironment, String homeID, String enemyID,
          java.util.Map<Unit,Integer> damageMap) {
    
    Unit mAttacker = modEnvironment.mod(attacker, homeID);
    
    int baseAttack = Math.max(mAttacker.getMelee(), 
            Math.max(mAttacker.getMissile(), mAttacker.getCharge()));
    
    Unit enemy = targets.get(die.nextInt(targets.size()));
    Unit modEnemy = modEnvironment.mod(enemy, enemyID);
    
    if(die.nextInt(19)+1+baseAttack >= modEnemy.getDef()) {
      damageMap.put(enemy, damageMap.getOrDefault(enemy, 0)+1);
      return 1;
    }
    return 0;
  }
}
