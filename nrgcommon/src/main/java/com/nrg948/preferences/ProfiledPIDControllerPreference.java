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
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Preferences;

/**
 * Manages a {@link ProfiledPIDController} whose gains are stored in WPILib {@link Preferences}.
 * This class provides a preference-backed wrapper around a Profiled PID controller so that kP, kI,
 * and kD can be tuned at runtime without recompiling code.
 *
 * <p>The internal {@link ProfiledPIDController} instance is configured using the stored preference
 * values, allowing this class to be used anywhere a regular {@code ProfiledPIDController} would be
 * used, while automatically persisting and retrieving gains from the robot preferences. In
 * addition, it implements {@link Sendable} so that its parameters can be exposed to dashboards and
 * other tools.
 */
public class ProfiledPIDControllerPreference extends PreferenceValue implements Sendable {
  private static final double DEFAULT_PERIOD = 0.02;

  private final double defaultP;
  private final double defaultI;
  private final double defaultD;
  private final ProfiledPIDController controller;

  /**
   * Creates a new ProfiledPIDControllerPreference with the given group, name, default PID gains,
   * and motion profile constraints. Uses a default period of 0.02 seconds.
   *
   * @param group The preference group.
   * @param name The preference name.
   * @param defaultP The default P gain.
   * @param defaultI The default I gain.
   * @param defaultD The default D gain.
   * @param constraints The motion profile constraints.
   */
  public ProfiledPIDControllerPreference(
      String group,
      String name,
      double defaultP,
      double defaultI,
      double defaultD,
      TrapezoidProfile.Constraints constraints) {
    this(group, name, defaultP, defaultI, defaultD, constraints, DEFAULT_PERIOD);
  }

  /**
   * Creates a new ProfiledPIDControllerPreference with the given group, name, default PID gains,
   * motion profile constraints, and period.
   *
   * @param group The preference group.
   * @param name The preference name.
   * @param defaultP The default P gain.
   * @param defaultI The default I gain.
   * @param defaultD The default D gain.
   * @param constraints The motion profile constraints.
   * @param period The period for this controller.
   */
  public ProfiledPIDControllerPreference(
      String group,
      String name,
      double defaultP,
      double defaultI,
      double defaultD,
      TrapezoidProfile.Constraints constraints,
      double period) {
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
    this.controller = new ProfiledPIDController(p, i, d, constraints, period);
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
   * Sets the PID gains.
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

  /** {@return the velocity and acceleration constraints for this controller} */
  public TrapezoidProfile.Constraints getConstraints() {
    return controller.getConstraints();
  }

  /**
   * Sets the velocity and acceleration constraints for this controller.
   *
   * @param constraints The constraints to set.
   */
  public void setConstraints(TrapezoidProfile.Constraints constraints) {
    controller.setConstraints(constraints);
  }

  /** {@return the period of this controller} */
  public double getPeriod() {
    return controller.getPeriod();
  }

  /**
   * Enables continuous input for this controller.
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
   * Sets the tolerance for this controller.
   *
   * @param errorTolerance The error which is considered acceptable.
   */
  public void setTolerance(double errorTolerance) {
    controller.setTolerance(errorTolerance);
  }

  /**
   * Sets the tolerance for this controller.
   *
   * @param errorTolerance The error which is considered acceptable.
   * @param errorDerivativeTolerance The error derivative which is considered acceptable.
   */
  public void setTolerance(double errorTolerance, double errorDerivativeTolerance) {
    controller.setTolerance(errorTolerance, errorDerivativeTolerance);
  }

  /** {@return the position tolerance for this controller} */
  public double getPositionTolerance() {
    return controller.getPositionTolerance();
  }

  /** {@return the velocity tolerance for this controller} */
  public double getVelocityTolerance() {
    return controller.getVelocityTolerance();
  }

  /** {@return the current setpoint of this controller} */
  public TrapezoidProfile.State getSetpoint() {
    return controller.getSetpoint();
  }

  /**
   * Returns whether this controller is at its setpoint.
   *
   * @return True if this controller is at its setpoint, false otherwise.
   */
  public boolean atSetpoint() {
    return controller.atSetpoint();
  }

  /**
   * Sets the goal for this controller.
   *
   * @param position The goal position to set.
   */
  public void setGoal(double position) {
    controller.setGoal(position);
  }

  /**
   * Sets the goal for this controller.
   *
   * @param goal The goal to set.
   */
  public void setGoal(TrapezoidProfile.State goal) {
    controller.setGoal(goal);
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

  /**
   * Resets the previous error and integral term.
   *
   * @param measuredPosition The current measured position of the system.
   */
  public void reset(double measuredPosition) {
    controller.reset(measuredPosition);
  }

  /**
   * Resets the previous error and integral term.
   *
   * @param state The current measured state of the system.
   */
  public void reset(TrapezoidProfile.State state) {
    controller.reset(state);
  }

  /**
   * Resets the previous error and integral term.
   *
   * @param measuredPosition The current measured position of the system.
   * @param measuredVelocity The current measured velocity of the system.
   */
  public void reset(double measuredPosition, double measuredVelocity) {
    controller.reset(measuredPosition, measuredVelocity);
  }

  /** {@return the position error for this controller} */
  public double getPositionError() {
    return controller.getPositionError();
  }

  /** {@return the velocity error for this controller} */
  public double getVelocityError() {
    return controller.getVelocityError();
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
