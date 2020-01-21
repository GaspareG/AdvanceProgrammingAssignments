package framework;

import java.util.*;
import java.util.stream.Stream;

import utility.Pair;

/*
  Exam: Advance Programming
  Year: 2019/2020
  Author: Gaspare Ferraro
  Mail: ferraro@gaspa.re
  Mat: 520549
*/

/**
 * A Map-Reduce software framework
 *
 * @param <K1> Key
 * @param <V1> Value
 * @param <K2> Key of mapped stream
 * @param <V2> Value of mapped stream
 * @param <V3> Value of reduced values
 */
public abstract class MapReduce<K1, V1, K2, V2, V3> {

  /**
   * Empty constructor
   */
  public MapReduce() {

  }

  // The template method itself should not be overridden: it can be declared a public final method

  /**
   * Start the framework
   */
  public final void start() {

    // Read and create stream
    Stream<Pair<K1, V1>> input_stream = read();

    // Map each element in the stream into a new stream
    Stream<Pair<K2, V2>> mapped_stream = map(input_stream);

    // Group elements in stream according to the key
    Stream<Pair<K2, List<V2>>> grouped_stream = group(mapped_stream);

    // Reduce elements with the same key
    Stream<Pair<K2, V3>> reduced_stream = reduce(grouped_stream);

    // Write results
    write(reduced_stream);

    /*
      read()
      .map(map_function)
      .collect(
        Collectors.groupingBy(Pair::getKey,
          Collectors.reducing(identity, Pair::getValue, reduce_function)
        )
      )
      .entrySet().stream().forEach(write_function);
   */
  }


  // The concrete operations can be declared private ensuring that they are only called by the template method

  /**
   * Function that collect values of the input stream with the same keys in a list
   *
   * @param mapped_stream input stream
   * @return Stream of Pair elements
   */
  private Stream<Pair<K2, List<V2>>> group(Stream<Pair<K2, V2>> mapped_stream) {

    Map<K2, List<V2>> grouped = new TreeMap<>(this::compare);

    // For each element in stream
    mapped_stream.forEach(p -> {

      // If first element with key, create list
      if (!grouped.containsKey(p.getKey()))
        grouped.put(p.getKey(), new ArrayList<>());

      // Put value in the corresponding list
      grouped.get(p.getKey()).add(p.getValue());
      
    });

    // Create stream from Map object
    return grouped.entrySet().stream().map(x -> new Pair<>(x.getKey(), x.getValue()));
  }

  // Primitive operations that must be overridden are declared protected abstract

  /**
   * The read function must return a stream of pairs
   *
   * @return Stream of <key, value> pairs
   */
  protected abstract Stream<Pair<K1, V1>> read();

  /**
   * The map function must take as input the output of read and must return a stream of pairs
   *
   * @param s Stream from read function
   * @return A new stream transformed according to the map function
   */
  protected abstract Stream<Pair<K2, V2>> map(Stream<Pair<K1, V1>> s);

  /**
   * The compare function should compare strings according to the standard alphanumeric ordering
   *
   * @param k1 First key
   * @param k2 Second key
   * @return < 0 if k1 < k2, == 0 if k1 == k2, > 0 if k1 > k2
   */
  protected abstract int compare(K2 k1, K2 k2);

  /**
   * The reduce function takes as input a stream of pairs, returns a corresponding stream of pairs
   *
   * @param s Stream from group function
   * @return A new stream where each List<V2> is reduced to an element V3
   */
  protected abstract Stream<Pair<K2, V3>> reduce(Stream<Pair<K2, List<V2>>> s);

  /**
   * The write function takes as input the output of reduce
   *
   * @param s Stream from reduce function
   */
  protected abstract void write(Stream<Pair<K2, V3>> s);

}
