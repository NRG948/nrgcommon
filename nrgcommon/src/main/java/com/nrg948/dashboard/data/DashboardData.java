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

import com.nrg948.preferences.PreferenceValue;
import com.nrg948.util.enums.EnumChooser;
import com.nrg948.util.function.ObjBooleanConsumer;
import com.nrg948.util.function.ObjFloatConsumer;
import com.nrg948.util.function.ToBooleanFunction;
import com.nrg948.util.function.ToFloatFunction;
import edu.wpi.first.cscore.VideoSource;
import edu.wpi.first.networktables.BooleanArrayPublisher;
import edu.wpi.first.networktables.BooleanArraySubscriber;
import edu.wpi.first.networktables.BooleanArrayTopic;
import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.BooleanTopic;
import edu.wpi.first.networktables.DoubleArrayPublisher;
import edu.wpi.first.networktables.DoubleArraySubscriber;
import edu.wpi.first.networktables.DoubleArrayTopic;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.DoubleTopic;
import edu.wpi.first.networktables.FloatArrayPublisher;
import edu.wpi.first.networktables.FloatArraySubscriber;
import edu.wpi.first.networktables.FloatArrayTopic;
import edu.wpi.first.networktables.FloatPublisher;
import edu.wpi.first.networktables.FloatSubscriber;
import edu.wpi.first.networktables.FloatTopic;
import edu.wpi.first.networktables.IntegerArrayPublisher;
import edu.wpi.first.networktables.IntegerArraySubscriber;
import edu.wpi.first.networktables.IntegerArrayTopic;
import edu.wpi.first.networktables.IntegerPublisher;
import edu.wpi.first.networktables.IntegerSubscriber;
import edu.wpi.first.networktables.IntegerTopic;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.networktables.RawPublisher;
import edu.wpi.first.networktables.RawSubscriber;
import edu.wpi.first.networktables.RawTopic;
import edu.wpi.first.networktables.StringArrayPublisher;
import edu.wpi.first.networktables.StringArraySubscriber;
import edu.wpi.first.networktables.StringArrayTopic;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.networktables.StringSubscriber;
import edu.wpi.first.networktables.StringTopic;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.SendableCameraWrapper;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

/** Abstract class representing dashboard data bindings. */
public abstract class DashboardData implements AutoCloseable {
  static final byte[] DEFAULT_RAW_VALUE = new byte[] {};

  static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[] {};
  static final long[] EMPTY_INTEGER_ARRAY = new long[] {};
  static final float[] EMPTY_FLOAT_ARRAY = new float[] {};
  static final double[] EMPTY_DOUBLE_ARRAY = new double[] {};
  static final String[] EMPTY_STRING_ARRAY = new String[] {};

  static final NetworkTable TABLE = NetworkTableInstance.getDefault().getTable("SmartDashboard");

  private static final ArrayList<DashboardData> bindings = new ArrayList<>();

  /**
   * Receives updates from the dashboard to update the bound data and/or publishes updates to the
   * dashboard received from the bound data.
   */
  protected abstract void update();

  @Override
  public abstract void close();

  /**
   * Bind a DashboardData instance to be updated.
   *
   * @param binding The DashboardData instance to bind.
   */
  public static void bind(DashboardData binding) {
    bindings.add(binding);
  }

