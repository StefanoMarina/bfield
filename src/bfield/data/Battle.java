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
import java.util.Map;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;
 
/**
 * The battle class contains all that is necessary for a BattleRule to
 * determine an attack result. As per 3rd ed BRCS, the following conditions are
 * recognized:
 * <ul>
 * <li>Weather</li>
 * <li>Terrain</li>
 * <li>Visibility</li>
 * </ul>
 * The <i>effectiveness</i> rule in BRCS adds a malus each time a unit lose
 * a hit. Only two armies are managed at this time: home team and away team.
 * @author ste
 */    
@XmlRootElement(name="battle")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Unit.class, Unit2nd.class, Army.class, Condition.class})
public class Battle {
  
  /**
   * Identifies the army that will go on the left side of a armyController form.
   * @see bfield.data.Army#setID(java.lang.String) 
   * @see bfield.data.Army#getID()
   */
  public static final String ID_HOME ="Home",

  /**
   * Identifies the army that will go on the right side of a armyController form.
   * @see bfield.data.Army#setID(java.lang.String) 
   * @see bfield.data.Army#getID()
   */
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
 
  /**
   * Reference to a single UnitRules object. should be removed
   */
  @XmlTransient
  protected UnitRules unitRules;
  
  
  /**
   * Sets the new Rules database.
   * @param rules a non-null rules database.
   */
  public void setRules(Rules rules) {
    this.rules = rules;
  }

  /**
   * returns the current rules database.
   * @return
   */
  public Rules getRules() {
    return rules;
  }
  
  /**
   * Returns a reference to an army looking by their id. the same as
   * getArmies().get(ID).
   * @param ID either ID_HOME or ID_AWAY
   * @return a valid army or null if ID is invalid.
   */
  public Army getArmy(String ID) {
    return armies.get(ID);
  }

  /**
   * adds a new Army to the battlefield, mapping it to ID. ID will be assigned to army.
   * Note that if the id was already used, the previous reference will be lost!
   * @param ID a valid ID
   * @param army
   */
  public void addArmy(String ID, Army army) {
    army.setID(ID);
    addArmy(army);
  }
  
  /**
   * Utility to getArmies().put(army).
   * @param army
   */
  public void addArmy(Army army) {
    getArmies().put(army.getID(), army);
  }
          
  /**
   * returns the armies map.
   * @return a valid map.
   */
  public Map<String, Army> getArmies() {
    if (armies == null)
      armies = new java.util.HashMap();
    
    return armies;
  }

  /**
   * gets the current round.
   * @return a number between 1 and Integer.MAX
   */
  public int getRound() {
    if (round == null) round = 1;
    return round;
  }

  /**
   * Sets the current round, 
   * @param round the new round
   * @throws IndexOutOfBoundsException if round < 1
   */
  public void setRound(int round) throws IndexOutOfBoundsException{
    if (round < 1)
      throw new IndexOutOfBoundsException("round must be at least 1");
    this.round = round;
  }
  
  /**
   * Increases the current round counter and returns the previous state. So with
   * getRound() = 1, nextRound() will move round to 2 but return 1.
   * @return the previous round.
   */
  public int nextRound() {
    if (round == null) 
      round = 1;
    round++;
    return (round-1);
  }

  /**
   * returns the battle display name. default is "skirmish".
   * @return
   */
  public String getName() {
    if (name == null)
      name = "Skirmish";
    
    return name;
  }

  /**
   * Sets the battle display name.
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * get the current Terrain condition.
   * @return a condition referenced as "terrain".
   */
  public Condition getTerrain() {
    return terrain;
  }

  /**
   * Sets the new terrain condition
   * @param terrain the new condition.
   */
  public void setTerrain(Condition terrain) {
    this.terrain = terrain;
  }

  /**
   * get the current weather condition.
   * @return a condition referenced as "weather".
   */
  public Condition getWeather() {
    return weather;
  }

  /**
   * Sets the new condition.
   * @param weather the new condition. should not be null.
   */
  public void setWeather(Condition weather) {
    this.weather = weather;
  }

  /**
   * Gets the current weather.
   * @return the current weather.
   */
  public Condition getVisibility() {
    return visibility;
  }

  /**
   * Sets the new condition.
   * @param weather the new condition. should not be null.
   */
  public void setVisibility(Condition visibility) {
    this.visibility = visibility;
  }

  /**
   *
   * @return
   */
  public UnitRules getUnitRules() {
    if (unitRules == null)
      unitRules = getRules().newUnitRules(this);
    
    return unitRules;
  }
  
  /**
   * Create a new ArmyRules based upon the actual conditions of the 
   * battlefield.
   * @param armyID a valid army id, by valid meaning any id registered as a key
   * in {@link bfield.data.Battle#armies
   * @return
   */
  public ArmyRules getArmyRules(String armyID) {
    return getRules().newArmyRules(this, armyID);
  }
}

