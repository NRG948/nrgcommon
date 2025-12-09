/*
  MIT License

  Copyright (c) 2025 Newport Robotics Group

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
package com.nrg948.dashboard.model;

/** Enum representing different game fields. */
public enum GameField {
  /** The Reefscape game field. */
  REEFSCAPE("Reefscape"),
  /** The Crescendo game field. */
  CRESCENDO("Crescendo"),
  /** The Charged Up game field. */
  CHARGED_UP("Charged Up"),
  /** The Rapid React game field. */
  RAPID_REACT("Rapid React"),
  /** The Infinite Recharge game field. */
  INFINITE_RECHARGE("Infinite Recharge"),
  /** The Destination: Deep Space game field. */
  DESTINATION_DEEP_SPACE("Destination: Deep Space"),
  /** The Power Up game field. */
  POWER_UP("Power Up");

  private final String displayName;

  /** Constructor for the GameField enum. */
  GameField(String displayName) {
    this.displayName = displayName;
  }

  /** Returns the display name of the game field. */
  public String getDisplayName() {
    return displayName;
  }

  /** Returns the JSON value of the game field. */
  public String toJsonValue() {
    return displayName;
  }
}
