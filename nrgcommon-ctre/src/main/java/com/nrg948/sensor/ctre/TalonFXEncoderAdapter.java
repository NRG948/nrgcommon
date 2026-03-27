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
import com.ctre.phoenix6.hardware.TalonFX;
import com.nrg948.sensor.RelativeEncoder;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;

/** A relative encoder implementation based on the CTR Electronics TalonFX motor controller. */
public final class TalonFXEncoderAdapter implements RelativeEncoder {
  private final TalonFX talonFX;
  private final StatusSignal<Angle> position;
  private final StatusSignal<AngularVelocity> velocity;
  private final double distancePerRotation;

  /**
   * Constructs a TalonFXEncoderAdapter.
   *
   * @param controller The TalonFX object containing the relative encoder to adapt.
   * @param distancePerRotation The distance the attached mechanism moves per rotation of the motor
   *     output shaft.
   *     <p>The unit of measure depends on the mechanism. For a mechanism that produces linear
   *     motion, the unit is typically in meters. For a mechanism that produces rotational motion,
   *     the unit is typically in radians.
   */
  public TalonFXEncoderAdapter(TalonFX controller, double distancePerRotation) {
    talonFX = controller;
    position = controller.getPosition();
    velocity = controller.getVelocity();
    this.distancePerRotation = distancePerRotation;
    reset();
  }

  @Override
  public void setPosition(double position) {
    // The TalonFX encoder position is in units of rotations, so we need to divide by the
    // distance per rotation to get the position in encoder units.
    talonFX.setPosition(position / distancePerRotation);
  }

  @Override
  public double getPosition() {
    // The TalonFX encoder position is in units of rotations, so we need to multiply by the
    // distance per rotation to get the position in the correct units.
    return position.refresh().getValueAsDouble() * distancePerRotation;
  }

  @Override
  public double getVelocity() {
    // The TalonFX encoder velocity is in units of rotations per second, so we need to multiply by
    // the distance per rotation to get the velocity in the correct units.
    return velocity.refresh().getValueAsDouble() * distancePerRotation;
  }

  @Override
  public void reset() {
    talonFX.setPosition(0);
  }
}
