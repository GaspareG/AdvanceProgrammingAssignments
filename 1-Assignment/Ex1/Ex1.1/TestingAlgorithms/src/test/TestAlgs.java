package test;

import registry.KeyRegistry;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*
  Exam: Advance Programming
  Year: 2019/2020
  Author: Gaspare Ferraro
  Mail: ferraro@gaspa.re
  Mat: 520549
*/

public class TestAlgs {

  private static final Character PADDING_CHAR = '#';
  private static final String PACKAGE_ALGOS = "crypto.algos.";
  private static final String PATH_SECRET_LIST = "crypto/secret.list";
  private static final String PATH_KEY_LIST = "crypto/keys.list";
  private static final String PATH_CRYPTO_ALGOS = "crypto/algos";

  private static final String PREFIX_ENC = "enc";
  private static final String PREFIX_DEC = "dec";
  private static final String SUFFIX_CLASS = ".class";

  public static void main(String[] args) throws Exception {

    String PATH_BASE;
    // PATH_BASE = "/home/gaspare/git/AdvanceProgrammingAssignments/1-Assignment/Ex1/";

    if (args.length == 0)
      throw new Exception("Please specify a parent directory");
    else
      PATH_BASE = args[0];

    if (!PATH_BASE.endsWith("/"))
      PATH_BASE = PATH_BASE.concat("/");

    ClassLoader classLoader = getClassLoader(PATH_BASE);
    KeyRegistry registry = loadRegistry(classLoader, PATH_BASE + PATH_KEY_LIST);
    checkClasses(classLoader, registry, PACKAGE_ALGOS, PATH_BASE + PATH_CRYPTO_ALGOS, PATH_BASE + PATH_SECRET_LIST);
  }

