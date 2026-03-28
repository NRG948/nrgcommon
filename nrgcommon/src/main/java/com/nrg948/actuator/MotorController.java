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

import com.nrg948.sensor.LimitSwitch;
import com.nrg948.sensor.RelativeEncoder;

/** An interface for a motor controller. */
public interface MotorController extends edu.wpi.first.wpilibj.motorcontrol.MotorController {
  /**
   * Creates a new motor controller that is configured to follow this motor controller.
   *
   * @param logPrefix The prefix for the log entries.
   * @param deviceID The device ID of the new motor controller.
   * @param isInvertedFromLeader Whether the new motor controller should be inverted in direction
   *     from this motor controller. This is useful for when the follower is mechanically connected
   *     to the leader but must spin in the opposite direction.
   * @return A new motor controller that is configured to follow this motor controller.
   */
  MotorController createFollower(String logPrefix, int deviceID, boolean isInvertedFromLeader);

  /** {@return the motor controller's relative encoder} */
  RelativeEncoder getEncoder();

  /** {@return the forward limit switch} */
  LimitSwitch getForwardLimitSwitch();

  /** {@return the reverse limit switch} */
  LimitSwitch getReverseLimitSwitch();

  /**
   * Sets the motor behavior when idle (i.e. brake or coast mode).
   *
   * @param idleMode The motor idle mode to set.
   */
  void setIdleMode(MotorIdleMode idleMode);

  /** Logs motor-specific telemetry to the data log. */
  void logTelemetry();
}
