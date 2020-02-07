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

import bfield.data.adapters.AttackAdapter;
import java.util.Arrays;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author ste
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(Unit.class)
public class Unit2nd extends Unit {
  
  @XmlAttribute(namespace=URI.ADND_URI, name="melee", required=true)
  @XmlJavaTypeAdapter(AttackAdapter.class)
  protected Integer[] melee2;
  
  @XmlAttribute(namespace=URI.ADND_URI,name="charge", required=false)
  @XmlJavaTypeAdapter(AttackAdapter.class)
  protected Integer[] charge2;
  
  @XmlAttribute(namespace=URI.ADND_URI,name="missile")
  @XmlJavaTypeAdapter(AttackAdapter.class)
  protected Integer[] missile2;

  @XmlAttribute(namespace=URI.ADND_URI,name="move")
  protected String movement2;
  
  @XmlAttribute(namespace=URI.ADND_URI,name="row")
  protected Integer rowing;
  
  private int safeIndex(Integer[] array) {
    return array[
            Math.min(array.length-1, Math.max(0, super.getCurrentHits()-1))];
  }

  private Integer[] linearScale(int value, boolean bForceNA) {
    Integer[] scale = new Integer[hits];
    int lastValue = value - hits +1;
    for (int i = 0; i < hits; i++) {
      if (bForceNA)
        scale[i] = (lastValue+i < 0) ? NA : lastValue + i;
      else
        scale[i] = lastValue+i;
    }
    
    return scale;
  }
  
  private void setValues() {
    if (melee2 != null)
      melee = safeIndex(melee2);
    if (missile2 != null)
      missile = safeIndex(missile2);

    if (charge2 != null)
      charge = safeIndex(charge2);
  }
  
  @Override
  public boolean hit() {
    boolean b = super.hit();
    setValues();
    return b;
  }

  @Override
  public boolean recover() {
    boolean b = super.recover();
    setValues();
    return b;
  }

  @Override
  public int getMelee() {
   if (melee2 == null && melee != null)
      melee2=linearScale(melee,true);
    
    if (melee == null && melee2 != null)
      melee = safeIndex(melee2);
    
    return super.getMelee(); 
  }

  @Override
  public Object getMovement() {
    if (movement2 != null)
      return movement2;
    else
    return super.getMovement();
  }

  public int getRowing() {
    return (rowing != null) ? rowing : Unit.NA;
  }
  
  @Override
  public int getMissile() {
    if (missile2 == null && missile != null)
      missile2=linearScale(missile,true);
    
    if (missile == null && missile2 != null)
      missile = safeIndex(missile2);
    
    return super.getMissile(); 
  }

  @Override
  public int getChargeBonus() {
    if (charge2 == null && charge != null)
      charge2 = linearScale(charge,true);
    
    if (charge == null && charge2 != null)
      charge = safeIndex(charge2);
    return super.getChargeBonus();
  }
  
  /*
  @Override
  public int getChargeBonus() {
    if (charge2 == null)
      return Unit.NA;
    
    return charge2[safeIndex(charge2.length)];
  }

  @Override
  public int getCharge() {
    if (charge == null && charge2 == null)
      return Unit.NA;
    
    if (charge != null)
      return super.getCharge() - (getHits()-getHealth());
    else
      return getMelee()+getChargeBonus();
  }

  
  @Override
  public int getMelee() {
    if (melee != null)
      return super.getMelee() - (getHits()-getHealth());
              
    return melee2[safeIndex(melee2.length)]
            + ((getHeroEL() != Unit.NA)
            ? bfield.RulesUtilities.getHeroAttackBonus(getHeroEL())
            : 0);
  }

  @Override
  public int getMissile() {
    if (missile == null && missile2 == null)
      return Unit.NA;
    
    if (missile != null)
      return super.getMissile() - (getHits()-getHealth());
    else if (missile2 != null)
      return missile2[safeIndex(missile2.length)];
    else
      return Unit.NA;
  }

  @Override
  void setMissile(int i) {
    if (missile != null) {
      super.setMissile(i);
    } else {
      missile2[safeIndex(missile2.length)] = i;
    }
  }

  @Override
  void setMelee(int i) {
    if (melee != null) {
      super.setMissile(i);
    } else {
      melee2[safeIndex(melee2.length)] = i;
    }
  }
  */
  @Override
  public Unit2nd clone() {
      Unit2nd obj = (Unit2nd) super.clone();
      if (melee2 != null)
        obj.melee2 = Arrays.<Integer>copyOf(melee2, melee2.length);
      if (missile2 != null)
        obj.missile2 = Arrays.<Integer>copyOf(missile2, missile2.length);
      if (charge2 != null)
        obj.charge2 = Arrays.<Integer>copyOf(charge2, charge2.length);
    
      return obj;
  }
}
