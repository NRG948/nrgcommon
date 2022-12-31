// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.nrg948.preferences;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/** A class to manage robot preferences. */
public class RobotPreferences {

  /** An interface to support the Visitor pattern on preferences values. */
  public interface IValueVisitor {

    /**
     * Called to apply the visitor's effect on a {@link StringValue}.
     * 
     * @param value The {@link StringValue} to visit.
     */
    void visit(StringValue value);

    /**
     * Called to apply the visitor's effect on a {@link BooleanValue}.
     * 
     * @param value The {@link BooleanValue} to visit.
     */
    void visit(BooleanValue value);

    /**
     * Called to apply the visitor's effect on a {@link DoubleValue}.
     * 
     * @param value The {@link DoubleValue} to visit.
     */
    void visit(DoubleValue value);

    /**
     * Called to apply the visitor's effect on an {@link EnumValue}.
     * 
     * @param <E>   The enum type.
     * @param value The {@link EnumValue} to visit.
     */
    <E extends Enum<E>> void visit(EnumValue<E> value);
  }

  /**
   * An abstract base class represented a keyed valued in the preferences store.
   */
  public static abstract class Value {

    protected final String group;
    protected final String name;
    protected final String key;

    /**
     * Constructs an instance of this class.
     * 
     * @param group The preference value's group name. This is usually the subsystem
     *              or class that uses the value.
     * @param name  The preferences value name.
     */
    public Value(String group, String name) {
      this.group = group;
      this.name = name;
      this.key = group + "/" + name;
    }

    /**
     * Returns the this value's group name.
     * 
     * @return The group name.
     */
    public String getGroup() {
      return group;
    }

    /**
     * Returns this value's name.
     * 
     * @return The value name.
     */
    public String getName() {
      return name;
    }

    /**
     * Returns this value key used to access it in the preferences store. The value
     * is generated from a concatenation of the group name and value name separated
     * by a forward slash ("/") character.
     * 
     * @return The key name.
     */
    public String getKey() {
      return key;
    }

    /**
     * Returns whether the value exists in the preferences store.
     * 
     * @return Whether the value exists in the preferences store.
     */
    public boolean exists() {
      return Preferences.containsKey(key);
    }

    /**
     * Accepts a visitor that operates on this value.
     * 
     * @param visitor A visitor to operate on this value.
     */
    public abstract void accept(IValueVisitor visitor);
  }

  /** A class representing a string value in the preferences store. */
  public static class StringValue extends Value {

    private final String defaultValue;

    /**
     * Constructs an instance of this class.
     * 
     * @param group        The preference value's group name. This is usually the
     *                     subsystem
     *                     or class that uses the value.
     * @param name         The preferences value name.
     * @param defaultValue The value supplied when the preference does not exist in
     *                     the preferences store.
     */
    public StringValue(String group, String name, String defaultValue) {
      super(group, name);
      this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value.
     * 
     * @return The default value.
     */
    public String getDefaultValue() {
      return defaultValue;
    }

    /**
     * Returns the current value.
     * 
     * @return The current value.
     */
    public String getValue() {
      return Preferences.getString(key, defaultValue);
    }

    /**
     * Sets the current value.
     * 
     * @param value The value to set.
     */
    public void setValue(String value) {
      Preferences.setString(key, value);
    }

    @Override
    public void accept(IValueVisitor visitor) {
      visitor.visit(this);
    }
  }

  /** A class representing a Boolean value in the preferences store. */
  public static class BooleanValue extends Value {

    private final boolean defaultValue;

    /**
     * Constructs an instance of this class.
     * 
     * @param group        The preference value's group name. This is usually the
     *                     subsystem
     *                     or class that uses the value.
     * @param name         The preferences value name.
     * @param defaultValue The value supplied when the preference does not exist in
     *                     the preferences store.
     */
    public BooleanValue(String group, String name, boolean defaultValue) {
      super(group, name);
      this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value.
     * 
     * @return The default value.
     */
    public boolean getDefaultValue() {
      return defaultValue;
    }

    /**
     * Returns the current value.
     * 
     * @return The current value.
     */
    public boolean getValue() {
      return Preferences.getBoolean(key, defaultValue);
    }

    /**
     * Sets the current value.
     * 
     * @param value The value to set.
     */
    public void setValue(Boolean value) {
      Preferences.setBoolean(key, value);
    }

    @Override
    public void accept(IValueVisitor visitor) {
      visitor.visit(this);
    }
  }

  /** A class representing a floating-point value in the preferences store. */
  public static class DoubleValue extends Value {

    private final double defaultValue;

