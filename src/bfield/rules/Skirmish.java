/*
 * Copyright (C) 2020 ste
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
package bfield.rules;

import bfield.data.Unit;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Skirmish is just a collection of 2 lists of units that will attack and
 * reply. This is according to SRD that says that engaged units must fight
 * inside the same area and cannot just attack randomly.
 * In future releases Skirmish wil be embed in data, now its just an utility class.
 * @author ste
 */
public class Skirmish extends HashMap.SimpleEntry<ArrayList<Unit>, ArrayList<Unit>> {
  
  public Skirmish() {
    super (new java.util.ArrayList(), new java.util.ArrayList());
  }
  public Skirmish (Unit k, Unit v) {
    super (new java.util.ArrayList(), new java.util.ArrayList());
    super.getKey().add(k);
    super.getValue().add(v);
  }
  
  public void addKey(Unit k) { super.getKey().add(k);}
  public void addValue(Unit v) { super.getValue().add(v);}
   
  public boolean containsKey(Unit u) {return super.getKey().contains(u); }
  public boolean containsValue(Unit u) {return super.getValue().contains(u); }

  public boolean isKeyDefeated() {return super.getKey().stream().allMatch( u -> { return u.isDead() || u.isRetired();});}
  public boolean isValueDefeated() {return super.getValue().stream().allMatch( u -> { return u.isDead() || u.isRetired();});}
}
