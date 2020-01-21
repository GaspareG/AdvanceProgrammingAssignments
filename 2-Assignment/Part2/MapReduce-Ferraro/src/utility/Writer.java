package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.stream.Stream;

/*
  Exam: Advance Programming
  Year: 2019/2020
  Author: Gaspare Ferraro
  Mail: ferraro@gaspa.re
  Mat: 520549
*/

public class Writer {

  public static void write(File dst, Stream<Pair<String, Integer>> res) throws FileNotFoundException {
    PrintStream ps = new PrintStream(dst);
    res.sorted(Comparator.comparing(Pair::getKey))
        .forEach(p -> ps.println(p.getKey() + ", " + p.getValue()));
    ps.close();
  }

  public static void writeInvertedIndex(File dst, Stream<Pair<String, Pair<String, Integer>>> res) throws FileNotFoundException {
    PrintStream ps = new PrintStream(dst);
    res.sorted(Comparator.comparing(Pair::getKey))
        .forEach(p -> ps.println(p.getKey() + ", " + p.getValue().getKey() + ", " + p.getValue().getValue()));
    ps.close();
  }


}
