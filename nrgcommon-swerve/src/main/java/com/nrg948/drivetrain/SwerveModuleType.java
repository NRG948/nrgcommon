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
package com.nrg948.drivetrain;

import static com.nrg948.actuator.MotorDirection.CLOCKWISE_POSITIVE;
import static com.nrg948.actuator.MotorDirection.COUNTER_CLOCKWISE_POSITIVE;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;

import com.nrg948.actuator.MotorDirection;
import com.nrg948.actuator.Motors;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.units.measure.LinearVelocity;

/**
 * A enum representing the properties on a specific swerve drive module.
 *
 * <p>The parameters for the MK4 modules are taken from <a
 * href="https://www.swervedrivespecialties.com/products/mk4-swerve-module">MK4 Swerve Module</a>.
 *
 * <p>The parameters for the MK4i modules are taken from <a
 * href="https://www.swervedrivespecialties.com/products/mk4i-swerve-module">MK4i Swerve Module</a>
 * and <a
 * href="https://www.swervedrivespecialties.com/collections/mk4i-parts/products/kit-adapter-16t-drive-pinion-gear-mk4i">Kit,
 * Adapter, 16T Drive Pinion Gear (MK4i)</a>.
 *
 * <p>The calculations for the theoretical maximum speeds and acceleration are taken from the <a
 * href=
 * "https://www.chiefdelphi.com/uploads/default/original/3X/f/7/f79d24101e6f1487e76099774e4ba60683e86cda.pdf">
 * FRC Drivetrain Characterization</a> paper by Noah Gleason and Eli Barnett of FRC Team 449 - The
 * Blair Robot Project.
 */
public enum SwerveModuleType {

  /** An MK4 Swerve Module in the L1 configuration. */
  MK4_L1(8.14, 12.8, COUNTER_CLOCKWISE_POSITIVE),

  /** An MK4 Swerve Module in the L2 configuration. */
  MK4_L2(6.75, 12.8, COUNTER_CLOCKWISE_POSITIVE),

  /** An MK4 Swerve Module in the L3 configuration. */
  MK4_L3(6.12, 12.8, COUNTER_CLOCKWISE_POSITIVE),

  /** An MK4 Swerve Module in the L4 configuration. */
  MK4_L4(5.14, 12.8, COUNTER_CLOCKWISE_POSITIVE),

  /** An MK4I Swerve Module in the L1 configuration. */
  MK4I_L1(8.14, 150.0 / 7.0, CLOCKWISE_POSITIVE),

  /** An MK4I Swerve Module in the L2 configuration. */
  MK4I_L2(6.75, 150.0 / 7.0, CLOCKWISE_POSITIVE),

  /** An MK4I Swerve Module in the L2+ configuration. */
  MK4I_L2_PLUS(5.9, 150.0 / 7.0, CLOCKWISE_POSITIVE),

  /** An MK4I Swerve Module in the L3 configuration. */
  MK4I_L3(6.12, 150.0 / 7.0, CLOCKWISE_POSITIVE);

  private final double driveGearRatio;
  private final double steeringGearRatio;
  private final MotorDirection steeringDirection;

  /**
   * Constructs an instance of this enum.
   *
   * @param driveGearRatio The drive gear ratio.
   * @param steeringGearRatio The steering gear ratio.
   * @param steeringDirection The direction the steering motor must rotate when a positive voltage
   *     is applied to rotate the wheel in the counter-clockwise direction.
   */
  SwerveModuleType(
      double driveGearRatio,
      double steeringGearRatio,
      MotorDirection steeringDirection) {
    this.driveGearRatio = driveGearRatio;
    this.steeringGearRatio = steeringGearRatio;
    this.steeringDirection = steeringDirection;
  }

  /**
   * Returns the drive gear ratio.
   *
   * @return The drive gear ratio.
   */
  public double getDriveGearRatio() {
    return this.driveGearRatio;
  }

  /**
   * Returns the steering gear ratio.
   *
   * @return The steering gear ratio.
   */
  public double getSteeringGearRatio() {
    return this.steeringGearRatio;
  }

  /**
   * Returns the direction the steering motor must rotate when a positive voltage is applied to
   * rotate the wheel in the counter-clockwise direction.
   *
   * @return The direction the steering motor must rotate when a positive voltage is applied to
   *     rotate the wheel in the counter-clockwise direction.
   */
  public MotorDirection getSteeringDirection() {
    return this.steeringDirection;
  }

  /**
   * Returns the theoretical maximum drive speed in m/s when using the specified motor.
   *
   * @param motor The motor parameters.
   * @param wheelDiameter The wheel diameter.
   * @return The theoretical maximum drive speed.
   */
  public LinearVelocity calculateMaxDriveSpeed(Motors motor, Distance wheelDiameter) {
    return MetersPerSecond.of((motor.getFreeSpeedRPM() * wheelDiameter.in(Units.Meters) * Math.PI) / (60.0 * this.driveGearRatio));
  }

  /**
   * Returns the theoretical maximum drive acceleration in m/s^2 when using the specified motor.
   *
   * @param motor The motor parameters.
   * @param wheelDiameter The wheel diameter.
   * @param robotMass the total robot mass in Kg including bumpers and battery.
   * @return The theoretical maximum drive acceleration.
   */
  public LinearAcceleration calculateMaxDriveAcceleration(Motors motor, Distance wheelDiameter, double robotMass) {
    return MetersPerSecondPerSecond.of((2 * 4 * motor.getStallTorque() * this.driveGearRatio)
        / (wheelDiameter.in(Units.Meters) * robotMass));
  }

  /**
   * Returns the theoretical maximum steering speed in rad/s when using the specified motor.
   *
   * @param motor The motor parameters.
   * @return The theoretical maximum drive speed.
   */
  public double calculateMaxSteeringSpeed(Motors motor) {
    return (motor.getFreeSpeedRPM() * 2 * Math.PI) / (60.0 * this.steeringGearRatio);
  }

  /**
   * Returns the theoretical maximum drive acceleration in rad/s^2 when using the specified motor.
   *
   * @param motor The motor parameters.
   * @param robotMass the total robot mass in Kg including bumpers and battery.
   * @return The theoretical maximum drive acceleration.
   */
  public double calculateMaxSteeringAcceleration(Motors motor, double robotMass) {
    return (2 * 4 * motor.getStallTorque() * this.steeringGearRatio * 2 * Math.PI) / robotMass;
  }
}
