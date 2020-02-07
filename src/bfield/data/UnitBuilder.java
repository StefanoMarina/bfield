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

import bfield.Utilities;

/**
 *
 * @author ste
 */
public interface UnitBuilder {
  
  static void createCommonUnitValues(Unit u, java.util.Map<String,String> values) {
    u.def = Integer.parseInt(values.get("defense"));
    u.hits = Integer.parseInt(values.get("hits"));
    u.icon = values.get("icon");
    u.name = values.get("name");
    
    StringBuilder sbname = new StringBuilder();
    switch (values.get("speed")) {
      case "Light": sbname.append("Lt"); break;
      case "Heavy": sbname.append("Hv");break;
      default : sbname.append("Md");break;
    }
    
    sbname.append(Utilities.classGetShortName(values.get("className")));
    
    
    switch (values.get("level")) {
      case "Green": sbname.append("-");break;
      case "Veteran": sbname.append("+");break;
    }
    
    u.className = sbname.toString();
  }
  
  static public void setNameMark(Unit u) {
    u.name += "*";
  }
          
  static public Unit createD20Unit(java.util.Map<String,String> values) {
    Unit u = new Unit();
    createCommonUnitValues(u, values);
    
    u.melee = Integer.parseInt(values.get("melee"));
    u.charge = Integer.parseInt(values.get("charge"));
    u.missile = Integer.parseInt(values.get("missile"));
    u.morale = Integer.parseInt(values.get("morale"));
    u.movement = (Integer)Integer.parseInt(values.get("movement"));
    
    return u;
  }
  
  static public void setUnitExperience(Unit u, String experience) {
    u.className = u.className.replaceAll("[\\-\\+]", "");
    u.className += experience;
  }
}
