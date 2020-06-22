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
package bfield.scene;

import java.util.List;
import javafx.scene.Group;
import javafx.scene.layout.Region;

/**
 * Utility class to handle SVG icon on a colored background.
 * Properties such as size and colro are set using css classes
 * @author ste
 */
public class Icon extends Group {
  private final Region back;
  private final Region front;
  private String sizeClass="";
  private String colorClass;
  private String iconClass;
  private String formatClass;
  
  public Icon() { 
    super(); 
    back = new Region();
    front = new Region();
    super.getChildren().add(back);
    super.getChildren().add(front);
  }

  public Icon(String formatClass) {
    super();
    back = new Region();
    front = new Region();
    super.getChildren().add(back);
    super.getChildren().add(front);
    
    this.setFormatClass(formatClass);
  }
  /**
   * Sets both size style classes for regions.
   * @param sizeClass xs|sm|md|lg
   */
  public void setSizeClass(String sizeClass) {
    this.sizeClass = sizeClass;
    
    //removes previous icon size
    back.getStyleClass().removeIf( str -> (str.matches("picture\\-\\w+\\-back")) );
    front.getStyleClass().removeIf( str -> (str.matches("picture\\-\\w+\\-front")) );
    back.getStyleClass().add(0,"picture-"+sizeClass+"-back");
    front.getStyleClass().add(0,"picture-"+sizeClass+"-front");
  }

  public String getSizeClass() {
    return sizeClass;
  }
  
  public Region getBack() { return back; }
  public Region getFront() { return front; }
  
  
  private static void parseClassString(String multiClassString, List<String> list) {
    String[] items = multiClassString.replaceAll(" ", "").split(",");
    for (String s : items)
      list.add(s);
  }
  
  /**
   * Sets the background color for this class. Class should start with 'b-'.
   * @param colorClass  a b-xxx class from content.css
   */
  public void setColorClass(String colorClass) {
    this.colorClass = colorClass;
    back.getStyleClass().removeIf( str -> (str.matches("b-.*")) );
    
    if (colorClass.contains(","))
      parseClassString(colorClass, back.getStyleClass());
    else
      back.getStyleClass().add(colorClass);
  }
  
  public String getColorClass() {
    return colorClass;
  }

  /**
   * Sets the icon class containing the shape. icon should start with 'icon-' as in icons.cc
   * @param iconClass a icon-xxx class from icons.css
   */
  public void setIconClass(String iconClass) {
    this.iconClass = iconClass;
    
    front.getStyleClass().removeIf( str -> (str.matches("icon-.*")));
    if (iconClass.contains(","))
      parseClassString(iconClass, front.getStyleClass());
    else
      front.getStyleClass().add(iconClass);
  }

  public String getIconClass() {
    return iconClass;
  }

  /**
   * FormatClass is a utility property to set all at once
   * format is sizeClass, colorClass, iconClass
   * format may be skipped in order, i.e. size, color or size
   * @param formatClass i.e 'xs, b-def, i-shield'
   */
  public void setFormatClass(String formatClass) {
    this.formatClass = formatClass;
    String[] elements = formatClass.replaceAll(" ", "").split(",");
    
    if (elements.length == 0)
      return;
    
    if (elements.length >= 1)
      setSizeClass(elements[0]);
    if (elements.length >= 2)
      setColorClass(elements[1]);
    if (elements.length >= 3)
      setIconClass(elements[2]);
  }
  
  public String getFormatClass() {
    return formatClass;
  }
  
  /**
   * Set the icon in 'disabled' mode.
   * @param value 
   */
  public void setGray(boolean value) {
    super.setDisabled(value);
    
    if (value) {
      back.getStyleClass().remove(colorClass);
      back.getStyleClass().add("b-disabled");
    } else {
      back.getStyleClass().remove("b-disabled");
      back.getStyleClass().add(colorClass);
    }
  }
}
