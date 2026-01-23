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
package com.nrg948.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;

/** An interface for creating autonomous commands to add to the {@link SendableChooser}. */
interface AutonomousCommandFactory extends Comparable<AutonomousCommandFactory> {
  /**
   * The name to display for the annotated {@link Command} in the {@link SendableChooser} returned
   * by {@link Autonomous#getChooser(Object...)}.
   *
   * @return The display name.
   */
  String getName();

  /**
   * Whether this command is the default {@link Command} in the {@link SendableChooser} returned by
   * {@link Autonomous#getChooser(Object...)}.
   *
   * @return Returns true if this is the default command, and false otherwise.
   */
  boolean isDefault();

  /**
   * Returns an instance of the {@link Command} to add to the {@link SendableChooser}.
   *
   * @param args A list objects passed to the constructor of the autonomous commands providing
   *     access to the robot subsystems. This is typically a single parameter of type <code>
   *     RobotContainer
   *     </code> but may be another type managing access to the subsystems or the list of subsystems
   *     themselves. All commands must accept the same types and number of arguments.
   * @return The {@link Command} to add to the {@link SendableChooser}.
   */
  Command newCommand(Object... args);

  @Override
  default int compareTo(AutonomousCommandFactory obj) {
    return getName().compareTo(obj.getName());
  }
}
