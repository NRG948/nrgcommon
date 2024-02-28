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

/**
 * The {@code com.nrg948.preferences} package eases management, use and presentation of robot
 * preferences through an annotation-based approach.
 *
 * <p>The {@link RobotPreferencesValue} annotation allows you to define preference value giving you
 * the flexibility of locating preferences near their point of use. The {@link
 * RobotPreferencesLayout} enables a specific layout for the group of PID preferences to be added
 * automatically to Shuffleboard.
 *
 * <p>The following example defines the PID constants in a class derived from {@link PIDSubsystem}
 * and enables a layout to be generated for the Shuffleboard.<br>
 *
 * <pre>
 * <code>
 * import edu.wpi.first.math.controller.PIDController;
 * import edu.wpi.first.wpilibj2.command.PIDSubsystem;
 *
 * import com.nrg948.RobotPreferences.DoubleValue;
 * import com.nrg948.RobotPreferencesLayout;
 * import com.nrg948.RobotPreferencesValue;
 *
 * {@literal @}RobotPreferencesLayout(
 *   groupName = "MyPIDSubsystem",
 *   column = 0,
 *   row = 0,
 *   width = 2,
 *   height = 3
 * )
 * public MyPIDSubsystem extend PIDSubsystem {
 *
 *   public static String kPreferencesGroupName = "MyPIDSubsystem";
 *
 *   {@literal @}RobotPreferencesValue
 *   public static DoubleValue kP = new DoubleValue(kPreferencesGroupName, "kP", 1.0);
 *   {@literal @}RobotPreferencesValue
 *   public static DoubleValue kI = new DoubleValue(kPreferencesGroupName, "kI", 1.0);
 *   {@literal @}RobotPreferencesValue
 *   public static DoubleValue kD = new DoubleValue(kPreferencesGroupName, "kD", 1.0);
 *
 *   // Construct the PID subsystem using the current preferences value.
 *   public MyPIDSubsystem() {
 *     super(new PIDController(kP.getValue(), kI.getValue(), kD.getValue()))
 *   }
 *
 *   // Override the {@link PIDSubsystem#enable()} method to refresh the PID values in
 *   // case they change.
 *   {@literal @}Override
 *   public void enable() {
 *     PIDController controller = getController();
 *
 *     controller.setP(kP.getValue());
 *     controller.setI(kI.getValue());
 *     controller.setD(kD.getValue());
 *     controller.reset();
 *
 *     super.enable();
 *   }
 * }
 * </code>
 * </pre>
 *
 * <p>To add the preferences layout to Shuffleboard, call the {@link
 * RobotPreferences#addShuffleBoardTab()} method where you add other Shuffleboard elements. The
 * method will add all layouts defined by the {@link RobotPreferencesLayout} annotation to a tab
 * named "Preferences" in Shuffleboard.
 */
package com.nrg948.preferences;

import edu.wpi.first.wpilibj2.command.PIDSubsystem;
