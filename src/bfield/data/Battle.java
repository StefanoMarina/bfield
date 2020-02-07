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


import bfield.data.adapters.RulesReferenceAdapter;
import bfield.rules.ArmyRules;
import bfield.rules.UnitRules;
import bfield.rules.srd.UnitSRD;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Random;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;
 /**
 *
 * @author root
 */    
@XmlRootElement(name="battle")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Unit.class, Unit2nd.class, Army.class, Condition.class})
public class Battle {
  
  public static final String ID_HOME ="Home",
          ID_AWAY ="Away";
  
  @XmlElementWrapper(name="armies")
  java.util.Map<String, Army> armies;
  
  @XmlAttribute(name="round", required=true)
  Integer round;
  
  @XmlElement(required=true)
  String name;
  
  @XmlElement(name="terrain", required=true)
  Condition terrain;
  
  @XmlElement(name="weather", required=true)
  Condition weather;
  
  @XmlElement(name="visibility", required=true)
  Condition visibility;
  
  @XmlElement(name="ruleset")
  @XmlJavaTypeAdapter(RulesReferenceAdapter.class)
  Rules rules;
 
  
  @XmlTransient
  protected UnitRules unitRules;
  
  @XmlTransient
  protected java.util.Map<String, ArmyRules> armyRules;
  
  public void setRules(Rules rules) {
    this.rules = rules;
  }

  public Rules getRules() {
    return rules;
  }
  
  public Army getArmy(String ID) {
    return armies.get(ID);
  }

  public void addArmy(String ID, Army army) {
    army.setID(ID);
    addArmy(army);
  }
  
  public void addArmy(Army army) {
    getArmies().put(army.getID(), army);
  }
          
  
      
  public Map<String, Army> getArmies() {
    if (armies == null)
      armies = new java.util.HashMap();
    
    return armies;
  }

  public int getRound() {
    if (round == null) round = 1;
    return round;
  }

  public void setRound(int round) {
    this.round = round;
  }
  
  public int nextRound() {
    if (round == null) 
      round = 1;
    round++;
    return (round-1);
  }

  public String getName() {
    if (name == null)
      name = "Skirmish";
    
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Condition getTerrain() {
    return terrain;
  }

  public void setTerrain(Condition terrain) {
    this.terrain = terrain;
  }

  public Condition getWeather() {
    return weather;
  }

  public void setWeather(Condition weather) {
    this.weather = weather;
  }

  public Condition getVisibility() {
    return visibility;
  }

  public void setVisibility(Condition visibility) {
    this.visibility = visibility;
  }

  public UnitRules getUnitRules() {
    if (unitRules == null)
      unitRules = getRules().newUnitRules(this);
    
    return unitRules;
  }
  
  public ArmyRules getArmyRules(String armyID) {
    return getRules().newArmyRules(this, armyID);
  }
}