  /**
   * Bind a boolean value to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the boolean value.
   * @param getter The function to retrieve the boolean value from the container.
   */
  public static <T> void bindBoolean(String topic, T container, ToBooleanFunction<T> getter) {
    BooleanTopic booleanTopic = TABLE.getBooleanTopic(topic);

    BooleanPublisher publisher = booleanTopic.publish();
    Consumer<BooleanPublisher> publishUpdates = (pub) -> pub.set(getter.applyAsBoolean(container));

    bindings.add(new DataBinding<BooleanPublisher, BooleanSubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a static boolean value to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the boolean value.
   */
  public static void bindStaticBoolean(String topic, BooleanSupplier getter) {
    BooleanTopic booleanTopic = TABLE.getBooleanTopic(topic);

    BooleanPublisher publisher = booleanTopic.publish();
    Consumer<BooleanPublisher> publishUpdates = (pub) -> pub.set(getter.getAsBoolean());

    bindings.add(new DataBinding<BooleanPublisher, BooleanSubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a boolean value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the boolean value.
   * @param getter The function to retrieve the boolean value from the container.
   * @param setter The function to update the boolean value in the container.
   */
  public static <T> void bindBoolean(
      String topic, T container, ToBooleanFunction<T> getter, ObjBooleanConsumer<T> setter) {
    BooleanTopic booleanTopic = TABLE.getBooleanTopic(topic);

    BooleanPublisher publisher = booleanTopic.publish();
    Consumer<BooleanPublisher> publishUpdates = (pub) -> pub.set(getter.applyAsBoolean(container));

    BooleanSubscriber subscriber =
        booleanTopic.subscribe(false, PubSubOption.excludePublisher(publisher));
    Consumer<BooleanSubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(container, update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a static boolean value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the boolean value.
   * @param setter The function to update the boolean value.
   */
  public static void bindStaticBoolean(
      String topic, BooleanSupplier getter, Consumer<Boolean> setter) {
    BooleanTopic booleanTopic = TABLE.getBooleanTopic(topic);

    BooleanPublisher publisher = booleanTopic.publish();
    Consumer<BooleanPublisher> publishUpdates = (pub) -> pub.set(getter.getAsBoolean());

    BooleanSubscriber subscriber =
        booleanTopic.subscribe(false, PubSubOption.excludePublisher(publisher));
    Consumer<BooleanSubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a boolean array to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the boolean array.
   * @param getter The function to retrieve the boolean array from the container.
   */
  public static <T> void bindBooleanArray(
      String topic, T container, Function<T, boolean[]> getter) {
    BooleanArrayTopic booleanArrayTopic = TABLE.getBooleanArrayTopic(topic);

    BooleanArrayPublisher publisher = booleanArrayTopic.publish();
    Consumer<BooleanArrayPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    bindings.add(
        new DataBinding<BooleanArrayPublisher, BooleanArraySubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a static boolean array to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the boolean array.
   */
  public static void bindStaticBooleanArray(String topic, Supplier<boolean[]> getter) {
    BooleanArrayTopic booleanArrayTopic = TABLE.getBooleanArrayTopic(topic);

    BooleanArrayPublisher publisher = booleanArrayTopic.publish();
    Consumer<BooleanArrayPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    bindings.add(
        new DataBinding<BooleanArrayPublisher, BooleanArraySubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a boolean array to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the boolean array.
   * @param getter The function to retrieve the boolean array from the container.
   * @param setter The function to update the boolean array in the container.
   */
  public static <T> void bindBooleanArray(
      String topic, T container, Function<T, boolean[]> getter, BiConsumer<T, boolean[]> setter) {
    BooleanArrayTopic booleanArrayTopic = TABLE.getBooleanArrayTopic(topic);

    BooleanArrayPublisher publisher = booleanArrayTopic.publish();
    Consumer<BooleanArrayPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    BooleanArraySubscriber subscriber =
        booleanArrayTopic.subscribe(EMPTY_BOOLEAN_ARRAY, PubSubOption.excludePublisher(publisher));
    Consumer<BooleanArraySubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(container, update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a static boolean array to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the boolean array.
   * @param setter The function to update the boolean array.
   */
  public static void bindStaticBooleanArray(
      String topic, Supplier<boolean[]> getter, Consumer<boolean[]> setter) {
    BooleanArrayTopic booleanArrayTopic = TABLE.getBooleanArrayTopic(topic);

    BooleanArrayPublisher publisher = booleanArrayTopic.publish();
    Consumer<BooleanArrayPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    BooleanArraySubscriber subscriber =
        booleanArrayTopic.subscribe(EMPTY_BOOLEAN_ARRAY, PubSubOption.excludePublisher(publisher));
    Consumer<BooleanArraySubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a float value to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the float value.
   * @param getter The function to retrieve the float value from the container.
   */
  public static <T> void bindFloat(String topic, T container, ToFloatFunction<T> getter) {
    FloatTopic floatTopic = TABLE.getFloatTopic(topic);

    FloatPublisher publisher = floatTopic.publish();
    Consumer<FloatPublisher> publishUpdates = (pub) -> pub.set(getter.applyAsFloat(container));

    bindings.add(new DataBinding<FloatPublisher, FloatSubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a static float value to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the float value.
   */
  public static void bindStaticFloat(String topic, Supplier<Float> getter) {
    FloatTopic floatTopic = TABLE.getFloatTopic(topic);

    FloatPublisher publisher = floatTopic.publish();
    Consumer<FloatPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    bindings.add(new DataBinding<FloatPublisher, FloatSubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a float value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the float value.
   * @param getter The function to retrieve the float value from the container.
   * @param setter The function to update the float value in the container.
   */
  public static <T> void bindFloat(
      String topic, T container, ToFloatFunction<T> getter, ObjFloatConsumer<T> setter) {
    FloatTopic floatTopic = TABLE.getFloatTopic(topic);

    FloatPublisher publisher = floatTopic.publish();
    Consumer<FloatPublisher> publishUpdates = (pub) -> pub.set(getter.applyAsFloat(container));

    FloatSubscriber subscriber =
        floatTopic.subscribe(0.0f, PubSubOption.excludePublisher(publisher));
    Consumer<FloatSubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(container, update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a static float value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the float value.
   * @param setter The function to update the float value.
   */
  public static void bindStaticFloat(String topic, Supplier<Float> getter, Consumer<Float> setter) {
    FloatTopic floatTopic = TABLE.getFloatTopic(topic);

    FloatPublisher publisher = floatTopic.publish();
    Consumer<FloatPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    FloatSubscriber subscriber =
        floatTopic.subscribe(0.0f, PubSubOption.excludePublisher(publisher));
    Consumer<FloatSubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a float array to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the float array.
   * @param getter The function to retrieve the float array from the container.
   */
  public static <T> void bindFloatArray(String topic, T container, Function<T, float[]> getter) {
    FloatArrayTopic floatArrayTopic = TABLE.getFloatArrayTopic(topic);

    FloatArrayPublisher publisher = floatArrayTopic.publish();
    Consumer<FloatArrayPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    bindings.add(
        new DataBinding<FloatArrayPublisher, FloatArraySubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a static float array to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the float array.
   */
  public static void bindStaticFloatArray(String topic, Supplier<float[]> getter) {
    FloatArrayTopic floatArrayTopic = TABLE.getFloatArrayTopic(topic);

    FloatArrayPublisher publisher = floatArrayTopic.publish();
    Consumer<FloatArrayPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    bindings.add(
        new DataBinding<FloatArrayPublisher, FloatArraySubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a float array to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the float array.
   * @param getter The function to retrieve the float array from the container.
   * @param setter The function to update the float array in the container.
   */
  public static <T> void bindFloatArray(
      String topic, T container, Function<T, float[]> getter, BiConsumer<T, float[]> setter) {
    FloatArrayTopic floatArrayTopic = TABLE.getFloatArrayTopic(topic);

    FloatArrayPublisher publisher = floatArrayTopic.publish();
    Consumer<FloatArrayPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    FloatArraySubscriber subscriber =
        floatArrayTopic.subscribe(EMPTY_FLOAT_ARRAY, PubSubOption.excludePublisher(publisher));
    Consumer<FloatArraySubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(container, update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a static float array to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the float array.
   * @param setter The function to update the float array.
   */
  public static void bindStaticFloatArray(
      String topic, Supplier<float[]> getter, Consumer<float[]> setter) {
    FloatArrayTopic floatArrayTopic = TABLE.getFloatArrayTopic(topic);

    FloatArrayPublisher publisher = floatArrayTopic.publish();
    Consumer<FloatArrayPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    FloatArraySubscriber subscriber =
        floatArrayTopic.subscribe(EMPTY_FLOAT_ARRAY, PubSubOption.excludePublisher(publisher));
    Consumer<FloatArraySubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a double value to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the double value.
   * @param getter The function to retrieve the double value from the container.
   */
  public static <T> void bindDouble(String topic, T container, ToDoubleFunction<T> getter) {
    DoubleTopic doubleTopic = TABLE.getDoubleTopic(topic);

    DoublePublisher publisher = doubleTopic.publish();
    Consumer<DoublePublisher> publishUpdates = (pub) -> pub.set(getter.applyAsDouble(container));

    bindings.add(new DataBinding<DoublePublisher, DoubleSubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a static double value to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the double value.
   */
  public static void bindStaticDouble(String topic, Supplier<Double> getter) {
    DoubleTopic doubleTopic = TABLE.getDoubleTopic(topic);

    DoublePublisher publisher = doubleTopic.publish();
    Consumer<DoublePublisher> publishUpdates = (pub) -> pub.set(getter.get());

    bindings.add(new DataBinding<DoublePublisher, DoubleSubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a double value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the double value.
   * @param getter The function to retrieve the double value from the container.
   * @param setter The function to update the double value in the container.
   */
  public static <T> void bindDouble(
      String topic, T container, ToDoubleFunction<T> getter, ObjDoubleConsumer<T> setter) {
    DoubleTopic doubleTopic = TABLE.getDoubleTopic(topic);

    DoublePublisher publisher = doubleTopic.publish();
    Consumer<DoublePublisher> publishUpdates = (pub) -> pub.set(getter.applyAsDouble(container));

    DoubleSubscriber subscriber =
        doubleTopic.subscribe(0.0, PubSubOption.excludePublisher(publisher));
    Consumer<DoubleSubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(container, update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a static double value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the double value.
   * @param setter The function to update the double value.
   */
  public static void bindStaticDouble(
      String topic, Supplier<Double> getter, Consumer<Double> setter) {
    DoubleTopic doubleTopic = TABLE.getDoubleTopic(topic);

    DoublePublisher publisher = doubleTopic.publish();
    Consumer<DoublePublisher> publishUpdates = (pub) -> pub.set(getter.get());

    DoubleSubscriber subscriber =
        doubleTopic.subscribe(0.0, PubSubOption.excludePublisher(publisher));
    Consumer<DoubleSubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a double array to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the double array.
   * @param getter The function to retrieve the double array from the container.
   */
  public static <T> void bindDoubleArray(String topic, T container, Function<T, double[]> getter) {
    DoubleArrayTopic doubleArrayTopic = TABLE.getDoubleArrayTopic(topic);

    DoubleArrayPublisher publisher = doubleArrayTopic.publish();
    Consumer<DoubleArrayPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    bindings.add(
        new DataBinding<DoubleArrayPublisher, DoubleArraySubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a static double array to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the double array.
   */
  public static void bindStaticDoubleArray(String topic, Supplier<double[]> getter) {
    DoubleArrayTopic doubleArrayTopic = TABLE.getDoubleArrayTopic(topic);

    DoubleArrayPublisher publisher = doubleArrayTopic.publish();
    Consumer<DoubleArrayPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    bindings.add(
        new DataBinding<DoubleArrayPublisher, DoubleArraySubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a double array to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the double array.
   * @param getter The function to retrieve the double array from the container.
   * @param setter The function to update the double array in the container.
   */
  public static <T> void bindDoubleArray(
      String topic, T container, Function<T, double[]> getter, BiConsumer<T, double[]> setter) {
    DoubleArrayTopic doubleArrayTopic = TABLE.getDoubleArrayTopic(topic);

    DoubleArrayPublisher publisher = doubleArrayTopic.publish();
    Consumer<DoubleArrayPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    DoubleArraySubscriber subscriber =
        doubleArrayTopic.subscribe(EMPTY_DOUBLE_ARRAY, PubSubOption.excludePublisher(publisher));
    Consumer<DoubleArraySubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(container, update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a static double array to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the double array.
   * @param setter The function to update the double array.
   */
  public static void bindStaticDoubleArray(
      String topic, Supplier<double[]> getter, Consumer<double[]> setter) {
    DoubleArrayTopic doubleArrayTopic = TABLE.getDoubleArrayTopic(topic);

    DoubleArrayPublisher publisher = doubleArrayTopic.publish();
    Consumer<DoubleArrayPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    DoubleArraySubscriber subscriber =
        doubleArrayTopic.subscribe(EMPTY_DOUBLE_ARRAY, PubSubOption.excludePublisher(publisher));
    Consumer<DoubleArraySubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a integer value to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the integer value.
   * @param getter The function to retrieve the integer value from the container.
   */
  public static <T> void bindInteger(String topic, T container, ToLongFunction<T> getter) {
    IntegerTopic integerTopic = TABLE.getIntegerTopic(topic);

    IntegerPublisher publisher = integerTopic.publish();
    Consumer<IntegerPublisher> publishUpdates = (pub) -> pub.set(getter.applyAsLong(container));

    bindings.add(new DataBinding<IntegerPublisher, IntegerSubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a static integer value to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the integer value.
   */
  public static void bindStaticInteger(String topic, Supplier<Long> getter) {
    IntegerTopic integerTopic = TABLE.getIntegerTopic(topic);

    IntegerPublisher publisher = integerTopic.publish();
    Consumer<IntegerPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    bindings.add(new DataBinding<IntegerPublisher, IntegerSubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a integer value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the integer value.
   * @param getter The function to retrieve the integer value from the container.
   * @param setter The function to update the integer value in the container.
   */
  public static <T> void bindInteger(
      String topic, T container, ToLongFunction<T> getter, ObjLongConsumer<T> setter) {
    IntegerTopic integerTopic = TABLE.getIntegerTopic(topic);

    IntegerPublisher publisher = integerTopic.publish();
    Consumer<IntegerPublisher> publishUpdates = (pub) -> pub.set(getter.applyAsLong(container));

    IntegerSubscriber subscriber =
        integerTopic.subscribe(0L, PubSubOption.excludePublisher(publisher));
    Consumer<IntegerSubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(container, update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a static integer value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the integer value.
   * @param setter The function to update the integer value.
   */
  public static void bindStaticInteger(String topic, Supplier<Long> getter, Consumer<Long> setter) {
    IntegerTopic integerTopic = TABLE.getIntegerTopic(topic);

    IntegerPublisher publisher = integerTopic.publish();
    Consumer<IntegerPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    IntegerSubscriber subscriber =
        integerTopic.subscribe(0L, PubSubOption.excludePublisher(publisher));
    Consumer<IntegerSubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a integer array to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the integer array.
   * @param getter The function to retrieve the integer array from the container.
   */
  public static <T> void bindIntegerArray(String topic, T container, Function<T, long[]> getter) {
    IntegerArrayTopic integerTopic = TABLE.getIntegerArrayTopic(topic);

    IntegerArrayPublisher publisher = integerTopic.publish();
    Consumer<IntegerArrayPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    bindings.add(
        new DataBinding<IntegerArrayPublisher, IntegerArraySubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a static integer array to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the integer array.
   */
  public static void bindStaticIntegerArray(String topic, Supplier<long[]> getter) {
    IntegerArrayTopic integerTopic = TABLE.getIntegerArrayTopic(topic);

    IntegerArrayPublisher publisher = integerTopic.publish();
    Consumer<IntegerArrayPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    bindings.add(
        new DataBinding<IntegerArrayPublisher, IntegerArraySubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a integer array to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the integer array.
   * @param getter The function to retrieve the integer array from the container.
   * @param setter The function to update the integer array in the container.
   */
  public static <T> void bindIntegerArray(
      String topic, T container, Function<T, long[]> getter, BiConsumer<T, long[]> setter) {
    IntegerArrayTopic integerTopic = TABLE.getIntegerArrayTopic(topic);

    IntegerArrayPublisher publisher = integerTopic.publish();
    Consumer<IntegerArrayPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    IntegerArraySubscriber subscriber =
        integerTopic.subscribe(EMPTY_INTEGER_ARRAY, PubSubOption.excludePublisher(publisher));
    Consumer<IntegerArraySubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(container, update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a static integer array to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the integer array.
   * @param setter The function to update the integer array.
   */
  public static void bindStaticIntegerArray(
      String topic, Supplier<long[]> getter, Consumer<long[]> setter) {
    IntegerArrayTopic integerTopic = TABLE.getIntegerArrayTopic(topic);

    IntegerArrayPublisher publisher = integerTopic.publish();
    Consumer<IntegerArrayPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    IntegerArraySubscriber subscriber =
        integerTopic.subscribe(EMPTY_INTEGER_ARRAY, PubSubOption.excludePublisher(publisher));
    Consumer<IntegerArraySubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a string value to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the string value.
   * @param getter The function to retrieve the string value from the container.
   */
  public static <T> void bindString(String topic, T container, Function<T, String> getter) {
    StringTopic stringTopic = TABLE.getStringTopic(topic);

    StringPublisher publisher = stringTopic.publish();
    Consumer<StringPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    bindings.add(new DataBinding<StringPublisher, StringSubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a static string value to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the string value.
   */
  public static void bindStaticString(String topic, Supplier<String> getter) {
    StringTopic stringTopic = TABLE.getStringTopic(topic);

    StringPublisher publisher = stringTopic.publish();
    Consumer<StringPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    bindings.add(new DataBinding<StringPublisher, StringSubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a string value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the string value.
   * @param getter The function to retrieve the string value from the container.
   * @param setter The function to update the string value in the container.
   */
  public static <T> void bindString(
      String topic, T container, Function<T, String> getter, BiConsumer<T, String> setter) {
    StringTopic stringTopic = TABLE.getStringTopic(topic);

    StringPublisher publisher = stringTopic.publish();
    Consumer<StringPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    StringSubscriber subscriber =
        stringTopic.subscribe("", PubSubOption.excludePublisher(publisher));
    Consumer<StringSubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(container, update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a static string value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the string value.
   * @param setter The function to update the string value.
   */
  public static void bindStaticString(
      String topic, Supplier<String> getter, Consumer<String> setter) {
    StringTopic stringTopic = TABLE.getStringTopic(topic);

    StringPublisher publisher = stringTopic.publish();
    Consumer<StringPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    StringSubscriber subscriber =
        stringTopic.subscribe("", PubSubOption.excludePublisher(publisher));
    Consumer<StringSubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a string array to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the string array.
   * @param getter The function to retrieve the string array from the container.
   */
  public static <T> void bindStringArray(String topic, T container, Function<T, String[]> getter) {
    StringArrayTopic stringTopic = TABLE.getStringArrayTopic(topic);

    StringArrayPublisher publisher = stringTopic.publish();
    Consumer<StringArrayPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    bindings.add(
        new DataBinding<StringArrayPublisher, StringArraySubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a static string array to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the string array.
   */
  public static void bindStaticStringArray(String topic, Supplier<String[]> getter) {
    StringArrayTopic stringTopic = TABLE.getStringArrayTopic(topic);

    StringArrayPublisher publisher = stringTopic.publish();
    Consumer<StringArrayPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    bindings.add(
        new DataBinding<StringArrayPublisher, StringArraySubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a string array to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the string array.
   * @param getter The function to retrieve the string array from the container.
   * @param setter The function to update the string array in the container.
   */
  public static <T> void bindStringArray(
      String topic, T container, Function<T, String[]> getter, BiConsumer<T, String[]> setter) {
    StringArrayTopic stringTopic = TABLE.getStringArrayTopic(topic);

    StringArrayPublisher publisher = stringTopic.publish();
    Consumer<StringArrayPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    StringArraySubscriber subscriber =
        stringTopic.subscribe(EMPTY_STRING_ARRAY, PubSubOption.excludePublisher(publisher));
    Consumer<StringArraySubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(container, update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a static string array to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the string array.
   * @param setter The function to update the string array.
   */
  public static void bindStaticStringArray(
      String topic, Supplier<String[]> getter, Consumer<String[]> setter) {
    StringArrayTopic stringTopic = TABLE.getStringArrayTopic(topic);

    StringArrayPublisher publisher = stringTopic.publish();
    Consumer<StringArrayPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    StringArraySubscriber subscriber =
        stringTopic.subscribe(EMPTY_STRING_ARRAY, PubSubOption.excludePublisher(publisher));
    Consumer<StringArraySubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a raw value to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param typeString The type string for the raw value.
   * @param container The object containing the raw value.
   * @param getter The function to retrieve the raw value from the container.
   */
  public static <T> void bindRaw(
      String topic, String typeString, T container, Function<T, byte[]> getter) {
    RawTopic rawTopic = TABLE.getRawTopic(topic);

    RawPublisher publisher = rawTopic.publish(typeString);
    Consumer<RawPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    bindings.add(new DataBinding<RawPublisher, RawSubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a static raw value to be published to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param typeString The type string for the raw value.
   * @param getter The function to retrieve the raw value.
   */
  public static void bindStaticRaw(String topic, String typeString, Supplier<byte[]> getter) {
    RawTopic rawTopic = TABLE.getRawTopic(topic);

    RawPublisher publisher = rawTopic.publish(typeString);
    Consumer<RawPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    bindings.add(new DataBinding<RawPublisher, RawSubscriber>(publisher, publishUpdates));
  }

  /**
   * Bind a raw value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param typeString The type string for the raw value.
   * @param container The object containing the raw value.
   * @param getter The function to retrieve the raw value from the container.
   * @param setter The function to update the raw value in the container.
   */
  public static <T> void bindRaw(
      String topic,
      String typeString,
      T container,
      Function<T, byte[]> getter,
      BiConsumer<T, byte[]> setter) {
    RawTopic rawTopic = TABLE.getRawTopic(topic);

    RawPublisher publisher = rawTopic.publish(typeString);
    Consumer<RawPublisher> publishUpdates = (pub) -> pub.set(getter.apply(container));

    RawSubscriber subscriber =
        rawTopic.subscribe(typeString, DEFAULT_RAW_VALUE, PubSubOption.excludePublisher(publisher));
    Consumer<RawSubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(container, update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind a static raw value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param topic The dashboard topic to bind to.
   * @param typeString The type string for the raw value.
   * @param getter The function to retrieve the raw value.
   * @param setter The function to update the raw value.
   */
  public static void bindStaticRaw(
      String topic, String typeString, Supplier<byte[]> getter, Consumer<byte[]> setter) {
    RawTopic rawTopic = TABLE.getRawTopic(topic);

    RawPublisher publisher = rawTopic.publish(typeString);
    Consumer<RawPublisher> publishUpdates = (pub) -> pub.set(getter.get());

    RawSubscriber subscriber =
        rawTopic.subscribe(typeString, DEFAULT_RAW_VALUE, PubSubOption.excludePublisher(publisher));
    Consumer<RawSubscriber> updateSubscriber =
        (sub) -> {
          for (var update : sub.readQueueValues()) {
            setter.accept(update);
          }
        };

    bindings.add(new DataBinding<>(publisher, publishUpdates, subscriber, updateSubscriber));
  }

  /**
   * Bind an enum value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param <E> The type of the enum.
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the enum value.
   * @param getter The function to retrieve the enum value from the container.
   * @param setter The function to update the enum value in the container.
   */
  public static <T, E extends Enum<E>> void bindEnum(
      String topic, T container, Function<T, E> getter, BiConsumer<T, E> setter) {
    var enumChooser = EnumChooser.fromDefault(getter.apply(container));

    enumChooser.onChange(v -> setter.accept(container, v));

    bindSendable(topic, enumChooser);
  }

  /**
   * Bind a static enum value to a dashboard topic that both publishes and subscribes to updates.
   *
   * @param <E> The type of the enum.
   * @param topic The dashboard topic to bind to.
   * @param getter The function to retrieve the enum value.
   * @param setter The function to update the enum value.
   */
  public static <E extends Enum<E>> void bindStaticEnum(
      String topic, Supplier<E> getter, Consumer<E> setter) {
    var enumChooser = EnumChooser.fromDefault(getter.get());

    enumChooser.onChange(v -> setter.accept(v));

    bindSendable(topic, enumChooser);
  }

  /**
   * Bind a {@link PreferenceValue} to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param preferenceValue The PreferenceValue to bind.
   */
  public static void bindPreferenceValue(String topic, PreferenceValue preferenceValue) {
    bindings.add(new PreferenceValueBinding(topic, preferenceValue));
  }

  /**
   * Bind a {@link Sendable} to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param sendable The Sendable to bind.
   */
  public static void bindSendable(String topic, Sendable sendable) {
    bindings.add(new SendableBinding(topic, sendable));
  }

  /**
   * Bind a {@link VideoSource} to a dashboard topic.
   *
   * @param topic The dashboard topic to bind to.
   * @param videoSource The VideoSource to bind.
   */
  public static void bindVideoSource(String topic, VideoSource videoSource) {
    bindSendable(topic, SendableCameraWrapper.wrap(videoSource));
  }

  /**
   * Bind the data in a layout container described by a layout definition.
   *
   * @param <T> The type of the layout container.
   * @param topic The dashboard topic to bind to.
   * @param container The object containing the layout container value.
   * @param binder The function to bind the layout container value to the layout data.
   */
  public static <T> void bindLayout(String topic, T container, BiConsumer<String, T> binder) {
    binder.accept(topic, container);
  }

  /** Update all bound dashboard data. */
  public static void updateAll() {
    for (var binding : bindings) {
      binding.update();
    }
  }

  /**
   * Start automatic updates of all bound dashboard data in the given robot's periodic loop.
   *
   * @param robot The robot whose periodic loop will be used for updates.
   */
  public static void startAutomaticUpdates(TimedRobot robot) {
    robot.addPeriodic(DashboardData::updateAll, robot.getPeriod());
  }
}
