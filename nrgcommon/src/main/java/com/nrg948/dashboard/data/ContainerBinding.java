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

import java.util.ArrayList;
import java.util.Arrays;

/** A container binding that holds multiple child bindings. */
abstract class ContainerBinding extends DashboardData {
  private ArrayList<DashboardData> childBindings = new ArrayList<>();

  /**
   * Constructs a ContainerBinding with the given child bindings.
   *
   * @param childBindings The child bindings to add to this container.
   */
  public ContainerBinding(DashboardData... childBindings) {
    this.childBindings.addAll(
        Arrays.stream(childBindings)
            .peek(
                c -> {
                  if (c == null) {
                    throw new IllegalArgumentException("Child binding cannot be null");
                  }
                })
            .toList());
  }

  /**
   * Adds a child binding to this container.
   *
   * @param childBinding The child binding to add.
   */
  protected void addChild(DashboardData childBinding) {
    if (childBinding == null) {
      throw new IllegalArgumentException("Child binding cannot be null");
    }

    childBindings.add(childBinding);
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
    for (var childBinding : childBindings) {
      childBinding.update();
    }
  }

  @Override
  public void close() {
    for (var childBinding : childBindings) {
      childBinding.close();
    }
  }
}
