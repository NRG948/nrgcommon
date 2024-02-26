/*
  MIT License

  Copyright (c) 2024 Newport Robotics Group

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
 * The NRG Common Library provides a set of classes, annotations and utilities
 * used by FIRST Robotics Competition Team 948 - Newport Robotics Group (NRG948).
 * 
 * <p>
 * To initialize the library, you must call the {@link Common#init(String...)}
 * method passing the name of the robot package. In the Command-based Robot,
 * this must be done in the <code>Robot.initRobot()</code> method before the
 * <code>RobotContainer</code> is created.<br>
 * 
 * <pre>
 * <code>
 * {@literal @}Override
 * public void robotInit() {
 *   // Initialize the NRG Common Library before creating the RobotContainer so that
 *   // it is initialized and ready for use by the subsystems.
 *   Common.init("frc.robot");
 * 
 *   // Instantiate our RobotContainer.  This will perform all our button bindings,
 *   // and put our autonomous chooser on the dashboard.
 *   m_robotContainer = new RobotContainer();
 * }
 * </code>
 * </pre>
 */
package com.nrg948;
