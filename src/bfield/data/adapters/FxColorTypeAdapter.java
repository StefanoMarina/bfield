/*
 * Copyright (C) 2019 Stefano
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
 * @author Stefano
 */
import javafx.scene.paint.Color;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class FxColorTypeAdapter extends XmlAdapter<String, Color> {

  @Override
  public String marshal(Color v) throws Exception {
    
    return String.format("#%02X%02X%02X%02X",
        (int)Math.floor(v.getRed()*255.0), 
        (int)Math.floor(v.getGreen()*255.0), 
        (int)Math.floor(v.getBlue()*255.0),
        (int)Math.floor(v.getOpacity()*255.0)
        );
  }

  @Override
  public Color unmarshal(String v) throws Exception {
    return Color.web(v);
  }
  
}
