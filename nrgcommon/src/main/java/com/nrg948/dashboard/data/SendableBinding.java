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

import edu.wpi.first.networktables.BooleanArrayTopic;
import edu.wpi.first.networktables.BooleanTopic;
import edu.wpi.first.networktables.DoubleArrayTopic;
import edu.wpi.first.networktables.DoubleTopic;
import edu.wpi.first.networktables.FloatArrayTopic;
import edu.wpi.first.networktables.FloatTopic;
import edu.wpi.first.networktables.IntegerArrayTopic;
import edu.wpi.first.networktables.IntegerTopic;
import edu.wpi.first.networktables.NTSendableBuilder;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.RawTopic;
import edu.wpi.first.networktables.StringArrayTopic;
import edu.wpi.first.networktables.StringTopic;
import edu.wpi.first.networktables.Topic;
import edu.wpi.first.util.function.BooleanConsumer;
import edu.wpi.first.util.function.FloatConsumer;
import edu.wpi.first.util.function.FloatSupplier;
import edu.wpi.first.util.sendable.Sendable;
import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/** A binding that binds a {@link Sendable} to dashboard data updates. */
final class SendableBinding extends DashboardData implements NTSendableBuilder {
  private final String topic;
  private final ArrayList<DashboardData> childBindings = new ArrayList<>();
  private final ArrayList<Runnable> updaters = new ArrayList<>();
  private final ArrayList<AutoCloseable> closeables = new ArrayList<>();

  /**
   * Constructs a SendableBinding with the given topic and sendable.
   *
   * @param topic The topic to bind the sendable to.
   * @param sendable The sendable to bind.
   */
  SendableBinding(String topic, Sendable sendable) {
    this.topic = topic;

    sendable.initSendable(this);

    publishConstBoolean(".controllable", true);
  }

  @Override
  public void enable() {
    for (var childBinding : childBindings) {
      childBinding.enable();
    }
  }

  @Override
  public void disable() {
    for (var childBinding : childBindings) {
      childBinding.disable();
    }
  }

  @Override
  public void update() {
    for (var updater : updaters) {
      try {
        updater.run();
      } catch (Exception e) {
        // Ignore.
      }
    }
  }

  @Override
  public void close() {
    for (var closeable : closeables) {
      try {
        closeable.close();
      } catch (Exception e) {
        // Ignore.
      }
    }
  }

  @Override
  public void setSmartDashboardType(String type) {
    TABLE.getEntry(String.join("/", topic, ".type")).setString(type);
  }

  @Override
  public void setActuator(boolean value) {
    TABLE.getEntry(String.join("/", topic, ".actuator")).setBoolean(value);
  }

  @Override
  public void setSafeState(Runnable func) {
    // TODO Implement setSafeState
  }

  @Override
  public void addBooleanProperty(String key, BooleanSupplier getter, BooleanConsumer setter) {
    BooleanTopic booleanTopic = TABLE.getBooleanTopic(String.join("/", topic, key));

    childBindings.add(bind(new BooleanBinding(booleanTopic, getter, setter)));
  }

  @Override
  public void publishConstBoolean(String key, boolean value) {
    TABLE.getEntry(String.join("/", topic, key)).setBoolean(value);
  }

  @Override
  public void addIntegerProperty(String key, LongSupplier getter, LongConsumer setter) {
    IntegerTopic integerTopic = TABLE.getIntegerTopic(String.join("/", topic, key));

    childBindings.add(bind(new IntegerBinding(integerTopic, getter, setter)));
  }

  @Override
  public void publishConstInteger(String key, long value) {
    TABLE.getEntry(String.join("/", topic, key)).setInteger(value);
  }

  @Override
  public void addFloatProperty(String key, FloatSupplier getter, FloatConsumer setter) {
    FloatTopic floatTopic = TABLE.getFloatTopic(String.join("/", topic, key));

    childBindings.add(bind(new FloatBinding(floatTopic, getter, setter)));
  }

  @Override
  public void publishConstFloat(String key, float value) {
    TABLE.getEntry(String.join("/", topic, key)).setFloat(value);
  }

  @Override
  public void addDoubleProperty(String key, DoubleSupplier getter, DoubleConsumer setter) {
    DoubleTopic doubleTopic = TABLE.getDoubleTopic(String.join("/", topic, key));

    childBindings.add(bind(new DoubleBinding(doubleTopic, getter, setter)));
  }

