# Battlefield!
Battlefield! an army manager for pen &amp; paper rpg mass battles

Battlefield! (with an exclamation mark) is based upon [Birthright.net's d20 SRD](www.birthright.net), a free version of AD&D(tm)/D&D(tm) Birthright (tm) Campaign setting.
This will allow the DM to handle easily all the troops on the field, and speed up the battle process.

## Features
- 2 armies battle handled as a list of war cards
- Condition modifiers support (weather, visibility, terrain, fortification, etc)
- Multiple auto-attack resolution methods
- TOTAL CUSTOMIZATION: you can edit your units (it's all in xml format), or improve the project by adding java classes to handle your mechanics
- Partial AD&D(tm) 2ed war card support
- Save battles & export/import individual armies

Already included:
- d20 SRD BR campaign army list
- [MarsupialMancer's Birthright 5E](https://marsupialmancer.blogspot.com/2019/08/birthright-5e-revised.html) rules for mass battles

## Requirements
Java 8 is required, or any java with JAXB and JavaFX support.

## Running the app
1. Ensure you have JAVA8 and know how to use it
2. Download zip file from *repo* folder
3. Extract everyhthing 
4. Launch the jar file

## Modding the app (users)
1. Create a new ruleset (check *clone* if you want it based upon an existing ruleset)
2. Edit units, changing their name if you want them to be new units

Sorry, conditions and similar still requires XML editing.

## Modding the app (developers)
On the bfield.rules packages there are 3 main classes:
* UnitRules, which job is to create a new unit based upon default values plus any conditions attached (weather, etc)
* ArmyRules, which job is to apply army status and retrieve default army modifiers (attack, special attack, defense)
* BattleRules, which returns a Map<String,String> of results by clashing the two armies.

Subpackages such as srd shows implementation of such classes. If you want to add your mechanics, implement, branch and add them.

## TO DO
- DOCUMENTATION, DOCUMENTATION, DOCUMENTATION
- Full ruleset edit

## ThankYou
- The Birthright.net community
- Artists Lorc, Delaouite, Skoll, Keir Heyl for their icons under CC-BY-3.

