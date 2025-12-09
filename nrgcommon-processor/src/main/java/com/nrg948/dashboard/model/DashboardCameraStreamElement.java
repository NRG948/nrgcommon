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

public class DashboardCameraStreamElement extends DashboardWidgetElement {
  private final int compression;
  private final int fps;
  private final int resolutionWidth;
  private final int resolutionHeight;

  public DashboardCameraStreamElement(
      String title,
      int column,
      int row,
      int width,
      int height,
      int compression,
      int fps,
      int resolutionWidth,
      int resolutionHeight) {
    super(title, column, row, width, height);
    this.compression = compression;
    this.fps = fps;
    this.resolutionWidth = resolutionWidth;
    this.resolutionHeight = resolutionHeight;
  }

  @Override
  public String getType() {
    return "Camera Stream";
  }

  @Override
  public void accept(DashboardElementVisitor visitor) {
    visitor.visit(this);
  }

  public int getCompression() {
    return compression;
  }

  public int getFps() {
    return fps;
  }

  public int getResolutionWidth() {
    return resolutionWidth;
  }

  public int getResolutionHeight() {
    return resolutionHeight;
  }
}
