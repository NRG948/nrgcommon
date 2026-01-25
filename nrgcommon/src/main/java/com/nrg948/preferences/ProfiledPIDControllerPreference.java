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
package com.nrg948.preferences;

import edu.wpi.first.math.MathSharedStore;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Preferences;

/**
 * Manages a {@link PIDController} whose gains are stored in WPILib {@link Preferences}. This class
 * provides a preference-backed wrapper around a PID controller so that kP, kI, and kD can be tuned
 * at runtime without recompiling code.
 *
 * <p>The internal {@link PIDController} instance is configured using the stored preference values,
 * allowing this class to be used anywhere a regular {@code PIDController} would be used, while
 * automatically persisting and retrieving gains from the robot preferences. In addition, it
 * implements {@link Sendable} so that its parameters can be exposed to dashboards and other tools.
 */
public class ProfiledPIDControllerPreference extends PreferenceValue implements Sendable {
  private final double defaultP;
  private final double defaultI;
  private final double defaultD;
  private final ProfiledPIDController controller;

  public ProfiledPIDControllerPreference(
      String group,
      String name,
      double defaultP,
      double defaultI,
      double defaultD,
      TrapezoidProfile.Constraints constraints) {
    super(group, name);
    this.defaultP = defaultP;
    this.defaultI = defaultI;
    this.defaultD = defaultD;
    this.controller = new ProfiledPIDController(defaultP, defaultI, defaultD, constraints);

    Preferences.initDouble(getKey() + "/kP", defaultP);
    Preferences.initDouble(getKey() + "/kI", defaultI);
    Preferences.initDouble(getKey() + "/kD", defaultD);
  }

  /**
   * Returns the default P value.
   *
   * @return The default P value.
   */
  public double getDefaultP() {
    return defaultP;
  }

  /**
   * Returns the current P value.
   *
   * @return The current P value.
   */
  public double getP() {
    return Preferences.getDouble(getKey() + "/kP", defaultP);
  }

  /**
   * Sets the P value.
   *
   * @param p The P value to set.
   */
  public void setP(double p) {
    controller.setP(p);
    Preferences.setDouble(getKey() + "/kP", p);
  }

  /**
   * Returns the default I value.
   *
   * @return The default I value.
   */
  public double getDefaultI() {
    return defaultI;
  }

  /**
   * Returns the current I value.
   *
   * @return The current I value.
   */
  public double getI() {
    return Preferences.getDouble(getKey() + "/kI", defaultI);
  }

  /**
   * Sets the I value.
   *
   * @param i The I value to set.
   */
  public void setI(double i) {
    controller.setI(i);
    Preferences.setDouble(getKey() + "/kI", i);
  }

  /**
   * Returns the default D value.
   *
   * @return The default D value.
   */
  public double getDefaultD() {
    return defaultD;
  }

  /**
   * Returns the current D value.
   *
   * @return The current D value.
   */
  public double getD() {
    return Preferences.getDouble(getKey() + "/kD", defaultD);
  }

  /**
   * Sets the D value.
   *
   * @param d The D value to set.
   */
  public void setD(double d) {
    controller.setD(d);
    Preferences.setDouble(getKey() + "/kD", d);
  }

  /**
   * Returns whether the PID controller is at its setpoint.
   *
   * @return True if the PID controller is at its setpoint, false otherwise.
   */
  public boolean atSetpoint() {
    return controller.atSetpoint();
  }

  /**
   * Calculates the output of the PID controller for the given measurement.
   *
   * @param measurement The current measurement.
   * @return The output of the PID controller.
   */
  public double calculate(double measurement) {
    return controller.calculate(measurement);
  }

  /**
   * Calculates the output of the PID controller for the given measurement and setpoint.
   *
   * @param measurement The current measurement.
   * @param setpoint The desired setpoint.
   * @return The output of the PID controller.
   */
  public double calculate(double measurement, double setpoint) {
    return controller.calculate(measurement, setpoint);
  }

  /**
   * Enables continuous input for the PID controller.
   *
   * @param minimumInput The minimum input value.
   * @param maximumInput The maximum input value.
   */
  public void enableContinuousInput(double minimumInput, double maximumInput) {
    controller.enableContinuousInput(minimumInput, maximumInput);
  }

  /** Disables continuous input for the PID controller. */
  public void disableContinuousInput() {
    controller.disableContinuousInput();
  }

  /** Resets the PID controller. */
  public void reset(double measuredPosition) {
    controller.reset(measuredPosition);
  }

  /** Resets the PID controller. */
  public void reset(TrapezoidProfile.State state) {
    controller.reset(state);
  }

  /** Resets the PID controller. */
  public void reset(double measuredPosition, double measuredVelocity) {
    controller.reset(measuredPosition, measuredVelocity);
  }

  /**
   * Sets the tolerance for the PID controller.
   *
   * @param errorTolerance The error which is considered acceptable.
   */
  public void setTolerance(double errorTolerance) {
    controller.setTolerance(errorTolerance);
  }

  /**
   * Sets the tolerance for the PID controller.
   *
   * @param errorTolerance The error which is considered acceptable.
   * @param errorDerivativeTolerance The error derivative which is considered acceptable.
   */
  public void setTolerance(double errorTolerance, double errorDerivativeTolerance) {
    controller.setTolerance(errorTolerance, errorDerivativeTolerance);
  }

  @Override
  public void accept(PreferenceValueVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("PIDController");
    builder.addDoubleProperty("p", this::getP, this::setP);
    builder.addDoubleProperty("i", this::getI, this::setI);
    builder.addDoubleProperty("d", this::getD, this::setD);
    builder.addDoubleProperty(
        "izone",
        controller::getIZone,
        (double toSet) -> {
          try {
            controller.setIZone(toSet);
          } catch (IllegalArgumentException e) {
            MathSharedStore.reportError("IZone must be a non-negative number!", e.getStackTrace());
          }
        });
    builder.addDoubleProperty("setpoint", () -> 0, null);
    builder.addDoubleProperty("measurement", () -> 0, null);
    builder.addDoubleProperty("error", () -> 0, null);
    builder.addDoubleProperty("error derivative", () -> 0, null);
    builder.addDoubleProperty("previous error", () -> 0, null);
    builder.addDoubleProperty("total error", controller::getAccumulatedError, null);
  }
}
