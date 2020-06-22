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

import bfield.data.Battle;
import bfield.data.adapters.FxColorTypeAdapter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class builds a whole HTML page from a battle result.
 * First it builds html header, where CSS is loaded and custom
 * CSS from armies color is created.
 * It then provides a body with a <table> element containing each
 * definition from a Map<String,String>. If the map has only 1 key,
 * a simple div is provided.
 * You should map result using the following keys:
 * css - any css you want to add.
 * main - anything before the table. use this if you don't want the table
 * ID / ID (i.e. Home or Away) use the armies ID to add content inside the table.
 * post - use this to add anything after the table
 * @author ste
 */
public class BattleResultFormatter {
  
  /**
   * return the link to local css file. override this to add your own css file
   * @param builder valid stringbuilder
   * @return builder
   */
  protected String getCSSUri() {
    return new StringBuilder().append("file://").append(System.getProperty("user.dir"))
            .append("/res/log.css").toString();
  }
  
  /**
   * Creates the <head> component, embedding default css and building custom css to match
   * army colors
   * @param battle a valid battle
   * @param builder the builder from toHTML
   * @param customCSS any custom css passed through the 'css' key in results. may be null
   * @return builder
   */
  protected StringBuilder createHead(Battle battle, StringBuilder builder, String customCSS) {
    builder.append("<link rel=\"stylesheet\" href=\"")
            .append(getCSSUri()).append("\">");
    builder.append("<style>");
    
    final FxColorTypeAdapter cta = new FxColorTypeAdapter();
    
    
    battle.getArmies().values().forEach( army -> {  
      String color;
      try {
        color = cta.marshal(army.getColor());
      } catch (Exception e) {
        throw new RuntimeException (e);
      }
      
      builder.append(".").append(army.getID()).append(" {")
                .append("background-color: ").append(color).append(";")
                .append("color: ");
      
      if (army.getColor().getBrightness() >= 0.8)
        builder.append("black; } ");
      else
        builder.append("white; } ");
      
      builder.append(".").append(army.getID()).append("-ul { border-color: ")
              .append(color).append(";} ");
    });
    
    if (customCSS != null)
      builder.append(customCSS);
    
    builder.append("</style>");
     
    return builder;
  }
  
  /**
   * create the <body> tag content. the tag itself is not added here, rather in toHTML();
   * @param builder valid StringBuilder
   * @param results results from BattleRules
   * @return builder
   */
  protected StringBuilder createBody(StringBuilder builder, Battle battle, Map<String,String> results) {
    if (results == null || results.isEmpty())
      throw new NullPointerException("missing battle results!");
    
    
    builder.append("<h1>Round ").append(battle.getRound()).append("</h1>");
    if (results.containsKey("main"))
      builder.append("<div id=\"main\">").append(results.get("main")).append("</div>");
    
    if (results.keySet().stream().anyMatch( 
            key -> { return battle.getArmies().containsKey(key); })) {
      builder.append("<table id=\"table\"><tr>");
      
      battle.getArmies().keySet().forEach(key -> {
        builder.append(
                String.format("<th><h2 class=\"army %s\">%s</h2></th>", key, key));
      });
      
      builder.append("</tr><tr>");
      
      battle.getArmies().keySet().forEach(armyID -> {
        builder.append("<td>").append(results.getOrDefault(armyID,""))
                .append("</td>");
      });
      
      builder.append("</tr></table>");
    }
    
    if (results.containsKey("post")) {
      builder.append("<div id=\"post\">").append(results.get("post")).append("</div>");
    }
    return builder; 
  }
  
  /**
   * Creates a full html page with the results. calls createHead and createBody
   * adding effectively the <head> and <body> tag.
   * @param b a valid battle
   * @param results results from the battle
   * @return a full html page.
   */
  public String toHTML(Battle b, Map<String,String> results) {
    StringBuilder html = new StringBuilder();
    html.append("<html><head>");
    createHead(b, html, results.getOrDefault("css", null));
    html.append("</head><body>");
    createBody(html, b, results);
    html.append("</body></html>");
    return html.toString();
  }
  
}
