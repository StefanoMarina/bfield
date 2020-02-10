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


import bfield.data.adapters.FxColorTypeAdapter;
import bfield.rules.UnitRules;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javafx.scene.paint.Color;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *  Representation of an army, a group of military units identified by
 *  name.
 *  Army does mostly 3 things:
 * <ul>
 * <ol>Manage and handle a list of units</ol>
 * <ol>Manage and handle army identity, such as color and name</ol>
 * <ol>Handle relationship (only enemy at this time) with other armies</ol>
 * </ul>
 * the ID property as of now is based upon fixed string found in 
 * {@link bfield.data.Battle} class.
 * 
 * @author ste
 */
@XmlRootElement(name = "army")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Unit.class, Unit2nd.class})
public class Army {

  /**
   * Unique id to the army. 
   * @see bfield.data.Battle#ID_HOME
   * @see bfield.data.Battle#ID_AWAY
   */
  @XmlAttribute
  protected String id;

  /**
   * Display name of the army.
   */
  @XmlElement
  protected String name;

  /**
   * Unit list. use only getUnits().
   * @see bfield.data.Army#getUnits() 
   */
  @XmlElementWrapper(name = "units")
  @XmlElements(value={
    @XmlElement(name="unit", type=Unit.class),
    @XmlElement(namespace=URI.ADND_URI, name="unit", type=Unit2nd.class)
  })
  protected java.util.ArrayList<Unit> units;

  @XmlAttribute
  Boolean hasHighGround;

  @XmlAttribute
  Boolean isFortified;

  @XmlAttribute(required = true)
  Integer attackMod;

  @XmlAttribute(required = true)
  Integer defenseMod;

  @XmlElement(name = "color")
  @XmlJavaTypeAdapter(FxColorTypeAdapter.class)
  javafx.scene.paint.Color color;

  @XmlElement(name = "enemyID")
  String enemyID;
  
  @XmlTransient
  Army enemy;
  @XmlElementWrapper(name = "ordinals", required = false)
  @XmlElement(name = "unitOrdinal")
  Map<String, Integer> ordinals;

/**
 * Returns the army's string identification.
 * @return a string with a valid identification
 * @see bfield.data.Battle.ID_HOME
 * @see bfield.data.Battle.ID_AWAY
 */
  public String getID() {
    return id;
  }

  /**
   * Sets this army's current id. This should be called only by battle
   * or battle managers.
   * @param id a valid String identifier
   */
  public void setID(String id) {
    this.id = id;
  }

  /**
   * Sets a reference to this army's enemy. This is useful when conditions
   * are dependent upon enemy status.
   * This will <b>not</b> automatically set this army as enemy's enemy.
   * This also sets <i>enemyID</i> property for serialization.
   * @param enemy a non-null army object to be identifed as the enemy.
   */
  public void setEnemy(Army enemy) {
    this.enemy = enemy;
    this.enemyID = enemy.getID();
  }

  /**
   * Return the army currently identified as the enemy.
   * @return the current enemy or null if no enemy is set.
   */
  public Army getEnemy() {
    return enemy;
  }

  /**
   * A safe utility to retrieve the enemy id. This will check the <i>enemy</i>
   * property first, then the <i>enemyID</i> property if enemy is not yet loaded.
   * @return a callto enemy.getID() or the enemyID property.
   */
  public String getEnemyID() {
    if (enemy == null) {
      return enemyID;
    } else {
      return enemy.getID();
    }
  }

  /**
   * Check if the army has the 'high ground' status set.
   * @return true if the army has the high ground.
   */
  public boolean hasHighGround() {
    if (hasHighGround == null) {
      hasHighGround = false;
    }

    return hasHighGround;
  }

  /**
   * Return the army's current color in JavaFX format.
   * @return the army's current color
   */
  public Color getColor() {
    return color;
  }

  /**
   * sets the army current color. The property will be serialized as a string.
   * @param color a valid JavaFX color Object.
   * @see javafx.scene.paint.Color
   */
  public void setColor(Color color) {
    this.color = color;
  }

  /**
   * Sets the high ground status, used by the rules object.
   * @param b high ground flag
   * @see bfield.rules.ArmyRules
   */
  public void setHighGround(boolean b) {
    hasHighGround = b;
  }

