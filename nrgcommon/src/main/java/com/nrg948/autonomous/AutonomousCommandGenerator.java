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
package com.nrg948.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import java.util.Collection;
import org.javatuples.LabelValue;

/**
 * Annotates a method that creates a {@link Collection} of {@link LabelValue} elements mapping the
 * name to display in user interface elements like {@link SendableChooser} to a {@link Command}.
 *
 * <p>The method must conform to the following prototype:
 *
 * <pre>
 * <code>
 * public static Collection&lt;LabelValue&lt;String, Command&gt;&gt; name(T container);
 * </code>
 * </pre>
 *
 * The type T is an object passed to the constructor of the autonomous command providing access to
 * the robot subsystems. This is typically an instance of <code>RobotContainer</code>, but could be
 * another type used to manage the subsystems
 */
public @interface AutonomousCommandGenerator {}
