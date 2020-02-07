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

/**
 *
 * @author root
 */
import javax.xml.bind.annotation.*;

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
  
  public String getName() {
    return (name != null) ? name : "Condition";
  }
  
  public boolean isChargeDenied() {
    if (denyCharge == null)
      denyCharge = false;
    return denyCharge;
  }

  public boolean isMissileDenied() {
    if (denyMissile == null)
      denyMissile = false;
    return denyMissile;
  }

  public boolean isArtsOnly() {
    return (artsOnly != null) ? artsOnly : false;
  }
  
  public int getMissileMod() {
    return (missileMod != null) ? missileMod : 0;
  }

  public String getHighGroundRule() {
    if (highGroundRule == null)
      highGroundRule = "";
    
    return highGroundRule;
  }
  
  public boolean isHighGroundAllowed() {
    return (highGroundRule != null  && !highGroundRule.isEmpty());
  }

  public boolean isShallowWater() {
    return (shallowWater != null) ? shallowWater : false;
  }

  public int getAttackMod() {
    if (attackMod == null)
      attackMod = 0;
    return attackMod;
  }

  public int getMountedUnitsMod() {
    if (mountedUnitsMod == null)
      mountedUnitsMod = 0;
    
    return mountedUnitsMod;
  }
  
  
  public void setAttackMod(int mod) {
    attackMod = mod;
  }

  public void setDenyCharge(boolean denyCharge) {
    this.denyCharge = denyCharge;
  }

  public void setDenyMissile(boolean denyMissile) {
    this.denyMissile = denyMissile;
  }

  public void setArtsOnly(boolean artsOnly) {
    this.artsOnly = artsOnly;
  }

  public void setMissileMod(int missileMod) {
    this.missileMod = missileMod;
  }
  
  public void setHighGroundRule(String ground) {
    this.highGroundRule = ground;
  }

  public void setMountedUnitsMod(int mountedUnitsMod) {
    this.mountedUnitsMod = mountedUnitsMod;
  }

  public void setShallowWater(boolean shallowWater) {
    this.shallowWater = shallowWater;
  }

  public void setName(String name) {
    this.name = name;
  }
  
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

  public int getDefenseMod() {
    if (defenseMod == null)
      defenseMod = 0;
    return defenseMod;
  }

  public void setDefenseMod(int defenseMod) {
    this.defenseMod = defenseMod;
  }
  
  
}

