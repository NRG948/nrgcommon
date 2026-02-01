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

/** Visitor interface for dashboard elements. */
public interface DashboardElementVisitor {
  /**
   * Default action for dashboard widget elements.
   *
   * @param element the dashboard widget element
   */
  default void defaultAction(DashboardWidgetElement element) {}

  /**
   * Default action for dashboard container elements.
   *
   * @param containerElement the dashboard container element
   */
  default void defaultAction(DashboardElementContainer containerElement) {
    for (var element : containerElement.getElements()) {
      element.accept(this);
    }
  }

  /**
   * Visit a dashboard text display element.
   *
   * @param textDisplayElement the dashboard text display element
   */
  default void visit(DashboardTextDisplayElement textDisplayElement) {
    defaultAction(textDisplayElement);
  }

  /**
   * Visit a dashboard number slider element.
   *
   * @param numberSliderElement the dashboard number slider element
   */
  default void visit(DashboardNumberSliderElement numberSliderElement) {
    defaultAction(numberSliderElement);
  }

  /**
   * Visit a dashboard number bar element.
   *
   * @param numberBarElement the dashboard number bar element
   */
  default void visit(DashboardNumberBarElement numberBarElement) {
    defaultAction(numberBarElement);
  }

  /**
   * Visit a dashboard voltage view element.
   *
   * @param voltageViewElement the dashboard voltage view element
   */
  default void visit(DashboardVoltageViewElement voltageViewElement) {
    defaultAction(voltageViewElement);
  }

  /**
   * Visit a dashboard radial gauge element.
   *
   * @param radialGaugeElement the dashboard radial gauge element
   */
  default void visit(DashboardRadialGaugeElement radialGaugeElement) {
    defaultAction(radialGaugeElement);
  }

  /**
   * Visit a dashboard graph element.
   *
   * @param graphElement the dashboard graph element
   */
  default void visit(DashboardGraphElement graphElement) {
    defaultAction(graphElement);
  }

  /**
   * Visit a dashboard match time element.
   *
   * @param matchTimeElement the dashboard match time element
   */
  default void visit(DashboardMatchTimeElement matchTimeElement) {
    defaultAction(matchTimeElement);
  }

  /**
   * Visit a dashboard boolean box element.
   *
   * @param booleanBoxElement the dashboard boolean box element
   */
  default void visit(DashboardBooleanBoxElement booleanBoxElement) {
    defaultAction(booleanBoxElement);
  }

  /**
   * Visit a dashboard toggle button element.
   *
   * @param toggleButtonElement the dashboard toggle button element
   */
  default void visit(DashboardToggleButtonElement toggleButtonElement) {
    defaultAction(toggleButtonElement);
  }

  /**
   * Visit a dashboard toggle switch element.
   *
   * @param toggleSwitchElement the dashboard toggle switch element
   */
  default void visit(DashboardToggleSwitchElement toggleSwitchElement) {
    defaultAction(toggleSwitchElement);
  }

  /**
   * Visit a dashboard single color view element.
   *
   * @param singleColorViewElement the dashboard single color view element
   */
  default void visit(DashboardSingleColorViewElement singleColorViewElement) {
    defaultAction(singleColorViewElement);
  }

  /**
   * Visit a dashboard multi color view element.
   *
   * @param multiColorViewElement the dashboard multi color view element
   */
  default void visit(DashboardMultiColorViewElement multiColorViewElement) {
    defaultAction(multiColorViewElement);
  }

  /**
   * Visit a dashboard 3-axis accelerometer element.
   *
   * @param accelerometerElement the dashboard 3-axis accelerometer element
   */
  default void visit(Dashboard3AxisAccelerometerElement accelerometerElement) {
    defaultAction(accelerometerElement);
  }

  /**
   * Visit a dashboard accelerometer element.
   *
   * @param accelerometerElement the dashboard accelerometer element
   */
  default void visit(DashboardAccelerometerElement accelerometerElement) {
    defaultAction(accelerometerElement);
  }

  /**
   * Visit a dashboard camera stream element.
   *
   * @param cameraStreamElement the dashboard camera stream element
   */
  default void visit(DashboardCameraStreamElement cameraStreamElement) {
    defaultAction(cameraStreamElement);
  }

