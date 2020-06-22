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

import bfield.Utilities;
import bfield.data.Army;
import bfield.data.Battle;
import bfield.data.Rules;
import bfield.data.Unit;
import bfield.rules.UnitRules;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import bfield.rules.BattleRules;
import bfield.rules.Skirmish;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.IntStream;

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
  final Army home = battle.getArmies().get(Battle.ID_HOME),
         away = battle.getArmies().get(Battle.ID_AWAY);
  java.util.HashMap<String, String> res = new java.util.HashMap();    
  
    //Create clone armies with active units only.
    java.util.List<Unit> homeUnits 
            = new java.util.ArrayList(
            Arrays.<Unit>asList(home.getActiveUnits()));
    java.util.List<Unit> awayUnits 
            = new java.util.ArrayList(
                    Arrays.<Unit>asList(away.getActiveUnits()));
    
    if (homeUnits.isEmpty() && !awayUnits.isEmpty()) {
      res.put("general", "<p>"+away.getName()+ " has defeated " + home.getUnits() + "<p>");
      return res;
    } else if (awayUnits.isEmpty() && !homeUnits.isEmpty()) {
      res.put("general", "<p>"+home.getName()+ " has defeated " + away.getUnits() + "<p>");
      return res;
    } else if (homeUnits.isEmpty() && awayUnits.isEmpty()) {
      res.put("general", "<p>None of the armies has available units!<p>");
      return res;
    }
    
    UnitRules mod = battle.getUnitRules();
       
    final Random dice = new Random();
    final ArrayList<Skirmish> skirmishes = new java.util.ArrayList();
    
    // We need to map damage, as damage must be applied AFTER skimirsh
    final java.util.HashMap<Unit, Integer> homeDamageMap = new java.util.HashMap();
    final java.util.HashMap<Unit, Integer> awayDamageMap = new java.util.HashMap();
    
    final StringBuilder skirmishLog = new StringBuilder(),
            homeLog = new StringBuilder(),
            awayLog = new StringBuilder();
    
    final int MIN = Math.min(homeUnits.size(), awayUnits.size());
    
    /* First we create a 1 on 1 list, home is key, away is value, 
     * in the cryptic-est lambda java8 way possibile
     * we remove from awayUnits but not from homeUnits as it is messy to remove
     * elements while stream-iterating through them
     */
    IntStream.range(0, MIN).mapToObj(homeUnits::get).forEach(unit -> {
      Skirmish sk = new Skirmish(unit, awayUnits.get(
          dice.nextInt(awayUnits.size())
      ));
      awayUnits.remove(sk.getValue().get(0));
      skirmishes.add(sk);
    });
    
    if (skirmishes.isEmpty())
      throw new RuntimeException ("No skirmish was created.");
    
    //then, we randomly add remaining units to existing skirmishes
    if (homeUnits.size() > MIN) {
      IntStream.range(MIN, homeUnits.size()).mapToObj(homeUnits::get)
              .forEach( unit -> {
        skirmishes.get(dice.nextInt(skirmishes.size())).addKey(unit);
      });
    }
    
    if (!awayUnits.isEmpty()) {
      awayUnits.forEach(unit -> {
        skirmishes.get(dice.nextInt(skirmishes.size())).addValue(unit);
      });
    }
    
    skirmishLog.append("<h3>Skirmishes</h3>");
    //now we log matches and see what happens
    skirmishes.forEach( skirmish -> {
      skirmishLog.append("<p>");
      writeUnitListSpan(skirmish.getKey(), home.getID(), skirmishLog);
      skirmishLog.append("</span> vs ");
      writeUnitListSpan(skirmish.getValue(), away.getID(), skirmishLog);
      skirmishLog.append("</span></p>");
      
      //resolve attacks
      skirmish.getKey().forEach(unit -> {
        attack (unit, skirmish.getValue(), dice, mod, home.getID(), away.getID(), awayDamageMap);
        
                /*
        if ( )
          homeLog.append("<p>").append(unit.getOrderAndName()).append(" attacked!</p>");
        else
         homeLog.append("<p>").append(unit.getOrderAndName()).append(" missed!</p>");
        */
      });
      skirmish.getValue().forEach(unit -> {
        attack (unit, skirmish.getKey(), dice, mod, away.getID(), home.getID(), homeDamageMap);                
        /*if ( )
          awayLog.append("<p>").append(unit.getOrderAndName()).append(" attacked!</p>");
        else
         awayLog.append("<p>").append(unit.getOrderAndName()).append(" missed!</p>");
        */
      });
                
    });
    
    //Apply damage
    int damageValue;
    for (Unit u : homeDamageMap.keySet()) {
      damageValue = homeDamageMap.get(u);
      homeLog.append("<p>").append(u.getOrderAndName())
              .append(" took ").append(damageValue).append(" hits.</p>");
      do { damageValue--;} while (!u.hit() && damageValue > 0);
    }
    for (Unit u : awayDamageMap.keySet()) {
      damageValue = awayDamageMap.get(u);
      awayLog.append("<p>").append(u.getOrderAndName())
              .append(" took ").append(damageValue).append(" hits.</p>");
      do { damageValue--;} while (!u.hit() && damageValue > 0);
    }
    
  
    res.put("css", " #main p {text-align:center;} ");
    res.put("main", skirmishLog.toString());
    res.put(home.getID(), homeLog.toString());
    res.put(away.getID(), awayLog.toString());
    
    return res;    
  }
  
  /**
   * Utility to perform an SRD attack. 
   * @param attacker the unit that will be perform the attack
   * @param targets a list of target, randomly chosen from.
   * @param die Random generator
   * @param modEnvironment UnitRules to mod engaging units
   * @param homeID id of the attacking unit
   * @param enemyID id of the enemy unit
   * @param damageMap map to retain all damage done to the enemy
   * @return true if the attack is succesful, otherwise false
   */
  private static boolean attack(Unit attacker, java.util.List<Unit> targets, Random die,
          UnitRules modEnvironment, String homeID, String enemyID,
          java.util.Map<Unit,Integer> damageMap) {
    
    Unit mAttacker = modEnvironment.mod(attacker, homeID);
    
    int baseAttack = Math.max(mAttacker.getMelee(), 
            Math.max(mAttacker.getMissile(), mAttacker.getCharge()));
    
    Unit enemy = targets.get(die.nextInt(targets.size()));
    Unit modEnemy = modEnvironment.mod(enemy, enemyID);
    
    boolean result = false;
    if(die.nextInt(19)+1+baseAttack >= modEnemy.getDef()) {
      damageMap.put(enemy, damageMap.getOrDefault(enemy, 0)+1);
      result = true;
    }
    
    if (attacker.getClassName().contains("Ship") && 
            attacker.getBunks() > 0) {
      
      for (Unit cargo : attacker.getCargo()) {
        result = result | attack (cargo, targets, die, modEnvironment, homeID, enemyID, damageMap);
      }
    }
    
    return result;
  }
  
  /**
   * Small utility that prints an army list under a <span> tag
   * @param list list of units
   * @param classId class to assign the span to
   * @param builder StringBuilder
   * @return builder
   */
  static private StringBuilder writeUnitListSpan(ArrayList<Unit> list, String classId, StringBuilder builder) {
    Utilities.tag(builder, "span", "armyline "+classId+"-ul", null);
      for (Unit unit : list) {
        builder.append(unit.getOrderAndName());
        if (list.indexOf(unit) != list.size()-1)
          builder.append(", ");
      }
    return builder;
  }
          
}
