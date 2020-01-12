package framework;

import java.util.stream.Stream;

public abstract class MapReduce<T1, T2> {

  // The template method itself should not be overridden: it can be declared a public final method

  public MapReduce() {

  }

  // The concrete operations can be declared private ensuring that they are only called by the template method

  // Primitive operations that must be overridden are declared protected abstract

  protected abstract Stream<Pair<T1, T2>> read();

  protected abstract Stream<Pair<T1, T2>> map(Stream<Pair<T1, T2>>);

  protected abstract boolean compare(Pair<T1, T2> p1, Pair<T1, T2> p2);

  protected abstract Stream<Pair<T1, T2>> reduce(Stream<Pair<T1, T2>>);

  protected abstract void write(Stream<Pair<T1, T2>>);

  // The hook operations that may be overridden are declared protected

}
