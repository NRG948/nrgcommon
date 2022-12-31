// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.nrg948.autonomous;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Annotates a class implementing a subclass of {@link Command} to run during
 * autonomous.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutonomousCommand {
    /**
     * The name to display for the annotated {@link Command} in the
     * {@link SendableChooser} returned by
     * {@link Autonomous#getChooser(Object, String...)}.
     * 
     * @return The display name.
     */
    String name();

    /**
     * Whether this command is the default {@link Command} in the
     * {@link SendableChooser} returned by
     * {@link Autonomous#getChooser(Object, String...)}.
     * 
     * @return Returns true if this is the default command, and false otherwise.
     */
    boolean isDefault() default false;
}
