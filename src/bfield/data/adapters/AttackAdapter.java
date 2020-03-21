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


import bfield.data.Unit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Attackadapter is used to parse adnd:X properties
 * so att="2,2,1" will become an int [2,2,1] array.
 * you have to put NA multiple times to correctly parse
 * the stat, so [2,NA,NA] for 3 hits.
 * @author ste
 */
public class AttackAdapter extends XmlAdapter<String,Integer[]>{

  @Override
  public Integer[] unmarshal(String v) throws Exception {
    java.util.List<String> data = (Arrays.<String>asList(v.split(",")));
    Collections.reverse(data);
    
    java.util.ArrayList<Integer> iList = new java.util.ArrayList();
    for (String d : data)
      if ("NA".equals(d)) {
        iList.add(Unit.NA);
        //break;
      } else {
        try {
          iList.add(Integer.parseInt(d));
        } catch(NumberFormatException | NullPointerException e) {
          iList.add(Unit.NA);
          System.err.println("AttackAdapter: could not recognize value " + d);
          break;
        }
    }
    
      
    return iList.toArray(new Integer[iList.size()]);
  }

  @Override
  public String marshal(Integer[] v) throws Exception {
    if (v == null) {
     return null;
    }
    
    StringBuilder str = new StringBuilder();
    java.util.List<Integer> li = Arrays.<Integer>asList(v);
    Collections.reverse(li);
/**
 * 1.0.2
 * you have to add NA multiple times to force disabling unit skill
 * before hits reach 0
 */    
    java.util.Iterator<Integer> it = li.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
    //for (Integer i : li) {
      if (i == null || i.equals(Unit.NA)) {
        str.append("NA");
      } else {
        str.append(i);
      }
      if (it.hasNext()){
          str.append(",");
      }
    }
    return str.toString();
  }
  
}
