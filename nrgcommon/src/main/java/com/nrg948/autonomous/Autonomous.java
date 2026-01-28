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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrg948.util.ReflectionUtil;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import io.arxila.javatuples.LabelValue;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/** A class containing utility methods to support autonomous operation. */
public final class Autonomous {
  static final String CONFIGURATION_RESOURCE_NAME = "META-INF/nrgcommon/autonomous.json";

  private static Autonomous instance;

  private final Class<?>[] commands;
  private final Method[] methods;
  private final Method[] generators;

  /**
   * Creates an {@link Autonomous} instance from JSON data.
   *
   * @param commands An array of fully qualified class names of {@link Command} subclasses annotated
   *     with {@link AutonomousCommand}.
   * @param methods An array of fully qualified method names of static methods annotated with {@link
   *     AutonomousCommandMethod}.
   * @param generators An array of fully qualified method names of static methods annotated with
   *     {@link AutonomousCommandGenerator}.
   */
  @JsonCreator
  Autonomous(
      @JsonProperty(value = "commands", required = true) String[] commands,
      @JsonProperty(value = "methods", required = true) String[] methods,
      @JsonProperty(value = "generators", required = true) String[] generators) {
    this.commands =
        Arrays.stream(commands)
            .map(Autonomous::getClass)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toArray(Class<?>[]::new);

    this.methods =
        Arrays.stream(methods)
            .map(Autonomous::getMethod)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toArray(Method[]::new);

    this.generators =
        Arrays.stream(generators)
            .map(Autonomous::getMethod)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toArray(Method[]::new);
  }

  /** Creates an empty {@link Autonomous} instance. */
  private Autonomous() {
    this.commands = new Class<?>[0];
    this.methods = new Method[0];
    this.generators = new Method[0];
  }

  /**
   * Returns the {@link Class} object for the specified class name.
   *
   * @param className The fully qualified name of the class.
   * @return An {@link Optional} containing the {@link Class} object if found; otherwise, {@link
   *     Optional#empty()}.
   */
  private static Optional<Class<?>> getClass(String className) {
    try {
      return Optional.of(Class.forName(className));
    } catch (ClassNotFoundException e) {
      System.err.printf("ERROR: Class not found: %s%n", className);
      e.printStackTrace();
      return Optional.empty();
    }
  }

  /**
   * Returns the {@link Method} object for the specified qualified method name.
   *
   * @param qualifiedMethodName The fully qualified name of the method, including parameter types.
   * @return An {@link Optional} containing the {@link Method} object if found; otherwise, {@link
   *     Optional#empty()}.
   */
  private static Optional<Method> getMethod(String qualifiedMethodName) {
    try {
      int paramStart = qualifiedMethodName.indexOf('(');
      if (paramStart == -1) {
        System.err.printf("ERROR: Method signature missing parameters: %s%n", qualifiedMethodName);
        return Optional.empty();
      }

      int lastDot = qualifiedMethodName.lastIndexOf('.', paramStart);
      Class<?> clazz = Class.forName(qualifiedMethodName.substring(0, lastDot));
      String simpleMethodName = qualifiedMethodName.substring(lastDot + 1, paramStart);
      Class<?>[] paramTypes =
          Arrays.stream(
                  qualifiedMethodName
                      .substring(paramStart + 1, qualifiedMethodName.indexOf(')', paramStart))
                      .split(","))
              .map(String::trim)
              .filter(s -> !s.isEmpty())
              .map(Autonomous::getClass)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .toArray(Class<?>[]::new);

      return Optional.of(clazz.getMethod(simpleMethodName, paramTypes));
    } catch (ClassNotFoundException | NoSuchMethodException e) {
      System.err.printf("ERROR: Method not found: %s%n", qualifiedMethodName);
      return Optional.empty();
    }
  }

