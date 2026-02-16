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
package com.nrg948.dashboard.data;

import edu.wpi.first.networktables.GenericPublisher;
import java.util.Optional;
import java.util.function.Supplier;

/** A binding that binds a constant value to dashboard data updates. */
final class ConstantBinding extends DashboardData {
  private Supplier<GenericPublisher> publisherSupplier;
  private Optional<GenericPublisher> publisher = Optional.empty();
  private int enabledCount = 0;

  /**
   * Constructs a ConstantBinding with the given topic and boolean value.
   *
   * @param topic The topic to bind the value to.
   * @param value The constant boolean value to bind.
   */
  public ConstantBinding(String topic, boolean value) {
    this.publisherSupplier =
        () -> {
          var publisher = TABLE.getTopic(topic).genericPublish("boolean");

          publisher.setBoolean(value);

          return publisher;
        };
  }

  /**
   * Constructs a ConstantBinding with the given topic and boolean array value.
   *
   * @param topic The topic to bind the value to.
   * @param value The constant boolean array value to bind.
   */
  public ConstantBinding(String topic, boolean[] value) {
    this.publisherSupplier =
        () -> {
          var publisher = TABLE.getTopic(topic).genericPublish("boolean[]");

          publisher.setBooleanArray(value);

          return publisher;
        };
  }

  /**
   * Constructs a ConstantBinding with the given topic and float value.
   *
   * @param topic The topic to bind the value to.
   * @param value The constant float value to bind.
   */
  public ConstantBinding(String topic, float value) {
    this.publisherSupplier =
        () -> {
          var publisher = TABLE.getTopic(topic).genericPublish("float");

          publisher.setFloat(value);

          return publisher;
        };
  }

  /**
   * Constructs a ConstantBinding with the given topic and float array value.
   *
   * @param topic The topic to bind the value to.
   * @param value The constant float array value to bind.
   */
  public ConstantBinding(String topic, float[] value) {
    this.publisherSupplier =
        () -> {
          var publisher = TABLE.getTopic(topic).genericPublish("float[]");

          publisher.setFloatArray(value);

          return publisher;
        };
  }

  /**
   * Constructs a ConstantBinding with the given topic and double value.
   *
   * @param topic The topic to bind the value to.
   * @param value The constant double value to bind.
   */
  public ConstantBinding(String topic, double value) {
    this.publisherSupplier =
        () -> {
          var publisher = TABLE.getTopic(topic).genericPublish("double");

          publisher.setDouble(value);

          return publisher;
        };
  }

  /**
   * Constructs a ConstantBinding with the given topic and double array value.
   *
   * @param topic The topic to bind the value to.
   * @param value The constant double array value to bind.
   */
  public ConstantBinding(String topic, double[] value) {
    this.publisherSupplier =
        () -> {
          var publisher = TABLE.getTopic(topic).genericPublish("double[]");

          publisher.setDoubleArray(value);

          return publisher;
        };
  }

  /**
   * Constructs a ConstantBinding with the given topic and long value.
   *
   * @param topic The topic to bind the value to.
   * @param value The constant long value to bind.
   */
  public ConstantBinding(String topic, long value) {
    this.publisherSupplier =
        () -> {
          var publisher = TABLE.getTopic(topic).genericPublish("int");

          publisher.setInteger(value);

          return publisher;
        };
  }

  /**
   * Constructs a ConstantBinding with the given topic and long array value.
   *
   * @param topic The topic to bind the value to.
   * @param value The constant long array value to bind.
   */
  public ConstantBinding(String topic, long[] value) {
    this.publisherSupplier =
        () -> {
          var publisher = TABLE.getTopic(topic).genericPublish("int[]");

          publisher.setIntegerArray(value);

          return publisher;
        };
  }

  /**
   * Constructs a ConstantBinding with the given topic and string value.
   *
   * @param topic The topic to bind the value to.
   * @param value The constant string value to bind.
   */
  public ConstantBinding(String topic, String value) {
    this.publisherSupplier =
        () -> {
          var publisher = TABLE.getTopic(topic).genericPublish("string");

          publisher.setString(value);

          return publisher;
        };
  }

  /**
   * Constructs a ConstantBinding with the given topic and string array value.
   *
   * @param topic The topic to bind the value to.
   * @param value The constant string array value to bind.
   */
  public ConstantBinding(String topic, String[] value) {
    this.publisherSupplier =
        () -> {
          var publisher = TABLE.getTopic(topic).genericPublish("string[]");

          publisher.setStringArray(value);

          return publisher;
        };
  }

  /**
   * Constructs a ConstantBinding with the given topic and raw byte array value.
   *
   * @param topic The topic to bind the value to.
   * @param typeString The type string to use for the publisher.
   * @param value The constant raw byte array value to bind.
   */
  public ConstantBinding(String topic, String typeString, byte[] value) {
    this.publisherSupplier =
        () -> {
          var publisher = TABLE.getTopic(topic).genericPublish(typeString);

          publisher.setRaw(value);

          return publisher;
        };
  }

  @Override
  public void enable() {
    if (enabledCount++ > 0) {
      return;
    }

    publisher = Optional.of(publisherSupplier.get());
  }

  @Override
  public void disable() {
    if (enabledCount <= 0) {
      throw new IllegalStateException("Cannot disable a binding that is not enabled");
    }

    if (--enabledCount == 0) {
      close();
    }
  }

  @Override
  protected void update() {
    // Constant values do not change and therefore do not need to be updated.
  }

  @Override
  public void close() {
    publisher.ifPresent(GenericPublisher::close);
    publisher = Optional.empty();
  }
}
