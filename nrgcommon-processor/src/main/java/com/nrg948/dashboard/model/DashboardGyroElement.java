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

/** Represents a dashboard element that provides a gyro visualization. */
public class DashboardGyroElement extends DashboardWidgetElement {
  private boolean ccwPositive;

  /**
   * Creates a new DashboardGyroElement.
   *
   * @param title the title of the dashboard gyro element
   * @param column the column position of the dashboard gyro element
   * @param row the row position of the dashboard gyro element
   * @param width the width of the dashboard gyro element
   * @param height the height of the dashboard gyro element
   * @param ccwPositive whether counter-clockwise rotation is considered positive
   */
  DashboardGyroElement(
      String title, int column, int row, int width, int height, boolean ccwPositive) {
    super(title, column, row, width, height);
    this.ccwPositive = ccwPositive;
  }

  @Override
  public String getType() {
    return "Gyro";
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }

  /** {@return whether counter-clockwise rotation is considered positive} */
  public boolean isCcwPositive() {
    return ccwPositive;
  }
}
