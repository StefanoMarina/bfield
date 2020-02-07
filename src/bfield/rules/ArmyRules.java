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
package bfield.rules;

import bfield.data.*;

/**
 * ArmyMechanics
 * Armymechs serve as a generator of armies' dynamic score, such as 
 * attack, special attack (best attack) or defense. each army should
 * implement at least Attack & Defense.
 * @author ste
 */
public abstract class ArmyRules {
  
  protected final Army army;
  protected final UnitRules modEnvironment;
  
  public ArmyRules(Battle b, String armyID) {
    army = b.getArmy(armyID);
    modEnvironment = b.getUnitRules();
  }
  
  public abstract double getAttack();
  public abstract double getDefense();
  public abstract double getSpecial();

  public Army getArmy() {
    return army;
  }

  public UnitRules getModEnvironment() {
    return modEnvironment;
  }
}
