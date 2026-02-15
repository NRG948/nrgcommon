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
package com.nrg948.dashboard.data;

import edu.wpi.first.cscore.HttpCamera;
import java.util.Arrays;

/** A binding that binds an {@link HttpCamera} to dashboard data updates. */
final class HttpCameraBinding extends DashboardData {
  private final String topic;
  private final HttpCamera camera;

  /**
   * Constructs an HttpCameraBinding with the given topic and camera.
   *
   * @param topic the topic to bind the camera to
   * @param camera the camera to bind
   */
  HttpCameraBinding(String topic, HttpCamera camera) {
    this.topic = topic;
    this.camera = camera;
  }

  @Override
  public void enable() {
    var streamUrls =
        Arrays.stream(camera.getUrls()).map(url -> "mjpg:" + url).toArray(String[]::new);
    TABLE.getEntry(String.join("/", topic, "streams")).setStringArray(streamUrls);
  }

  @Override
  public void disable() {}

  @Override
  protected void update() {}

  @Override
  public void close() {}
}
