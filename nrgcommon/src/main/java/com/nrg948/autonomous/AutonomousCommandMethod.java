/*
  MIT License

  Copyright (c) 2023 Newport Robotics Group

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

package com.nrg948.autonomous;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Annotates a methed that creates a new subclass of {@link Command} to run
 * during autonomous.
 * <p>
 * The method must conform to the following prototype:
 * 
 * <pre>
 * <code>
 * public static Command name(T container);
 * </code>
 * </pre>
 *
 * The type T is an object passed to the constructor of the autonomous command
 * providing access to the robot subsystems. This is typically an instance of
 * <code>RobotContainer</code>, but could be another type used to manage the
 * subsystems
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutonomousCommandMethod {
  /**
   * The name to display for the annotated method returning a {@link Command} in
   * the {@link SendableChooser} returned by
   * {@link Autonomous#getChooser(Object, String...)}.
   * 
   * @return The display name.
   */
  String name();

  /**
   * Whether this command returned by the method is the default {@link Command} in
   * the {@link SendableChooser} returned by
   * {@link Autonomous#getChooser(Object, String...)}.
   * 
   * @return Returns true if this is the default command, and false otherwise.
   */
  boolean isDefault() default false;
}
