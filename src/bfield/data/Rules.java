/*
 * Copyright (C) 2019 ste
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
package bfield.data;

import bfield.data.adapters.BattleMechanicsAdapter;
import bfield.rules.ArmyRules;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import bfield.rules.BattleRules;
import bfield.rules.UnitRules;
import java.lang.reflect.Constructor;

/**
 *
 * @author ste
 */
@XmlRootElement(name="rules")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rules {
  
  @XmlElement
  protected int baseAttackBonus;
  
  @XmlElement
  protected double baseAttackMultiplier;
  
  @XmlElement
  protected int classModifier;
  
  @XmlElement
  protected int effectiveness;
  
  @XmlElement
  protected int fortificationMod;
  
  @XmlElement
  protected double highGroundMod;
  
  @XmlElement
  protected int minimalDamage;
  
  @XmlElement
  protected int shallowWaterMod;
  
  @XmlElement
  protected int stormMalus;
  
  @XmlAttribute
  protected boolean effectivenessOn;  
  
  @XmlElementWrapper(name = "battleRules")
  @XmlElement(name = "rule")
  @XmlJavaTypeAdapter(value = BattleMechanicsAdapter.class)
  protected List<BattleRules> battleRules;

  @XmlElement(name="unitRules")
  protected String unitRulesClass;
  
  @XmlElement(name="armyRules")
  protected String armyRulesClass;
  
  
  public int getBaseAttackMod() {
    return baseAttackBonus;
  }

  public void setBaseAttackMod(int baseAttackBonus) {
    this.baseAttackBonus = baseAttackBonus;
  }

  public double getBaseAttackMultiplier() {
    return baseAttackMultiplier;
  }

  public void setBaseAttackMultiplier(double baseAttackMultiplier) {
    this.baseAttackMultiplier = baseAttackMultiplier;
  }

  public int getClassModifier() {
    return classModifier;
  }

  public void setClassModifier(int classModifier) {
    this.classModifier = classModifier;
  }

  public int getEffectiveness() {
    return effectiveness;
  }

  public void setEffectiveness(int effectiveness) {
    this.effectiveness = effectiveness;
  }

  public int getFortificationMod() {
    return fortificationMod;
  }

  public void setFortificationMod(int fortificationMod) {
    this.fortificationMod = fortificationMod;
  }

  public double getHighGroundMod() {
    return highGroundMod;
  }

  public void setHighGroundMod(double highGroundBonus) {
    this.highGroundMod = highGroundBonus;
  }

  public int getMinimalDamage() {
    return minimalDamage;
  }

  public void setMinimalDamage(int minimalDamage) {
    this.minimalDamage = minimalDamage;
  }

  public int getShallowWaterMod() {
    return shallowWaterMod;
  }

  public void setShallowWaterMod(int shallowWaterMod) {
    this.shallowWaterMod = shallowWaterMod;
  }

  public int getStormMalus() {
    return stormMalus;
  }

  public void setStormMod(int stormMalus) {
    this.stormMalus = stormMalus;
  }

  public boolean isEffectivenessOn() {
    return effectivenessOn;
  }

  public void setEffectivenessOn(boolean effectivenessOn) {
    this.effectivenessOn = effectivenessOn;
  }

  public List<BattleRules> getBattleMechanics() {
    if (battleRules == null) {
      battleRules = new ArrayList();
    }
    return battleRules;
  }

  public String getUnitRulesClass() {
    return unitRulesClass;
  }

  public String getArmyRulesClass() {
    return armyRulesClass;
  }
  
  public UnitRules newUnitRules(Battle b) {
    try {
        Constructor c = Class.forName(unitRulesClass).getConstructor(Battle.class);
        return (UnitRules) c.newInstance(b);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public ArmyRules newArmyRules(Battle b, String armyID) {
    try {
      Constructor c = Class.forName(armyRulesClass).getConstructor(Battle.class, String.class);
      return (ArmyRules) c.newInstance(b, armyID);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
  }
}
