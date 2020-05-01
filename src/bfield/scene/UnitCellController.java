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

import bfield.Application;
import bfield.IconFont;
import bfield.Utilities;
import bfield.data.Unit;
import bfield.event.ArmyEvent;
import bfield.event.UnitChangeEvent;
import bfield.rules.UnitRules;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author ste
 */
public class UnitCellController {

  /**
   * Items Before Bunks (IBB), the icon and the unit column
   * this is how many columns must be ignored by layout when
   * handling bunks.
   */
  
  final static int IBB = 2;
 
  /**
   * How much the root panes should increase its width for each new bunk
   */
  final static int BUNK_COLUMN= 50;
  
  @FXML
  private Label lblName;
  @FXML
  private Region rPictureBackground;
  @FXML
  private Region rPictureIcon;
  
  private boolean selected;
  /**
   * Unit rules to mod original unit.
   */
  private UnitRules modEnvironment;
  
  /**
   * Army id, to reference unit. Army IDs are final
   */
  private String armyId;
  
  /**
   * Army color, to reference army color
   */
  private Color armyColor;
  
  @FXML
  private Label lDef;
  
  @FXML
  private Label lMel;
  @FXML
  private Label lMis;
  @FXML
  private Label lSpc;
  
  @FXML
  private ProgressBar pBar;
  @FXML
  private GridPane gpStatuses;
  
  private Unit sourceUnit;
  
  private java.util.Map<String,Boolean> showStatus;
  @FXML
  private GridPane root;
  
  @FXML
  private Label lBunks;
  
  private ContextMenu menu;
  
  private UnitContextMenuController ucmc;
  
  private java.util.List<UnitCellController> bunks;
  
  @FXML
  private Group gPicture;
  
  protected List<UnitCellController> getBunks() {
    if (bunks == null)
      bunks = new java.util.ArrayList();
    return bunks;
  }
  
  /**
   * Allows change in army color
   * @param armyColor if null, item will be transparent
   */
  public void setArmyColor(Color armyColor) {
    this.armyColor = armyColor;
  }

  public void setArmyId(String armyId) {
    this.armyId = armyId;
  }
  
  public Unit getUnit() {return sourceUnit;}
  
  public void initialize() {
   //context menu
   try {
     FXMLLoader loader = new FXMLLoader(this.getClass()
                                        .getResource("unitContextMenu.fxml"));
     loader.load();
     ucmc = loader.getController();
     menu = loader.<ContextMenu>getRoot();
     root.setOnContextMenuRequested( e-> {
      menu.setY(e.getScreenY()); 
      menu.setX(e.getScreenX()); 
      menu.show(Application.getApp().getStage());
     });

     //any standard change, just refresh
     menu.addEventHandler(UnitChangeEvent.UNIT_CHANGE, (eh) -> {
       refreshUnit();
     });
     
     //embed hero request
     menu.addEventHandler(UnitChangeEvent.UNIT_EMBED_HERO, (eh) ->{
      if (modEnvironment == null)
        return;
      
      modEnvironment.embedHero(getUnit());
      root.fireEvent(new UnitChangeEvent(getUnit()));
      refreshUnit();
     });
     
     //delete unit request, standard  (will be overrided if embarked)
     ucmc.setDeleteAction( (eh) ->{
        root.fireEvent(new UnitChangeEvent(getUnit(), root, 
                UnitChangeEvent.UNIT_REMOVE));
     });
     
     //disembark
     //set 'disembark' handler
    menu.addEventHandler(UnitChangeEvent.UNIT_DISEMBARK,
            (eh) ->{
              UnitCellController.this.getUnit().getCargo().remove(eh.getUnit());
              removeCargo(eh.getUnit());
              root.fireEvent(new ArmyEvent(null, ArmyEvent.ARMY_DISEMBARK_UNIT,
                      null, eh.getUnit()));
            });
   } catch (IOException e) {
     System.err.println("Failure in menu loading: " + e.getMessage());
     throw new RuntimeException(e);
   }
  }
  
  public void setStatusEnabled(String status, Boolean value) {
    if (showStatus == null)
      showStatus = new java.util.HashMap();
    
    showStatus.put(status, value);
  }
  
  public void setModEnvironment(UnitRules modEnvironment) {
    this.modEnvironment = modEnvironment;
  }

  public void setUnit(String armyID, Unit u, UnitRules environment) {
    setArmyId(armyID);
    sourceUnit = u;
    modEnvironment = environment;
    ucmc.setUnit(u);
    
    if (u.getClassName().contains("Ship")) {
      root.getChildren().remove(pBar);
      root.add(pBar, 0, 1, 1, 1);
      lBunks.setVisible(true);
    }
    
    refreshUnit();
  }
  
