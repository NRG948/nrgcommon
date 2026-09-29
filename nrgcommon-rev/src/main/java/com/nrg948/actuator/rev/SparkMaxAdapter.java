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

import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkBaseConfigAccessor;
import com.revrobotics.spark.config.SparkMaxConfig;

/** A motor controller implementation based on the REV Robotics {@link SparkMax} controllers. */
public final class SparkMaxAdapter extends SparkAdapter {
  /** The accessor for the SparkMax motor controller. */
  protected static final class SparkMaxAccessor implements SparkAdapter.Accessor {
    private final SparkMax spark;
    private final SparkMaxConfig config = new SparkMaxConfig();

    /**
     * Constructs a SparkMaxAccessor.
     *
     * @param spark The SparkMax motor controller to access.
     */
    protected SparkMaxAccessor(SparkMax spark) {
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
      return new SparkMaxConfig();
    }

    @Override
    public SparkAdapter newAdapter(String logPrefix, int deviceID) {
      SparkMax sparkMax = new SparkMax(deviceID, spark.getMotorType());

      return new SparkMaxAdapter(logPrefix, sparkMax);
    }
  }

  /**
   * Constructs a SparkMaxAdapter.
   *
   * @param logPrefix The prefix for the log entries.
   * @param sparkMax The {@link SparkMax} object to adapt.
   */
  public SparkMaxAdapter(String logPrefix, SparkMax sparkMax) {
    super(logPrefix, "SparkMax", new SparkMaxAccessor(sparkMax));
  }

  /**
   * Constructs a SparkMaxAdapter.
   *
   * @param logPrefix The prefix for the log entries.
   * @param deviceID The device ID of the SparkMax motor controller.
   */
  public SparkMaxAdapter(String logPrefix, int deviceID) {
    this(logPrefix, new SparkMax(deviceID, MotorType.kBrushless));
  }
}
