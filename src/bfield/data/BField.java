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

import bfield.data.adapters.XMLFactoryReferenceAdapter;
import java.io.File;
import java.util.Map;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;
import bfield.rules.BattleRules;


/**
 * This is a container serialization object that serves as the root of a XML document.
 * BField stores all metadata and reference to rules and factories choosen when
 * creating a new battle. BField also serves as a bridge between raw data 
 * (bfield.data) and rules that manage them (bfield.rules).
 * Factories, being stored globally by the app, are serialized through the special
 * {@link bfield.data.adapters.XMLFactoryReferenceAdapter} class, so new bfield
 * documents always refer to application rules.
 * Rules and similar instances are serialized by saving the rule class simple name.
 * 
 * @author ste
 */
@XmlRootElement(name="bfield")
@XmlSeeAlso( {Battle.class, Army.class, Unit.class, Unit2nd.class, Condition.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class BField {
  
  /**
   * the current battle element
   */
  @XmlElement
  protected Battle battle;
  
  /**
   * The current factory. This will be a reference to Application
   * @see bfield.Application#getFactories() 
   */
  @XmlJavaTypeAdapter(XMLFactoryReferenceAdapter.class)
  @XmlElement(name="ruleset")
  protected XMLFactory factory;
  
  /**
   * Current selected battle rules mechanic from user menu. This will be saved
   * as the BattleRules class.
   * @see bfield.data.BField#getSelectedBattleMechanic() 
   * @see bfield.rules.BattleRules
   */
  @XmlElement(name="selectedBattleMechanic")
  protected String sBattleMechanic;
  
  /**
   * non-serialized instance of the BattleRules object. This should not be
   * needed to be a constat property.
   */
  @XmlTransient
  protected BattleRules bmSelected;
  
  @XmlTransient
  File file;
  
  @XmlTransient
  boolean upToDate = true;

  @XmlElementWrapper(name="ordinals", required=false)
  @XmlElement(name="unitOrdinal")
  java.util.Map<String,Integer> ordinals;
  
  /**
   * user preference: should units show a roman number before the name?
   */
  @XmlAttribute(name="useOrdinals")
  protected boolean bUseOrdinals;
  
  /**
   * Utility to store the document status. When the document is changed and not
   * saved, call setUpToDate(false). The program will use isUpToDate() to check
   * the document status. This is not serialized.
   * @return true if the document has unsaved changes
   */
  public boolean isUpToDate() {
    return upToDate;
  }

  /**
   * Utility to store the document status. When the document is changed and not
   * saved, call setUpToDate(false). The program will use isUpToDate() to check
   * the document status. This is not serialized.
   * @param upToDate true if all changes are saved.
   */
  public void setUpToDate(boolean upToDate) {
    this.upToDate = upToDate;
  }

  /**
   * This will create a BattleRules class according to the XMLFactory standard.
   * While the BattleRules created is stored as a property, only the class
   * name will be serialized as a preference. If a preference has been set but
   * no BattleRule has been created, a new BattleRule will be created.
   * 
   * @return true if the document has unsaved changes
   * @see bfield.rules.BattleRules
   */
  public BattleRules getSelectedBattleMechanic() {
    if (bmSelected == null && sBattleMechanic != null) {
      for (BattleRules bm : getFactory().getRulesFactory().getBattleMechanics()) {
        if (bm.getClass().getSimpleName().equals(sBattleMechanic)) {
          bmSelected = bm;
          break;
        }
      }
    } 
    
    //lookup failed or empty string
    if (bmSelected == null) {
      bmSelected = getFactory().getRulesFactory().getBattleMechanics().get(0);
      sBattleMechanic = bmSelected.getClass().getSimpleName();
    }
      
    return bmSelected;
  }

  /**
   * Sets the BattleRule to reference when handling encounters.
   * @param selectedBattle the new BattleRule. the class name will be serialized.
   * @see bfield.rules.BattleRules
   */
  public void setSelectedBattleMechanic(BattleRules selectedBattle) {
    this.bmSelected = selectedBattle;
    sBattleMechanic = bmSelected.getClass().getSimpleName();
  }
  
  
  /**
   * Returns the current battle.
   * @return a valid battle object.
   */
  public Battle getBattle() {
    return battle;
  }

  /**
   * Returns the reference to the XML Factory.
   * @return the current XML Factory.
   */
  public XMLFactory getFactory() {
    return factory;
  }

  
  /**
   * Sets the current battle
   * @param battle the new battle to reference
   */
  public void setBattle(Battle battle) {
    this.battle = battle;
  }

  /**
   * sets the current XMLFactory.
   * @param factory the new XmlFactory to use
   */
  public void setFactory(XMLFactory factory) {
    this.factory = factory;
  }

  /**
   * Returns the current file path.
   * @return the current file or null if no file set.
   */
  public File getFile() {
    return file;
  }

  /**
   * Sets the file path.
   * @param file the new file.
   */
  public void setFile(File file) {
    this.file = file;
  }

  /**
   *
   * @return
   */
  public Map<String, Integer> getOrdinals() {
    if (ordinals == null)
      ordinals = new java.util.HashMap();
    return ordinals;
  }

  /**
   *
   * @return
   */
  public boolean isUseOrdinals() {
    return bUseOrdinals;
  }

  /**
   *
   * @param bUseOrdinals
   */
  public void setUseOrdinals(boolean bUseOrdinals) {
    this.bUseOrdinals = bUseOrdinals;
  }
  
  /**
   *
   * @param unitName
   * @return
   */
  public Integer nextOrdinal(String unitName) {
    if (!getOrdinals().containsKey(unitName))
      getOrdinals().put(unitName, 0);
    
    //Increase & return
    getOrdinals().put(unitName, getOrdinals().get(unitName)+1);
    return getOrdinals().get(unitName);
  }
  
}
