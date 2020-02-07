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
package bfield.data.adapters;

import bfield.data.Rules;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import bfield.rules.BattleRules;
/**
 *
 * @author ste
 */
public class BattleMechanicsAdapter extends XmlAdapter<String, BattleRules> {

  @Override
  public BattleRules unmarshal(String v) throws Exception {
    return (BattleRules) Class.forName(v).newInstance();
  }

  @Override
  public String marshal(BattleRules v) throws Exception {
    return v.getClass().getSimpleName();
  } 
}
