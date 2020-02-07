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
import bfield.data.Unit;
import bfield.rules.ArmyRules;

/**
 *
 * @author ste
 */
public class ArmyMM extends ArmyRules {

  private final int FORTIFICATION_MOD, CLASS_MOD;
  
  public ArmyMM(Battle b, String armyID) {
    super(b, armyID);
    FORTIFICATION_MOD = b.getRules().getFortificationMod();
    CLASS_MOD= b.getRules().getClassModifier();
  }

  @Override
  public double getAttack() {
    Army enemy = army.getEnemy();
    
    boolean bEnemyHasArchers =
          enemy.getActiveUnitsStream()
            .anyMatch((un) -> un.getClassName().contains("Achr"));
    boolean bEnemyHasKnights =
          enemy.getActiveUnitsStream()
            .anyMatch((un) -> un.getClassName().contains("Cav"));
    boolean bYouHaveKnights =
          enemy.getActiveUnitsStream()
            .anyMatch((un) -> un.getClassName().contains("Cav"));
    boolean bYouHavePikes =
          enemy.getActiveUnitsStream()
            .anyMatch((un) -> un.getClassName().contains("Pike"));
    
    int modifier = 0;
    if (bEnemyHasArchers && !bYouHaveKnights)
      modifier += CLASS_MOD;
    if (bEnemyHasKnights && !bYouHaveKnights && !bYouHavePikes)
      modifier += CLASS_MOD;

    int hDef = (int)getArmyBCR(army), aDef = (int)getArmyBCR(enemy);
    
    int balance = hDef - aDef;
    if (balance > 0) {
      modifier += Math.min(3, balance/2);
    } else if (balance < 0)
      modifier += Math.max(-3, balance/2);
    
    if (army.isFortified() && !
            enemy.getActiveUnitsStream().anyMatch( (un) -> un.getClassName()
            .contains("Art"))) {
      modifier += FORTIFICATION_MOD;
    }
    
    return (double) modifier;
  }

  private static double getArmyBCR(Army ar) {
    //base value
    int bcr = ar.getActiveUnitsStream().map( u -> u.getDef()).reduce(0, Integer::sum);
    
    //each levy counts as 0.25
    double irr = ar.getActiveUnitsStream().filter( u -> u.getClassName().equals("LtIrr") )
            .count() * 0.25;
    
    //each MdIrr counts as 0.5
    irr += (ar.getActiveUnitsStream().filter( u -> u.getClassName().matches("(Md)?Irr") )
            .count()) * 0.5;
    return bcr+irr;
  }
  
  @Override
  public double getDefense() {
    return getArmyBCR(army);
  }

  @Override
  public double getSpecial() {
    return Unit.NA;
  }
}