  /**
   * Returns a {@link AutonomousCommandFactory} implementation used to create a {@link Command} to
   * add to the {@link SendableChooser}.
   *
   * @param commandClass The {@link Class} object of the {@link Command} created by this instance.
   * @return A {@link AutonomousCommandFactory} implementation used to create a {@link Command} to
   *     add to the {@link SendableChooser}.
   */
  private static AutonomousCommandFactory toCommandFactory(Class<?> commandClass) {
    AutonomousCommand annotation = commandClass.getAnnotation(AutonomousCommand.class);

    return new AutonomousCommandFactory() {
      @Override
      public String getName() {
        return annotation.name();
      }

      @Override
      public boolean isDefault() {
        return annotation.isDefault();
      }

      @Override
      public Command newCommand(Object... args) {
        var argTypes = ReflectionUtil.getParameterTypes(args);

        try {
          return (Command) commandClass.getConstructor(argTypes).newInstance(args);
        } catch (NoSuchMethodException e) {
          System.err.printf(
              "ERROR: The constructor %s(%s) is undefined.%n",
              commandClass.getSimpleName(), ReflectionUtil.toArgumentTypeList(argTypes));
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          System.err.printf(
              "ERROR: The constructor %s(%s) is not visible.%n",
              commandClass.getSimpleName(), ReflectionUtil.toArgumentTypeList(argTypes));
          e.printStackTrace();
        } catch (InstantiationException
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
   * Returns a {@link AutonomousCommandFactory} implementation used to create a {@link Command} to
   * add to the {@link SendableChooser}.
   *
   * @param commandMethod The {@link Method} object invoked to create the {@link Command} of this
   *     instance.
   * @return A {@link AutonomousCommandFactory} implementation used to create a {@link Command} to
   *     add to the {@link SendableChooser}.
   */
  private static AutonomousCommandFactory toCommandFactory(Method commandMethod) {
    AutonomousCommandMethod annotation = commandMethod.getAnnotation(AutonomousCommandMethod.class);

    return new AutonomousCommandFactory() {
      @Override
      public String getName() {
        return annotation.name();
      }

      @Override
      public boolean isDefault() {
        return annotation.isDefault();
      }

      @Override
      public Command newCommand(Object... args) {
        try {
          return (Command) commandMethod.invoke(null, args);
        } catch (IllegalArgumentException | InvocationTargetException e) {
          System.err.printf(
              "ERROR: The method %s(%s) in the type %s is not defined for the arguments (%s).%n",
              commandMethod.getName(),
              ReflectionUtil.toArgumentTypeList(commandMethod.getParameterTypes()),
              commandMethod.getDeclaringClass().getSimpleName(),
              ReflectionUtil.toArgumentTypeList(args));
          e.printStackTrace();
        } catch (ClassCastException e) {
          System.err.printf(
              "ERROR: Method %s(%s) in the type %s returns %s instead of Command.%n",
              commandMethod.getName(),
              ReflectionUtil.toArgumentTypeList(commandMethod.getParameterTypes()),
              commandMethod.getDeclaringClass().getSimpleName(),
              commandMethod.getReturnType().getSimpleName());
          e.printStackTrace();
        } catch (IllegalAccessException | SecurityException e) {
          System.err.printf(
              "ERROR: An unexpected exception was caught while invoking method %s(%s) in the type %s: %s.%n",
              commandMethod.getName(),
              ReflectionUtil.toArgumentTypeList(commandMethod.getParameterTypes()),
              commandMethod.getDeclaringClass().getSimpleName(),
              e.getMessage());
          e.printStackTrace();
        }

        return null;
      }
    };
  }

  /**
   * Returns a {@link Stream} of {@link AutonomousCommandFactory} implementations used to create
   * {@link Command} objects to add to the {@link SendableChooser}.
   *
   * @param commandGenerator The {@link Method} object invoked to create the {@link Command}
   *     objects.
   * @return A {@link Stream} of {@link AutonomousCommandFactory} implementations used to create
   *     {@link Command} objects to add to the {@link SendableChooser}.
   */
  private static Stream<AutonomousCommandFactory> generateCommands(
      Method commandGenerator, Object... args) {
    ArrayList<AutonomousCommandFactory> factories = new ArrayList<>();

    try {
      Object rawCommands = commandGenerator.invoke(null, args);

      // TODO: Validate collection type.

      @SuppressWarnings("unchecked")
      Collection<LabelValue<String, Command>> commands =
          (Collection<LabelValue<String, Command>>) rawCommands;

      return commands.stream()
          .map(
              lv -> {
                return new AutonomousCommandFactory() {

                  @Override
                  public String getName() {
                    return lv.label();
                  }

                  @Override
                  public boolean isDefault() {
                    return false;
                  }

                  @Override
                  public Command newCommand(Object... args) {
                    return lv.value();
                  }
                };
              });
    } catch (IllegalArgumentException | InvocationTargetException e) {
      System.err.printf(
          "ERROR: The method %s(%s) in the type %s is not defined for the arguments (%s).%n",
          commandGenerator.getName(),
          ReflectionUtil.toArgumentTypeList(commandGenerator.getParameterTypes()),
          commandGenerator.getDeclaringClass().getSimpleName(),
          ReflectionUtil.toArgumentTypeList(args));
      e.printStackTrace();
    } catch (ClassCastException e) {
      System.err.printf(
          "ERROR: Method %s(%s) in the type %s returns %s instead of Collection<LabelValue<String,Command>>.%n",
          commandGenerator.getName(),
          ReflectionUtil.toArgumentTypeList(commandGenerator.getParameterTypes()),
          commandGenerator.getDeclaringClass().getSimpleName(),
          commandGenerator.getReturnType().getSimpleName());
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      System.err.printf(
          "ERROR: The method %s(%s) in the type %s is not visible.%n",
          commandGenerator.getName(),
          ReflectionUtil.toArgumentTypeList(commandGenerator.getParameterTypes()),
          commandGenerator.getDeclaringClass().getSimpleName());
      e.printStackTrace();
    }

    return factories.stream();
  }

  /** Returns the singleton {@link Autonomous} instance loaded from the configuration resource. */
  private static Autonomous getInstance() {
    if (instance == null) {
      try {
        var classLoader = ClassLoader.getSystemClassLoader();
        var mapper = new ObjectMapper();

        try (var configStream = classLoader.getResourceAsStream(CONFIGURATION_RESOURCE_NAME)) {
          if (configStream == null) {
            throw new IllegalArgumentException(
                String.format(
                    "Autonomous configuration resource '%s' is missing.",
                    CONFIGURATION_RESOURCE_NAME));
          }

          instance = mapper.readValue(configStream, Autonomous.class);
        }
      } catch (IOException e) {
        System.err.printf(
            "ERROR: Failed to load autonomous commands configuration: %s%n", e.getMessage());
        e.printStackTrace();

        instance = new Autonomous();
      } catch (IllegalArgumentException e) {
        System.err.printf("ERROR: Failed to load autonomous configuration: %s%n", e.getMessage());
        e.printStackTrace();

        instance = new Autonomous();
      }
    }
    return instance;
  }

  /**
   * Returns a {@link SendableChooser} object enabling interactive selection of autonomous commands
   * annotated with {@link AutonomousCommand}.
   *
   * @param args A list of objects passed to the constructor of the autonomous commands providing
   *     access to the robot subsystems. This is typically an instance of <code>RobotContainer
   *     </code> but could be another type that manages the subsystems or the list of subsystems
   *     themselves. All commands must accept the same types and number of arguments.
   * @return A {@link SendableChooser} object containing the autonomous commands.
   */
  public static SendableChooser<Command> getChooser(Object... args) {
    SendableChooser<Command> chooser = new SendableChooser<>();

    var my = getInstance();
    var commandClasses = Arrays.stream(my.commands).map(Autonomous::toCommandFactory);
    var commandMethods = Arrays.stream(my.methods).map(Autonomous::toCommandFactory);
    var commandGenerators = Arrays.stream(my.generators).flatMap(m -> generateCommands(m, args));

    Stream.of(commandClasses, commandMethods, commandGenerators)
        .flatMap(s -> s)
        .sorted()
        .forEach(
            f -> {
              var command = f.newCommand(args);

              chooser.addOption(f.getName(), command);

              if (f.isDefault()) {
                chooser.setDefaultOption(f.getName(), command);
              }
            });

    return chooser;
  }
}
