package instance;

import framework.MapReduce;
import utility.Pair;
import utility.Reader;
import utility.Writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

/*
  Exam: Advance Programming
  Year: 2019/2020
  Author: Gaspare Ferraro
  Mail: ferraro@gaspa.re
  Mat: 520549
*/

/**
 * a program that generates an Inverted Index (for words of length greater than 3). T
 * Given as input the absolute path of a directory, the program prints in a CSV file for each word w
 * (of length greater than 3) appearing in the .txt documents of the directory, a line w, filename, line
 * if w appears in line number line of file filename
 */
public class InvertedIndex extends MapReduce<String, List<Pair<String, Integer>>, String, Pair<String, Integer>, Pair<String, Integer>> {

  private final Path input_path;
  private final File output_file;

  public InvertedIndex(String input_path, String output_file) {
    this.input_path = Paths.get(input_path);
    this.output_file = new File(output_file);
  }

  // Pair<FileName, List<Pair<Line, LineNumber>>>
  @Override
  protected Stream<Pair<String, List<Pair<String, Integer>>>> read() {
    try {
      // Read all the files in the input path
      return new Reader(this.input_path).read().map(f -> {
        List<Pair<String, Integer>> ln = new ArrayList<>();
        AtomicInteger idx = new AtomicInteger();
        // Create <line, line_number>
        f.getValue().forEach(l -> {
          ln.add(new Pair<>(l, idx.getAndIncrement())); // Stateful
        });
        // Create <filename, <line, line_number>
        return new Pair<>(f.getKey(), ln);
      });
    } catch (IOException e) {
      return Stream.empty();
    }
  }

  @Override
  protected Stream<Pair<String, Pair<String, Integer>>> map(Stream<Pair<String, List<Pair<String, Integer>>>> s) {

    return s.map(x -> x.getValue()
        .stream()
        .map(y -> Arrays
            // Remove all non alphabetic chars, then split by space
            .stream(y.getKey().toLowerCase().replaceAll("[^a-z]", " ").split(" "))
            // Each word of length > 3
            .filter(w -> w.length() > 3)
            // Create <word, <file_name, line_number>>
            .map(w ->
                new Pair<>(w, new Pair<>(x.getKey(), y.getValue()))
            )
        )
        .flatMap(Function.identity())
    ).flatMap(Function.identity());

  }

  @Override
  protected int compare(String k1, String k2) {
    return k1.compareTo(k2);
  }

  @Override
  protected Stream<Pair<String, Pair<String, Integer>>> reduce(Stream<Pair<String, List<Pair<String, Integer>>>> s) {
    return s.map(x -> x
        .getValue()
        .stream()
        .map(y -> new Pair<>(x.getKey(), new Pair<>(y.getKey(), y.getValue())))
    ).flatMap(Function.identity());
  }

  @Override
  protected void write(Stream<Pair<String, Pair<String, Integer>>> s) {
    try {
      Writer.writeInvertedIndex(this.output_file, s);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

}
