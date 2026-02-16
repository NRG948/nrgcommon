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

/**
 * The {@code com.nrg948.dashboard} package contains classes and annotations for defining dashboard
 * elements and data bindings in a robotics program. A companion annotation processor provides
 * compile-time generation of an Elastic dashboard configuration and data binding code based on
 * annotated classes, fields and methods.
 *
 * <p>To use the dashboard annotations, add the {@code nrgcommon-processor} dependency in your robot
 * program's {@code build.gradle} file in addition to the {@code nrgcommon} library dependency.
 *
 * <pre>
 * <code>
 * dependencies {
 *   implementation "com.nrg948:nrgcommon:${nrgcommon.version}"
 *
 *   annotationProcessor "com.nrg948:nrgcommon-processor:${nrgcommon.version}"
 * }
 * </code>
 * </pre>
 *
 * <p>The Elastic configuration is generated at compile time in the generated build output
 * directory. Add the following to your {@code build.gradle} file to copy the generated {@code
 * elastic-dashboard.json} file to your robot program's {@code deploy} folder so it is included in
 * the robot code deployment.
 *
 * <pre>
 * <code>
 * // Copy the elastic-dashboard.json to the deploy folder after compilation.
 * task copyElasticConfig(type: Copy) {
 *   dependsOn compileJava
 *
 *   from("${buildDir}/generated/sources/annotationProcessor/java/main/deploy")
 *   include("elastic-*.json")
 *   into("${projectDir}/src/main/deploy")
 * }
 *
 * tasks.named('build') {
 *   dependsOn copyElasticConfig
 * }
 * </code>
 * </pre>
 *
 * The {@link DashboardDefinition} annotation is applied to a class and is used to define the
 * elements contained within a tab or layout in the dashboard. Fields and/or methods of the target
 * class are annotated with specific dashboard element annotations such as {@link
 * DashboardTextDisplay} or {@link DashboardRadialGauge} to define the individual elements within
 * the dashboard layout.
 *
 * <p>The following example shows how to use the dashboard annotations to create a layout that shows
 * the state of each swerve module. This will be incorporated into a tab layout in a later example.
 *
 * <pre>
 * <code>
 * {@literal @}DashboardDefinition
 * public class SwerveModule {
 *   private SwerveModuleState state;
 *
 *   public SwerveModuleState getState() {
 *     return state;
 *   }
 *
 *   {@literal @}DashboardRadialGauge(
 *       title = "Wheel Angle (deg)",
 *       startAngle = -180,
 *       endAngle = 180,
 *       min = -180,
 *       max = 180,
 *       numberOfLabels = 0,
 *       showPointer = true)
 *   public double getWheelAngleDegrees() {
 *     return getState().angle.getDegrees();
 *   }
 *
 *   {@literal @}DashboardTextDisplay(title = "Wheel Velocity (m/s)")
 *   public double getWheelVelocityMetersPerSecond() {
 *     return getState().speedMetersPerSecond;
 *   }
 *
 *   // The rest of the module implementation is omitted for brevity.
 * }
 * </code>
 * </pre>
 *
 * <p>The following example shows how to annotate a {@code SwerveDrive} class to define the contents
 * of a "Swerve Drive" tab. It incorporates the module layouts as well as additional dashboard
 * elements.
 *
 * <pre>
 * <code>
 * {@literal @}DashboardDefinition
 * public class SwerveDrive {
 *   private final SwerveDriveOdometry odometry;
 *
 *   {@literal @}DashboardLayout(title = "Front Left Module", column = 0, row = 0, width = 2, height = 3)
 *   private SwerveModule frontLeftModule;
 *   {@literal @}DashboardLayout(title = "Front Right Module", column = 0, row = 3, width = 2, height = 3)
 *   private SwerveModule frontRightModule;
 *   {@literal @}DashboardLayout(title = "Back Left Module", column = 2, row = 0, width = 2, height = 3)
 *   private SwerveModule backLeftModule;
 *   {@literal @}DashboardLayout(title = "Back Right Module", column = 2, row = 3, width = 2, height = 3)
 *   private SwerveModule backRightModule;
 *
 *   {@literal @}DashboardRadialGauge(
 *     title = "Robot Heading (deg)",
 *     column = 4,
 *     row = 0,
 *     width = 2,
 *     height = 3,
 *     startAngle = -180,
 *     endAngle = 180,
 *     min = -180,
 *     max = 180,
 *     showPointer = true)
 *   public double getRobotHeadingDegrees() {
 *     return odometry.getPoseMeters().getRotation().getDegrees();
 *   }
 *
 *   {@literal @}DashboardTextDisplay(title = "Robot X Position (m)", column = 4, row = 3, width = 2, height = 1)
 *   public double getRobotXPositionMeters() {
 *     return odometry.getPoseMeters().getTranslation().getX();
 *   }
 *
 *   {@literal @}DashboardTextDisplay(title = "Robot Y Position (m)", column = 4, row = 4, width = 2, height = 1)
 *   public double getRobotYPositionMeters() {
 *     return odometry.getPoseMeters().getTranslation().getY();
 *   }
 *
 *   // The rest of the swerve drive implementation is omitted for brevity.
 * }
 * </code>
 * </pre>
 *
 * <p>The following example creates a "Swerve Drive" tab that incorporates the {@code SwerveDrive}
 * dashboard definition.
 *
 * <pre>
 * <code>
 * public class RobotContainer {
 *   {@literal @}DashboardTab(title = "Swerve Drive")
 *   private final SwerveDrive swerveDrive = new SwerveDrive();
 *
 *   public RobotContainer() {
 *     RobotContainerDashboardTabs.bind(this);
 *   }
 *
 *  // The rest of the RobotContainer implementation is omitted for brevity.
 * }
 * </code>
 * </pre>
 *
 * <p>Note that you must call the {@code RobotContainerDashboardTabs.bind(this)} in the {@code
 * RobotContainer} constructor to enable binding the dashboard tab data. The annotation processor
 * creates a {@code XXXDashboardTabs} class where {@code XXX} is the name of the class annotated
 * with {@link DashboardTab}.
 *
 * <p>Finally, apply the {@link Dashboard} annotation to your {@code Robot} class and call {@code
 * DashboardServer.start(this)} in the its constructor to enable the dashboard functionality. In
 * addition to binding the dashboard elements to their source fields and methods, this will also
 * start the web server that serves the dashboard configuration file to Elastic.
 *
 * <pre>
 * <code>
 * {@literal @}Dashboard
 * public class Robot extends TimedRobot {
 *   private final AutoCloseable dashboardServer;
 *
 *   public Robot() {
 *     dashboardServer = DashboardServer.start(this);
 *   }
 *
 *   // The rest of the Robot implementation is omitted for brevity.
 * }
 * </code>
 * </pre>
 */
package com.nrg948.dashboard;

import com.nrg948.dashboard.annotations.Dashboard;
import com.nrg948.dashboard.annotations.DashboardDefinition;
import com.nrg948.dashboard.annotations.DashboardRadialGauge;
import com.nrg948.dashboard.annotations.DashboardTab;
import com.nrg948.dashboard.annotations.DashboardTextDisplay;
