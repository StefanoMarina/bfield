/*
 * Copyright (C) 2019 Stefano
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

import java.io.File;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;

/**
 *
 * @author Stefano
 */
public interface Utilities {
  public static String loadSVGFile(String filename) throws Exception {
    File f = new File("svg"+File.separator+filename);
    
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = null;
    try {
       db = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException ex) {
      throw new RuntimeException(ex);
    }
    
    
    Document d = db.parse(f);
    Element path = 
        (Element)((Element)d.getDocumentElement().getElementsByTagName("g").item(0))
        .getElementsByTagName("path").item(0);
    if (path != null)
      return path.getAttribute("d");
    else
      throw new Exception("SVG file is not regular.");
  }
  
  static public Integer[] generatePositiveLinearArray(int from, int to) {
    Integer[] result = new Integer[to-(from-1)];
    for (int i = 0; i < result.length; i++) {
      result[i] = from+i;
    }
    return result;
  }
  
  static public String classGetFullName(String fullClass) {
    String test = fullClass.replaceAll("(Hv|Md|Lt)", "")
            .replaceAll("[\\+\\-]","");
    switch (test) {
      case "Inf": return "Infantry";
      case "Pike": return "Pikemen";
      case "Arch": return "Archers";
      case "Art": return "Artillery";
      case "Irr": return "Irregulars";
      case "Cav": return "Cavalry";
      case "Ship": return "Ship";
      default: return "Special";
    }
  } 
  
  static public String classGetShortName(String longName) {
    switch (longName) {
      case "Infantry": return "Inf";
      case "Pikemen": return "Pike";
      case "Archers": return "Arch";
      case "Artillery": return "Art";
      case "Irregulars": return "Irr";
      case "Cavalry": return "Cav";
      case "Ship": return "Ship";
      default: return "Special";
    }
  }
  
  static public StringBuilder tag(StringBuilder builder, String tag, String classes, String style) { 
    builder.append("<").append(tag);
    if (classes != null)
      builder.append(" class=\"").append(classes).append("\"");
    if (style != null)
      builder.append(" style=\"").append(style).append("\"");
    
    return builder.append(">");
  }
}
