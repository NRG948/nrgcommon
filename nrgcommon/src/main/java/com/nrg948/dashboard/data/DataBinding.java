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

import edu.wpi.first.networktables.Publisher;
import edu.wpi.first.networktables.Subscriber;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A data binding that binds a publisher and/or subscriber to dashboard data updates.
 *
 * @param <P> The type of the publisher.
 * @param <S> The type of the subscriber.
 */
final class DataBinding<P extends Publisher, S extends Subscriber> extends DashboardData {
  private final Optional<P> publisher;
  private final Optional<Consumer<P>> publishUpdates;
  private final Optional<S> subscriber;
  private final Optional<Consumer<S>> updateSubscriber;

  /**
   * Constructs a DataBinding with the given publisher, publishUpdates function, subscriber, and
   * updateSubscriber function.
   *
   * @param publisher The publisher to bind.
   * @param publishUpdates The function to update the publisher.
   * @param subscriber The subscriber to bind.
   * @param updateSubscriber The function to update the subscriber.
   */
  public DataBinding(
      Optional<P> publisher,
      Optional<Consumer<P>> publishUpdates,
      Optional<S> subscriber,
      Optional<Consumer<S>> updateSubscriber) {
    this.publisher = publisher;
    this.publishUpdates = publishUpdates;
    this.subscriber = subscriber;
    this.updateSubscriber = updateSubscriber;

    if (publisher.isPresent() != publishUpdates.isPresent()) {
      throw new IllegalArgumentException(
          "The publisher and publishUpdates arguments must both be present or both be absent.");
    }

    if (subscriber.isPresent() != updateSubscriber.isPresent()) {
      throw new IllegalArgumentException(
          "The subscriber and readUpdates arguments must both be present or both be absent.");
    }
  }

  /**
   * Constructs a DataBinding with only a publisher.
   *
   * @param publisher The publisher to bind.
   * @param publishUpdates The function to update the publisher.
   */
  public DataBinding(P publisher, Consumer<P> publishUpdates) {
    this(Optional.of(publisher), Optional.of(publishUpdates), Optional.empty(), Optional.empty());
  }

  /**
   * Constructs a DataBinding with both a publisher and a subscriber.
   *
   * @param publisher The publisher to bind.
   * @param publishUpdates The function to update the publisher.
   * @param subscriber The subscriber to bind.
   * @param updateSubscriber The function to update the subscriber.
   */
  public DataBinding(
      P publisher, Consumer<P> publishUpdates, S subscriber, Consumer<S> updateSubscriber) {
    this(
        Optional.of(publisher),
        Optional.of(publishUpdates),
        Optional.of(subscriber),
        Optional.of(updateSubscriber));
  }

  @Override
  protected void update() {
    subscriber.ifPresent(sub -> updateSubscriber.get().accept(sub));
    publisher.ifPresent(pub -> publishUpdates.get().accept(pub));
  }

  @Override
  public void close() {
    publisher.ifPresent(Publisher::close);
    subscriber.ifPresent(Subscriber::close);
  }
}
