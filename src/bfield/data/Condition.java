/*
 * Copyright (C) 2019 root
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

import javax.xml.bind.annotation.*;

/**
 * Conditions are a list of modifiers that will be applied by rules to
 * unit, armies, or battles.
 * Those conditions are hard-coded to be based upon 3E, future release will
 * work toward flexible conditions.
 * @author ste
 */
@XmlRootElement(name="condition")
@XmlAccessorType(XmlAccessType.FIELD)
public class Condition {
  
  @XmlAttribute
  Boolean denyCharge;
  
  @XmlAttribute
  Boolean denyMissile;
  
  @XmlAttribute
  Boolean artsOnly;
  
  @XmlAttribute
  Integer missileMod;
  
  @XmlAttribute
  Integer attackMod;
  
  @XmlAttribute
  Integer defenseMod;
  
  @XmlAttribute
  String highGroundRule;
  
  @XmlAttribute
  Integer mountedUnitsMod;
  
  @XmlAttribute
  Boolean shallowWater;

  @XmlAttribute
  String name;

  @Override
  public String toString() {
    return getName();
  }
  
  /**
   * return the condition display name. default "condition".
   * @return
   */
  public String getName() {
    return (name != null) ? name : "Condition";
  }
  
  /**
   * Returns if this condition, when applied, does not allow charges.
   * @return
   */
  public boolean isChargeDenied() {
    if (denyCharge == null)
      denyCharge = false;
    return denyCharge;
  }

  /**
   * Returns if this condition, when applied, does not allow missile attacks.
   * @return
   */
  public boolean isMissileDenied() {
    if (denyMissile == null)
      denyMissile = false;
    return denyMissile;
  }

  /**
   * Returns if this condition, when applied, denies missile to all but artillerists.
   * @return
   */
  public boolean isArtsOnly() {
    return (artsOnly != null) ? artsOnly : false;
  }
  
  /**
   * Returns the value that will change the unit's missile rating.
   * @return
   */
  public int getMissileMod() {
    return (missileMod != null) ? missileMod : 0;
  }

  /**
   * If this method returns empty, the condition does not apply high ground rules
   * otherwise it may return "hill" for hill high ground or "mountain".
   * TODO: replace with class name
   * @return the high ground type (hill or mountain) or null if no high ground 
   * rlue.
   */
  public String getHighGroundRule() {
    if (highGroundRule == null)
      highGroundRule = "";
    
    return highGroundRule;
  }
  
  /**
   * Tells if this condition denies or allows high ground on other conditions.
   * @return
   */
  public boolean isHighGroundAllowed() {
    return (highGroundRule != null  && !highGroundRule.isEmpty());
  }

  /**
   * Tells if this conditions requires the shallow water rule to be applied.
   * TODO: replace with class name
   * @return
   */
  public boolean isShallowWater() {
    return (shallowWater != null) ? shallowWater : false;
  }

  /**
   * Returns a modifier to the unit's melee/special attack.
   * @return
   */
  public int getAttackMod() {
    if (attackMod == null)
      attackMod = 0;
    return attackMod;
  }

  /**
   * returns a melee modifier reserved to mounted units.
   * @return a melee/special attack modifier reserved to cavalry.
   */
  public int getMountedUnitsMod() {
    if (mountedUnitsMod == null)
      mountedUnitsMod = 0;
    
    return mountedUnitsMod;
  }
  
  /**
   *
   * @param mod
   * @see #getAttackMod() 
   */
  public void setAttackMod(int mod) {
    attackMod = mod;
  }

  /**
   *
   * @see #isChargeDenied() 
   * @param denyCharge
   */
  public void setDenyCharge(boolean denyCharge) {
    this.denyCharge = denyCharge;
  }

  /**
   *
   * @see #isMissileDenied() 
   * @param denyMissile
   */
  public void setDenyMissile(boolean denyMissile) {
    this.denyMissile = denyMissile;
  }

  /**
   *
   * @param artsOnly
   * @see #isArtsOnly() 
   */
  public void setArtsOnly(boolean artsOnly) {
    this.artsOnly = artsOnly;
  }

  /**
   *
   * @param missileMod
   * @see #getMissileMod() 
   */
  public void setMissileMod(int missileMod) {
    this.missileMod = missileMod;
  }
  
  /**
   * @see #getHighGroundRule() 
   * @param ground
   */
  public void setHighGroundRule(String ground) {
    this.highGroundRule = ground;
  }

  /**
   *
   * @see #getMountedUnitsMod() 
   * @param mountedUnitsMod
   */
  public void setMountedUnitsMod(int mountedUnitsMod) {
    this.mountedUnitsMod = mountedUnitsMod;
  }

  /**
   *
   * @see #isShallowWater() 
   * @param shallowWater
   */
  public void setShallowWater(boolean shallowWater) {
    this.shallowWater = shallowWater;
  }

  /**
   * set this condition's display name.
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * By calling this method, the condition will set itself to the values of 
   * Condition <i> c </i>, following the directive to allow only rules allowed
   * to both. The attack, defense, missile and mounted units modifiers are 
   * added together.
   * @param c the condition to be compared.
   */
  public void worstOf(Condition c) {
    this.setArtsOnly( isArtsOnly() || c.isArtsOnly());
    this.setAttackMod( getAttackMod() + c.getAttackMod());
    this.setDefenseMod( getDefenseMod() + c.getDefenseMod());
    this.setDenyCharge(this.isChargeDenied() || c.isChargeDenied());
    this.setDenyMissile(this.isMissileDenied() || c.isMissileDenied());
    this.setMissileMod(getMissileMod()+c.getMissileMod());
    this.setMountedUnitsMod(getMountedUnitsMod() + c.getMountedUnitsMod());
    this.setShallowWater(isShallowWater() || c.isShallowWater());
    this.setHighGroundRule(
            this.getHighGroundRule() +
                    (c.isHighGroundAllowed()
            ? ","+c.getHighGroundRule() : ""));
  }

  /**
   * returns a modifier to defense.
   * @return
   */
  public int getDefenseMod() {
    if (defenseMod == null)
      defenseMod = 0;
    return defenseMod;
  }

  /**
   * @see #getDefenseMod(int) 
   * @param defenseMod
   */
  public void setDefenseMod(int defenseMod) {
    this.defenseMod = defenseMod;
  }
  
  
}

