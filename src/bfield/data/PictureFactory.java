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

/**
 *
 * @author root
 *
*/
import java.util.List;
import javax.xml.bind.annotation.*;

@XmlRootElement(name="data")
public class PictureFactory {
  
  @XmlElement(name="picture")
  java.util.List<Picture> pictures;

  public List<Picture> getPictures() {
    if (pictures == null)
      pictures = new java.util.ArrayList();
    
    return pictures;
  }
  
  public Picture findPicture(String name) {
    for (Picture p : getPictures())
      if (name.equals(p.getClassName()))
        return p;
    return null;
  }
}
