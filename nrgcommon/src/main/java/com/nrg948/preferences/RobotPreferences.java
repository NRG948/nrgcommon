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
package com.nrg948.preferences;

import static org.reflections.scanners.Scanners.FieldsAnnotated;
import static org.reflections.scanners.Scanners.TypesAnnotated;

import com.nrg948.annotations.Annotations;
import edu.wpi.first.networktables.BooleanTopic;
import edu.wpi.first.networktables.DoubleTopic;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringTopic;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardComponent;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.javatuples.Pair;

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
     * @param <E> The enum type.
     * @param value The {@link EnumValue} to visit.
     */
    <E extends Enum<E>> void visit(EnumValue<E> value);
  }

  /** An abstract base class represented a keyed valued in the preferences store. */
  public abstract static class Value {

    /** The preferences value group name. */
    protected final String group;

    /** The preferences value name. */
    protected final String name;

    /** The preferences value key. */
    protected final String key;

    /**
     * Constructs an instance of this class.
     *
     * @param group The preference value's group name. This is usually the subsystem or class that
     *     uses the value.
     * @param name The preferences value name.
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
     * Returns this value key used to access it in the preferences store. The value is generated
     * from a concatenation of the group name and value name separated by a forward slash ("/")
     * character.
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
     * @param group The preference value's group name. This is usually the subsystem or class that
     *     uses the value.
     * @param name The preferences value name.
     * @param defaultValue The value supplied when the preference does not exist in the preferences
     *     store.
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
     * @param group The preference value's group name. This is usually the subsystem or class that
     *     uses the value.
     * @param name The preferences value name.
     * @param defaultValue The value supplied when the preference does not exist in the preferences
     *     store.
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
     * @param group The preference value's group name. This is usually the subsystem or class that
     *     uses the value.
     * @param name The preferences value name.
     * @param defaultValue The value supplied when the preference does not exist in the preferences
     *     store.
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
     * @param group The preference value's group name. This is usually the subsystem or class that
     *     uses the value.
     * @param name The preferences value name.
     * @param defaultValue The value supplied when the preference does not exist in the preferences
     *     store.
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
    private RobotPreferencesValue metadata;

    /**
     * Constructs a visitor that creates Shuffleboard widgets for preference values.
     *
     * @param layout The Shuffleboard layout to add a widget for the visited value.
     * @param metadata A {@link RobotPreferencesValue} annotation containing the value's metadata.
     */
    public ShuffleboardWidgetBuilder(ShuffleboardLayout layout, RobotPreferencesValue metadata) {
      this.layout = layout;
      this.metadata = metadata;
    }

    @Override
    public void visit(StringValue value) {
      SimpleWidget widget =
          layout.add(value.getName(), value.getValue()).withWidget(BuiltInWidgets.kTextView);

      configureWidget(widget);

      GenericEntry entry = widget.getEntry();

      entry.setString(value.getValue());

      StringTopic topic = new StringTopic(entry.getTopic());
      NetworkTableInstance ntInstance = NetworkTableInstance.getDefault();

      ntInstance.addListener(
          topic,
          EnumSet.of(NetworkTableEvent.Kind.kValueAll),
          (event) -> value.setValue(event.valueData.value.getString()));
    }

    @Override
    public void visit(BooleanValue value) {
      SimpleWidget widget =
          layout.add(value.getName(), value.getValue()).withWidget(BuiltInWidgets.kToggleSwitch);

      configureWidget(widget);

      GenericEntry entry = widget.getEntry();

      entry.setBoolean(value.getValue());

      BooleanTopic topic = new BooleanTopic(entry.getTopic());
      NetworkTableInstance ntInstance = NetworkTableInstance.getDefault();

      ntInstance.addListener(
          topic,
          EnumSet.of(NetworkTableEvent.Kind.kValueAll),
          (event) -> value.setValue(event.valueData.value.getBoolean()));
    }

    @Override
    public void visit(DoubleValue value) {
      SimpleWidget widget =
          layout.add(value.getName(), value.getValue()).withWidget(BuiltInWidgets.kTextView);

      configureWidget(widget);

      GenericEntry entry = widget.getEntry();

      entry.setDouble(value.getValue());

      DoubleTopic topic = new DoubleTopic(entry.getTopic());
      NetworkTableInstance ntInstance = NetworkTableInstance.getDefault();

      ntInstance.addListener(
          topic,
          EnumSet.of(NetworkTableEvent.Kind.kValueAll),
          (event) -> value.setValue(event.valueData.value.getDouble()));
    }

    @Override
    public <E extends Enum<E>> void visit(EnumValue<E> value) {
      E currentValue = value.getValue();
      SendableChooser<E> chooser = new SendableChooser<E>();

      EnumSet.allOf(currentValue.getDeclaringClass()).stream()
          .forEach(e -> chooser.addOption(e.toString(), e));
      chooser.setDefaultOption(currentValue.toString(), currentValue);

      ComplexWidget widget = layout.add(value.getName(), chooser);

      configureWidget(widget);

      chooser.onChange((choice) -> value.setValue(choice));
    }

    /**
     * Configures the visited value's widget according to the metadata information.
     *
     * @param widget The Shuffleboard widget to configure.
     */
    private void configureWidget(ShuffleboardComponent<?> widget) {
      if (layout.getType().equals(BuiltInLayouts.kGrid.getLayoutName())) {
        int column = metadata.column();
        int row = metadata.row();

        if (column >= 0 && row >= 0) {
          widget.withPosition(column, row);
        }

        int width = metadata.width();
        int height = metadata.height();

        if (width > 0 && height > 0) {
          widget.withSize(width, height);
        }
      }
    }
  }

  /** The name of the Shuffleboard tab containing the preferences widgets. */
  public static final String kShufflboardTabName = "Preferences";

  /** Whether to write the default values to the preferences file on startup. */
  @RobotPreferencesValue
  public static BooleanValue writeDefault = new BooleanValue("Preferences", "WriteDefault", true);

  /** Initializes the robot preferences. */
  public static void init() {
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

  /** Adds a tab to the Shuffleboard allowing the robot operator to adjust values. */
  public static void addShuffleBoardTab() {
    ShuffleboardTab prefsTab = Shuffleboard.getTab(kShufflboardTabName);

    Set<Class<?>> classes =
        Annotations.get(TypesAnnotated.with(RobotPreferencesLayout.class).asClass());

    classes.stream()
        .map(c -> c.getAnnotation(RobotPreferencesLayout.class))
        .forEach(
            layout -> {
              var shuffleboardLayout =
                  prefsTab
                      .getLayout(layout.groupName(), layout.type())
                      .withPosition(layout.column(), layout.row())
                      .withSize(layout.width(), layout.height());

              if (layout.type().equals(BuiltInLayouts.kGrid.getLayoutName())) {
                int gridColumns = layout.gridColumns();
                int gridRows = layout.gridRows();

                if (gridColumns > 0 && gridRows > 0) {
                  shuffleboardLayout.withProperties(
                      Map.of("Number of columns", gridColumns, "Number of rows", gridRows));
                }
              }
            });

    getFields()
        .map(RobotPreferences::mapToPair)
        .collect(Collectors.groupingBy(p -> p.getValue1().group))
        .forEach(
            (group, pairs) -> {
              ShuffleboardLayout layout = prefsTab.getLayout(group);

              pairs.stream()
                  .forEach(
                      pair -> {
                        ShuffleboardWidgetBuilder builder =
                            new ShuffleboardWidgetBuilder(layout, pair.getValue0());

                        pair.getValue1().accept(builder);
                      });
            });
  }

  /** Returns a stream of fields containing preferences values. */
  private static Stream<Field> getFields() {
    Set<Field> fields =
        Annotations.get(FieldsAnnotated.with(RobotPreferencesValue.class).as(Field.class));

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

  /** Maps a preferences field to a pair of its annotation and value instance */
  private static Pair<RobotPreferencesValue, Value> mapToPair(Field field) {
    return Pair.with(field.getAnnotation(RobotPreferencesValue.class), mapToValue(field));
  }

  /** Returns all preferences values. */
  private static Stream<Value> getAllValues() {
    return getFields().map(RobotPreferences::mapToValue).filter(v -> v != null);
  }
}
