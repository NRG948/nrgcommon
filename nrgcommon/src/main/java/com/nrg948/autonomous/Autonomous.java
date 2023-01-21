/*
  MIT License

  Copyright (c) 2022 Newport Robotics Group

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

package com.nrg948.autonomous;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.MethodsAnnotated;
import static org.reflections.util.ReflectionUtilsPredicates.withAnnotation;
import static org.reflections.util.ReflectionUtilsPredicates.withStatic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;

/** A class containing utility methods to support autonomous operation. */
public final class Autonomous {

  /**
   * An interface for creating autonomous commands to add to the
   * {@link SendableChooser}.
   *
   * @param <T> The subsystem container type. The container is an object passed to
   *            the constructor of the autonomous commands providing access to the
   *            robot subsystems. This is typically an instance of
   *            <code>RobotContainer</code>, but could be another type used to
   *            manage the subsystems.
   */
  private interface CommandFactory<T> extends Comparable<CommandFactory<T>> {
    /**
     * The name to display for the annotated {@link Command} in the
     * {@link SendableChooser} returned by
     * {@link Autonomous#getChooser(Object, String...)}.
     * 
     * @return The display name.
     */
    String getName();

    /**
     * Whether this command is the default {@link Command} in the
     * {@link SendableChooser} returned by
     * {@link Autonomous#getChooser(Object, String...)}.
     * 
     * @return Returns true if this is the default command, and false otherwise.
     */
    boolean isDefault();

    /**
     * Returns an instance of the {@link Command} to add to the
     * {@link SendableChooser}.
     * 
     * @param container An object passed to the constructor of the automonous
     *                  commands providing access to the robot subsystems. This
     *                  is typically an instance of <code>RobotContainer</code> but
     *                  could be another type that manages the subsystems.
     * @return
     */
    Command newCommand(T container);

    @Override
    default int compareTo(CommandFactory<T> obj) {
      return getName().compareTo(obj.getName());
    }
  }

  /**
   * Returns a {@link SendableChooser} object enabling interactive selection of
   * autonomous commands annotated with {@link AutonomousCommand}.
   * 
   * @param <T>       The subsystem container type.
   * @param container An object passed to the constructor of the automonous
   *                  commands providing access to the robot subsystems. This
   *                  is typically an instance of <code>RobotContainer</code> but
   *                  could be another type that manages the subsystems.
   * @param pkgs      The packages to scan for {@link Command} subclasses
   *                  annotated with {@link AutonomousCommand}.
   * 
   * @return A {@link SendableChooser} object containing the autonomous commands.
   */
  public static <T> SendableChooser<Command> getChooser(T container, String... pkgs) {
    Reflections reflections = new Reflections(
        new ConfigurationBuilder()
            .forPackages(pkgs)
            .addScanners(SubTypes, MethodsAnnotated));
    SendableChooser<Command> chooser = new SendableChooser<>();

    Stream<CommandFactory<T>> commandClasses = reflections
        .get(SubTypes
            .of(Command.class)
            .asClass()
            .filter(withAnnotation(AutonomousCommand.class)))
        .stream()
        .map(Autonomous::<T>toCommandFactory);
    Stream<CommandFactory<T>> commandMethods = reflections
        .get(MethodsAnnotated
            .with(AutonomousCommandMethod.class)
            .as(Method.class)
            .filter(withStatic()))
        .stream()
        .map(Autonomous::<T>toCommandFactory);

    Stream.concat(commandClasses, commandMethods)
        .sorted()
        .forEach((CommandFactory<T> commandFactory) -> {
          Command command = commandFactory.newCommand(container);

          chooser.addOption(commandFactory.getName(), command);

          if (commandFactory.isDefault()) {
            chooser.setDefaultOption(commandFactory.getName(), command);
          }
        });

    return chooser;
  }

  /**
   * Returns a {@link CommandFactory} implementation used to create a
   * {@link Command} to add to the {@link SendableChooser}.
   * 
   * @param <T>          The subsystem container type.
   * @param commandClass The {@link Class} object of the {@link Command} created
   *                     by this instance.
   * 
   * @return A {@link CommandFactory} implementation used to create a
   *         {@link Command} to add to the {@link SendableChooser}.
   */
  private static <T> CommandFactory<T> toCommandFactory(Class<?> commandClass) {
    AutonomousCommand annotation = commandClass.getAnnotation(AutonomousCommand.class);

    return new CommandFactory<T>() {
      @Override
      public String getName() {
        return annotation.name();
      }

      @Override
      public boolean isDefault() {
        return annotation.isDefault();
      }

      @Override
      public Command newCommand(T container) {
        Class<? extends Object> containerClass = container.getClass();

        try {
          return (Command) commandClass.getConstructor(containerClass).newInstance(container);

        } catch (NoSuchMethodException e) {
          System.err.printf(
              "ERROR: Class %s does not define the public constructor: %s(%s)%n",
              commandClass.getName(),
              commandClass.getSimpleName(),
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
              commandClass.getName());
          e.printStackTrace();
        }

        return null;
      }
    };
  }

  /**
   * Returns a {@link CommandFactory} implementation used to create a
   * {@link Command} to add to the {@link SendableChooser}.
   * 
   * @param <T>           The subsystem container type.
   * @param commandMethod The {@link Method} object invoked to create the
   *                      {@link Command} of this instance.
   * 
   * @return A {@link CommandFactory} implementation used to create a
   *         {@link Command} to add to the {@link SendableChooser}.
   */
  private static <T> CommandFactory<T> toCommandFactory(Method commandMethod) {
    AutonomousCommandMethod annotation = commandMethod.getAnnotation(AutonomousCommandMethod.class);

    return new CommandFactory<T>() {
      @Override
      public String getName() {
        return annotation.name();
      }

      @Override
      public boolean isDefault() {
        return annotation.isDefault();
      }

      @Override
      public Command newCommand(T container) {
        Class<? extends Object> containerClass = container.getClass();

        try {
          return (Command) commandMethod.invoke(null, container);

        } catch (InvocationTargetException e) {
          System.err.printf(
              "ERROR: Method %s does not take a single parameter of type %s.%n",
              commandMethod.getName(),
              containerClass.getSimpleName());
          e.printStackTrace();
        } catch (ClassCastException e) {
          System.err.printf(
              "ERROR: Method %s returns %s instead of a Command.%n",
              commandMethod.getName(),
              commandMethod.getReturnType().getSimpleName(),
              containerClass.getSimpleName());
          e.printStackTrace();
        } catch (
            IllegalAccessException
            | IllegalArgumentException
            | SecurityException e) {
          System.err.printf(
              "ERROR: An unexpected exception was caught while creating an instance of %s.%n",
              containerClass.getName());
          e.printStackTrace();
        }

        return null;
      }
    };
  }

  private Autonomous() {

  }
}
