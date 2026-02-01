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

import static com.nrg948.dashboard.model.LabelPosition.TOP;
import static com.nrg948.dashboard.model.LayoutType.LIST;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.nrg948.dashboard.model.LabelPosition;
import com.nrg948.dashboard.model.LayoutType;
import java.lang.annotation.Target;

/**
 * Annotation for a layout widget on the dashboard.
 *
 * <p>This annotation can be applied to fields or methods that return an object annotated with
 * {@link DashboardDefinition} that defines the elements of the layout.
 */
@Target({FIELD, METHOD})
public @interface DashboardLayout {
  /**
   * {@return the title of the layout} If empty, the default title is the name of the annotated
   * field or method.
   */
  String title() default "";

  /** {@return the column position of the layout in the dashboard grid} */
  int column() default 0;

  /** {@return the row position of the layout in the dashboard grid} */
  int row() default 0;

  /** {@return the width of the layout in the dashboard grid} */
  int width() default 1;

  /** {@return the height of the layout in the dashboard grid} */
  int height() default 1;

  /** {@return the type of layout for the layout} */
  LayoutType type() default LIST;

  /** {@return the position of the label for the layout} */
  LabelPosition labelPosition() default TOP;
}
