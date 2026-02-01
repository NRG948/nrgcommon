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

/** Represents a dashboard element that displays data from a 3-axis accelerometer. */
public class Dashboard3AxisAccelerometerElement extends DashboardWidgetElement {
  /**
   * Creates a new Dashboard3AxisAccelerometerElement.
   *
   * @param title the title of the accelerometer element
   * @param column the column position of the element
   * @param row the row position of the element
   * @param width the width of the element
   * @param height the height of the element
   */
  public Dashboard3AxisAccelerometerElement(
      String title, int column, int row, int width, int height) {
    super(title, column, row, width, height);
  }

  @Override
  public String getType() {
    return "3 Axis Accelerometer";
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }
}
