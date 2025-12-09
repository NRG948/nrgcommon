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

public interface DashboardElementVisitor {
  default void defaultAction(DashboardWidgetElement element) {}

  default void defaultAction(DashboardElementContainer containerElement) {
    for (var element : containerElement.getElements()) {
      element.accept(this);
    }
  }

  default void visit(DashboardTextDisplayElement textDisplayElement) {
    defaultAction(textDisplayElement);
  }

  default void visit(DashboardNumberSliderElement numberSliderElement) {
    defaultAction(numberSliderElement);
  }

  default void visit(DashboardNumberBarElement numberBarElement) {
    defaultAction(numberBarElement);
  }

  default void visit(DashboardVoltageViewElement voltageViewElement) {
    defaultAction(voltageViewElement);
  }

  default void visit(DashboardRadialGaugeElement radialGaugeElement) {
    defaultAction(radialGaugeElement);
  }

  default void visit(DashboardGraphElement graphElement) {
    defaultAction(graphElement);
  }

  default void visit(DashboardMatchTimeElement matchTimeElement) {
    defaultAction(matchTimeElement);
  }

  default void visit(DashboardBooleanBoxElement booleanBoxElement) {
    defaultAction(booleanBoxElement);
  }

  default void visit(DashboardToggleButtonElement toggleButtonElement) {
    defaultAction(toggleButtonElement);
  }

  default void visit(DashboardToggleSwitchElement toggleSwitchElement) {
    defaultAction(toggleSwitchElement);
  }

  default void visit(DashboardSingleColorViewElement singleColorViewElement) {
    defaultAction(singleColorViewElement);
  }

  default void visit(DashboardMultiColorViewElement multiColorViewElement) {
    defaultAction(multiColorViewElement);
  }

  default void visit(Dashboard3AxisAccelerometerElement accelerometerElement) {
    defaultAction(accelerometerElement);
  }

  default void visit(DashboardAccelerometerElement accelerometerElement) {
    defaultAction(accelerometerElement);
  }

  default void visit(DashboardCameraStreamElement cameraStreamElement) {
    defaultAction(cameraStreamElement);
  }

  default void visit(DashboardComboBoxChooserElement comboBoxChooserElement) {
    defaultAction(comboBoxChooserElement);
  }

  default void visit(DashboardSplitButtonChooserElement splitButtonChooserElement) {
    defaultAction(splitButtonChooserElement);
  }

  default void visit(DashboardCommandElement commandElement) {
    defaultAction(commandElement);
  }

  default void visit(DashboardSubsystemElement subsystemElement) {
    defaultAction(subsystemElement);
  }

  default void visit(DashboardDifferentialDriveElement differentialDriveElement) {
    defaultAction(differentialDriveElement);
  }

  default void visit(DashboardSwerveDriveElement swerveDriveElement) {
    defaultAction(swerveDriveElement);
  }

  default void visit(DashboardEncoderElement encoderElement) {
    defaultAction(encoderElement);
  }

  default void visit(DashboardFieldElement fieldElement) {
    defaultAction(fieldElement);
  }

  default void visit(DashboardFMSInfoElement fmsInfoElement) {
    defaultAction(fmsInfoElement);
  }

  default void visit(DashboardGyroElement gyroElement) {
    defaultAction(gyroElement);
  }

  default void visit(DashboardPIDControllerElement pidControllerElement) {
    defaultAction(pidControllerElement);
  }

  default void visit(DashboardPowerDistributionElement powerDistributionElement) {
    defaultAction(powerDistributionElement);
  }

  default void visit(DashboardRelayElement relayElement) {
    defaultAction(relayElement);
  }

  default void visit(DashboardUltrasonicElement ultrasonicElement) {
    defaultAction(ultrasonicElement);
  }

  default void visit(DashboardAlertsElement alertsElement) {
    defaultAction(alertsElement);
  }

  default void visit(DashboardLayoutElement layoutElement) {
    defaultAction(layoutElement);
  }

  default void visit(DashboardTabElement tabElement) {
    defaultAction(tabElement);
  }

  default void visit(DashboardElement rootElement) {
    defaultAction(rootElement);
  }
}
