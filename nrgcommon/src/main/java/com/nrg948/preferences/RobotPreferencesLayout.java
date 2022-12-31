// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

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
}
