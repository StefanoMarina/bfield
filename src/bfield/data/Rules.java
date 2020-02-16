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
 * <i> Rules </i> represents the<i> rules.xml </i> XML document in the "rules"
 * packages. It contains various modifiers to dice rolls as well as classes with
 * specific rules to be instantiated when it is necessary to calculate how the 
 * Conditions modify the units, the armies, and the rules of combat. 
 * Many of the properties are specific to the BRCS but are applicable
 * easily to various types of battle game.
 * Rules is basically a collection of numbers, it's up to the game mechanics and
 * Condition how to use them.
 * @see bfield.data.Condition
 * @see bfield.rules.UnitRules
 * @see bfield.rules.ArmyRules
 * @see bfield.rules.BattleRules
 * @author ste
 */
@XmlRootElement(name="rules")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rules {
  
  /**
   * BRCS formula's first step is <i>baseAttackBonus</i>+army attack - enemy defense.
   * I think it represents average dice roll (11 on BRCS).
   * @see #getBaseAttackMod() 
   * @see #setBaseAttackMod(int) 
   */
  @XmlElement
  protected int baseAttackBonus;
  
  /**
   * baseAttackMultiplier is the 2nd step of BRCS formula, on BRCS is 0.05,
   * meaning maybe an average sucess roll (1 on 20).
   * @see #baseAttackBonus
   * @see #getBaseAttackMultiplier() 
   * @see #setBaseAttackMultiplier(double) 
   */
  @XmlElement
  protected double baseAttackMultiplier;
  
  /**
   * Units facing each other may have a fixed bonus IE infantry vs irregulars.
   * Currently not used on BRCS.
   */
  @XmlElement
  protected int classModifier;
  
  /**
   * BRCS 3E states that each hit reduces a unit's attack value. default is 2.
   */
  @XmlElement
  protected int effectiveness;
  
  /**
   * How much an army/unit will change if the army is fortified. rules dependant
   * how it is used.
   */
  @XmlElement
  protected int fortificationMod;
  
  /**
   * BRCS states that you can have a high ground bonus unless the enemy have
   * some type of unit (ie. mountain vs dwarves). This is used by mechanics 
   * to be divided counting how many resistant units the enemy have. So if your
   * army gets +2 to defense for being on a hill, and the enemy has 50% of his army
   * to be ignoring high ground, highGroundMod will be 50%, +1.
   */
  @XmlElement
  protected double highGroundMod;
  
  /**
   * The minimum amount of damage you can do in one mass attack. should be 0 or
   * the minimal unit of damage your rules use.
   */
  @XmlElement
  protected int minimalDamage;
  
  /**
   * BRCS states that large ships suffer a malus on shallow water. this will
   * tell you how much.
   */
  @XmlElement
  protected int shallowWaterMod;
  
  /**
   * How much unit will suffer when under a storm.
   */
  @XmlElement
  protected int stormMalus;
  
  /**
   * Should effectiveness rule be used?
   */
  @XmlAttribute
  protected boolean effectivenessOn;  
  
  /**
   * List of classes, under java class names (package.Class), that will be used
   * to instantiate a Battle Rule and represent various type of attacks. IE
   * BRCS allows a special attack (first round, best value) and melee attacks
   * (melee value only).
   * @see bfield.rules.BattleRules
   */
  @XmlElementWrapper(name = "battleRules")
  @XmlElement(name = "rule")
  @XmlJavaTypeAdapter(value = BattleMechanicsAdapter.class)
  protected List<BattleRules> battleRules;

  /**
   * java class type string used to instantiate the object responsible for applying
   * conditions to a unit.
   * @see bfield.rules.UnitRules
   */
  @XmlElement(name="unitRules")
  protected String unitRulesClass;
  
  /**
   * java class type string used to instantiate the object responsible for applying
   * conditions to an army.
   */
  @XmlElement(name="armyRules")
  protected String armyRulesClass;
  /**
   * Name of the tutorial file that should be opened from help menu.
   * All tutorial names are filename only, html format, and should be
   * placed in the "text" subfolder.
   */
  @XmlElement
  String tutorialFilename;
  
  /**
   * 
   * @return
   */
  public int getBaseAttackMod() {
    return baseAttackBonus;
  }

  /**
   *
   * @param baseAttackBonus
   */
  public void setBaseAttackMod(int baseAttackBonus) {
    this.baseAttackBonus = baseAttackBonus;
  }

  /**
   *
   * @return
   */
  public double getBaseAttackMultiplier() {
    return baseAttackMultiplier;
  }

  /**
   *
   * @param baseAttackMultiplier
   */
  public void setBaseAttackMultiplier(double baseAttackMultiplier) {
    this.baseAttackMultiplier = baseAttackMultiplier;
  }

  /**
   *
   * @return
   */
  public int getClassModifier() {
    return classModifier;
  }

  /**
   *
   * @param classModifier
   */
  public void setClassModifier(int classModifier) {
    this.classModifier = classModifier;
  }

  /**
   *
   * @return
   */
  public int getEffectiveness() {
    return effectiveness;
  }

  /**
   *
   * @param effectiveness
   */
  public void setEffectiveness(int effectiveness) {
    this.effectiveness = effectiveness;
  }

  /**
   *
   * @return
   */
  public int getFortificationMod() {
    return fortificationMod;
  }

  /**
   *
   * @param fortificationMod
   */
  public void setFortificationMod(int fortificationMod) {
    this.fortificationMod = fortificationMod;
  }

  /**
   *
   * @return
   */
  public double getHighGroundMod() {
    return highGroundMod;
  }

  /**
   *
   * @param highGroundBonus
   */
  public void setHighGroundMod(double highGroundBonus) {
    this.highGroundMod = highGroundBonus;
  }

  /**
   *
   * @return
   */
  public int getMinimalDamage() {
    return minimalDamage;
  }

  /**
   *
   * @param minimalDamage
   */
  public void setMinimalDamage(int minimalDamage) {
    this.minimalDamage = minimalDamage;
  }

  /**
   *
   * @return
   */
  public int getShallowWaterMod() {
    return shallowWaterMod;
  }

  /**
   *
   * @param shallowWaterMod
   */
  public void setShallowWaterMod(int shallowWaterMod) {
    this.shallowWaterMod = shallowWaterMod;
  }

  /**
   *
   * @return
   */
  public int getStormMalus() {
    return stormMalus;
  }

  /**
   *
   * @param stormMalus
   */
  public void setStormMod(int stormMalus) {
    this.stormMalus = stormMalus;
  }

  /**
   *
   * @return
   */
  public boolean isEffectivenessOn() {
    return effectivenessOn;
  }

  /**
   *
   * @param effectivenessOn
   */
  public void setEffectivenessOn(boolean effectivenessOn) {
    this.effectivenessOn = effectivenessOn;
  }

  /**
   *
   * @return
   */
  public List<BattleRules> getBattleMechanics() {
    if (battleRules == null) {
      battleRules = new ArrayList();
    }
    return battleRules;
  }

  /**
   *
   * @return
   */
  public String getUnitRulesClass() {
    return unitRulesClass;
  }

  /**
   *
   * @return
   */
  public String getArmyRulesClass() {
    return armyRulesClass;
  }
  
  /**
   *
   * @param b
   * @return
   */
  public UnitRules newUnitRules(Battle b) {
    try {
        Constructor c = Class.forName(unitRulesClass).getConstructor(Battle.class);
        return (UnitRules) c.newInstance(b);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  /**
   *
   * @param b
   * @param armyID
   * @return
   */
  public ArmyRules newArmyRules(Battle b, String armyID) {
    try {
      Constructor c = Class.forName(armyRulesClass).getConstructor(Battle.class, String.class);
      return (ArmyRules) c.newInstance(b, armyID);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
  }
  
  
  /**
   * Returns the filename of the html tutorial.
   * @return a string containing a filename without path
   * @see #tutorialFilename
   */
  public String getTutorialFilename() {
    return tutorialFilename;
  }
}
