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
package com.nrg948.sensor.ctre;

import com.ctre.phoenix6.StatusSignal;
import com.nrg948.sensor.LimitSwitch;

/** A limit switch implementation based on the CTR Electronics TalonFX motor controller. */
public final class TalonFXLimitSwitchAdapter<T extends Enum<T>> implements LimitSwitch {
  private final StatusSignal<T> limitSwitch;
  private final T pressedState;

  /**
   * Constructs a TalonFXLimitSwitchAdapter.
   *
   * @param limitSwitch The TalonFX limit switch signal.
   * @param pressedState The state of the limit switch when it is pressed.
   */
  public TalonFXLimitSwitchAdapter(StatusSignal<T> limitSwitch, T pressedState) {
    this.limitSwitch = limitSwitch;
    this.pressedState = pressedState;
  }

  @Override
  public boolean isPressed() {
    return limitSwitch.refresh().getValue() == pressedState;
  }
}
