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
import com.ctre.phoenix6.signals.ForwardLimitValue;
import com.ctre.phoenix6.signals.ReverseLimitValue;
import com.nrg948.sensor.LimitSwitch;

/**
 * A limit switch implementation based on the CTR Electronics TalonFX motor controller.
 *
 * @param <T> The type of enum representing the limit switch signal, either {@link
 *     ForwardLimitValue} or {@link ReverseLimitValue}.
 */
public final class TalonFXLimitSwitchAdapter<T extends Enum<T>> implements LimitSwitch {
  private final StatusSignal<T> limitSwitch;
  private final T pressedState;

  /**
   * Creates a TalonFXLimitSwitchAdapter for a forward limit switch signal and its pressed state.
   *
   * @param limitSwitch The forward limit switch signal.
   * @param pressedState The state of the forward limit switch when it is pressed.
   * @return A new instance of TalonFXLimitSwitchAdapter.
   */
  public static TalonFXLimitSwitchAdapter<ForwardLimitValue> of(
      StatusSignal<ForwardLimitValue> limitSwitch, ForwardLimitValue pressedState) {
    return new TalonFXLimitSwitchAdapter<>(limitSwitch, pressedState);
  }

  /**
   * Creates a TalonFXLimitSwitchAdapter for a reverse limit switch signal and its pressed state.
   *
   * @param limitSwitch The reverse limit switch signal.
   * @param pressedState The state of the reverse limit switch when it is pressed.
   * @return A new instance of TalonFXLimitSwitchAdapter.
   */
  public static TalonFXLimitSwitchAdapter<ReverseLimitValue> of(
      StatusSignal<ReverseLimitValue> limitSwitch, ReverseLimitValue pressedState) {
    return new TalonFXLimitSwitchAdapter<>(limitSwitch, pressedState);
  }

  /**
   * Constructs a TalonFXLimitSwitchAdapter.
   *
   * @param limitSwitch The TalonFX limit switch signal.
   * @param pressedState The state of the limit switch when it is pressed.
   */
  private TalonFXLimitSwitchAdapter(StatusSignal<T> limitSwitch, T pressedState) {
    this.limitSwitch = limitSwitch;
    this.pressedState = pressedState;
  }

  @Override
  public boolean isPressed() {
    return limitSwitch.refresh().getValue() == pressedState;
  }
}
