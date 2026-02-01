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

/** An abstract base class for dashboard elements that provides a widget visualization. */
public abstract class DashboardWidgetElement extends DashboardElementBase {
  /**
   * Creates a new DashboardWidgetElement.
   *
   * @param title the title of the dashboard widget element
   * @param column the column position of the dashboard widget element
   * @param row the row position of the dashboard widget element
   * @param width the width of the dashboard widget element
   * @param height the height of the dashboard widget element
   */
  public DashboardWidgetElement(String title, int column, int row, int width, int height) {
    super(title, column, row, width, height);
  }

  /** {@return the topic of the dashboard widget element} */
  public String getTopic() {
    String topic = "/";
    var parentOpt = getParent();

    while (true) {
      if (parentOpt.isEmpty()) {
        return topic + getTitle().replace("/", "+");
      }
      var parent = parentOpt.get();
      topic = "/" + parent.getTitle().replace("/", "+") + topic;
      parentOpt = parent.getParent();
    }
  }

  /** {@return the update period of the dashboard widget element} */
  public double getPeriod() {
    return 0.06;
  }
}
