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

/** Abstract base class for dashboard elements that can contain other elements. */
public abstract class DashboardElementContainer extends DashboardElementBase {
  private static final DashboardElementBase[] NO_ELEMENTS = new DashboardElementBase[0];

  private DashboardElementBase[] elements;

  /**
   * Creates a new DashboardElementContainer.
   *
   * @param title the title of the dashboard element container
   * @param column the column position of the dashboard element container
   * @param row the row position of the dashboard element container
   * @param width the width of the dashboard element container
   * @param height the height of the dashboard element container
   * @param elements the elements contained in the dashboard element container
   */
  public DashboardElementContainer(
      String title, int column, int row, int width, int height, DashboardElementBase[] elements) {
    super(title, column, row, width, height);

    setElements(elements);
  }

  /**
   * Creates a new DashboardElementContainer with no contained elements.
   *
   * @param title the title of the dashboard element container
   * @param column the column position of the dashboard element container
   * @param row the row position of the dashboard element container
   * @param width the width of the dashboard element container
   * @param height the height of the dashboard element container
   */
  public DashboardElementContainer(String title, int column, int row, int width, int height) {
    this(title, column, row, width, height, NO_ELEMENTS);
  }

  /**
   * Sets the elements contained in the dashboard element container.
   *
   * @param elements the elements to set
   */
  public void setElements(DashboardElementBase[] elements) {
    for (var element : elements) {
      element.setParent(this);
    }

    this.elements = elements;
  }

  /** {@return the elements contained in the dashboard element container} */
  public DashboardElementBase[] getElements() {
    return elements;
  }
}
