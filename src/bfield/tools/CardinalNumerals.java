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
package bfield.tools;

/**
 *
 * @author ste
 */
public interface CardinalNumerals {

  public static String toCardinal(long value) {
    if (value <= 0)
      return String.valueOf(value);
    
    StringBuilder sb = new StringBuilder().append(value);
    String digit = sb.substring(sb.length()-1, sb.length());
    switch (digit) {
      case "1" : return sb.append("st").toString();
      case "2" : return sb.append("nd").toString();
      case "3" : return sb.append("rd").toString();
      default: return sb.append("th").toString();
    }
  }  
}
