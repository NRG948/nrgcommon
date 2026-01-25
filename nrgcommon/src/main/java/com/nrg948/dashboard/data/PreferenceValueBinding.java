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

import com.nrg948.preferences.BooleanPreference;
import com.nrg948.preferences.DoublePreference;
import com.nrg948.preferences.EnumPreference;
import com.nrg948.preferences.PIDControllerPreference;
import com.nrg948.preferences.PreferenceValue;
import com.nrg948.preferences.PreferenceValueVisitor;
import com.nrg948.preferences.ProfiledPIDControllerPreference;
import com.nrg948.preferences.StringPreference;

/** A data binding that binds a preference value to the dashboard. */
final class PreferenceValueBinding extends DashboardData implements PreferenceValueVisitor {
  private final String topic;

  /**
   * Constructs an instance of this class.
   *
   * @param topic The dashboard topic to bind to.
   * @param preferenceValue The preference value to bind.
   */
  public PreferenceValueBinding(String topic, PreferenceValue preferenceValue) {
    this.topic = topic;

    preferenceValue.accept(this);
  }

  @Override
  public void visit(StringPreference stringPreference) {
    bindString(topic, stringPreference, StringPreference::getValue, StringPreference::setValue);
  }

  @Override
  public void visit(BooleanPreference booleanPreference) {
    bindBoolean(topic, booleanPreference, BooleanPreference::getValue, BooleanPreference::setValue);
  }

  @Override
  public void visit(DoublePreference value) {
    bindDouble(topic, value, DoublePreference::getValue, DoublePreference::setValue);
  }

  @Override
  public <E extends Enum<E>> void visit(EnumPreference<E> enumPreference) {
    bindEnum(topic, enumPreference, EnumPreference::getValue, EnumPreference::setValue);
  }

  @Override
  public void visit(PIDControllerPreference pidControllerPreference) {
    bindSendable(topic, pidControllerPreference);
  }

  @Override
  public void visit(ProfiledPIDControllerPreference pidControllerPreference) {
    bindSendable(topic, pidControllerPreference);
  }

  @Override
  protected void update() {
    // Updates are handled by the individual bindings.
  }

  @Override
  public void close() {
    // Nothing to do here since this generates no closeable bindings.
  }
}
