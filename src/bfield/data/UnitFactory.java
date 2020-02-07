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

/**
 *
 * @author root
 */

import java.util.List;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "units")
@XmlSeeAlso(Unit.class)
@XmlAccessorType(XmlAccessType.FIELD)
public class UnitFactory {
  
  @XmlElements(value={
    @XmlElement(name="unit", type=Unit.class),
    @XmlElement(namespace=URI.ADND_URI,name="unit", type=Unit2nd.class)
  })
  protected java.util.List<Unit> units;

  public java.util.List<String> getAllNames() {
    java.util.ArrayList<String> result = new java.util.ArrayList();
    for (Unit u : getUnits()){
      result.add(u.getName());
    }
    
    return result;
  }

  public Unit createUnit(String name) throws IllegalArgumentException {
    
    if (name == null)
      throw new IllegalArgumentException("name param is null");

    String lcName = name.toLowerCase();
    for (Unit u : getUnits()) {
      if (u.getName().toLowerCase().equals(lcName)) {
        if (u instanceof Unit2nd)
          return ((Unit2nd)u).clone();
        else
          return u.clone();
      }
    }
    throw new IllegalArgumentException("Could not find unit [" + name+"]");
  }
  
  public Unit createUnit(String name, int ordinal) throws IllegalArgumentException {
    Unit u = createUnit(name);
    u.setOrdinal(ordinal);
    return u;
  }
  
  public List<Unit> getUnits() {
    if (units == null)
      units = new java.util.ArrayList();
    
    return units;
  }
  
  public void setUnit(Unit unit) throws ArrayIndexOutOfBoundsException {
    for (int i = 0; i < units.size(); i++) {
      if (units.get(i).getName().toLowerCase().equals(unit.getName().toLowerCase())) {
        units.set(i, unit);
        return;
      }
    }
    throw new ArrayIndexOutOfBoundsException("could not find unit to replace");
  }
  
  public void appendUnit(Unit u) throws IllegalArgumentException{
    if (getAllNames().contains(u.getName()))
      throw new IllegalArgumentException(u.getName() +  " already exists.");
    
    units.add(u);
  }
  
  public void deleteUnit(Unit u) throws IllegalArgumentException {
    if (!getAllNames().contains(u.getName()))
      throw new IllegalArgumentException(u.getName() +  " not available.");
    
    units.remove(u);
  }
}