  /**
   * @param classLoader ClassLoader to use to retrieve the class
   * @param registry    A filled registry to use for the keys
   * @param packageName Prefix of the package name for the class
   * @param folder      Path of the folder where .class files are located
   * @param secret      Path of the file containing the strings to test
   */
  private static void checkClasses(ClassLoader classLoader, KeyRegistry registry, String packageName, String folder, String secret) {
    try {
      List<String> secretLines = getFileLines(secret);
      // For each file in the folder
      Files.walk(Paths.get(folder))
          // Filter only regular files
          .filter(Files::isRegularFile)
          // And filter only .class files
          .filter(path -> path.toString().endsWith(SUFFIX_CLASS))
          // For each .class file check it
          .forEach(path -> TestAlgs.checkClass(path, classLoader, registry, packageName, secretLines));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param path        Base path where the .class file is located
   * @param classLoader ClassLoader to use to retrieve the class
   * @param registry    A filled registry to use for the keys
   * @param packageName Prefix of the package name for the class
   * @param secret      List of string to test for encryption and decryption
   */
  private static void checkClass(Path path, ClassLoader classLoader, KeyRegistry registry, String packageName, List<String> secret) {

    try {

      // Load specified class
      String fileName = path.getFileName().toString().replace(SUFFIX_CLASS, "");
      String classPackage = packageName.concat(fileName);
      Class loaded = classLoader.loadClass(classPackage);

      System.out.println(fileName);

      // If it is an encryption algorithm test it
      if (isEncryptionAlgorithm(loaded))
        testClass(loaded, registry.get(loaded), secret);
      else
        System.out.println("Enc/Dec methods not found");

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

  }

  /**
   * Test a class using a list of strings.
   * An encryption algorithm is correct if given a word w we have that:
   * decryption(encryption(w)) == w followed by one or more padding characters '#'
   *
   * @param loaded Class to test
   * @param key    Key to use for the constructor
   * @param secret List of string to test for encryption and decryption
   */
  private static void testClass(Class loaded, String key, List<String> secret) {
    try {

      // Get constructor and create a new class instance
      Constructor constructor = loaded.getConstructor(String.class);
      Object crypto = constructor.newInstance(key);

      // Get encryption and decryption methods
      Method encryption = getMethodsStartWith(loaded, PREFIX_ENC).get(0);
      Method decryption = getMethodsStartWith(loaded, PREFIX_DEC).get(0);

      for (String w : secret) {

        // Try to do decryption(encryption(w))
        String e = (String) encryption.invoke(crypto, w);
        String d = (String) decryption.invoke(crypto, e);

        // And check if the two strings are the same
        if (!checkUnpadded(w, d))
          System.out.println("KO: " + w + " -> " + e + " -> " + d);
      }
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param w Original word
   * @param d Decrypted word
   * @return true if the two strings are the same without padding, false otherwise
   */
  private static boolean checkUnpadded(String w, String d) {
    // w is a prefix of d
    if (!d.startsWith(w)) return false;
    // followed by only PADDING_CHAR characters
    for (int i = w.length(); i < d.length(); i++) {
      if (d.charAt(i) != PADDING_CHAR) return false;
    }
    return true;
  }

  /**
   * @param path Base path for the ClassLoader
   * @return A ClassLoader with the specified path
   */
  private static ClassLoader getClassLoader(String path) {
    File file = new File(path);
    ClassLoader classLoader = null;

    try {
      URL url = file.toURI().toURL();
      URL[] urls = new URL[]{url};
      classLoader = new URLClassLoader(urls);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    return classLoader;
  }

  /**
   * @param classLoader A ClassLoader to retrieve the classes
   * @param path        the path of file to read for the classes
   * @return a KeyRegistry containing all the pairs class/key found in the path
   */
  private static KeyRegistry loadRegistry(ClassLoader classLoader, String path) {
    KeyRegistry registry = new KeyRegistry();

    List<String> keyLines = getFileLines(path);

    for (String line : keyLines) {
      // Get pair <class, key> from  line
      String[] classKey = line.split(" ");

      try {
        String className = classKey[0];
        String key = classKey[1];

        // Try to load class and add to the KeyRegistry
        Class toAdd = classLoader.loadClass(className);
        registry.add(toAdd, key);
      } catch (ClassNotFoundException | IndexOutOfBoundsException e) {
        e.printStackTrace();
      }
    }
    return registry;
  }

  /**
   * Check if a class is an encryption algorithm
   * A class is an encryption algorithm if it has:
   * (1) a public constructor
   * (2) a method starting with enc
   * (3) a method starting with dec
   * all three with one String parameter.
   *
   * @param toVerify Class to verify
   * @return true is the specified class is an encryption class, false otherwise
   */
  private static boolean isEncryptionAlgorithm(Class toVerify) {
    // Public constructor
    boolean checkConstructor = hasStringConstructor(toVerify);

    // A method starting with enc
    boolean checkEncryption = getMethodsStartWith(toVerify, PREFIX_ENC).size() == 1;

    // A method starting with dec
    boolean checkDecryption = getMethodsStartWith(toVerify, PREFIX_DEC).size() == 1;

    return checkConstructor && checkEncryption && checkDecryption;
  }

  /**
   * @param toVerify Class to verify
   * @return true if the class as a constructor with a single string parameter, false otherwise
   */
  private static boolean hasStringConstructor(Class toVerify) {
    boolean check = true;
    try {
      toVerify.getConstructor(String.class);
    } catch (NoSuchMethodException e) {
      check = false;
    }
    return check;
  }

  /**
   * @param toVerify Class in which to look
   * @param prefix   Prefix of methods to search
   * @return List of methods that start with the specified prefix
   */
  private static List<Method> getMethodsStartWith(Class toVerify, String prefix) {
    // Stream with all the class methods
    return Arrays.stream(toVerify.getMethods())
        // Filter only methods with one parameter
        .filter(m -> m.getParameterCount() == 1)
        // Filter only methods with string parameter
        .filter(m -> m.getParameterTypes()[0] == String.class)
        // Filter only methods which name starts with prefix
        .filter(m -> m.getName().startsWith(prefix))
        // Collect stream to list
        .collect(Collectors.toList());
  }

  /**
   * @param path the path of the file to read
   * @return List of lines in the specified path
   */
  private static List<String> getFileLines(String path) {
    List<String> lines = new ArrayList<>();

    try {
      BufferedReader reader = new BufferedReader(new FileReader(path));
      String line = reader.readLine();
      while (line != null) {
        lines.add(line);
        line = reader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return lines;
  }

}
