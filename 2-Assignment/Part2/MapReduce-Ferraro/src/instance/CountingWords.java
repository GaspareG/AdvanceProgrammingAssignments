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
 * A program that counts the occurrences of words of length greater than 3 in a given set of documents
 */
public class CountingWords extends MapReduce<String, List<String>, String, Integer, Integer> {

  private final Path input_path;
  private final File output_file;

  /**
   * @param input_path  Input files Directory
   * @param output_file Output CSV filename
   */
  public CountingWords(String input_path, String output_file) {
    this.input_path = Paths.get(input_path);
    this.output_file = new File(output_file);
  }

  @Override
  protected Stream<Pair<String, List<String>>> read() {
    try {
      // Read all the files in the input path
      return new Reader(this.input_path).read();
    } catch (IOException e) {
      return Stream.empty();
    }
  }

  @Override
  protected Stream<Pair<String, Integer>> map(Stream<Pair<String, List<String>>> s) {
    return s.map(x -> x.getValue()
        .stream()
        .map(y -> {
          // Remove all non alphabetic chars, then split by space
          String[] words = y.toLowerCase().replaceAll("[^a-z]", " ").split(" ");

          // Map Char -> #Char in the line
          Map<String, Pair<String, Integer>> count = new HashMap<>();

          // Stream from words
          Arrays.stream(words)
              // Each word of length > 3
              .filter(w -> w.length() > 3)
              // Add word to the counting map
              .forEach(w -> count.put(w, new Pair<>(w, count.getOrDefault(w, new Pair<>(w, 0)).getValue() + 1)));

          return count.values();
        })
        .flatMap(Collection::stream)
    ).flatMap(Function.identity());
  }

  @Override
  protected int compare(String k1, String k2) {
    return k1.compareTo(k2);
  }

  @Override
  protected Stream<Pair<String, Integer>> reduce(Stream<Pair<String, List<Integer>>> s) {
    // Reduce the list by summing the elements
    return s.map(x ->
        new Pair<>(x.getKey(),
            x.getValue().stream().reduce(0, Integer::sum)
        )
    );
  }

  @Override
  protected void write(Stream<Pair<String, Integer>> s) {
    try {
      // Write the stream to the output file
      Writer.write(this.output_file, s);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
