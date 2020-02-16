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
package bfield.data.adapters;

/**
 *
 * @author ste
 */

import bfield.Application;
import bfield.data.Rules;
import bfield.data.XMLFactory;
import java.util.Map.Entry;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RulesReferenceAdapter extends XmlAdapter<String,Rules>{

  @Override
  public Rules unmarshal(String v) throws Exception {
    XMLFactory data = Application.getApp().getFactories().get(v);
    
    if (data == null) {
      System.err.println("RulesTypeAdapter.Unmarshal: could not find ruleset " + v);
      return null;
    }
    
    return data.getRules();
  }

  @Override
  public String marshal(Rules v) throws Exception {
    for (Entry<String,XMLFactory> e : Application.getApp().getFactories().entrySet()) {
      if (e.getValue().getRules() == v) {
        return e.getKey();
      }
    }
    System.err.println("RulesTypeAdapter.Marshal: could not find ruleset");
      return null;
  }

}