  /**
   * Returns the status of the "fortified" condition.
   * @return true if the army is fortified
   * @see bfield.rules.ArmyRules
   */
  public boolean isFortified() {
    if (isFortified == null)
      isFortified = false;
    return isFortified;
  }

  /**
   * Sets the status of the army fortification
   * @param fortified true if the army is fortified.
   * @see bfield.rules.ArmyRules
   */
  public void setFortified(boolean fortified) {
    isFortified = fortified;
  }


  /**
   * Utility that calls {@link bfield.data.Army#getActiveUnitsStream()}.count().
   * @return how many units in the army are not dead and not retired from battle.
   */
  public int getActiveUnitsSize() {
    return (int) getActiveUnitsStream().count();
  }

  /**
   * Creates a new array with all active units.
   * Active units are units on the battlefield, so they must be with 1+ hits
   * and not retired.
   * @return a new array with the active units.
   */
  public Unit[] getActiveUnits() {
    return getUnits().stream().filter(x -> !x.isDead() && !x.isRetired())
            .toArray(Unit[]::new);
  }
  
  /**
   * Gets a filtered stream with all active units.
   * Active units are units on the battlefield, so they must be with 1+ hits
   * and not retired.
   * @return a stream with the active units.
   */
  public Stream<Unit> getActiveUnitsStream() {
    return getUnits().stream().filter(x -> !x.isDead() && !x.isRetired());
  }
  
  /**
   * Returns the army display name.
   * @return the army's name.
   */
  public String getName() {
    return name;
  }

  /**
   * Allows access to the army's full list of units.
   * @return the whole list of units, regardless of status.
   */
  public ArrayList<Unit> getUnits() {
    if (units == null) {
      units = new java.util.ArrayList();
    }
    return units;
  }

  /**
   * Enable or disable the 'ignoreWeather' flag on every unit present.
   * @param ignoreSuffering true if the army is immune to weather condition.
   * @see bfield.data.Unit
   * @see bfield.data.Unit#setIgnoreWeather(boolean)
   */
  public void ignoreWeather(boolean ignoreSuffering) {
    getUnits().forEach((u) -> {
      u.setIgnoreWeather(ignoreSuffering);
    });
  }

  /**
   * Adds a new unit to the army.
   * @param u the unit to be added.
   */
  public void addUnit(Unit u) {
    getUnits().add(u);
  }

  /**
   * Returns the user's modifier to standard and special attack.
   * @return the current modifier or 0 if no modifier set.
   */
  public int getAttackMod() {
    if (attackMod == null)
      attackMod = 0;
    return attackMod;
  }

  /**
   * Returns the user's modifier to defense.
   * @return the current modifier or 0 if no modifier set.
   */
  public Integer getDefenseMod() {
    if (defenseMod == null)
      defenseMod = 0;
    return defenseMod;
  }

  /**
   * Sets the new modifier to standard and special attack
   * @param attackMod the new value
   */
  public void setAttackMod(Integer attackMod) {
    this.attackMod = attackMod;
  }

  /**
   * Sets the new modifier to standard defense
   * @param defenseMod the new value
   */
  public void setDefenseMod(Integer defenseMod) {
    this.defenseMod = defenseMod;
  }

  /**
   * Sets the army's display name.
   * @param name 
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Automatically increase the ordinal number on the internal store
   * @param unitName unit name
   * @return the new ordinal, updated
   * @see #getOrdinals()
   */
  public Integer nextOrdinal(String unitName) {
    if (!getOrdinals().containsKey(unitName)) {
      getOrdinals().put(unitName, 0);
    }
    //Increase & return
    getOrdinals().put(unitName, getOrdinals().get(unitName) + 1);
    return getOrdinals().get(unitName);
  }

  /**
   * Ordinal numbers tied to unit (IE 1st infantry, etc) are stored
   * internally on a unitname/currentOrdinal map, which is serialized.
   * @return the internal unit/number store map.
   * @see #nextOrdinal(String)
   */
  public Map<String, Integer> getOrdinals() {
    if (ordinals == null) {
      ordinals = new HashMap();
    }
    return ordinals;
  }
}