  protected ContextMenu getContextMenu() {return menu;}
  protected UnitContextMenuController getContextMenuController() {return ucmc;}
  
  public void setMenuEnabled(boolean enable) {
    if (!enable) {
      root.setOnContextMenuRequested(null);
    } else {
      root.setOnContextMenuRequested( e-> {
        menu.setY(e.getScreenY()); 
        menu.setX(e.getScreenX()); 
        menu.show(Application.getApp().getStage());
       });
    }
  }
  
  /**
   * Utility to recalculate column costraints %es.
   * This is used when ships add bunks.
   * @param columns each column after 2 standard columns
   * So if you want an extra column it should be 1
   */
  private void recalcCostraintsTo(int additionalColumns) {
    
    ObservableList<ColumnConstraints> list =
            root.getColumnConstraints();
    
    double wIcon, wLabel;
    switch (additionalColumns) {
      case 0: wIcon = 75; wLabel = 25; break;
      case 1: wIcon = 60; wLabel = 20; break;
      case 2: wIcon = 55; wLabel = 15; break;
      case 3: wIcon = 50; wLabel = 12.5; break;
      case 4: wIcon = 30; wLabel = 15; break;
      default:
        wLabel = ((additionalColumns+1)*10);
        wIcon = Math.min(10,100-wLabel);
        break;
    }
    
    list.get(0).setPercentWidth(wIcon);
    list.get(1).setPercentWidth(wLabel);
    
    if (list.size()-IBB < additionalColumns) {
      while (list.size()-IBB < additionalColumns) {
        ColumnConstraints cc = new ColumnConstraints();
        cc.setFillWidth(true);
        cc.setPercentWidth(wLabel);
        cc.setHalignment(HPos.CENTER);
        
        list.add(cc);
      }
    } else if (list.size()-IBB > additionalColumns) {
      list.remove(additionalColumns+IBB, list.size());  
      for (int i = IBB; i < list.size(); i++)
        list.get(i).setPercentWidth(wLabel);
    }
  }
  
  protected Group getPicture() {return gPicture;}
  
  /**
   * EmbarkMode will alter some menu functionalities such as
   * EventType called when removed, and add an extra layer to show
   * embarking 
   * @param newUnit
   */
  public void embark(Unit newUnit) {
    UnitCellController cc;
    
    FXMLLoader loader = new FXMLLoader(UnitCellController.class.
            getResource("unitcell.fxml"));
    try {
      loader.load();
    } catch (IOException e) { throw new RuntimeException(e); }
    
    cc= loader.getController();
    
    cc.setArmyColor(armyColor);
    cc.setUnit(armyId, newUnit, modEnvironment);
    cc.setMenuEnabled(false);
    GridPane ccRoot = cc.getRootPane();
    
    //TODO: This is REALLY ugly, must be an efficient/nice way to do this
    Node[] nodeArray = new Node[6];
    for (Node n : ccRoot.getChildren()) {
      if (n.getId() != null) {
        switch (n.getId()) {
          case "pBar" : 
            nodeArray[1] = n; 
            ((ProgressBar)n).setPrefWidth(25.0); 
          break;
          case "lDef" : nodeArray[2] = n; break;
          case "lMel" : nodeArray[3] = n; break;
          case "lMis" : nodeArray[4] = n; break;
          case "lSpc" : nodeArray[5] = n; break;
          default: break;
        }
      }
    }
    
    nodeArray[0] = cc.getPicture();

    for (int i = 2; i < nodeArray.length; i++) {
      if (nodeArray[i] != null)
        root.add(nodeArray[i], getBunks().size()+IBB, i,1,1);
    }
    
    //Adjusting picture size
    Group g = (Group) nodeArray[0];
    Region back = (Region) g.getChildren().filtered( e ->{
      return "rPictureBackground".equals(e.getId());}).get(0);
    
    Region front = (Region) g.getChildren().filtered( e ->{
      return "rPictureIcon".equals(e.getId());}).get(0);
    
    back.getStyleClass().clear();
    back.getStyleClass().add("picture-sm-back");
    front.getStyleClass().clear();
    front.getStyleClass().add("picture-sm-front");
    
    VBox box = new VBox(1.0, nodeArray[1], nodeArray[0]);
    root.add(box, getBunks().size()+IBB, 0, 1, 2);
    
    //fix cell size
    getBunks().add(cc);
    root.setPrefWidth(root.getPrefWidth() + BUNK_COLUMN);
    recalcCostraintsTo(getBunks().size());
    
    //grab context menu
    ucmc.addBunkMenu(newUnit, cc.getContextMenu());
    
    //rewrite 'delete' action
    cc.getContextMenuController().setDeleteAction( 
          (eh) -> {
              UnitContextMenuController ccmc = cc.getContextMenuController();
              UnitCellController.this.getUnit().getCargo().remove(
                                                              ccmc.getUnit());
              removeCargo(ccmc.getUnit());
              root.fireEvent(new UnitChangeEvent(UnitCellController.
                      this.sourceUnit));
          });
  }
  
