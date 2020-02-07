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
import bfield.data.Condition;
import bfield.data.Rules;
import bfield.data.Unit;

/**
 *
 * @author ste
 */
public class UnitSRD extends bfield.rules.UnitRules {

  public UnitSRD(Battle b) {
    super(b);
  }
    
  @Override
  public Unit mod(Unit u, String ownerArmyID) {
    final Army a = context.getArmy(ownerArmyID),
            enemy = a.getEnemy();
    
    Condition c = conditionFor(u, enemy);
    
    Unit newUnit = u.clone();
            
    newUnit.setMelee(newUnit.getMelee() + c.getAttackMod());
    if (newUnit.getMissile() != Unit.NA && !c.isMissileDenied()) {
      newUnit.setMissile(newUnit.getMissile() + c.getMissileMod());
    } else {
      newUnit.setMissile(Unit.NA);
    }

    //Morale bonuses
    if (newUnit.getHeroEL() > 0) {
      if (newUnit.getMelee() != Unit.NA)
        newUnit.setMelee(newUnit.getMelee()+getHeroAttackBonus(newUnit.getHeroEL()));
      if (newUnit.getDef() != Unit.NA)
        newUnit.setDef(newUnit.getDef()+getHeroDefenseBonus(newUnit.getHeroEL()));
      if (newUnit.getMorale() != Unit.NA)
        newUnit.setMorale(newUnit.getMorale()+getHeroMoraleBonus(newUnit.getHeroEL()));
    }
    
    //There is no specific charge mod
    if (c.isChargeDenied()) {
      newUnit.setChargeMod(Unit.NA);
    }

    /**
     * Mountain high ground - 1st unit only Requirements: 1st unit, army has
     * high ground, Enemy has no unit with ignoreMountain
     */
    if (a.hasHighGround() && u == a.getUnits().get(0)
            && c.getHighGroundRule().contains("mountain")) {
      int hgm = getHighGroundModifier(enemy, context.getRules());
      newUnit.setMelee(newUnit.getMelee()+hgm);
      newUnit.setDef(newUnit.getDef()+hgm);
    }
    
    newUnit.setDef(newUnit.getDef() + c.getDefenseMod());
    
    return newUnit;
  }

  public static int getHighGroundModifier(Army enemy, Rules rules) {

    double allUnits = enemy.getActiveUnitsSize();
    final double baseValue = rules.getHighGroundMod();
    
    if (allUnits <= 0.0) {
      return (int) baseValue; 
    }

    double ignoreUnits
            = enemy.getUnits().stream().filter(x -> !x.isDead() && !x.isRetired())
                    .filter(x -> x.getIgnoreTerrain() || x.getIgnoreMountain())
                    .count();
    double modifier;
    modifier = 1.0 - (ignoreUnits / allUnits);
    
    return (int) Math.round(baseValue * modifier);
  }
  
  @Override
  public Condition conditionFor(Unit u, Army enemy) {
    Condition result = new Condition();
    final Condition terrain = context.getTerrain(),
            visibility = context.getVisibility(),
            weather = context.getWeather();
    final Rules rules = context.getRules();
    
    if (!u.getIgnoreTerrain() && terrain != null) {
      result.worstOf(terrain);

      /**
       * High ground - Hills: deny charge if enemy has high ground To allow
       * dwarven resolve in consideration, Mountain high ground is handled as a
       * -2 attack & defense malus to the enemy instead of a +2 bonus. This will
       * penalize all but dwarves, who get to contribute with full defense and
       * attack
       */
      if (terrain.isHighGroundAllowed() && enemy.hasHighGround()) {
        result.setDenyCharge(true);
      }

      if (u.getIgnoreMountain()) {
        result.setHighGroundRule(null);
      }

     /**
     * Shallow water hurts open sea ships
     */
      if (terrain.isShallowWater() && "HvShip".equals(u.getClassName())) {
        final int MALUS = rules.getShallowWaterMod();
        
        result.setAttackMod(result.getAttackMod() + MALUS);
        result.setMissileMod(result.getMissileMod()+ MALUS);
        result.setDefenseMod(result.getDefenseMod()+ MALUS);
      }
    }

    if (!u.getIgnoreVisibility() && visibility != null) {
      result.worstOf(visibility);
    }

    if (weather != null) {
      result.worstOf(weather);
      /**
       * Weather Units affected by abnormal weather (anything but storm, which
       * ALWAYS applies) suffers a -2 malus.
       */
      if (!"Storm".equals(weather.getName())
              && !u.getIgnoreWeather()) {
        final int STORM_MALUS=rules.getStormMalus();
        result.setAttackMod(result.getAttackMod() +STORM_MALUS);
        result.setMissileMod(result.getMissileMod()+STORM_MALUS);
      }
    }

    /**
     * Fortification handled as -1
     */
    if (enemy.isFortified() && !u.getIgnoreFortification()) {
      result.setAttackMod(result.getAttackMod() +rules.getFortificationMod());
    }

    /**
     * Artillerists can attack on some occasions where archers can't
     */
    if (result.isArtsOnly() && !u.getClassName().contains("Art")) {
      result.setDenyMissile(true);
    }

    /**
     * Effectiveness rule -2 to all attacks for each hit point lost
     */
    if (rules.isEffectivenessOn() && u.isDamaged()) {
      int hits = (u.getHits() - u.getCurrentHits()) * rules.getEffectiveness();
      result.setAttackMod(result.getAttackMod() + hits);
      result.setMissileMod(result.getMissileMod() + hits);
    }

    /**
     * Mounted units malus 'Cav' unit classes suffers -2 to melee & defense
     */
    if (u.getClassName().contains("Cav")) {
      result.setAttackMod(result.getAttackMod() + result.getMountedUnitsMod());
      result.setDefenseMod(result.getDefenseMod() + result.getMountedUnitsMod());
    } else {
      result.setMountedUnitsMod(0);
    }
      
    return result;
  }

  public static int getHeroAttackBonus(int EL) {
    if (EL < 8) return 0;
    else if (EL >= 8 && EL < 14) return 2;
    else if (EL >= 14 && EL <= 19) return 4;
    else return 6;
  }
  
  public static int getHeroMoraleBonus(int EL) {
    if (EL < 6) return 0;
    else if (EL < 12) return 2;
    else if (EL < 18) return 4;
    else return 6;
  }
  
  public static int getHeroDefenseBonus(int EL) {
    if (EL < 10) return 0;
    else if (EL >= 10 && EL < 16) return 1;
    else return 2;
  }
}
