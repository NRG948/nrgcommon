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
package com.nrg948.actuator.rev;

import com.nrg948.actuator.MotorController;
import com.nrg948.actuator.MotorDirection;
import com.nrg948.actuator.MotorIdleMode;
import com.nrg948.sensor.LimitSwitch;
import com.nrg948.sensor.RelativeEncoder;
import com.nrg948.sensor.rev.SparkEncoderAdapter;
import com.nrg948.sensor.rev.SparkLimitSwitchAdapter;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkBaseConfigAccessor;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;

/** A motor controller implementation based on the REV Robotics Spark controllers. */
abstract class SparkAdapter implements MotorController {
  protected interface Accessor {
    /** Returns the motor controller. */
    SparkBase get();

    /** Returns the controller's configuration accessor. */
    SparkBaseConfigAccessor getConfigAccessor();

    /** Returns a new controller-specific configuration. */
    SparkBaseConfig newConfig();

    /** Gets the controller-specific configuration. */
    SparkBaseConfig getConfig();

    /**
     * Returns a new adapter of the same controller type.
     *
     * @param logPrefix The prefix for the log entries.
     * @param deviceID The device ID of the new controller.
     */
    SparkAdapter newAdapter(String logPrefix, int deviceID);
  }

  private static final DataLog LOG = DataLogManager.getLog();

  @SuppressWarnings("unused")
  private final String logPrefix;

  private final Accessor spark;

  private final DoubleLogEntry logOutputCurrent;
  private final DoubleLogEntry logTemperature;

  /**
   * Converts a {@link MotorIdleMode} to the corresponding Spark-specific {@link IdleMode}.
   *
   * @param idleMode The motor idle mode to convert.
   * @return The corresponding Spark-specific idle mode.
   */
  private static IdleMode convertIdleMode(MotorIdleMode idleMode) {
    switch (idleMode) {
      case BRAKE:
        return IdleMode.kBrake;
      case COAST:
        return IdleMode.kCoast;
      default:
        throw new IllegalArgumentException("Unsupported idle mode: " + idleMode);
    }
  }

  /**
   * Constructs a SparkAdapter for a {@link SparkMax} motor controller.
   *
   * <p>This protected constructor is intended for use only by delegating public constructors or
   * factory methods. It assumes the SparkMax object is already configured or will be configured
   * appropriately by the public constructors or factory methods.
   *
   * @param logPrefix The prefix for the log entries.
   * @param spark The SparkMax object to adapt.
   */
  protected SparkAdapter(String logPrefix, String deviceName, Accessor spark) {
    this.logPrefix = logPrefix;
    this.spark = spark;

    String name = String.format("%s/%s-%d", logPrefix, deviceName, spark.get().getDeviceId());
    this.logOutputCurrent = new DoubleLogEntry(LOG, name + "/OutputCurrent");
    this.logTemperature = new DoubleLogEntry(LOG, name + "/Temperature");
  }

  /**
   * Constructs a SparkAdapter for a {@link SparkMax} motor controller.
   *
   * @param logPrefix The prefix for the log entries.
   * @param spark The SparkMax object to adapt.
   */
  protected SparkAdapter(
      String logPrefix,
      String deviceName,
      Accessor spark,
      MotorDirection direction,
      MotorIdleMode idleMode,
      double distancePerRotation) {
    this(logPrefix, deviceName, spark);

    configure(direction, idleMode, distancePerRotation);
  }

  /**
   * Configures the motor controller.
   *
   * @param direction The direction the motor rotates when a positive voltage is applied.
   * @param idleMode The motor behavior when idle (i.e. brake or coast mode).
   * @param distancePerRotation The distance the attached mechanism moves per rotation of the motor
   *     output shaft.
   *     <p>The unit of measure depends on the mechanism. For a mechanism that produces linear
   *     motion, the unit is typically in meters. For a mechanism that produces rotational motion,
   *     the unit is typically in radians.
   */
  private void configure(
      MotorDirection direction, MotorIdleMode idleMode, double distancePerRotation) {
    SparkBaseConfig driveMotorConfig = spark.getConfig();

    driveMotorConfig.inverted(direction.isInverted()).idleMode(convertIdleMode(idleMode));

    driveMotorConfig
        .encoder
        .positionConversionFactor(distancePerRotation)
        // Convert from rotations per minute to rotations per second.
        .velocityConversionFactor(distancePerRotation / 60.0);

    spark
        .get()
        .configure(
            driveMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  @Override
  public void set(double speed) {
    spark.get().set(speed);
  }

  @Override
  public double get() {
    return spark.get().get();
  }

  @Override
  public void setVoltage(double outputVoltage) {
    spark.get().setVoltage(outputVoltage);
  }

  @Override
  public void setInverted(boolean isInverted) {
    SparkBaseConfig config = spark.getConfig();

    config.inverted(isInverted);

    spark.get().configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  @Override
  public boolean getInverted() {
    return spark.getConfigAccessor().getInverted();
  }

  @Override
  public void setIdleMode(MotorIdleMode idleMode) {
    SparkBaseConfig config = spark.getConfig();

    config.idleMode(convertIdleMode(idleMode));

    spark
        .get()
        .configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
  }

  @Override
  public void disable() {
    spark.get().disable();
  }

  @Override
  public void stopMotor() {
    spark.get().stopMotor();
  }

  @Override
  public MotorController createFollower(
      String logPrefix, int deviceID, boolean isInvertedFromLeader) {
    SparkAdapter follower = spark.newAdapter(logPrefix, deviceID);
    SparkBaseConfigAccessor configAccessor = spark.getConfigAccessor();
    SparkBaseConfig motorOutputConfigs = spark.newConfig();

    // Get the motor output configuration from the leader and apply it to the
    // follower.
    motorOutputConfigs
        .inverted(configAccessor.getInverted())
        .idleMode(configAccessor.getIdleMode());

    motorOutputConfigs
        .encoder
        .positionConversionFactor(configAccessor.encoder.getPositionConversionFactor())
        .velocityConversionFactor(configAccessor.encoder.getVelocityConversionFactor());

    // Configure the follower to follow the leader.
    motorOutputConfigs.follow(spark.get(), isInvertedFromLeader);

    follower
        .spark
        .get()
        .configure(
            motorOutputConfigs, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    return follower;
  }

  @Override
  public RelativeEncoder getEncoder() {
    return new SparkEncoderAdapter(spark.get().getEncoder());
  }

  @Override
  public LimitSwitch getForwardLimitSwitch() {
    return new SparkLimitSwitchAdapter(spark.get().getForwardLimitSwitch());
  }

  @Override
  public LimitSwitch getReverseLimitSwitch() {
    return new SparkLimitSwitchAdapter(spark.get().getReverseLimitSwitch());
  }

  @Override
  public void logTelemetry() {
    logOutputCurrent.append(spark.get().getOutputCurrent());
    logTemperature.append(spark.get().getMotorTemperature());
  }
}
