/*
  MIT License

  Copyright (c) 2022 Newport Robotics Group

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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;

/**
 * Annotates a class containing one or morte RobotPreferences values. It is used
 * to define the layout on the Shuffleboard used by the robot operator to adjust
 * preference values.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RobotPreferencesLayout {
  /**
   * The group name of the RobotPreferences value for which to generate widgets in
   * the Shuffleboard layout.
   * 
   * @return The group name.
   */
  String groupName();

  /**
   * The column position for the Shuffleboard layout.
   * 
   * @return The layout column position.
   */
  int column();

  /**
   * The row position for the Shuffleboard layout.
   * 
   * @return The layout row position.
   */
  int row();

  /**
   * The width of the Shuffleboard layout.
   * 
   * @return The layout width.
   */
  int width();

  /**
   * The height of the Shuffleboard layout.
   *
   * @return The layout height.
   */
  int height();

  /**
   * The type of layout. Valid values are defined by the {@link BuiltInLayouts}
   * enum.
   * 
   * @return The layout type.
   */
  String type() default "List Layout";

  /**
   * The number of columns in a grid layout. This value is ignored if the
   * {@link RobotPreferencesLayout#type} is "List Layout".
   * 
   * @return The number of columns.
   */
  int gridColumns() default -1;

  /**
   * The number of rows in a grid layout. This value is ignored if the
   * {@link RobotPreferencesLayout#type} is "List Layout".
   * 
   * @return The number of rows.
   */
  int gridRows() default -1;
}
