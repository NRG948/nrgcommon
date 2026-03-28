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
package com.nrg948.actuator.ctre;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.ForwardLimitValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.ReverseLimitValue;
import com.nrg948.actuator.MotorController;
import com.nrg948.actuator.MotorDirection;
import com.nrg948.actuator.MotorIdleMode;
import com.nrg948.sensor.LimitSwitch;
import com.nrg948.sensor.RelativeEncoder;
import com.nrg948.sensor.ctre.TalonFXEncoderAdapter;
import com.nrg948.sensor.ctre.TalonFXLimitSwitchAdapter;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;

/** A motor controller implementation based on the CTR Electronics TalonFX controller. */
public final class TalonFXAdapter implements MotorController {
  private static final DataLog LOG = DataLogManager.getLog();

  @SuppressWarnings("unused")
  private final String logPrefix;

  private final TalonFX talonFX;
  private final double distancePerRotation;
  private final MotorOutputConfigs motorOutputConfigs;
  private final StatusSignal<Current> supplyCurrent;
  private final StatusSignal<Current> statorCurrent;
  private final StatusSignal<Temperature> temperature;

  private final DoubleLogEntry logSupplyCurrent;
  private final DoubleLogEntry logStatorCurrent;
  private final DoubleLogEntry logTemperature;

  /**
   * Converts a {@link MotorIdleMode} to the corresponding {@link NeutralModeValue} for TalonFX.
   *
   * @param idleMode The motor idle mode.
   * @return The corresponding TalonFX neutral mode value.
   */
  private static NeutralModeValue convertIdleModeToNeutralMode(MotorIdleMode idleMode) {
    return switch (idleMode) {
      case COAST -> NeutralModeValue.Coast;
      case BRAKE -> NeutralModeValue.Brake;
    };
  }

  /**
   * Converts a {@link MotorDirection} to the corresponding {@link InvertedValue} for TalonFX.
   *
   * @param direction The motor direction.
   * @return The corresponding TalonFX inverted value.
   */
  private static InvertedValue convertDirectionToInvertedValue(MotorDirection direction) {
    return switch (direction) {
      case COUNTER_CLOCKWISE_POSITIVE -> InvertedValue.CounterClockwise_Positive;
      case CLOCKWISE_POSITIVE -> InvertedValue.Clockwise_Positive;
    };
  }

  /**
   * Constructs a TalonFXAdapter.
   *
   * <p>This constructor assumes the {@link TalonFX} object is already configured or will be
   * configured to match the provided motor output configuration by the caller.
   *
   * @param logPrefix The prefix for the log entries.
   * @param talonFX The TalonFX object to adapt.
   * @param motorOutputConfigs The motor output configuration.
   * @param distancePerRotation The distance the attached mechanism moves per rotation of the motor
   *     output shaft.
   *     <p>The unit of measure depends on the mechanism. For a mechanism that produces linear
   *     motion, the unit is typically in meters. For a mechanism that produces rotational motion,
   *     the unit is typically in radians.
   */
  public TalonFXAdapter(
      String logPrefix,
      TalonFX talonFX,
      MotorOutputConfigs motorOutputConfigs,
      double distancePerRotation) {
    this.logPrefix = logPrefix;
    this.talonFX = talonFX;
    this.motorOutputConfigs = motorOutputConfigs;
    this.distancePerRotation = distancePerRotation;
    this.supplyCurrent = talonFX.getSupplyCurrent();
    this.statorCurrent = talonFX.getStatorCurrent();
    this.temperature = talonFX.getDeviceTemp();

    String name = String.format("%s/TalonFX-%d", logPrefix, talonFX.getDeviceID());

    this.logSupplyCurrent = new DoubleLogEntry(LOG, name + "/SupplyCurrent");
    this.logStatorCurrent = new DoubleLogEntry(LOG, name + "/StatorCurrent");
    this.logTemperature = new DoubleLogEntry(LOG, name + "/Temperature");
  }

  /**
   * Constructs a TalonFXAdapter.
   *
   * @param logPrefix The prefix for the log entries.
   * @param talonFX The TalonFX object to adapt.
   * @param direction The direction the motor rotates when a positive voltage is applied.
   * @param idleMode The motor behavior when idle (i.e. brake or coast mode).
   * @param distancePerRotation The distance the attached mechanism moves per rotation of the motor
   *     output shaft.
   *     <p>The unit of measure depends on the mechanism. For a mechanism that produces linear
   *     motion, the unit is typically in meters. For a mechanism that produces rotational motion,
   *     the unit is typically in radians.
   */
  public TalonFXAdapter(
      String logPrefix,
      TalonFX talonFX,
      MotorDirection direction,
      MotorIdleMode idleMode,
      double distancePerRotation) {
    this(logPrefix, talonFX, new MotorOutputConfigs(), distancePerRotation);

    motorOutputConfigs.NeutralMode = convertIdleModeToNeutralMode(idleMode);
    motorOutputConfigs.Inverted = convertDirectionToInvertedValue(direction);

    applyConfig(talonFX, motorOutputConfigs);
  }

