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
package com.nrg948.actuator;

import static com.nrg948.actuator.MotorDirection.COUNTER_CLOCKWISE_POSITIVE;
import static com.nrg948.actuator.MotorIdleMode.COAST;

/**
 * A record that contains the motor configuration.
 *
 * @param direction The direction the motor rotates when a positive voltage is applied.
 * @param idleMode The motor behavior when idle (i.e. brake or coast mode).
 * @param distancePerRotation The distance the attached mechanism moves per rotation of the motor
 *     output shaft.
 *     <p>The unit of measure depends on the mechanism. For a mechanism that produces linear motion,
 *     the unit is typically in meters. For a mechanism that produces rotational motion, the unit is
 *     typically in radians.
 */
public record MotorConfig(
    MotorDirection direction, MotorIdleMode idleMode, double distancePerRotation) {
  /**
   * Constructs a MotorConfig with default values for any parameters that are null or zero. The
   * default values are as follows:
   *
   * <ul>
   *   <li>direction: COUNTER_CLOCKWISE_POSITIVE
   *   <li>idleMode: COAST
   *   <li>distancePerRotation: 1.0
   * </ul>
   *
   * @param direction The direction the motor rotates when a positive voltage is applied.
   * @param idleMode The motor behavior when idle (i.e. brake or coast mode).
   * @param distancePerRotation The distance the attached mechanism moves per rotation of the motor
   *     output shaft. The unit of measure depends on the mechanism. For a mechanism that produces
   *     linear motion, the unit is typically in meters. For a mechanism that produces rotational
   *     motion, the unit is typically in radians.
   */
  public MotorConfig {
    if (direction == null) {
      direction = COUNTER_CLOCKWISE_POSITIVE;
    }
    if (idleMode == null) {
      idleMode = COAST;
    }
    if (distancePerRotation == 0.0) {
      distancePerRotation = 1.0;
    }
  }
}
