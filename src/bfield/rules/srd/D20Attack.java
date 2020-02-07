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
package bfield.rules.srd;

import bfield.data.Rules;

/**
 *
 * @author ste
 */
public class D20Attack extends FixedAttack {

  @Override
  protected Integer getFixedNumber(Rules r) {
    return bfield.RulesUtilities.roll(20, 0);
  }

  @Override
  public String toString() {
    return "1d20 + attack";
  }
}