    /**
     * Constructs an instance of this class.
     * 
     * @param group        The preference value's group name. This is usually the
     *                     subsystem
     *                     or class that uses the value.
     * @param name         The preferences value name.
     * @param defaultValue The value supplied when the preference does not exist in
     *                     the preferences store.
     */
    public DoubleValue(String group, String name, double defaultValue) {
      super(group, name);
      this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value.
     * 
     * @return The default value.
     */
    public double getDefaultValue() {
      return defaultValue;
    }

    /**
     * Returns the current value.
     * 
     * @return The current value.
     */
    public double getValue() {
      return Preferences.getDouble(key, defaultValue);
    }

    /**
     * Sets the current value.
     * 
     * @param value The value to set.
     */
    public void setValue(double value) {
      Preferences.setDouble(key, value);
    }

    @Override
    public void accept(IValueVisitor visitor) {
      visitor.visit(this);
    }
  }

  /** A class representing an enum value in the preferences store. */
  public static class EnumValue<E extends Enum<E>> extends Value {

    private final E defaultValue;

    /**
     * Constructs an instance of this class.
     * 
     * @param group        The preference value's group name. This is usually the
     *                     subsystem
     *                     or class that uses the value.
     * @param name         The preferences value name.
     * @param defaultValue The value supplied when the preference does not exist in
     *                     the preferences store.
     */
    public EnumValue(String group, String name, E defaultValue) {
      super(group, name);
      this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value.
     * 
     * @return The default value.
     */
    public E getDefaultValue() {
      return defaultValue;
    }

    /**
     * Returns the current value.
     * 
     * @return The current value.
     */
    public E getValue() {
      try {
        return E.valueOf(
            defaultValue.getDeclaringClass(), Preferences.getString(key, defaultValue.name()));
      } catch (IllegalArgumentException e) {
        return defaultValue;
      }
    }

    /**
     * Sets the current value.
     * 
     * @param value The value to set.
     */
    public void setValue(E value) {
      Preferences.setString(key, value.name());
    }

    /**
     * Sets the current value as a string.
     *
     * @implNote No validation is done on the string value. If a string value that cannot be
     *           converted to an value of the enum type, {@link getValue} will return the
     *           default value.
     * 
     * @param value The string value to set.
     */
    private void setValue(String value) {
      Preferences.setString(key, value);
    }

    @Override
    public void accept(IValueVisitor visitor) {
      visitor.visit(this);
    }

  }

  /** A Visitor that writes the default preferences value to the store. */
  private static class DefaultValueWriter implements IValueVisitor {

    private static void printMessage(String group, String name, String value) {
      System.out.println("WRITING DEFAULT VALUE: " + group + "/" + name + " = " + value);
    }

    @Override
    public void visit(StringValue value) {
      value.setValue(value.getDefaultValue());
      printMessage(value.getGroup(), value.getName(), value.getValue());
    }

    @Override
    public void visit(BooleanValue value) {
      value.setValue(value.getDefaultValue());
      printMessage(value.getGroup(), value.getName(), Boolean.toString(value.getValue()));
    }

    @Override
    public void visit(DoubleValue value) {
      value.setValue(value.getDefaultValue());
      printMessage(value.getGroup(), value.getName(), Double.toString(value.getValue()));
    }

    @Override
    public <E extends Enum<E>> void visit(EnumValue<E> value) {
      value.setValue(value.getDefaultValue());
      printMessage(value.getGroup(), value.getName(), value.getValue().name());
    }

  }

  /** A Visitor to print non default Values to the console. */
  private static class NonDefaultValuePrinter implements IValueVisitor {
    private static void printMessage(String group, String name, String value) {
      System.out.println("NON-DEFAULT VALUE: " + group + "/" + name + " = " + value);
    }

    @Override
    public void visit(StringValue value) {
      if (!value.getValue().equals(value.getDefaultValue())) {
        printMessage(value.getGroup(), value.getName(), value.getValue());
      }
    }

    @Override
    public void visit(BooleanValue value) {
      if (value.getValue() != value.getDefaultValue()) {
        printMessage(value.getGroup(), value.getName(), Boolean.toString(value.getValue()));
      }
    }

    @Override
    public void visit(DoubleValue value) {
      if (value.getValue() != value.getDefaultValue()) {
        printMessage(value.getGroup(), value.getName(), Double.toString(value.getValue()));
      }
    }

    @Override
    public <E extends Enum<E>> void visit(EnumValue<E> value) {
      if (value.getValue() != value.getDefaultValue()) {
        printMessage(value.getGroup(), value.getName(), value.getValue().name());
      }
    }
  }

  /** A Visitor that adds widgets to the Shuffleboard preferences layout. */
  private static class ShuffleboardWidgetBuilder implements IValueVisitor {

    private ShuffleboardLayout layout;

    public ShuffleboardWidgetBuilder(ShuffleboardLayout layout) {
      this.layout = layout;
    }

    @Override
    public void visit(StringValue value) {
      SimpleWidget widget = layout.add(value.getName(), value.getValue()).withWidget(BuiltInWidgets.kTextView);
      NetworkTableEntry entry = widget.getEntry();

      entry.setString(value.getValue());
      entry.addListener(
          (event) -> value.setValue(event.getEntry().getString(value.getDefaultValue())),
          EntryListenerFlags.kUpdate);
    }

