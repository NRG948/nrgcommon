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

/**
 * Represents a dashboard layout element that can contain other dashboard elements arranged in a
 * specific layout type and with a specified label position.
 */
public class DashboardLayoutElement extends DashboardElementContainer {
  private final LayoutType type;
  private final LabelPosition labelPosition;

  /**
   * Creates a new DashboardLayoutElement.
   *
   * @param title the title of the dashboard layout element
   * @param column the column position of the dashboard layout element
   * @param row the row position of the dashboard layout element
   * @param width the width of the dashboard layout element
   * @param height the height of the dashboard layout element
   * @param type the layout type of the dashboard layout element
   * @param labelPosition the label position of the dashboard layout element
   * @param elements the child elements of the dashboard layout element
   */
  public DashboardLayoutElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      LayoutType type,
      LabelPosition labelPosition,
      DashboardElementBase[] elements) {
    super(title, column, row, width, height, elements);
    this.type = type;
    this.labelPosition = labelPosition;
  }

  /**
   * Creates a new DashboardLayoutElement with no child elements.
   *
   * @param title the title of the dashboard layout element
   * @param column the column position of the dashboard layout element
   * @param row the row position of the dashboard layout element
   * @param width the width of the dashboard layout element
   * @param height the height of the dashboard layout element
   * @param type the layout type of the dashboard layout element
   * @param labelPosition the label position of the dashboard layout element
   */
  public DashboardLayoutElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      LayoutType type,
      LabelPosition labelPosition) {
    super(title, column, row, width, height);
    this.type = type;
    this.labelPosition = labelPosition;
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String getType() {
    return type.toJsonValue();
  }

  /** {@return the layout type of the dashboard layout element} */
  public LayoutType getLayoutType() {
    return type;
  }

  /** {@return the label position of the dashboard layout element} */
  public LabelPosition getLabelPosition() {
    return labelPosition;
  }
}
