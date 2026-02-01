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

/** Represents a dashboard element that provides a command button. */
public class DashboardCommandElement extends DashboardWidgetElement {
  private final boolean showType;
  private final boolean fillWidget;

  /**
   * Creates a new DashboardCommandElement.
   *
   * @param title the title of the command element
   * @param column the column position of the command element
   * @param row the row position of the command element
   * @param width the width of the command element
   * @param height the height of the command element
   * @param showType whether to show the type of the command
   * @param fillWidget whether to fill the widget area
   */
  public DashboardCommandElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      boolean showType,
      boolean fillWidget) {
    super(title, column, row, width, height);
    this.showType = showType;
    this.fillWidget = fillWidget;
  }

  @Override
  public String getType() {
    return "Command";
  }

  /** {@return whether to show the type of the command} */
  public boolean showType() {
    return showType;
  }

  /** {@return whether to fill the widget area} */
  public boolean fillWidget() {
    return fillWidget;
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }
}
