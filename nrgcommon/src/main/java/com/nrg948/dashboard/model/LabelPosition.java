/*
  MIT License

  Copyright (c) 2026 Newport Robotics Group

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

/** Enum representing label positions for dashboard widgets in a layout. */
public enum LabelPosition {
  /** The label is positioned at the top of the widget. */
  TOP("Top"),
  /** The label is positioned at the bottom of the widget. */
  BOTTOM("Bottom"),
  /** The label is positioned to the left of the widget. */
  LEFT("Left"),
  /** The label is positioned to the right of the widget. */
  RIGHT("Right"),
  /** The label is hidden and not displayed. */
  HIDDEN("Hidden");

  private final String displayName;

  /** Constructor for the LabelPosition enum. */
  LabelPosition(String displayName) {
    this.displayName = displayName;
  }

  /** Returns the display name of the label position. */
  public String getDisplayName() {
    return displayName;
  }

  /** Returns the JSON value of the label position. */
  public String toJsonValue() {
    return displayName.toUpperCase();
  }
}
