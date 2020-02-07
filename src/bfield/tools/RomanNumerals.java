/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bfield.tools;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Chepe Lucho
 */
public class RomanNumerals {

  private static LinkedHashMap<String, Integer> roman_numerals;

  private static void createTable() {
    roman_numerals = new LinkedHashMap();
    roman_numerals.put("M", 1000);
    roman_numerals.put("CM", 900);
    roman_numerals.put("D", 500);
    roman_numerals.put("CD", 400);
    roman_numerals.put("C", 100);
    roman_numerals.put("XC", 90);
    roman_numerals.put("L", 50);
    roman_numerals.put("XL", 40);
    roman_numerals.put("X", 10);
    roman_numerals.put("IX", 9);
    roman_numerals.put("V", 5);
    roman_numerals.put("IV", 4);
    roman_numerals.put("I", 1);
  }

  public static String toRoman(int Int) {
    if (roman_numerals == null) {
      createTable();
    }

    String res = "";

    for (Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
      int matches = Int / entry.getValue();
      res += repeat(entry.getKey(), matches);
      Int = Int % entry.getValue();
    }
    return res;
  }

  private static String repeat(String s, int n) {
    if (s == null) {
      return null;
    }
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n; i++) {
      sb.append(s);
    }
    return sb.toString();
  }

  public static int toArabic(String num) {
    if (roman_numerals == null) {
      createTable();
    }

    int intNum = 0;
    int prev = 0;
    for (int i = num.length() - 1; i >= 0; i--) {
      int temp = roman_numerals.get(num.substring(i, i + 1));
      if (temp < prev) {
        intNum -= temp;
      } else {
        intNum += temp;
      }
      prev = temp;
    }
    return intNum;
  }
}
