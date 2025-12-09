/*
  MIT License

  Copyright (c) 2026 Newport Robotics Group

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
package com.nrg948.util.enums;

import java.util.Map;
import java.util.Optional;

/** Interface providing selection methods for enums. */
public interface SelectorEnum<E extends Enum<E>> {
  /**
   * Selects a value from the provided options map based on the enum instance.
   *
   * @param <V> The type of the values in the options map.
   * @param options The map of enum values to corresponding values.
   * @return An {@link Optional} containing the selected value, or empty if not found.
   */
  @SuppressWarnings("unchecked")
  default <V> Optional<V> select(Map<E, V> options) {
    return Optional.ofNullable(options.get((E) this));
  }

  /**
   * Selects a value from the provided options map based on the enum instance, or returns a default
   * value if not found.
   *
   * @param <V> The type of the values in the options map.
   * @param options The map of enum values to corresponding values.
   * @param defaultValue The default value to return if the enum instance is not found in the
   *     options map.
   * @return The selected value, or the default value if not found.
   */
  @SuppressWarnings("unchecked")
  default <V> V selectOrDefault(Map<E, V> options, V defaultValue) {
    return options.getOrDefault((E) this, defaultValue);
  }
}
