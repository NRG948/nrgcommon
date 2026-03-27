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

import com.nrg948.actuator.MotorDirection;
import com.nrg948.actuator.MotorIdleMode;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkBaseConfigAccessor;
import com.revrobotics.spark.config.SparkFlexConfig;

/** A motor controller implementation based on the REV Robotics {@link SparkFlex} controllers. */
public final class SparkFlexAdapter extends SparkAdapter {
  protected static final class SparkFlexAccessor implements SparkAdapter.Accessor {
    private final SparkFlex spark;
    private final SparkFlexConfig config = new SparkFlexConfig();

    protected SparkFlexAccessor(SparkFlex spark) {
      this.spark = spark;
    }

    @Override
    public SparkBase get() {
      return spark;
    }

    @Override
    public SparkBaseConfigAccessor getConfigAccessor() {
      return spark.configAccessor;
    }

    @Override
    public SparkBaseConfig getConfig() {
      return config;
    }

    @Override
    public SparkBaseConfig newConfig() {
      return new SparkFlexConfig();
    }

    @Override
    public SparkAdapter newAdapter(String logPrefix, int deviceID) {
      SparkFlex sparkFlex = new SparkFlex(deviceID, spark.getMotorType());

      return new SparkFlexAdapter(logPrefix, new SparkFlexAccessor(sparkFlex));
    }
  }

  /**
   * Constructs a SparkFlexAdapter.
   *
   * @param logPrefix The prefix for the log entries.
   * @param sparkFlex The {@link SparkFlex} object to adapt.
   * @param direction The direction the motor rotates when a positive voltage is applied.
   * @param idleMode The motor behavior when idle (i.e. brake or coast mode).
   * @param distancePerRotation The distance the attached mechanism moves per rotation of the motor
   *     output shaft.
   *     <p>The unit of measure depends on the mechanism. For a mechanism that produces linear
   *     motion, the unit is typically in meters. For a mechanism that produces rotational motion,
   *     the unit is typically in radians.
   */
  public SparkFlexAdapter(
      String logPrefix,
      SparkFlex sparkFlex,
      MotorDirection direction,
      MotorIdleMode idleMode,
      double distancePerRotation) {
    super(
        logPrefix,
        "SparkFlex",
        new SparkFlexAccessor(sparkFlex),
        direction,
        idleMode,
        distancePerRotation);
  }

  /**
   * Constructs a SparkFlexAdapter.
   *
   * @param logPrefix The prefix for the log entries.
   * @param deviceID The device ID of the SparkFlex motor controller.
   * @param direction The direction the motor rotates when a positive voltage is applied.
   * @param idleMode The motor behavior when idle (i.e. brake or coast mode).
   * @param distancePerRotation The distance the attached mechanism moves per rotation of the motor
   *     output shaft.
   *     <p>The unit of measure depends on the mechanism. For a mechanism that produces linear
   *     motion, the unit is typically in meters. For a mechanism that produces rotational motion,
   *     the unit is typically in radians.
   */
  public SparkFlexAdapter(
      String logPrefix,
      int deviceID,
      MotorDirection direction,
      MotorIdleMode idleMode,
      double distancePerRotation) {
    this(
        logPrefix,
        new SparkFlex(deviceID, MotorType.kBrushless),
        direction,
        idleMode,
        distancePerRotation);
  }

  /**
   * Constructs a SparkFlexAdapter.
   *
   * <p>This private constructor is intended for use only when creating a follower. It is assumed
   * that the controller will be configured appropriately after creation.
   *
   * @param logPrefix The prefix for the log entries.
   * @param accessor The accessor for the SparkFlex motor controller.
   */
  private SparkFlexAdapter(String logPrefix, SparkAdapter.Accessor accessor) {
    super(logPrefix, "SparkFlex", accessor);
  }
}
