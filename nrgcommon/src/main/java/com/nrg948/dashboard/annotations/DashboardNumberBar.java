/*
  MIT License

  Copyright (c) 2025 Newport Robotics Group

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
package com.nrg948.dashboard.annotations;

import static com.nrg948.dashboard.model.Orientation.HORIZONTAL;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.nrg948.dashboard.model.Orientation;
import java.lang.annotation.Target;

/**
 * Annotation for a number bar widget on the dashboard.
 *
 * <p>This annotation can be applied to fields or methods that return a {@code double} value.
 */
@Target({FIELD, METHOD})
public @interface DashboardNumberBar {
  /**
   * {@return the title of the widget} If empty, the default title is the name of the annotated
   * field or method.
   */
  String title() default "";

  /** {@return the column position of the widget in the dashboard grid} */
  int column() default 0;

  /** {@return the row position of the widget in the dashboard grid} */
  int row() default 0;

  /** {@return the width of the widget in the dashboard grid} */
  int width() default 1;

  /** {@return the height of the widget in the dashboard grid} */
  int height() default 1;

  /** {@return the minimum value of the number bar} */
  double min() default -1.0;

  /** {@return the maximum value of the number bar} */
  double max() default 1.0;

  /** {@return the number of divisions in the number bar} */
  int divisions() default 5;

  /** {@return whether the number bar is inverted} */
  boolean inverted() default false;

  /** {@return the orientation of the number bar} */
  Orientation orientation() default HORIZONTAL;
}
