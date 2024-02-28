/*
  MIT License

  Copyright (c) 2024 Newport Robotics Group

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
*/
package com.nrg948.annotations;

import static org.reflections.scanners.Scanners.FieldsAnnotated;
import static org.reflections.scanners.Scanners.MethodsAnnotated;
import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.reflections.Reflections;
import org.reflections.Store;
import org.reflections.serializers.Serializer;
import org.reflections.serializers.XmlSerializer;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.QueryFunction;

/** A class providing access to types annotated by the NRG Common Library annotations. */
public final class Annotations {
  private static Reflections reflections;

  /* Disallow instantiation */
  private Annotations() {}

  /**
   * Initializes the annotation metadata for the NRG Common Library.
   *
   * @param pkgs The packages to scan for annotations implemented by the NRG Common Library.
   */
  public static void init(String... pkgs) {
    reflections = loadFromMetadata().orElseGet(() -> scanPackages(pkgs));
  }

  /**
   * Creates and initializes a {@link Reflections} instance from metadata, if present.
   *
   * <p>Reflections metadata is stored in the META-INF/reflections directory in the program's JAR
   * file.
   *
   * @return An optional {@link Reflections} instance initialized from metadata. If no metadata is
   *     present, {@link Optional#empty()} is returned.
   */
  private static Optional<Reflections> loadFromMetadata() {
    Optional<Reflections> reflections = Optional.empty();
    Serializer xmlSerializer = new XmlSerializer();
    ClassLoader loader = ClassLoader.getSystemClassLoader();

    try {
      Enumeration<URL> resources = loader.getResources("META-INF/reflections");

      while (resources.hasMoreElements()) {
        URL url = resources.nextElement();
        JarURLConnection connection = (JarURLConnection) url.openConnection();

        try (JarFile jar = connection.getJarFile()) {
          Enumeration<JarEntry> entries = jar.entries();

          while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            if (entry.getName().endsWith("-reflections.xml")) {
              if (reflections.isEmpty()) {
                reflections = Optional.of(new Reflections());
              }

              reflections.get().collect(jar.getInputStream(entry), xmlSerializer);
            }
          }
        }
      }
    } catch (Exception e) {
      System.err.println("WARNING: Failed to load Reflections metadata: " + e.getMessage());
    }

    return reflections;
  }

  /**
   * Scans the specified packages for annotations implemented by the NRG Common Library.
   *
   * @param pkgs The packages to scan for annotations implemented by the NRG Common Library.
   * @return The annotation metadata for the specified packages.
   */
  private static Reflections scanPackages(String... pkgs) {
    String[] allPkgs = Arrays.copyOf(pkgs, pkgs.length + 1);

    allPkgs[allPkgs.length - 1] = "com.nrg948";

    return new Reflections(
        new ConfigurationBuilder()
            .forPackages(allPkgs)
            .setScanners(FieldsAnnotated, MethodsAnnotated, SubTypes, TypesAnnotated));
  }

  /**
   * Returns the set of elements annotated with the specified annotation.
   *
   * @param <T> The type of element.
   * @param query The query function.
   * @return The set of elements annotated with the specified annotation.
   */
  public static <T> Set<T> get(QueryFunction<Store, T> query) {
    return reflections.get(query);
  }
}
