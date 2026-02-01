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
package com.nrg948.dashboard.model;

/** Represents a dashboard element that provides a swerve drive visualization. */
public class DashboardSwerveDriveElement extends DashboardWidgetElement {
  private final boolean showRobotRotation;
  private final RotationUnit rotationUnits;

  /**
   * Creates a new DashboardSwerveDriveElement.
   *
   * @param title the title of the dashboard swerve drive element
   * @param column the column position of the dashboard swerve drive element
   * @param row the row position of the dashboard swerve drive element
   * @param width the width of the dashboard swerve drive element
   * @param height the height of the dashboard swerve drive element
   * @param showRobotRotation whether to show the robot's rotation
   * @param rotationUnits the units for the robot's rotation
   */
  public DashboardSwerveDriveElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      boolean showRobotRotation,
      RotationUnit rotationUnits) {
    super(title, column, row, width, height);
    this.showRobotRotation = showRobotRotation;
    this.rotationUnits = rotationUnits;
  }

  @Override
  public String getType() {
    return "SwerveDrive";
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }

  /** {@return whether to show the robot's rotation} */
  public boolean showRobotRotation() {
    return showRobotRotation;
  }

  /** {@return the units for the robot's rotation} */
  public RotationUnit getRotationUnits() {
    return rotationUnits;
  }
}
