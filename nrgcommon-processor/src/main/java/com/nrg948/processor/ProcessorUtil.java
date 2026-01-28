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
package com.nrg948.processor;

import java.util.Optional;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor14;

public final class ProcessorUtil {

  /**
   * Converts an element to a type element if possible.
   *
   * @param element The element to convert.
   * @return An {@link Optional} containing the type element if the conversion is possible,
   *     otherwise empty.
   */
  public static Optional<TypeElement> asTypeElement(Element element) {
    return element.accept(
        new SimpleElementVisitor14<Optional<TypeElement>, Void>(Optional.empty()) {
          @Override
          public Optional<TypeElement> visitType(TypeElement e, Void p) {
            return Optional.of(e);
          }
        },
        null);
  }

  private ProcessorUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}
