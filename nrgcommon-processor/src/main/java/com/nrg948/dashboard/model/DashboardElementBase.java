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

public abstract class DashboardElementBase {
  private final String title;
  private final int column;
  private final int row;
  private final int width;
  private final int height;
  private Optional<DashboardElementContainer> parent = Optional.empty();

  public DashboardElementBase(String title, int column, int row, int width, int height) {
    this.title = title;
    this.column = column;
    this.row = row;
    this.width = width;
    this.height = height;
  }

  public Optional<DashboardElementContainer> getParent() {
    return parent;
  }

  public void setParent(DashboardElementContainer parent) {
    this.parent = Optional.ofNullable(parent);
  }

  public String getTitle() {
    return title;
  }

  public int getColumn() {
    return column;
  }

  public int getRow() {
    return row;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public abstract String getType();

  public abstract void accept(DashboardElementVisitor visitor);
}
