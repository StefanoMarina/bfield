/* 
* Copyright (C) 2019 Stefano Marina
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

import java.util.Optional;
import javax.xml.bind.annotation.*;



@XmlAccessorType(XmlAccessType.FIELD)
public class Unit implements Cloneable {
  
  @XmlAttribute(required = true)
  protected String name;
  @XmlAttribute(required = true)
  protected Integer def;
  @XmlAttribute
  protected Integer charge;
  @XmlAttribute(required = true)
  protected Integer hits;
  @XmlAttribute
  protected Integer health;
  
  @XmlAttribute
  protected Boolean retired;
  
  @XmlAttribute(name="class")
  protected String className;
  
  @XmlAttribute(required = true)
  protected Integer melee;
  @XmlAttribute
  protected Integer missile;

  @XmlAttribute
  protected Integer heroEL;
  
  @XmlAttribute
  protected Boolean ignoreTerrain;
  
  @XmlAttribute
  protected Boolean ignoreVisibility;
  
  @XmlAttribute
  protected Boolean ignoreWeather;
  
  @XmlAttribute
  protected Boolean ignoreFortification;
  
  @XmlAttribute
  protected Boolean ignoreMountain;
         
  @XmlAttribute
  protected String icon;
  
  @XmlAttribute(name="move")
  protected Integer movement;
  
  @XmlAttribute(name="mrl")
  protected Integer morale;
  
  @XmlAttribute(name="ord", required=false)
  protected Integer ordinal;
  
  public static final int NA = -999;
  
  @XmlAttribute(name="bulk", required=false)
  protected Integer bulk;
  
  @XmlElementWrapper(name="cargo")
  @XmlElements(value={
    @XmlElement(name="unit", type=Unit.class),
    @XmlElement(namespace=URI.ADND_URI, name="unit", type=Unit2nd.class)
  })
  protected java.util.List<Unit> cargo;
  
  public java.util.List<Unit> getCargo() {
    if (cargo == null) {
      cargo = new java.util.ArrayList();
    }
    return cargo;
  }
  
  public String getName() {
    return name;
  }
  
  public int getDef() {
    return Optional.ofNullable(def).orElse(NA);
  }

  public int getChargeBonus() {
    if (charge == null || NA == charge)
      return NA;
    else
      return charge;
  }
  
  public int getCharge() {
    return (charge != null && NA != charge) 
            ?  getMelee() + charge
            : NA;
  }
  
  public int getHits() {
    return hits;
  }

  public boolean isRetired() {
    if (retired == null)
      retired = false;
    return retired;
  }
  
  public void setRetired(boolean b) {
    retired = b;
  }
  
  public int getMelee() {
    return melee;
  }

  public int getBulk() {
    return Optional.ofNullable(bulk).orElse(NA);
  }
  
  public int getMissile() {
    return Optional.ofNullable(missile).orElse(NA);
  }
  
  public int getCurrentHits() {
    if (health == null)
      health = hits;
    
    return health;
  }

  public Object getMovement() {
    return movement;
  }
  
  public int getMorale() {
    return Optional.ofNullable(morale).orElse(NA);
  }

  public void setMorale(int morale) {
    this.morale = morale;
  }
  
  
  public boolean getIgnoreMountain() {
    if (ignoreMountain == null)
      ignoreMountain = false;
    
    return ignoreMountain;
  }

  public void setIgnoreMountain(boolean ignoreMountain) {
    this.ignoreMountain = ignoreMountain;
  }
  
  public int getHeroEL() {
   return Optional.ofNullable(heroEL).orElse(NA);
  }
  
  public void attachHeroUnit(int el) {
    heroEL = el;
  }
  
  public void detachHeroUnit() {heroEL = null;}
  
  /**
   * Takes 1 hit of damage
   * @return true if health is 0 or lower
   */
  public boolean hit() {
    health = Math.max(0, getCurrentHits()-1);
    return (health <= 0);
  }

  public boolean die() {
    if (isDead())
      return false;
    else {
      health = 0;
      return true;
    }
  }
  
  public boolean recover() {
    health = Math.min(getCurrentHits()+1, hits);
    return (health >= 0);
  }
  
  public boolean getIgnoreTerrain() {
    if (ignoreTerrain == null)
      ignoreTerrain = false;
    return ignoreTerrain;
  }

  public void setIgnoreTerrain(boolean ignoreTerrain) {
    this.ignoreTerrain = ignoreTerrain;
  }

  public boolean getIgnoreVisibility() {
    if (ignoreVisibility == null)
      ignoreVisibility = false;
    return ignoreVisibility;
  }

  public void setIgnoreVisibility(boolean ignoreVisibility) {
    this.ignoreVisibility = ignoreVisibility;
  }

  public boolean getIgnoreWeather() {
    if (ignoreWeather == null)
      ignoreWeather = true;
    return ignoreWeather;
  }

  public void setIgnoreWeather(boolean ignoreWeather) {
    this.ignoreWeather = ignoreWeather;
  }

  public boolean getIgnoreFortification() {
    if (ignoreFortification == null)
      ignoreFortification = false;
    return ignoreFortification;
  }

  
  public void setIgnoreFortification(boolean ignoreFortification) {
    this.ignoreFortification = ignoreFortification;
  }

  public Unit clone() {
    try {
      return (Unit) super.clone();
    } catch(CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public String getClassName() {
    return className;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public void setChargeMod(int charge) {
    this.charge = charge;
  }
  
  public void setMelee(int i) {
    melee = i;
  }
  
  public void setMissile(int i) {
    missile = i;
  }

  public void setDef(Integer def) {
    this.def = def;
  }

  
  public void setOrdinal(Integer ordinal) {
    this.ordinal = ordinal;
  }

  public Integer getOrdinal() {
    return ordinal;
  }
  
  public boolean isDamaged() {return getCurrentHits() < getHits();}
  public boolean isDead() {return getCurrentHits() <= 0 || isRetired();}

  @Override
  public String toString() {
    if (getOrdinal() != null)
      return getName() + "["+getOrdinal()+"]";
    else
      return getName();
  }
}