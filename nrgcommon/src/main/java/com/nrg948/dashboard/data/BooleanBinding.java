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

import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.BooleanTopic;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.util.function.BooleanConsumer;
import java.util.Optional;
import java.util.function.BooleanSupplier;

/** A data binding that binds a boolean publisher and/or subscriber to dashboard data updates. */
final class BooleanBinding extends DataBinding<BooleanPublisher, BooleanSubscriber> {
  private static final boolean DEFAULT_VALUE = false;

  private final BooleanTopic topic;
  private final Optional<BooleanSupplier> supplier;
  private final Optional<BooleanConsumer> consumer;

  /**
   * Creates a new BooleanBinding with the given topic and supplier.
   *
   * @param topic The topic to bind to.
   * @param supplier The supplier to use for publishing updates, or null if no publisher is needed
   *     for this binding.
   */
  public BooleanBinding(BooleanTopic topic, BooleanSupplier supplier) {
    this(topic, supplier, null);
  }

  /**
   * Creates a new BooleanBinding with the given topic, supplier, and consumer.
   *
   * @param topic The topic to bind to.
   * @param supplier The supplier to use for publishing updates, or null if no publisher is needed
   *     for this binding.
   * @param consumer The consumer to use for updating the subscriber, or null if no subscriber is
   *     needed for this binding.
   */
  public BooleanBinding(BooleanTopic topic, BooleanSupplier supplier, BooleanConsumer consumer) {
    this.topic = topic;
    this.supplier = Optional.ofNullable(supplier);
    this.consumer = Optional.ofNullable(consumer);
  }

  @Override
  protected Optional<BooleanPublisher> newPublisher() {
    return supplier.map(s -> topic.publish());
  }

  @Override
  protected Optional<BooleanSubscriber> newSubscriber(PubSubOption... options) {
    return consumer.map(c -> topic.subscribe(DEFAULT_VALUE, options));
  }

  @Override
  protected void publishUpdates(BooleanPublisher publisher) {
    publisher.set(supplier.get().getAsBoolean());
  }

  @Override
  protected void updateSubscriber(BooleanSubscriber subscriber) {
    for (var value : subscriber.readQueueValues()) {
      consumer.get().accept(value);
    }
  }
}
