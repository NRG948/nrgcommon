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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.nrg948.dashboard.model.GameField;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import java.lang.annotation.Target;

/**
 * Annotation for a field widget on the dashboard.
 *
 * <p>This annotation can be applied to fields or methods that return a {@link Field2d} object.
 */
@Target({FIELD, METHOD})
public @interface DashboardField {
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

  /** {@return the game field to display} */
  GameField game() default GameField.REEFSCAPE;

  /** {@return the width of the robot on the field} */
  double robotWidth() default 0.85;

  /** {@return the length of the robot on the field} */
  double robotLength() default 0.85;

  /** {@return whether to show other objects on the field} */
  boolean showOtherObjects() default true;

  /** {@return whether to show trajectories on the field} */
  boolean showTrajectories() default true;

  /** {@return the rotation of the field in degrees} */
  double fieldRotationDegrees() default 0.0;

  /** {@return the color of the robot on the field} */
  String robotColor() default "Red";

  /** {@return the color of the trajectories on the field} */
  String trajectoryColor() default "White";
}
