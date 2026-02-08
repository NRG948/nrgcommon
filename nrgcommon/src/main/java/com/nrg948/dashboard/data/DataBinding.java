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

import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.networktables.Publisher;
import edu.wpi.first.networktables.Subscriber;
import java.util.Optional;

/**
 * An abstract base class for data bindings that bind a publisher and/or subscriber to dashboard
 * data updates.
 *
 * @param <P> The type of the publisher.
 * @param <S> The type of the subscriber.
 */
abstract class DataBinding<P extends Publisher, S extends Subscriber> extends DashboardData {
  private static final PubSubOption[] NO_OPTIONS = new PubSubOption[0];

  private Optional<P> publisher = Optional.empty();
  private Optional<S> subscriber = Optional.empty();
  private int enabledCount = 0;

  /**
   * Creates a new publisher for this binding.
   *
   * @return An optional containing the new publisher, or empty if no publisher is needed for this
   *     binding.
   */
  protected abstract Optional<P> newPublisher();

  /**
   * Creates a new subscriber for this binding with the given options.
   *
   * @param options The options to use when creating the subscriber.
   * @return An optional containing the new subscriber, or empty if no subscriber is needed for this
   *     binding.
   */
  protected abstract Optional<S> newSubscriber(PubSubOption... options);

  /**
   * Publishes updates to the dashboard using the given publisher.
   *
   * <p>This method will only be called if a publisher is present for this binding, and will be
   * called on every update cycle while the binding is enabled.
   *
   * @param publisher The publisher to use for publishing updates.
   */
  protected abstract void publishUpdates(P publisher);

  /**
   * Updates the subscriber with the latest data from the dashboard.
   *
   * <p>This method will only be called if a subscriber is present for this binding, and will be
   * called on every update cycle while the binding is enabled.
   *
   * @param subscriber The subscriber to update.
   */
  protected abstract void updateSubscriber(S subscriber);

  @Override
  public void enable() {
    if (enabledCount++ > 0) {
      return;
    }

    publisher = newPublisher();

    var options =
        publisher
            .map(p -> new PubSubOption[] {PubSubOption.excludePublisher(p)})
            .orElse(NO_OPTIONS);

    subscriber = newSubscriber(options);
  }

  @Override
  public void disable() {
    if (--enabledCount <= 0) {
      if (enabledCount < 0) {
        throw new IllegalStateException(
            getClass().getSimpleName() + " disabled more times than it was enabled");
      }
      return;
    }

    publisher.ifPresent(Publisher::close);
    subscriber.ifPresent(Subscriber::close);
    publisher = Optional.empty();
    subscriber = Optional.empty();
  }

  @Override
  protected void update() {
    subscriber.ifPresent(this::updateSubscriber);
    publisher.ifPresent(this::publishUpdates);
  }

  @Override
  public void close() {
    publisher.ifPresent(Publisher::close);
    subscriber.ifPresent(Subscriber::close);
  }
}
