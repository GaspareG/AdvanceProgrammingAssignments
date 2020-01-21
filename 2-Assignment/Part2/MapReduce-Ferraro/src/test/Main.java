package test;

import instance.CountingWords;
import instance.InvertedIndex;

import java.util.Arrays;

public class Main {

  public static void main(String[] args) {

    String path = "../Books";

    CountingWords cw = new CountingWords(path, "output_counting.csv");
    cw.start();

    InvertedIndex ii = new InvertedIndex(path, "output_inverted.csv");
    ii.start();

  }
}
