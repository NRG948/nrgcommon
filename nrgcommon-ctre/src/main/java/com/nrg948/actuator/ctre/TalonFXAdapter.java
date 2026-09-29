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

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.ParentConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.ForwardLimitValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.ReverseLimitValue;
import com.nrg948.actuator.MotorConfig;
import com.nrg948.actuator.MotorConfigException;
import com.nrg948.actuator.MotorController;
import com.nrg948.actuator.MotorCurrentConfig;
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
import java.util.function.Function;

/** A motor controller implementation based on the CTR Electronics TalonFX controller. */
public final class TalonFXAdapter implements MotorController {
  private static final int NUM_RETRIES = 5;

  private static final DataLog LOG = DataLogManager.getLog();

  private final TalonFX talonFX;
  private double distancePerRotation;
  private final StatusSignal<Current> supplyCurrent;
  private final StatusSignal<Current> statorCurrent;
  private final StatusSignal<Temperature> temperature;

  private final DoubleLogEntry logSupplyCurrent;
  private final DoubleLogEntry logStatorCurrent;
  private final DoubleLogEntry logTemperature;

  private TalonFXEncoderAdapter encoderAdapter;
  private TalonFXLimitSwitchAdapter<ForwardLimitValue> forwardLimitSwitchAdapter;
  private TalonFXLimitSwitchAdapter<ReverseLimitValue> reverseLimitSwitchAdapter;

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
   */
  public TalonFXAdapter(String logPrefix, TalonFX talonFX) {
    this.talonFX = talonFX;
    this.supplyCurrent = talonFX.getSupplyCurrent();
    this.statorCurrent = talonFX.getStatorCurrent();
    this.temperature = talonFX.getDeviceTemp();

    BaseStatusSignal.setUpdateFrequencyForAll(10.0, this.supplyCurrent, this.statorCurrent);

    String name = String.format("%s/TalonFX-%d", logPrefix, talonFX.getDeviceID());

    this.logSupplyCurrent = new DoubleLogEntry(LOG, name + "/SupplyCurrent");
    this.logStatorCurrent = new DoubleLogEntry(LOG, name + "/StatorCurrent");
    this.logTemperature = new DoubleLogEntry(LOG, name + "/Temperature");
  }