  private void removeCargo(Unit u) {
    int index = -1;
    for (int i = 0;i < getBunks().size(); i++) {
      if (getBunks().get(i).getUnit() == u) {
        index = i;
        break;
      }
    }
    if (index == -1) {
      System.err.println("could not find unit to remove");
      return;
    }
    
    final int reference = index;
    //remove in a cryptic java8 way any node in <reference> column
    List<Node> list = new java.util.ArrayList(
            root.getChildren().filtered(node -> {
      return GridPane.getColumnIndex(node)-IBB == reference;
    }));
    for (Node n : list) {
      root.getChildren().remove(n);
    }
    
    //change  any node which column is > reference to column -1 
    root.getChildren().filtered(node -> {
      return GridPane.getColumnIndex(node)-IBB > reference;
    }).forEach(node -> {GridPane.setColumnIndex(node, 
            GridPane.getColumnIndex(node)-1); });
    
    this.recalcCostraintsTo(getBunks().size()-1);
    root.setPrefWidth(root.getPrefWidth()-BUNK_COLUMN);
    getBunks().remove(index);
  }
  
  public void setSelected(boolean selected) {
    if (!this.selected && selected &&
            !root.getStyleClass().contains("cell-selected"))
      root.getStyleClass().add("cell-selected");
    else
      root.getStyleClass().remove("cell-selected");
    
    this.selected=selected;
  }
  
  public boolean isSelected() {return selected;} 
  
