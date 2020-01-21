package framework;

import java.util.*;
import java.util.stream.Stream;

import utility.Pair;

public abstract class MapReduce<K1, V1, K2, V2, V3> {

  public MapReduce() {

  }

  // The template method itself should not be overridden: it can be declared a public final method

  public final void start() {

    // Read and create stream
    Stream<Pair<K1, V1>> input_stream = read();

    // Map each element in the stream into a new stream
    Stream<Pair<K2, V2>> mapped_stream = map(input_stream);

    // Group elements in stream according to the key
    Stream<Pair<K2, List<V2>>> groupped_stream = group(mapped_stream);

    // Reduce elements with the same key
    Stream<Pair<K2, V3>> reduced_stream = reduce(groupped_stream);

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

  private Stream<Pair<K2, List<V2>>> group(Stream<Pair<K2, V2>> mapped_stream) {

    Map<K2, List<V2>> groupped = new TreeMap<>((k1, k2) -> compare(k1, k2));

    // For each element in stream
    mapped_stream.forEach(p -> {
      // If first element with key, create list
      if (!groupped.containsKey(p.getKey()))
        groupped.put(p.getKey(), new ArrayList<>());

      // Put value in the corresponding list
      groupped.get(p.getKey()).add(p.getValue());
    });

    // Create stream from Map object
    return groupped.entrySet().stream().map(x -> new Pair<>(x.getKey(), x.getValue()));
  }

  //Primitive operations that must be overridden are declared protected abstract

  // The read function must return a stream of pairs
  protected abstract Stream<Pair<K1, V1>> read();

  // The map function must take as input the output of read and must return a stream of pairs
  protected abstract Stream<Pair<K2, V2>> map(Stream<Pair<K1, V1>> s);

  // The compare function should compare strings according to the standard alphanumeric ordering
  protected abstract int compare(K2 k1, K2 k2);

  // The reduce function takes as input a stream of pairs, returns a corresponding stream of pairs
  protected abstract Stream<Pair<K2, V3>> reduce(Stream<Pair<K2, List<V2>>> s);

  // The write function takes as input the output of reduce
  protected abstract void write(Stream<Pair<K2, V3>> s);

}
