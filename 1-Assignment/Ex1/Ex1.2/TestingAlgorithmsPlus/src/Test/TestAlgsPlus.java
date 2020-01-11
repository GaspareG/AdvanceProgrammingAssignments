package Test;

import Registry.KeyRegistry;

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

public class TestAlgsPlus {

  private static final Character PADDING_CHAR = '#';
  private static final String PACKAGE_ALGOS = "crypto.algos.";
  private static final String PACKAGE_ANNOTATION = "crypto.annot.";
  private static final String PATH_SECRET_LIST = "crypto/secret.list";
  private static final String PATH_KEY_LIST = "crypto/keys.list";
  private static final String PATH_CRYPTO_ALGOS = "crypto/algos";
  private static String PATH_BASE;

  public static void main(String[] args) throws Exception {

    if (args.length == 0) {
      PATH_BASE = "/home/gaspare/git/AdvanceProgrammingAssignments/1-Assignment/Ex1/";
      // throw new Exception("Please specify a parent directory");
    } else
      PATH_BASE = args[0];

    if (!PATH_BASE.endsWith("/"))
      PATH_BASE = PATH_BASE.concat("/");

    ClassLoader classLoader = getClassLoader(PATH_BASE);
    KeyRegistry registry = loadRegistry(classLoader, PATH_BASE + PATH_KEY_LIST);
    checkClasses(classLoader, registry, PACKAGE_ALGOS, PACKAGE_ANNOTATION, PATH_BASE + PATH_CRYPTO_ALGOS, PATH_BASE + PATH_SECRET_LIST);
  }

