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

import com.nrg948.util.ColorUtil;
import edu.wpi.first.wpilibj.util.Color8Bit;

public class DashboardFieldElement extends DashboardWidgetElement {
  private final GameField game;
  private final double robotWidth;
  private final double robotLength;
  private final boolean showOtherObjects;
  private final boolean showTrajectories;
  private final double fieldRotationDegrees;
  private final Color8Bit robotColor;
  private final Color8Bit trajectoryColor;

  public DashboardFieldElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      GameField game,
      double robotWidth,
      double robotLength,
      boolean showOtherObjects,
      boolean showTrajectories,
      double fieldRotationDegrees,
      String robotColor,
      String trajectoryColor) {
    super(title, column, row, width, height);
    this.game = game;
    this.robotWidth = robotWidth;
    this.robotLength = robotLength;
    this.showOtherObjects = showOtherObjects;
    this.showTrajectories = showTrajectories;
    this.fieldRotationDegrees = fieldRotationDegrees;
    this.robotColor = ColorUtil.stringToColor(robotColor);
    this.trajectoryColor = ColorUtil.stringToColor(trajectoryColor);
  }

  @Override
  public String getType() {
    return "Field";
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }

  public GameField getGame() {
    return game;
  }

  public double getRobotWidth() {
    return robotWidth;
  }

  public double getRobotLength() {
    return robotLength;
  }

  public boolean showOtherObjects() {
    return showOtherObjects;
  }

  public boolean showTrajectories() {
    return showTrajectories;
  }

  public double getFieldRotationDegrees() {
    return fieldRotationDegrees;
  }

  public Color8Bit getRobotColor() {
    return robotColor;
  }

  public Color8Bit getTrajectoryColor() {
    return trajectoryColor;
  }
}
