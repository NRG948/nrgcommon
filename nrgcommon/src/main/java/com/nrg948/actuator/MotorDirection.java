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
package com.nrg948.actuator;

/** The direction the motor output shaft rotates when positive voltage is applied. */
public enum MotorDirection {
  /** The motor output shaft rotates counter-clockwise when positive voltage is applied. */
  COUNTER_CLOCKWISE_POSITIVE,

  /** The motor output shaft rotates clockwise when positive voltage is applied. */
  CLOCKWISE_POSITIVE;

  /**
   * {@return whether the motor rotates in the clockwise direction when a positive voltage is
   * applied (i.e. the motor is "inverted")} This method returns {@code false} if the motor rotates
   * in the counter-clockwise direction when a positive voltage is applied.
   */
  public boolean isInverted() {
    return this == CLOCKWISE_POSITIVE;
  }
}