  /**
   * @param classLoader ClassLoader to use to retrieve the class
   * @param registry    A filled registry to use for the keys
   * @param packageName Prefix of the package name for the class
   * @param folder      Path of the folder where .class files are located
   * @param secret      Path of the file containing the strings to test
   */
  public static void checkClasses(ClassLoader classLoader, KeyRegistry registry, String packageName, String packageAnnotation, String folder, String secret) {
    try {
      List<String> secretLines = getFileLines(secret);

      // Get annotations
      Class encryptionAnnotation = classLoader.loadClass(packageAnnotation + "Encrypt");
      Class decryptionAnnotation = classLoader.loadClass(packageAnnotation + "Decrypt");

      // For each file in the folder
      Files.walk(Paths.get(folder))
          // Filter only regular files
          .filter(Files::isRegularFile)
          // And filter only .class files
          .filter(path -> path.toString().endsWith(".class"))
          // For each .class file check it
          .forEach(path -> TestAlgsPlus.checkClass(path, classLoader, registry, packageName, secretLines, encryptionAnnotation, decryptionAnnotation));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
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
  public static void checkClass(Path path, ClassLoader classLoader, KeyRegistry registry, String packageName, List<String> secret, Class encryptionAnnotation, Class decryptionAnnotation) {

    try {

      // Load specified class
      String fileName = path.getFileName().toString().replace(".class", "");
      String classPackage = packageName.concat(fileName);
      Class loaded = classLoader.loadClass(classPackage);

      System.out.println(fileName);

      // If it is an encryption algorithm test it
      if (isEncryptionAlgorithm(loaded, encryptionAnnotation, decryptionAnnotation))
        testClass(loaded, registry.get(loaded), secret, encryptionAnnotation, decryptionAnnotation);
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
  public static void testClass(Class loaded, String key, List<String> secret, Class encryptionAnnotation, Class decryptionAnnotation) {
    try {

      // Get constructor and create a new class instance
      Constructor constructor = loaded.getConstructor(String.class);
      Object crypto = constructor.newInstance(key);

      // Get encryption and decryption methods
      Method encryption = null;
      Method decryption = null;

      if (hasAnnotations(loaded, encryptionAnnotation, decryptionAnnotation)) {
        encryption = getMethodsAnnotated(loaded, encryptionAnnotation).get(0);
        decryption = getMethodsAnnotated(loaded, decryptionAnnotation).get(0);
      } else {
        encryption = getMethodsStartWith(loaded, "enc").get(0);
        decryption = getMethodsStartWith(loaded, "dec").get(0);
      }

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
  public static boolean checkUnpadded(String w, String d) {
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
  public static ClassLoader getClassLoader(String path) {
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
  public static KeyRegistry loadRegistry(ClassLoader classLoader, String path) {
    KeyRegistry registry = new KeyRegistry();

    List<String> keyLines = getFileLines(path);

    for (String line : keyLines) {
      // Get pair <class, key> from  line
      String[] classKey = line.split(" ");
      String className = classKey[0];
      String key = classKey[1];

      try {
        // Try to load class and add to the KeyRegistry
        Class toAdd = classLoader.loadClass(className);
        registry.add(toAdd, key);
      } catch (ClassNotFoundException e) {
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
  public static boolean isEncryptionAlgorithm(Class toVerify, Class encryptAnnotation, Class decryptAnnotation) {
    // Public constructor
    boolean checkConstructor = hasStringConstructor(toVerify);

    boolean checkMethods = hasAnnotations(toVerify, encryptAnnotation, decryptAnnotation) || hasMethods(toVerify);

    return checkConstructor && checkMethods;
  }

  private static boolean hasMethods(Class toVerify) {
    // A method starting with enc
    boolean checkEncryption = getMethodsStartWith(toVerify, "enc").size() == 1;

    // A method starting with dec
    boolean checkDecryption = getMethodsStartWith(toVerify, "dec").size() == 1;

    return checkEncryption && checkDecryption;
  }

  private static boolean hasAnnotations(Class toVerify, Class encryptAnnotation, Class decryptAnnotation) {
    // A method starting with enc
    boolean checkEncryption = getMethodsAnnotated(toVerify, encryptAnnotation).size() == 1;

    // A method starting with dec
    boolean checkDecryption = getMethodsAnnotated(toVerify, decryptAnnotation).size() == 1;

    return checkEncryption && checkDecryption;
  }


  /**
   * @param toVerify Class to verify
   * @return true if the class as a constructor with a single string parameter, false otherwise
   */
  public static boolean hasStringConstructor(Class toVerify) {
    boolean check = true;
    try {
      toVerify.getConstructor(String.class);
    } catch (NoSuchMethodException e) {
      check = false;
    }
    return check;
  }

  /**
   * @param toVerify   Class in which to look
   * @param annotation Annotation class to search
   * @return List of methods with the specified annotation
   */
  public static List<Method> getMethodsAnnotated(Class toVerify, Class annotation) {
    // Stream with all the class methods
    List<Method> methods = Arrays.stream(toVerify.getMethods())
        // Filter only methods with one parameter
        .filter(m -> m.getParameterCount() == 1)
        // Filter only methods with string parameter
        .filter(m -> m.getParameterTypes()[0] == String.class)
        // Filter only methods annotated with the specified annotation
        .filter(m -> m.isAnnotationPresent(annotation))
        // Collect stream to list
        .collect(Collectors.toList());
    return methods;
  }

  /**
   * @param toVerify Class in which to look
   * @param prefix   Prefix of methods to search
   * @return List of methods that start with the specified prefix
   */
  public static List<Method> getMethodsStartWith(Class toVerify, String prefix) {
    // Stream with all the class methods
    List<Method> methods = Arrays.stream(toVerify.getMethods())
        // Filter only methods with one parameter
        .filter(m -> m.getParameterCount() == 1)
        // Filter only methods with string parameter
        .filter(m -> m.getParameterTypes()[0] == String.class)
        // Filter only methods which name starts with prefix
        .filter(m -> m.getName().startsWith(prefix))
        // Collect stream to list
        .collect(Collectors.toList());
    return methods;
  }

  /**
   * @param path the path of the file to read
   * @return List of lines in the specified path
   */
  public static List<String> getFileLines(String path) {
    List<String> lines = new ArrayList<String>();

    try {
      BufferedReader reader = new BufferedReader(new FileReader(path));
      String line = reader.readLine();
      while (line != null) {
        lines.add(line);
        line = reader.readLine();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return lines;
  }

}
