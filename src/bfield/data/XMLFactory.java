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

import java.io.File;
import java.io.IOException;
import javafx.scene.paint.Color;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.*;

/**
 *
 * @author root
 */

@XmlRootElement(name="dataset")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLFactory {
  @XmlTransient
  protected ConditionFactory conditionFactory;
  @XmlTransient
  protected Rules rules;
  @XmlTransient
  protected UnitFactory unitFactory;
    
  @XmlElement
  protected String name;
  
  @XmlElement
  protected String description;

  
  @XmlTransient
  private File path;

  public File getPath() {
    return path;
  }
  
  public void setPath(File path) {
    this.path = path;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  
  
  public Army newArmy(String ID) {
    Army p = new Army();
    p.setAttackMod(0);
    p.setDefenseMod(0);
    p.setID(ID);
    p.setName(ID);
    p.setFortified(false);
    p.setHighGround(false);
    
    return p;
  }
  
  public Battle newBattle() {
    return newBattle("Home", "Away", "Skirmish");
  }
  
  public Battle newBattle(String shome, String saway, String name) {
    Battle b = new Battle();
    b.setName(name);
    b.setRound(1);
    
    Army home = newArmy(Battle.ID_HOME);
    Army away = newArmy(Battle.ID_AWAY);
    
    
    home.setName(shome);
    away.setName(saway);
    home.setEnemy(away);
    away.setEnemy(home);
    
    home.setColor(Color.TOMATO);
    away.setColor(Color.ROYALBLUE);
    
    b.addArmy(Battle.ID_HOME, home);
    b.addArmy(Battle.ID_AWAY, away);
    
    
    b.terrain = conditionFactory.getTerrain().get(0);
    b.weather = conditionFactory.getWeather().get(0);
    b.visibility = conditionFactory.getVisibility().get(0);
    b.setRules(rules);
    
    return b;
  }
 
  public ConditionFactory getConditionFactory() {
    return conditionFactory;
  }

  public Rules getRules() {
    return rules;
  }

  public UnitFactory getUnitFactory() {
    return unitFactory;
  }

  public static XMLFactory loadDataSet(File fDir) throws IOException {
    //File fDir = new File(System.getProperty("user.dir")+File.separator+"rules"+File.separator+dir);
    if (!fDir.exists() || !fDir.isDirectory())
      throw new IOException(fDir.getAbsolutePath() + " is not a valid directory.");
    
    File fUnits = new File(fDir.getAbsolutePath()+File.separator+"units.xml");
    File fConditions = new File(fDir.getAbsolutePath()+File.separator+"conditions.xml");
    File fRules = new File(fDir.getAbsolutePath()+File.separator+"rules.xml");
    File fDescription = new File(fDir.getAbsolutePath()+File.separator+"description.xml");
    if (!fUnits.exists())
      throw new IOException("Unit file missing.");
    else if (!fConditions.exists())
      throw new IOException("Conditions file missing.");
    else if (!fRules.exists())
      throw new IOException("Rules file missing.");
    
    XMLFactory factory;
    if (fDescription.exists())
      factory = JAXB.unmarshal(fDescription, XMLFactory.class);
    else {
      factory = new XMLFactory();
      factory.name=fDir.getName();
      factory.description="description file missing";
    }
    
    factory.setPath(fDir);
    
    try {
      factory.rules = JAXB.unmarshal(fRules, Rules.class);
    } catch (Exception e) {
      throw new IOException("Rules file failed to load. Cause: " + e.getLocalizedMessage());
    }
    
    try {
      factory.conditionFactory = JAXB.unmarshal(fConditions, ConditionFactory.class);
    } catch (Exception e) {
      throw new IOException("Conditions file failed to load. Cause: " + e.getLocalizedMessage());
    }
    
    try {
      factory.unitFactory = JAXB.unmarshal(fUnits, UnitFactory.class);
    } catch (Exception e) {
      throw new IOException("Units file failed to load. Cause: " + e.getLocalizedMessage());
    }
    
    return factory;
  }
  
  public void save() throws IOException {
    File fDir = getPath();
    if (!fDir.exists() || !fDir.isDirectory())
      throw new IOException(fDir.getAbsolutePath() + " is not a valid directory.");
    
    File fUnits = new File(fDir.getAbsolutePath()+File.separator+"units.xml");
    File fConditions = new File(fDir.getAbsolutePath()+File.separator+"conditions.xml");
    File fRules = new File(fDir.getAbsolutePath()+File.separator+"rules.xml");
    File fDescription = new File(fDir.getAbsolutePath()+File.separator+"description.xml");
    
    try {
       JAXB.marshal(rules, fRules);
    } catch (Exception ioe) {
      throw new IOException("Rules save failed.", ioe);
    } 
    
    try {
      JAXB.marshal(getUnitFactory(), fUnits);
    } catch (Exception e) {
      throw new IOException("Unit factory save failed.", e);
    }
    
    try {
      JAXB.marshal(getConditionFactory(), fConditions);
    } catch (Exception e) {
      throw new IOException("Condition factory save failed.", e);
    }
    
    try {
      JAXB.marshal(this, fDescription);
    } catch (Exception e) {
      throw new IOException("Description factory save failed.", e);
    }
    
  }
}
