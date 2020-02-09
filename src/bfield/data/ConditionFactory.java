package bfield.data;

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

import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * ConditionFactory is the object whose serialization is represented by the 
 * conditions.xml file inside a rules folder. It is a way to manage the
 * condition divided by categories. At this moment the categories are fixed 
 * on those of 3rd Edition: Visibility, terrain and climate.
 * @author ste
 */
@XmlRootElement(name="conditions")
@XmlSeeAlso(Condition.class)
@XmlAccessorType(XmlAccessType.FIELD)
public class ConditionFactory {
  
  @XmlElementWrapper(name="terrain")
  @XmlElement(name="condition")
  java.util.List<Condition> terrain;
  
  @XmlElementWrapper(name="weather")
  @XmlElement(name="condition")
  java.util.List<Condition> weather;
  
  @XmlElementWrapper(name="visibility")
  @XmlElement(name="condition")
  java.util.List<Condition> visibility;
  
  /**
   * Returns all the available terrain conditions.
   * @return a condition list.
   */
  final public List<Condition> getTerrain() {
    if (terrain == null)
      terrain = new java.util.ArrayList();
    return terrain;
  }

  /**
   * Returns all the available visibility conditions.
   * @return a condition list.
   */
  public List<Condition> getVisibility() {
    if (visibility == null)
      visibility = new java.util.ArrayList();
    return visibility;
  }
  
  /**
   * Returns all the available weather conditions.
   * @return a condition list.
   */
  public List<Condition> getWeather() {
    if (weather == null)
      weather = new java.util.ArrayList();
    return weather;
  }
  
  private Condition find(String name, java.util.List<Condition> array) 
      throws IllegalArgumentException{
    for (Condition c : array)
      if (name.equalsIgnoreCase(c.getName()))
        return c;
    
    throw new IllegalArgumentException("cannot find ["+name+"]");
  }
  
  /**
   * Utility to find a condition inside the terrain list, looking by its name.
   * Since it should be used for hard-coding only it throws IllegalArgumentException
   * if the condition is not found. Search is case-insensitive.
   * @param name to be searched
   * @return reference to requested Condition
   * @throws IllegalArgumentException if no condition match <i>name</i>.
   */
  public Condition getTerrain(String name) throws IllegalArgumentException {
    return find(name, getTerrain());
  }
  
  /**
   * Utility to find a condition inside the terrain list, looking by its name.
   * Since it should be used for hard-coding only it throws IllegalArgumentException
   * if the condition is not found. Search is case-insensitive.
   * @param name to be searched
   * @return reference to requested Condition
   * @throws IllegalArgumentException if no condition match <i>name</i>.
   */
  public Condition getWeather(String name) throws IllegalArgumentException {
    return find(name, getWeather());
  }

  /**
   * Utility to find a condition inside the terrain list, looking by its name.
   * Since it should be used for hard-coding only it throws IllegalArgumentException
   * if the condition is not found. Search is case-insensitive.
   * @param name to be searched
   * @return reference to requested Condition
   * @throws IllegalArgumentException if no condition match <i>name</i>.
   */
  public Condition getVisibility(String name) throws IllegalArgumentException {
    return find(name, getVisibility());
  }
   
}
