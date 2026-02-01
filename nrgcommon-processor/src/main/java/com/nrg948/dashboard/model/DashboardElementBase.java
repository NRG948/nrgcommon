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

import java.util.Optional;

/** Abstract base class for all dashboard elements. */
public abstract class DashboardElementBase {
  private final String title;
  private final int column;
  private final int row;
  private final int width;
  private final int height;
  private Optional<DashboardElementContainer> parent = Optional.empty();

  /**
   * Creates a new DashboardElementBase.
   *
   * @param title the title of the dashboard element
   * @param column the column position of the dashboard element
   * @param row the row position of the dashboard element
   * @param width the width of the dashboard element
   * @param height the height of the dashboard element
   */
  public DashboardElementBase(String title, int column, int row, int width, int height) {
    this.title = title;
    this.column = column;
    this.row = row;
    this.width = width;
    this.height = height;
  }

  /** {@return the parent container of the dashboard element, if any} */
  public Optional<DashboardElementContainer> getParent() {
    return parent;
  }

  /**
   * Sets the parent container of the dashboard element.
   *
   * @param parent the parent container of the dashboard element
   */
  public void setParent(DashboardElementContainer parent) {
    this.parent = Optional.ofNullable(parent);
  }

  /** {@return the title of the dashboard element} */
  public String getTitle() {
    return title;
  }

  /** {@return the column position of the dashboard element} */
  public int getColumn() {
    return column;
  }

  /** {@return the row position of the dashboard element} */
  public int getRow() {
    return row;
  }

  /** {@return the width of the dashboard element} */
  public int getWidth() {
    return width;
  }

  /** {@return the height of the dashboard element} */
  public int getHeight() {
    return height;
  }

  /** {@return the type of the dashboard element} */
  public abstract String getType();

  /**
   * Accepts a visitor to perform operations on the dashboard element.
   *
   * @param visitor the visitor to accept
   */
  public abstract void accept(DashboardElementVisitor visitor);
}
