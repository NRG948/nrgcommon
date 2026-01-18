/*
  MIT License

  Copyright (c) 2024 Newport Robotics Group

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
package com.nrg948;

import com.nrg948.annotations.Annotations;

/** A class to initialize the NRG Common Library. */
public final class Common {
  /* Disallow instantiation */
  private Common() {}

  /**
   * Initializes the NRG Common library.
   *
   * <p>This method must be called in the <code>Robot</code> constructor before the <code>
   * RobotContainer</code> is created.
   *
   * @param pkgs The packages to scan for annotations implemented by the NRG Common Library.
   */
  public static void init(String... pkgs) {
    Annotations.init(pkgs);
  }
}
