/**
 * This package contains classes and interfaces for controlling various types of actuators,
 * including motor controllers from different manufacturers.
 *
 * <p>Each motor controller implementation is designed to provide a consistent interface for
 * controlling motors, regardless of the underlying hardware. This allows for easier integration and
 * code reuse across different projects and platforms.
 *
 * <p>Motor controller implementations in this package include adapters for REV Robotics Spark Max
 * and Spark Flex motor controllers, as well as CTRE Talon FX motor controllers. Each adapter
 * provides methods for setting motor output, configuring motor behavior, and logging relevant data
 * such as current and temperature.
 *
 * <p>The main {@code nrgcommon} module provides the core interfaces and classes for motor control
 * while manufacturer specific modules provide implementations for their specific motor controllers.
 * There are currently two manufacturer specific modules: {@code nrgcommon-rev} for REV Robotics
 * Spark Max and Spark Flex controllers and {@code nrgcommon-ctre} for CTRE Talon FX controllers.
 * Each module provides a set of adapters and utilities specific to the manufacturer's hardware.
 *
 * <p>To use a motor controller adapter, include the appropriate manufacturer-specific module as a
 * dependency in your project. You can then use the {@link Motors#newController(String, int,
 * MotorDirection, MotorIdleMode, double)} method to create a new motor controller instance with the
 * desired configuration.
 */
package com.nrg948.actuator;
