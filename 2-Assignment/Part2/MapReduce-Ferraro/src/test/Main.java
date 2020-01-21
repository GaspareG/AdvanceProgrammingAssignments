package test;

import instance.CountingWords;
import instance.InvertedIndex;

/*
  Exam: Advance Programming
  Year: 2019/2020
  Author: Gaspare Ferraro
  Mail: ferraro@gaspa.re
  Mat: 520549
*/

public class Main {

  public static void main(String[] args) {

    String path = "../Books";

    // Test Exercise 4 - Counting Words
    CountingWords cw = new CountingWords(path, "output_counting.csv");
    cw.start();

    // Test Exercise 5 - Inverted Index
    InvertedIndex ii = new InvertedIndex(path, "output_inverted.csv");
    ii.start();

  }
}
