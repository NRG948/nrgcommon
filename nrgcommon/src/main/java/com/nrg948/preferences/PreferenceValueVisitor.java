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
package com.nrg948.preferences;

/** An interface to support the Visitor pattern on preferences values. */
public interface PreferenceValueVisitor {

  /**
   * Called to apply the visitor's effect on a {@link StringPreference}.
   *
   * @param value The {@link StringPreference} to visit.
   */
  void visit(StringPreference value);

  /**
   * Called to apply the visitor's effect on a {@link BooleanPreference}.
   *
   * @param value The {@link BooleanPreference} to visit.
   */
  void visit(BooleanPreference value);

  /**
   * Called to apply the visitor's effect on a {@link DoublePreference}.
   *
   * @param value The {@link DoublePreference} to visit.
   */
  void visit(DoublePreference value);

  /**
   * Called to apply the visitor's effect on an {@link EnumPreference}.
   *
   * @param <E> The enum type.
   * @param value The {@link EnumPreference} to visit.
   */
  <E extends Enum<E>> void visit(EnumPreference<E> value);

  /**
   * Called to apply the visitor's effect on a {@link PIDControllerPreference}.
   *
   * @param value The {@link PIDControllerPreference} to visit.
   */
  void visit(PIDControllerPreference value);
}
