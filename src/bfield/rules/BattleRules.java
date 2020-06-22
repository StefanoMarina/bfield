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

import java.util.Map;
import java.util.Properties;

/**
 * BattleMechanics
 * BattleMechanics do the actual battle encounter. They are designed to be
 * full massive formula, not individual combat.
 * @author ste
 */
public interface BattleRules {
 
  /**
   * Perform the actual encounter between all armies.
   * The resulting Map<String,String> should be mapped like this: Keys should be
   * the army ID, while properties should be an HTML-formatted string. The BattleResultFormatter
   * will automatically add a column for each key, adding a <p> tag if only one key is found.
   * @param rules valid Rules object
   * @param battle valid Battle object
   * @return a Map with the result of the battle. the rules for filling this map are
   * explained in bfield.scene.BattleResultFormatter.
   * @see bfield.scene.BattleResultFormatter
   */
  public Map<String,String> doBattle(bfield.data.Rules rules, bfield.data.Battle battle);
  
  @Override
  public abstract String toString();
}