    @Override
    public void visit(BooleanValue value) {
      SimpleWidget widget = layout.add(value.getName(), value.getValue())
          .withWidget(BuiltInWidgets.kToggleSwitch);
      NetworkTableEntry entry = widget.getEntry();

      entry.setBoolean(value.getValue());
      entry.addListener(
          (event) -> value.setValue(event.getEntry().getBoolean(value.getDefaultValue())),
          EntryListenerFlags.kUpdate);
    }

    @Override
    public void visit(DoubleValue value) {
      SimpleWidget widget = layout.add(value.getName(), value.getValue()).withWidget(BuiltInWidgets.kTextView);
      NetworkTableEntry entry = widget.getEntry();

      entry.setDouble(value.getValue());
      entry.addListener(
          (event) -> value.setValue(event.getEntry().getDouble(value.getDefaultValue())),
          EntryListenerFlags.kUpdate);
    }

    @Override
    public <E extends Enum<E>> void visit(EnumValue<E> value) {
      E currentValue = value.getValue();
      SendableChooser<E> chooser = new SendableChooser<E>();

      EnumSet.allOf(currentValue.getDeclaringClass())
          .stream()
          .forEach(e -> chooser.addOption(e.toString(), e));
      chooser.setDefaultOption(currentValue.toString(), currentValue);

      layout.add(value.getName(), chooser);

      NetworkTableInstance.getDefault()
        .getTable(Shuffleboard.kBaseTableName)
        .getSubTable(kShufflboardTabName)
        .getSubTable(value.getGroup())
        .getSubTable(value.getName())
        .getEntry("active")
        .addListener(
          (event) -> value.setValue(event.getEntry().getString(value.getDefaultValue().name())),
          EntryListenerFlags.kUpdate);
    }

  }

  public static final String kShufflboardTabName = "Preferences";

  @RobotPreferencesValue
  public static BooleanValue writeDefault = new BooleanValue("Preferences", "WriteDefault", true);

  private static Reflections reflections;

  /**
   * Initializes the robot preferences.
   * 
   * @param pkgs The packages to scan for {@link RobotPreferencesValue} and
   *             {@link RobotPreferencesLayout} annotations.
   **/
  public static void init(String... pkgs) {
    String[] allPkgs = Arrays.copyOf(pkgs, pkgs.length + 1);

    allPkgs[allPkgs.length - 1] = "com.nrg948";
    reflections = new Reflections(
        new ConfigurationBuilder()
            .forPackages(allPkgs)
            .setScanners(Scanners.TypesAnnotated, Scanners.FieldsAnnotated));

    DefaultValueWriter writeDefaultValue = new DefaultValueWriter();

    if (writeDefault.getValue()) {
      Preferences.removeAll();
      getAllValues().forEach(v -> v.accept(writeDefaultValue));
      writeDefault.setValue(false);
    } else {
      getAllValues().filter(v -> !v.exists()).forEach(v -> v.accept(writeDefaultValue));

      NonDefaultValuePrinter printer = new NonDefaultValuePrinter();

      getAllValues().forEach((v) -> v.accept(printer));
    }
  }

  /**
   * Adds a tab to the Shuffleboard allowing the robot operator to adjust values.
   */
  public static void addShuffleBoardTab() {
    ShuffleboardTab prefsTab = Shuffleboard.getTab(kShufflboardTabName);

    Set<Class<?>> classes = reflections.get(
        Scanners.TypesAnnotated
            .with(RobotPreferencesLayout.class)
            .asClass());

    classes.stream().map(c -> c.getAnnotation(RobotPreferencesLayout.class)).forEach(layout -> {
      prefsTab.getLayout(layout.groupName(), layout.type())
          .withPosition(layout.column(), layout.row())
          .withSize(layout.width(), layout.height());
    });

    getAllValues().collect(Collectors.groupingBy(Value::getGroup)).forEach((group, values) -> {
      ShuffleboardLayout layout = prefsTab.getLayout(group);
      ShuffleboardWidgetBuilder builder = new ShuffleboardWidgetBuilder(layout);
      values.stream().forEach((value) -> value.accept(builder));
    });
  }

  /** Returns a stream of fields containing preferences values. */
  private static Stream<Field> getFields() {
    Set<Field> fields = reflections.get(
        Scanners.FieldsAnnotated
            .with(RobotPreferencesValue.class)
            .as(Field.class));

    return fields.stream().filter(f -> Modifier.isStatic(f.getModifiers()));
  }

  /** Maps a preferences field to its value instance. */
  private static Value mapToValue(Field field) {
    try {
      return (Value) field.get(null);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  /** Returns all preferences values. */
  private static Stream<Value> getAllValues() {
    return getFields().map(RobotPreferences::mapToValue).filter(v -> v != null);
  }

}