  /**
   * Constructs a TalonFXAdapter.
   *
   * @param logPrefix The prefix for the log entries.
   * @param deviceID The device ID of the TalonFX.
   */
  public TalonFXAdapter(String logPrefix, int deviceID) {
    this(logPrefix, new TalonFX(deviceID, CANBus.roboRIO()));
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
    try {
      var motorOutputConfigs = getMotorOutputConfig();
      motorOutputConfigs.Inverted =
          isInverted ? InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive;
      applyMotorOutputConfig(motorOutputConfigs);
    } catch (MotorConfigException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean getInverted() {
    MotorOutputConfigs motorOutputConfigs;
    try {
      motorOutputConfigs = getMotorOutputConfig();
      return motorOutputConfigs.Inverted == InvertedValue.Clockwise_Positive;
    } catch (MotorConfigException e) {

    }

    return false;
  }

  @Override
  public void setIdleMode(MotorIdleMode idleMode) {
    try {
      var motorOutputConfigs = getMotorOutputConfig();
      motorOutputConfigs.NeutralMode = convertIdleModeToNeutralMode(idleMode);
      applyMotorOutputConfig(motorOutputConfigs);
    } catch (MotorConfigException e) {

    }
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
      String logPrefix, int deviceID, boolean isInvertedFromLeader) throws MotorConfigException {
    TalonFX follower = new TalonFX(deviceID, talonFX.getNetwork());
    TalonFXAdapter followerAdapter = new TalonFXAdapter(logPrefix, follower);

    // Get the motor output configuration from the leader and apply it to the follower.
    followerAdapter.applyMotorOutputConfig(getMotorOutputConfig());

    // Configure the follower to follow the leader.
    Follower followerConfig =
        new Follower(
            talonFX.getDeviceID(),
            isInvertedFromLeader ? MotorAlignmentValue.Opposed : MotorAlignmentValue.Aligned);

    follower.setControl(followerConfig);

    return followerAdapter;
  }

  @Override
  public RelativeEncoder getEncoder() {
    if (encoderAdapter == null) {
      encoderAdapter = new TalonFXEncoderAdapter(talonFX, distancePerRotation);
    }

    return encoderAdapter;
  }

  @Override
  public LimitSwitch getForwardLimitSwitch() {
    if (forwardLimitSwitchAdapter == null) {
      forwardLimitSwitchAdapter =
          new TalonFXLimitSwitchAdapter<ForwardLimitValue>(
              talonFX.getForwardLimit(), ForwardLimitValue.ClosedToGround);
    }

    return forwardLimitSwitchAdapter;
  }

  @Override
  public LimitSwitch getReverseLimitSwitch() {
    if (reverseLimitSwitchAdapter == null) {
      reverseLimitSwitchAdapter =
          new TalonFXLimitSwitchAdapter<ReverseLimitValue>(
              talonFX.getReverseLimit(), ReverseLimitValue.ClosedToGround);
    }
    return reverseLimitSwitchAdapter;
  }

  @Override
  public void logTelemetry() {
    logSupplyCurrent.append(this.supplyCurrent.refresh().getValueAsDouble());
    logStatorCurrent.append(this.statorCurrent.refresh().getValueAsDouble());
    logTemperature.append(this.temperature.refresh().getValueAsDouble());
  }

  /**
   * Sets the MotionMagic voltage
   *
   * @param voltage The voltage and optional feedforward to set for the MotionMagic control mode.
   */
  public void setControl(MotionMagicVoltage voltage) {
    talonFX.setControl(voltage);
  }

  /**
   * Sets the MotionMagic velocity
   *
   * @param velocity The velocity and optional feedforward to set for the MotionMagic control mode.
   */
  public void setControl(MotionMagicVelocityVoltage velocity) {
    talonFX.setControl(velocity);
  }

  /**
   * Gets a configuration from the TalonFX with retries and error handling.
   *
   * @param configClass The class of the configuration to retrieve.
   * @param refresher A function that refreshes the configuration.
   * @param <T> The type of the configuration.
   * @return The retrieved configuration.
   * @throws MotorConfigException If the configuration cannot be retrieved.
   */
  private <T extends ParentConfiguration> T getConfig(
      Class<T> configClass, Function<T, StatusCode> refresher) throws MotorConfigException {
    T config;
    StatusCode status = StatusCode.OK;

    try {
      config = configClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      String errorMessage =
          String.format(
              "Failed to create instance of config class %s: %s",
              configClass.getSimpleName(), e.getMessage());
      DriverStation.reportError(errorMessage, true);
      throw new MotorConfigException(errorMessage, e);
    }

    for (int i = 0; i < NUM_RETRIES; i++) {
      status = refresher.apply(config);
      if (status.isOK()) {
        return config;
      }
    }

    String errorMessage =
        String.format(
            "Failed to get %s from ID %d: %s (%s)",
            configClass.getSimpleName(),
            talonFX.getDeviceID(),
            status.getDescription(),
            status.getName());

    DriverStation.reportError(errorMessage, true);

    throw new MotorConfigException(errorMessage);
  }

  /**
   * Applies a configuration to the TalonFX with retries and error handling.
   *
   * @param config The configuration to apply.
   * @param applier A function that applies the configuration.
   * @param <T> The type of the configuration.
   * @throws MotorConfigException If the configuration cannot be applied.
   */
  private <T extends ParentConfiguration> void applyConfig(
      T config, Function<T, StatusCode> applier) throws MotorConfigException {
    StatusCode status = StatusCode.OK;

    for (int i = 0; i < NUM_RETRIES; i++) {
      status = applier.apply(config);
      if (status.isOK()) {
        return;
      }
    }

    String errorMessage =
        String.format(
            "Failed to apply %s to ID %d: %s (%s)",
            config.getClass().getSimpleName(),
            talonFX.getDeviceID(),
            status.getDescription(),
            status.getName());

    DriverStation.reportError(errorMessage, true);

    throw new MotorConfigException(errorMessage);
  }

  /**
   * Gets the current motor output configuration.
   *
   * @return The current motor output configuration.
   * @throws MotorConfigException If the configuration cannot be retrieved.
   */
  public MotorOutputConfigs getMotorOutputConfig() throws MotorConfigException {
    return getConfig(MotorOutputConfigs.class, talonFX.getConfigurator()::refresh);
  }

  /**
   * Applies the motor output configuration.
   *
   * @param motorOutputConfigs The motor output configuration to apply.
   * @throws MotorConfigException If the configuration is invalid or we fail to apply it for any
   *     reason.
   */
  public void applyMotorOutputConfig(MotorOutputConfigs motorOutputConfigs)
      throws MotorConfigException {
    applyConfig(motorOutputConfigs, talonFX.getConfigurator()::apply);
  }

  /**
   * Gets the current Talon FX configuration.
   *
   * @return The current Talon FX configuration.
   * @throws MotorConfigException If the configuration cannot be retrieved.
   */
  public TalonFXConfiguration getTalonFXConfiguration() throws MotorConfigException {
    return getConfig(TalonFXConfiguration.class, talonFX.getConfigurator()::refresh);
  }

  /**
   * Applies a full TalonFX configuration.
   *
   * @param config the TalonFX configuration to apply.
   * @throws MotorConfigException If the configuration is invalid or we fail to apply it for any
   *     reason.
   */
  public void applyTalonFXConfiguration(TalonFXConfiguration config) throws MotorConfigException {
    applyConfig(config, talonFX.getConfigurator()::apply);
  }

  @Override
  public MotorController apply(MotorConfig config) throws MotorConfigException {
    var talonFXConfig = getTalonFXConfiguration();
    talonFXConfig.MotorOutput.Inverted = convertDirectionToInvertedValue(config.direction());
    talonFXConfig.MotorOutput.NeutralMode = convertIdleModeToNeutralMode(config.idleMode());
    talonFXConfig.Feedback.SensorToMechanismRatio = 1.0;
    applyTalonFXConfiguration(talonFXConfig);
    distancePerRotation = config.distancePerRotation();
    return this;
  }

  @Override
  public MotorController apply(MotorCurrentConfig config) throws MotorConfigException {
    var talonFXConfig = getTalonFXConfiguration();
    talonFXConfig.CurrentLimits.StatorCurrentLimitEnable = config.enableCurrentLimit();
    talonFXConfig.CurrentLimits.SupplyCurrentLimitEnable = config.enableCurrentLimit();
    talonFXConfig.CurrentLimits.StatorCurrentLimit = config.statorCurrentLimit();
    talonFXConfig.CurrentLimits.SupplyCurrentLimit = config.supplyCurrentLimit();
    applyTalonFXConfiguration(talonFXConfig);
    return this;
  }
}
