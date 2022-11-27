// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.nrg948.autonomous;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.util.ReflectionUtilsPredicates.withAnnotation;

import java.lang.reflect.InvocationTargetException;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;

/** A class containing utility methods to support autonomous operation. */
public final class Autonomous {

  /**
   * Returns a {@link SendableChooser} object enabling interactive selection of
   * autonomous commands annotated with {@link AutonomousCommand}.
   * 
   * @param <T>       The container type.
   * @param container An object passed to the constructor of the automonous
   *                  commands providing access to the robot subsystems. This
   *                  is typically an instance of RobotContainer but could be
   *                  another type that manages the subsystems.
   * @param pkgs      The packages to scan for {@link Command} subclasses
   *                  annotated with {@link AutonomousCommand}.
   * 
   * @return A {@link SendableChooser} object containing the autonomous commands.
   */
  public static <T> SendableChooser<Command> getChooser(T container, String... pkgs) {
    Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(pkgs));
    SendableChooser<Command> chooser = new SendableChooser<>();
    Class<? extends Object> containerClass = container.getClass();

    reflections.get(SubTypes.of(Command.class).asClass().filter(withAnnotation(AutonomousCommand.class)))
        .stream()
        .forEach(cc -> {
          try {
            AutonomousCommand annotation = cc.getAnnotation(AutonomousCommand.class);
            Command command = (Command) cc.getConstructor(containerClass).newInstance(container);

            chooser.addOption(annotation.name(), command);

            if (annotation.isDefault()) {
              chooser.setDefaultOption(annotation.name(), command);
            }
          } catch (NoSuchMethodException e) {
            System.err.printf(
                "ERROR: Class %s does not define the public constructor: %s(%s)%n",
                cc.getName(),
                cc.getSimpleName(),
                containerClass.getSimpleName());
            e.printStackTrace();
          } catch (
              InstantiationException
              | IllegalAccessException
              | IllegalArgumentException
              | ClassCastException
              | InvocationTargetException
              | SecurityException e) {
            System.err.printf(
                "ERROR: An unexpected exception was caught while creating an instance of %s.%n",
                cc.getName());
            e.printStackTrace();
          }
        });

    return chooser;
  }

  private Autonomous() {

  }
}