  /**
   * Visit a dashboard combo box chooser element.
   *
   * @param comboBoxChooserElement the dashboard combo box chooser element
   */
  default void visit(DashboardComboBoxChooserElement comboBoxChooserElement) {
    defaultAction(comboBoxChooserElement);
  }

  /**
   * Visit a dashboard split button chooser element.
   *
   * @param splitButtonChooserElement the dashboard split button chooser element
   */
  default void visit(DashboardSplitButtonChooserElement splitButtonChooserElement) {
    defaultAction(splitButtonChooserElement);
  }

  /**
   * Visit a dashboard command element.
   *
   * @param commandElement the dashboard command element
   */
  default void visit(DashboardCommandElement commandElement) {
    defaultAction(commandElement);
  }

  /**
   * Visit a dashboard subsystem element.
   *
   * @param subsystemElement the dashboard subsystem element
   */
  default void visit(DashboardSubsystemElement subsystemElement) {
    defaultAction(subsystemElement);
  }

  /**
   * Visit a dashboard differential drive element.
   *
   * @param differentialDriveElement the dashboard differential drive element
   */
  default void visit(DashboardDifferentialDriveElement differentialDriveElement) {
    defaultAction(differentialDriveElement);
  }

  /**
   * Visit a dashboard swerve drive element.
   *
   * @param swerveDriveElement the dashboard swerve drive element
   */
  default void visit(DashboardSwerveDriveElement swerveDriveElement) {
    defaultAction(swerveDriveElement);
  }

  /**
   * Visit a dashboard encoder element.
   *
   * @param encoderElement the dashboard encoder element
   */
  default void visit(DashboardEncoderElement encoderElement) {
    defaultAction(encoderElement);
  }

  /**
   * Visit a dashboard field element.
   *
   * @param fieldElement the dashboard field element
   */
  default void visit(DashboardFieldElement fieldElement) {
    defaultAction(fieldElement);
  }

  /**
   * Visit a dashboard FMS info element.
   *
   * @param fmsInfoElement the dashboard FMS info element
   */
  default void visit(DashboardFMSInfoElement fmsInfoElement) {
    defaultAction(fmsInfoElement);
  }

  /**
   * Visit a dashboard gyro element.
   *
   * @param gyroElement the dashboard gyro element
   */
  default void visit(DashboardGyroElement gyroElement) {
    defaultAction(gyroElement);
  }

  /**
   * Visit a dashboard PID controller element.
   *
   * @param pidControllerElement the dashboard PID controller element
   */
  default void visit(DashboardPIDControllerElement pidControllerElement) {
    defaultAction(pidControllerElement);
  }

  /**
   * Visit a dashboard power distribution element.
   *
   * @param powerDistributionElement the dashboard power distribution element
   */
  default void visit(DashboardPowerDistributionElement powerDistributionElement) {
    defaultAction(powerDistributionElement);
  }

  /**
   * Visit a dashboard relay element.
   *
   * @param relayElement the dashboard relay element
   */
  default void visit(DashboardRelayElement relayElement) {
    defaultAction(relayElement);
  }

  /**
   * Visit a dashboard ultrasonic element.
   *
   * @param ultrasonicElement the dashboard ultrasonic element
   */
  default void visit(DashboardUltrasonicElement ultrasonicElement) {
    defaultAction(ultrasonicElement);
  }

  /**
   * Visit a dashboard alerts element.
   *
   * @param alertsElement the dashboard alerts element
   */
  default void visit(DashboardAlertsElement alertsElement) {
    defaultAction(alertsElement);
  }

  /**
   * Visit a dashboard layout element.
   *
   * @param layoutElement the dashboard layout element
   */
  default void visit(DashboardLayoutElement layoutElement) {
    defaultAction(layoutElement);
  }

  /**
   * Visit a dashboard tab element.
   *
   * @param tabElement the dashboard tab element
   */
  default void visit(DashboardTabElement tabElement) {
    defaultAction(tabElement);
  }

  /**
   * Visit the root dashboard element.
   *
   * @param rootElement the root dashboard element
   */
  default void visit(DashboardElement rootElement) {
    defaultAction(rootElement);
  }
}
