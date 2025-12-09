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

/** Enum representing icons for false boolean values in dashboard widgets. */
public enum FalseIcon {
  /** No icon is displayed for false values. */
  NONE("None"),
  /** An X icon is displayed for false values. */
  X("X"),
  /** An exclamation point icon is displayed for false values. */
  EXCLAMATION_POINT("Exclamation Point");

  private final String displayName;

  /** Constructor for the FalseIcon enum. */
  FalseIcon(String displayName) {
    this.displayName = displayName;
  }

  /** Returns the display name of the icon. */
  public String getDisplayName() {
    return displayName;
  }

  /** Returns the JSON value of the icon. */
  public String toJsonValue() {
    return displayName;
  }
}
