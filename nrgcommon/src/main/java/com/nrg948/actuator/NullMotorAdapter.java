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

/**
 * A motor controller adapter that does nothing. This is useful for testing and for mechanisms that
 * may not have a motor controller (e.g. a passive joint).
 */
public class NullMotorAdapter implements MotorController {

  /** Creates a new NullMotorAdapter. */
  public NullMotorAdapter() {}

  @Override
  public void set(double speed) {}

  @Override
  public double get() {
    return 0;
  }

  @Override
  public void setInverted(boolean isInverted) {}

  @Override
  public boolean getInverted() {
    return false;
  }

  @Override
  public void disable() {}

  @Override
  public void stopMotor() {}

  @Override
  public MotorController createFollower(
      String logPrefix, int deviceID, boolean isInvertedFromLeader) {
    return new NullMotorAdapter();
  }

  @Override
  public RelativeEncoder getEncoder() {
    return new RelativeEncoder() {

      @Override
      public void setPosition(double position) {}

      @Override
      public double getPosition() {
        return 0;
      }

      @Override
      public double getVelocity() {
        return 0;
      }

      @Override
      public void reset() {}
    };
  }

  @Override
  public LimitSwitch getForwardLimitSwitch() {
    return new LimitSwitch() {

      @Override
      public boolean isPressed() {
        return false;
      }
    };
  }

  @Override
  public LimitSwitch getReverseLimitSwitch() {
    return getForwardLimitSwitch();
  }

  @Override
  public void setIdleMode(MotorIdleMode idleMode) {}

  @Override
  public void logTelemetry() {}
}
