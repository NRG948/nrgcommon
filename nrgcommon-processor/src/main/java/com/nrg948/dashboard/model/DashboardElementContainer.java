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

public abstract class DashboardElementContainer extends DashboardElementBase {
  private static final DashboardElementBase[] NO_ELEMENTS = new DashboardElementBase[0];

  private DashboardElementBase[] elements;

  public DashboardElementContainer(
      String title, int column, int row, int width, int height, DashboardElementBase[] elements) {
    super(title, column, row, width, height);

    setElements(elements);
  }

  public DashboardElementContainer(String title, int column, int row, int width, int height) {
    this(title, column, row, width, height, NO_ELEMENTS);
  }

  public void setElements(DashboardElementBase[] elements) {
    for (var element : elements) {
      element.setParent(this);
    }

    this.elements = elements;
  }

  public DashboardElementBase[] getElements() {
    return elements;
  }
}