  /**
   * Constructs a TalonFXAdapter.
   *
   * @param logPrefix The prefix for the log entries.
   * @param deviceID The device ID of the TalonFX.
   * @param direction The direction the motor rotates when a positive voltage is applied.
   * @param idleMode The motor behavior when idle (i.e. brake or coast mode).
   * @param distancePerRotation The distance the attached mechanism moves per rotation of the motor
   *     output shaft.
   *     <p>The unit of measure depends on the mechanism. For a mechanism that produces linear
   *     motion, the unit is typically in meters. For a mechanism that produces rotational motion,
   *     the unit is typically in radians.
   */
  public TalonFXAdapter(
      String logPrefix,
      int deviceID,
      MotorDirection direction,
      MotorIdleMode idleMode,
      double distancePerRotation) {
    this(
        logPrefix,
        new TalonFX(deviceID, CANBus.roboRIO()),
        direction,
        idleMode,
        distancePerRotation);
  }

  @Override
  public void set(double speed) {
    talonFX.set(speed);
  }

  @Override
  public double get() {
    return talonFX.get();
  }

  @Override
  public void setVoltage(double outputVoltage) {
    talonFX.setVoltage(outputVoltage);
  }

  @Override
  public void setInverted(boolean isInverted) {
    talonFX.getConfigurator().refresh(motorOutputConfigs);
    motorOutputConfigs.Inverted =
        isInverted ? InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive;

    applyConfig(talonFX, motorOutputConfigs);
  }

  @Override
  public boolean getInverted() {
    return motorOutputConfigs.Inverted == InvertedValue.Clockwise_Positive;
  }

  @Override
  public void setIdleMode(MotorIdleMode idleMode) {
    motorOutputConfigs.NeutralMode = convertIdleModeToNeutralMode(idleMode);
    applyConfig(talonFX, motorOutputConfigs);
  }

  @Override
  public void disable() {
    talonFX.disable();
  }

  @Override
  public void stopMotor() {
    talonFX.stopMotor();
  }

  @Override
  public MotorController createFollower(
      String logPrefix, int deviceID, boolean isInvertedFromLeader) {
    TalonFX follower = new TalonFX(deviceID, talonFX.getNetwork());

    // Get the motor output configuration from the leader and apply it to the follower.
    MotorOutputConfigs followerMotorOutputConfigs = new MotorOutputConfigs();

    followerMotorOutputConfigs.Inverted = motorOutputConfigs.Inverted;
    followerMotorOutputConfigs.NeutralMode = motorOutputConfigs.NeutralMode;
    followerMotorOutputConfigs.DutyCycleNeutralDeadband =
        motorOutputConfigs.DutyCycleNeutralDeadband;
    followerMotorOutputConfigs.PeakForwardDutyCycle = motorOutputConfigs.PeakForwardDutyCycle;
    followerMotorOutputConfigs.PeakReverseDutyCycle = motorOutputConfigs.PeakReverseDutyCycle;
    followerMotorOutputConfigs.ControlTimesyncFreqHz = motorOutputConfigs.ControlTimesyncFreqHz;

    applyConfig(follower, followerMotorOutputConfigs);

    // Configure the follower to follow the leader.
    Follower followerConfig =
        new Follower(
            talonFX.getDeviceID(),
            isInvertedFromLeader ? MotorAlignmentValue.Opposed : MotorAlignmentValue.Aligned);

    follower.setControl(followerConfig);

    return new TalonFXAdapter(logPrefix, follower, followerMotorOutputConfigs, distancePerRotation);
  }

  @Override
  public RelativeEncoder getEncoder() {
    return new TalonFXEncoderAdapter(talonFX, distancePerRotation);
  }

  @Override
  public LimitSwitch getForwardLimitSwitch() {
    return new TalonFXLimitSwitchAdapter<ForwardLimitValue>(
        talonFX.getForwardLimit(), ForwardLimitValue.ClosedToGround);
  }

  @Override
  public LimitSwitch getReverseLimitSwitch() {
    return new TalonFXLimitSwitchAdapter<ReverseLimitValue>(
        talonFX.getReverseLimit(), ReverseLimitValue.ClosedToGround);
  }

  @Override
  public void logTelemetry() {
    logSupplyCurrent.append(this.supplyCurrent.refresh().getValueAsDouble());
    logStatorCurrent.append(this.statorCurrent.refresh().getValueAsDouble());
    logTemperature.append(this.temperature.refresh().getValueAsDouble());
  }

  private static void applyConfig(TalonFX talonFX, MotorOutputConfigs motorOutputConfigs) {
    for (int i = 0; i < 5; i++) {
      StatusCode status = talonFX.getConfigurator().apply(motorOutputConfigs);

      if (status.isOK()) {
        return;
      }

      DriverStation.reportError(
          String.format(
              "ERROR: Failed to apply motor output configs of TalonFX ID %d: %s (%s)",
              talonFX.getDeviceID(), status.getDescription(), status.getName()),
          false);
    }
  }

  /**
   * Sets the MotionMagic voltage
   *
   * @param voltage The voltage to set the motor to using MotionMagic control mode.
   *     <p>MotionMagic is a control mode that uses a trapezoidal motion profile to move the motor
   *     to a target position. The voltage parameter is used to set the maximum voltage that can be
   *     applied to the motor during the motion profile. The actual voltage applied to the motor
   *     will be determined by the motion profile and the current position of the motor.
   */
  public void setControl(MotionMagicVoltage voltage) {
    talonFX.setControl(voltage);
  }
}
