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
package bfield;

import java.security.SecureRandom;
import java.util.regex.Matcher;

/**
 *
 * @author root
 */
public class RulesUtilities {
  
  public static String parseClassName(String className) {
    final String CLASS_REGEX="(Lt|Md|Hv)?(\\w+)([\\-\\+]*)";
    Matcher m = java.util.regex.Pattern.compile(CLASS_REGEX).matcher(className);
    StringBuilder builder = new StringBuilder();
    
    if (!m.find())
      throw new RuntimeException(className + " is not a valid class name.");
    
    if (m.group(1) != null) {
      switch(m.group(1)) {
        case "Lt": builder.append("Light"); break;
        case "Md": builder.append("Medium"); break;
        case "Hv": builder.append("Heavy"); break;
        default: builder.append("Standard"); break;
      }
    }
    builder.append(" ");
    
    switch (m.group(2)) {
      case "Inf": builder.append("Infantry"); break;
      case "Irr": builder.append("Irregulars"); break;
      case "Art": builder.append("Artillerists"); break;
      case "Cav": builder.append("Cavalry"); break;
      case "Pike": builder.append("Pikemen"); break;
      case "Ship": builder.append("Ship");break;
      case "Arch": builder.append("Archer");break;
      default: builder.append("Special"); break;
    }
    
    if (m.group(3) != null) {
      if (m.group(3).contains("+"))
        builder.append(", veterans");
      else if (className.contains("-"))
        builder.append(", green");
    }
    return builder.toString();
  }
  

  
  public static int roll(int die, int mod) {
    double d = (double)die;
    double result = (Math.random()*d)+1+mod;
    return (int) Math.round(result);
  }
  
  public static String rollHeroDefeated(int mod) {
    int result = roll(20, mod);
    if (result < 4) return "Character is slain on the battlefield";
    else if (result >= 4 && result < 10) 
      return "Character is captured by the enemy";
    else if (result >= 10 && result < 15) 
      return "Character escapes the rout, but is exhausted, wounded, "
              + "and may not participate in the remainder of the battle";
    else if (result >= 15 && result < 20)
      return "The character escapes the rout and "
              + "returns to the reserve in " + roll (4,1) + " [1d4 + 1] "
              + "tactical rounds";
    else
      return "The character escapes the rout and returns to the reserve at "
              + "the end of the tactical round";
  }
  
  public static String rollWeather(int mod) {
    int result = roll(100, mod);
    
    if (result < 71) return "Normal";
    else if (result < 81) return "Abnormal";
    else if (result < 90) return "Inclement";
    else if (result < 99) return "Storm";
    else
      return "Major Storm";
  }
}
