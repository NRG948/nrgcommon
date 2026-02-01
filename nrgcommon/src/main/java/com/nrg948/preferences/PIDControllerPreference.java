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
public class PIDControllerPreference extends PreferenceValue implements Sendable {
  private static final double DEFAULT_PERIOD = 0.02;

  private final double defaultP;
  private final double defaultI;
  private final double defaultD;
  private final PIDController controller;

  /**
   * Creates a new PIDControllerPreference with the given group, name, and default PID gains. Uses a
   * default period of 0.02 seconds.
   *
   * @param group The preference group.
   * @param name The preference name.
   * @param defaultP The default P gain.
   * @param defaultI The default I gain.
   * @param defaultD The default D gain.
   */
  public PIDControllerPreference(
      String group, String name, double defaultP, double defaultI, double defaultD) {
    this(group, name, defaultP, defaultI, defaultD, DEFAULT_PERIOD);
  }

  /**
   * Creates a new PIDControllerPreference with the given group, name, default PID gains, and
   * period.
   *
   * @param group The preference group.
   * @param name The preference name.
   * @param defaultP The default P gain.
   * @param defaultI The default I gain.
   * @param defaultD The default D gain.
   * @param period The period for this controller.
   */
  public PIDControllerPreference(
      String group, String name, double defaultP, double defaultI, double defaultD, double period) {
    super(group, name);
    this.defaultP = defaultP;
    this.defaultI = defaultI;
    this.defaultD = defaultD;

    // Initialize preference entries with defaults if they do not already exist.
    Preferences.initDouble(getKey() + "/kP", defaultP);
    Preferences.initDouble(getKey() + "/kI", defaultI);
    Preferences.initDouble(getKey() + "/kD", defaultD);

    // Read the effective gains from preferences (existing tuned values or defaults).
    double p = Preferences.getDouble(getKey() + "/kP", defaultP);
    double i = Preferences.getDouble(getKey() + "/kI", defaultI);
    double d = Preferences.getDouble(getKey() + "/kD", defaultD);

    // Construct the controller using the stored preference values.
    this.controller = new PIDController(p, i, d, period);
  }

  /** {@return the default proportional gain} */
  public double getDefaultP() {
    return defaultP;
  }

  /** {@return the current proportional gain} */
  public double getP() {
    return Preferences.getDouble(getKey() + "/kP", defaultP);
  }

  /**
   * Sets the proportional gain.
   *
   * @param p The proportional gain to set.
   */
  public void setP(double p) {
    controller.setP(p);
    Preferences.setDouble(getKey() + "/kP", p);
  }

  /** {@return the default integral gain} */
  public double getDefaultI() {
    return defaultI;
  }

  /** {@return the current integral gain} */
  public double getI() {
    return Preferences.getDouble(getKey() + "/kI", defaultI);
  }

  /**
   * Sets the integral gain.
   *
   * @param i The integral gain to set.
   */
  public void setI(double i) {
    controller.setI(i);
    Preferences.setDouble(getKey() + "/kI", i);
  }

  /** {@return the default derivative gain} */
  public double getDefaultD() {
    return defaultD;
  }

  /** {@return the current derivative gain} */
  public double getD() {
    return Preferences.getDouble(getKey() + "/kD", defaultD);
  }

  /**
   * Sets the derivative gain.
   *
   * @param d The derivative gain to set.
   */
  public void setD(double d) {
    controller.setD(d);
    Preferences.setDouble(getKey() + "/kD", d);
  }

  /**
   * Sets the PID coefficients.
   *
   * @param p The proportional gain to set.
   * @param i The integral gain to set.
   * @param d The derivative gain to set.
   */
  public void setPID(double p, double i, double d) {
    controller.setPID(p, i, d);
    Preferences.setDouble(getKey() + "/kP", p);
    Preferences.setDouble(getKey() + "/kI", i);
    Preferences.setDouble(getKey() + "/kD", d);
  }

  /** {@return the maximum magnitude of the error to allow integral control} */
  public double getIZone() {
    return controller.getIZone();
  }

  /**
   * Sets the maximum magnitude of the error to allow integral control.
   *
   * @param iZone The IZone to set.
   */
  public void setIZone(double iZone) {
    controller.setIZone(iZone);
  }