  public void refreshUnit() {
    Unit unit = (modEnvironment != null) 
            ? (modEnvironment.mod(sourceUnit, armyId))
            : sourceUnit;

      //load and set unit symbol
    String svgFile = (unit.getIcon() != null ? unit.getIcon()
              : Application.getApp().getAppropriateIcon(unit.getClassName()));
    try {
      String svg = Utilities.loadSVGFile(svgFile);
      rPictureIcon.setStyle("-fx-shape: '"+svg+"'");
    } catch (Exception e) {
        System.out.println("::setUnit: Could not load " + svgFile);  
    }

    /*
    rPictureIcon.getStyleClass().clear();
    rPictureIcon.getStyleClass().add("picture-front");
    */
  
    //Background color
    if (armyColor == null) {
      rPictureBackground.setVisible(false);
      rPictureIcon.setStyle("-fx-background-color: black");
    } else if (unit.isDead())  {
      rPictureBackground.setVisible(true);
      rPictureBackground.setStyle("-fx-background-color: darkgray");
    } else {
      rPictureBackground.setVisible(true);
      rPictureBackground.setStyle("-fx-background-color: " +
              armyColor.toString().replaceAll("0x","#"));
    }
 
    lblName.setFont(Application.getApp().getStatusFont());
    
    if (unit.getOrdinal() != null) {
    lblName.setText(
            bfield.tools.RomanNumerals.toRoman(unit.getOrdinal()) 
        + " " + sanitizeName(unit.getName()));
    } else {
      lblName.setText(sanitizeName(unit.getName()));
    }
    
    style(sourceUnit.getDef(), unit.getDef(), lDef, "b-def", false);
    style(sourceUnit.getMelee(), unit.getMelee(), lMel, "b-melee", false);
    style(sourceUnit.getMissile(), unit.getMissile(), lMis, "b-missile", false);
    style(sourceUnit.getCharge(), unit.getCharge(), lSpc, "b-special",false);
    
    //health bar
    if (!unit.isDead()) {
      pBar.setVisible(true);
      pBar.setProgress((double)unit.getCurrentHits()/(double)unit.getHits());
    } else {
      pBar.setVisible(false);
    }
    
    if (showStatus == null)
      showStatus = new java.util.HashMap();
    
    //statuses
    gpStatuses.getChildren().clear();
    if (showStatus.getOrDefault("visibility", true) && unit.getIgnoreVisibility())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_VISIBILITY,"bonus"));
    if (showStatus.getOrDefault("terrain", true) && unit.getIgnoreTerrain())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_TERRAIN,"bonus"));
    if (showStatus.getOrDefault("mountain", true) && unit.getIgnoreMountain())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_MOUNTAIN,"bonus"));
    if (showStatus.getOrDefault("weather", false) && unit.getIgnoreWeather())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_WEATHER,"bonus"));
    
    
    if (showStatus.getOrDefault("visibility", true) && !unit.getIgnoreVisibility() &&
            modEnvironment.getContext().getVisibility().isBad())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_VISIBILITY,"malus"));
    if ( showStatus.getOrDefault("weather", true) && !unit.getIgnoreWeather() &&
            modEnvironment.getContext().getWeather().isBad())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_WEATHER,"malus"));
    if (showStatus.getOrDefault("terrain", true) && !unit.getIgnoreTerrain() &&
            modEnvironment.getContext().getTerrain().isBad())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_TERRAIN,"malus"));
    if (showStatus.getOrDefault("mountain", true) && !unit.getIgnoreMountain()&&
            modEnvironment.getContext().getTerrain().isBad())
      enqueueStatusDisplay(createStatusLabel(IconFont.I_MOUNTAIN,"malus"));
    
    //Ships, cargo count
    if(sourceUnit.getClassName().contains("Ship")) {
      if (unit.getBunks() <= 0)
        style(Unit.NA, Unit.NA,  lSpc, "b-cargo",false);
      else
        lBunks.setText(String.format("%d/%d", unit.getCargo().stream()
        .filter(cun -> {return !cun.isDead();} ).count(), unit.getBunks()));
      
      //Check if cargo synchs
      /*
      if (getBunks().size() < sourceUnit.getCargo().size()) {
        for (int i = (sourceUnit.getCargo().size() - getBunks().size())-1; i
                < sourceUnit.getCargo().size(); i++) {
          embark(sourceUnit.getCargo().get(i));
        }
      }*/
      
     
      boolean found;
      //updates && adds any missing unit
      for (Unit u : sourceUnit.getCargo()) {
        found = false;
        for (UnitCellController ucc : getBunks()) {
          if (ucc.getUnit() == u) {
            ucc.refreshUnit();
            found = true;
            break;
          }
        }
        if (!found)
          embark(u);
      }
    }
  }
 
  /**
  * Utility to create a status label
  * @param shape name of the icon
  * @param type bonus or malus
  * @return a label
  */ 
  private Label createStatusLabel(String shape, String type) {
    Label label = new Label();
    //label.setFont(Application.getApp().getStatusFont());
    label.setText(shape);
    label.getStyleClass().add("status-label");
    label.getStyleClass().add(type);
    return label;
  }
  
   
  /**
   * Adds a status to the grid
   * @param newStatus 
   */
  private void enqueueStatusDisplay(Node newStatus) {
    int nextR, nextC;
    if (!gpStatuses.getChildren().isEmpty()) {
      //int nextSlot = (int)(Math.ceil((double)gpStatuses.getChildren().size()/12.0));
      nextR = gpStatuses.getChildren().size() / 4;
      nextC = gpStatuses.getChildren().size() - (nextR*4);
    } else {
      nextR = 0;
      nextC = 0;
    }
    
    gpStatuses.add(newStatus, nextC, nextR);
  }
  
  /**
   * Utility to style text. If an attribute is not set, "--" will be print
   * and background class will be dark grey.
   * @param source original value
   * @param value current value
   * @param text attribute label
   * @param okClass which background class represents a valid value
   * @param bSign should the sign "+" be shown?
   */
  private void style (int source, int value, Label text, String okClass, boolean bSign) {
    text.getStyleClass().remove(okClass);
    text.getStyleClass().remove("b-disabled");
    text.getStyleClass().remove("t-worse");
    text.getStyleClass().remove("t-better");
    
    if (value == Unit.NA) {
      text.getStyleClass().add("b-disabled");
      text.setText("-");
    } else {
      text.getStyleClass().add(okClass);
      
      if (bSign)
        text.setText(new DecimalFormat("+#,##0;-#").format(value));
      else
        text.setText(String.valueOf(value));
      
      if (value < source) {
       text.getStyleClass().add("t-worse");
      } else if (value > source) 
       text.getStyleClass().add("t-better");
    }
    
    text.setTooltip(new Tooltip(String.valueOf(value)));
  }
  
  /**
   * Utility to abbreviate long names.
   * @param unitName
   * @return 
   */
  private String sanitizeName(String unitName) {
    if (unitName.length() > 20) {
      int iSpace = unitName.indexOf(' ');
      if (iSpace == -1)
        return unitName.substring(0, 20)+".";
      
      return unitName.charAt(0) + ". " + unitName.substring(iSpace+1);
    }
    
    return unitName;
  }
  
  public GridPane getRootPane() {return root;}
}