  @Override
  public void publishConstDouble(String key, double value) {
    TABLE.getEntry(String.join("/", topic, key)).setDouble(value);
  }

  @Override
  public void addStringProperty(String key, Supplier<String> getter, Consumer<String> setter) {
    StringTopic stringTopic = TABLE.getStringTopic(String.join("/", topic, key));

    childBindings.add(bind(new StringBinding(stringTopic, getter, setter)));
  }

  @Override
  public void publishConstString(String key, String value) {
    TABLE.getEntry(String.join("/", topic, key)).setString(value);
  }

  @Override
  public void addBooleanArrayProperty(
      String key, Supplier<boolean[]> getter, Consumer<boolean[]> setter) {
    BooleanArrayTopic booleanTopic = TABLE.getBooleanArrayTopic(String.join("/", topic, key));

    childBindings.add(bind(new BooleanArrayBinding(booleanTopic, getter, setter)));
  }

  @Override
  public void publishConstBooleanArray(String key, boolean[] value) {
    TABLE.getEntry(String.join("/", topic, key)).setBooleanArray(value);
  }

  @Override
  public void addIntegerArrayProperty(
      String key, Supplier<long[]> getter, Consumer<long[]> setter) {
    IntegerArrayTopic integerTopic = TABLE.getIntegerArrayTopic(String.join("/", topic, key));

    childBindings.add(bind(new IntegerArrayBinding(integerTopic, getter, setter)));
  }

  @Override
  public void publishConstIntegerArray(String key, long[] value) {
    TABLE.getEntry(String.join("/", topic, key)).setIntegerArray(value);
  }

  @Override
  public void addFloatArrayProperty(
      String key, Supplier<float[]> getter, Consumer<float[]> setter) {
    FloatArrayTopic floatTopic = TABLE.getFloatArrayTopic(String.join("/", topic, key));

    childBindings.add(bind(new FloatArrayBinding(floatTopic, getter, setter)));
  }

  @Override
  public void publishConstFloatArray(String key, float[] value) {
    TABLE.getEntry(String.join("/", topic, key)).setFloatArray(value);
  }

  @Override
  public void addDoubleArrayProperty(
      String key, Supplier<double[]> getter, Consumer<double[]> setter) {
    DoubleArrayTopic doubleTopic = TABLE.getDoubleArrayTopic(String.join("/", topic, key));

    childBindings.add(bind(new DoubleArrayBinding(doubleTopic, getter, setter)));
  }

  @Override
  public void publishConstDoubleArray(String key, double[] value) {
    TABLE.getEntry(String.join("/", topic, key)).setDoubleArray(value);
  }

  @Override
  public void addStringArrayProperty(
      String key, Supplier<String[]> getter, Consumer<String[]> setter) {
    StringArrayTopic stringTopic = TABLE.getStringArrayTopic(String.join("/", topic, key));

    childBindings.add(bind(new StringArrayBinding(stringTopic, getter, setter)));
  }

  @Override
  public void publishConstStringArray(String key, String[] value) {
    TABLE.getEntry(String.join("/", topic, key)).setStringArray(value);
  }

  @Override
  public void addRawProperty(
      String key, String typeString, Supplier<byte[]> getter, Consumer<byte[]> setter) {
    RawTopic rawTopic = TABLE.getRawTopic(String.join("/", topic, key));

    childBindings.add(bind(new RawBinding(rawTopic, typeString, getter, setter)));
  }

  @Override
  public void publishConstRaw(String key, String typeString, byte[] value) {
    TABLE.getEntry(String.join("/", topic, key)).setRaw(value);
  }

  @Override
  public BackendKind getBackendKind() {
    return BackendKind.kNetworkTables;
  }

  @Override
  public boolean isPublished() {
    return true;
  }

  @Override
  public void clearProperties() {
    // TODO Implement clearProperties
  }

  @Override
  public void addCloseable(AutoCloseable closeable) {
    closeables.add(closeable);
  }

  @Override
  public void setUpdateTable(Runnable func) {
    updaters.add(func);
  }

  @Override
  public Topic getTopic(String key) {
    return TABLE.getTopic(String.join("/", topic, key));
  }

  @Override
  public NetworkTable getTable() {
    return TABLE.getSubTable(topic);
  }
}