  /**
   * Sets the minimum and maximum contributions of the integral term.
   *
   * @param minimumIntegral The minimum integral value.
   * @param maximumIntegral The maximum integral value.
   */
  public void setIntegratorRange(double minimumIntegral, double maximumIntegral) {
    controller.setIntegratorRange(minimumIntegral, maximumIntegral);
  }

  /** {@return the period of this controller} */
  public double getPeriod() {
    return controller.getPeriod();
  }

  /**
   * Enables continuous input for this controller.
   *
   * <p>Rather than use the minimum and maximum input as constraints, the controller considers them
   * to be the endpoints of a continuous range. This is useful for circular inputs, such as angles.
   *
   * @param minimumInput The minimum input value.
   * @param maximumInput The maximum input value.
   */
  public void enableContinuousInput(double minimumInput, double maximumInput) {
    controller.enableContinuousInput(minimumInput, maximumInput);
  }

  /** Disables continuous input for this controller. */
  public void disableContinuousInput() {
    controller.disableContinuousInput();
  }

  /**
   * Returns whether continuous input is enabled for this controller.
   *
   * @return Returns true if continuous input is enabled, false otherwise.
   */
  public boolean isContinuousInputEnabled() {
    return controller.isContinuousInputEnabled();
  }

  /** Resets this controller. */
  public void reset() {
    controller.reset();
  }

  /**
   * Sets the error which is considered tolerable for use with {@link #atSetpoint()}.
   *
   * @param errorTolerance The error which is tolerable.
   */
  public void setTolerance(double errorTolerance) {
    controller.setTolerance(errorTolerance);
  }

  /**
   * Sets the error which is considered tolerable for use with {@link #atSetpoint()}.
   *
   * @param errorTolerance The error which is tolerable.
   * @param errorDerivativeTolerance The error derivative which is tolerable.
   */
  public void setTolerance(double errorTolerance, double errorDerivativeTolerance) {
    controller.setTolerance(errorTolerance, errorDerivativeTolerance);
  }

  /** {@return the error tolerance of this controller} */
  public double getErrorTolerance() {
    return controller.getErrorTolerance();
  }

  /** {@return the error derivative tolerance of this controller} */
  public double getErrorDerivativeTolerance() {
    return controller.getErrorDerivativeTolerance();
  }

  /** {@return the current setpoint of this controller} */
  public double getSetpoint() {
    return controller.getSetpoint();
  }

  /**
   * Returns whether this controller is at its setpoint.
   *
   * @return Returns true if this controller is at its setpoint, false otherwise.
   */
  public boolean atSetpoint() {
    return controller.atSetpoint();
  }

  /**
   * Sets the setpoint for this controller.
   *
   * @param setpoint The setpoint to set.
   */
  public void setSetpoint(double setpoint) {
    controller.setSetpoint(setpoint);
  }

  /**
   * Calculates the output of this controller for the given measurement.
   *
   * @param measurement The current measurement.
   * @return The output of this controller.
   */
  public double calculate(double measurement) {
    return controller.calculate(measurement);
  }

  /**
   * Calculates the output of this controller for the given measurement and setpoint.
   *
   * @param measurement The current measurement.
   * @param setpoint The desired setpoint.
   * @return The output of this controller.
   */
  public double calculate(double measurement, double setpoint) {
    return controller.calculate(measurement, setpoint);
  }

  /** {@return the current error of this controller} */
  public double getError() {
    return controller.getError();
  }

  /** {@return the current accumulated error used in the integral calculation of this controller} */
  public double getAccumulatedError() {
    return controller.getAccumulatedError();
  }

  /** {@return the current error derivative of this controller} */
  public double getErrorDerivative() {
    return controller.getErrorDerivative();
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
    builder.addDoubleProperty("setpoint", controller::getSetpoint, controller::setSetpoint);
    builder.addDoubleProperty("measurement", () -> 0, null);
    builder.addDoubleProperty("error", controller::getError, null);
    builder.addDoubleProperty("error derivative", controller::getErrorDerivative, null);
    builder.addDoubleProperty("previous error", () -> 0, null);
    builder.addDoubleProperty("total error", controller::getAccumulatedError, null);
  }
}
