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
package com.nrg948.preferences;

import edu.wpi.first.wpilibj.Preferences;

/** Utility class for managing WPILib Preferences. */
public final class PreferencesUtil {
  /**
   * Writes all default preference values to the WPILib Preferences storage. This will overwrite any
   * existing values.
   */
  public static void writeDefaultPreferences() {
    Preferences.removeAll(); // Clear existing preferences

    PreferenceValue.visitAll(
        new PreferenceValueVisitor() {
          private static void printMessage(PreferenceValue preference, String value) {
            System.out.println(
                "WRITING DEFAULT VALUE: "
                    + preference.getGroup()
                    + "/"
                    + preference.getName()
                    + " = "
                    + value);
          }

          @Override
          public void visit(StringPreference preference) {
            printMessage(preference, preference.getDefaultValue());
            preference.setValue(preference.getDefaultValue());
          }

          @Override
          public void visit(BooleanPreference preference) {
            printMessage(preference, Boolean.toString(preference.getDefaultValue()));
            preference.setValue(preference.getDefaultValue());
          }

          @Override
          public void visit(DoublePreference preference) {
            printMessage(preference, Double.toString(preference.getDefaultValue()));
            preference.setValue(preference.getDefaultValue());
          }

          @Override
          public <E extends Enum<E>> void visit(EnumPreference<E> preference) {
            printMessage(preference, preference.getDefaultValue().name());
            preference.setValue(preference.getDefaultValue());
          }

          @Override
          public void visit(PIDControllerPreference preference) {
            printMessage(
                preference,
                String.format(
                    "kP: %.4f, kI: %.4f, kD: %.4f",
                    preference.getDefaultP(), preference.getDefaultI(), preference.getDefaultD()));
            preference.setP(preference.getDefaultP());
            preference.setI(preference.getDefaultI());
            preference.setD(preference.getDefaultD());
          }
        });
  }

  /** Prints all non-default preference values to the console. */
  public static void printNonDefaultPreferences() {
    PreferenceValue.visitAll(
        new PreferenceValueVisitor() {
          private static void printMessage(PreferenceValue preference, String value) {
            System.out.println(
                "NON-DEFAULT VALUE: "
                    + preference.getGroup()
                    + "/"
                    + preference.getName()
                    + " = "
                    + value);
          }

          @Override
          public void visit(StringPreference preference) {
            if (!preference.getValue().equals(preference.getDefaultValue())) {
              printMessage(preference, preference.getValue());
            }
          }

          @Override
          public void visit(BooleanPreference preference) {
            if (preference.getValue() != preference.getDefaultValue()) {
              printMessage(preference, Boolean.toString(preference.getValue()));
            }
          }

          @Override
          public void visit(DoublePreference preference) {
            if (preference.getValue() != preference.getDefaultValue()) {
              printMessage(preference, Double.toString(preference.getValue()));
            }
          }

          @Override
          public <E extends Enum<E>> void visit(EnumPreference<E> preference) {
            if (preference.getValue() != preference.getDefaultValue()) {
              printMessage(preference, preference.getValue().name());
            }
          }

          @Override
          public void visit(PIDControllerPreference preference) {
            boolean isNonDefault =
                preference.getP() != preference.getDefaultP()
                    || preference.getI() != preference.getDefaultI()
                    || preference.getD() != preference.getDefaultD();

            if (isNonDefault) {
              printMessage(
                  preference,
                  String.format(
                      " kP: %.4f, kI: %.4f, kD: %.4f",
                      preference.getP(), preference.getI(), preference.getD()));
            }
          }
        });
  }

  private PreferencesUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}
