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
package com.nrg948.util;

import com.nrg948.util.function.ObjBooleanConsumer;
import com.nrg948.util.function.ObjFloatConsumer;
import com.nrg948.util.function.ToBooleanFunction;
import com.nrg948.util.function.ToFloatFunction;
import edu.wpi.first.util.function.BooleanConsumer;
import edu.wpi.first.util.function.FloatConsumer;
import edu.wpi.first.util.function.FloatSupplier;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

/** Utility methods for reflection operations. */
public final class ReflectionUtil {
  private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPES =
      Map.of(
          Boolean.class, Boolean.TYPE,
          Character.class, Character.TYPE,
          Byte.class, Byte.TYPE,
          Short.class, Short.TYPE,
          Integer.class, Integer.TYPE,
          Long.class, Long.TYPE,
          Float.class, Float.TYPE,
          Double.class, Double.TYPE,
          Void.class, Void.TYPE);

  /**
   * Returns the primitive type corresponding to the given wrapper type, or the type itself if not a
   * wrapper.
   */
  public static Class<?> unbox(Class<?> type) {
    return PRIMITIVE_TYPES.getOrDefault(type, type);
  }

  /**
   * Gets the value of the field represented by the given {@link VarHandle} from the specified
   * instance.
   *
   * @param <I> The type of the instance.
   * @param <T> The type of the field value.
   * @param fieldHandle The {@code VarHandle} representing the field.
   * @param instance The instance from which to get the field value.
   * @return The value of the field.
   */
  public static <I, T> T get(VarHandle fieldHandle, I instance) {
    return (T) fieldHandle.get(instance);
  }

  /**
   * Gets the value of the static field represented by the given {@link VarHandle}.
   *
   * @param <T> The type of the field value.
   * @param fieldHandle The {@code VarHandle} representing the static field.
   * @return The value of the static field.
   */
  public static <T> T getStatic(VarHandle fieldHandle) {
    return (T) fieldHandle.get();
  }

  /**
   * Gets the value returned by the method referenced by the given {@link MethodHandle} from the
   * specified instance.
   *
   * <p>The referenced method must be an instance method that takes no parameters and returns a
   * value.
   *
   * @param <I> The type of the instance.
   * @param <T> The type of the method return value.
   * @param methodHandle The {@code MethodHandle} representing the method.
   * @param instance The instance from which to invoke the method.
   * @return The value returned by the method.
   */
  public static <I, T> T get(MethodHandle methodHandle, I instance) {
    try {
      return (T) methodHandle.invoke(instance);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Gets the value returned by the static method referenced by the given {@link MethodHandle}.
   *
   * <p>The referenced method must be a static method that takes no parameters and returns a value.
   *
   * @param <T> The type of the method return value.
   * @param methodHandle The {@code MethodHandle} representing the static method.
   * @return The value returned by the static method.
   */
  public static <T> T getStatic(MethodHandle methodHandle) {
    try {
      return (T) methodHandle.invoke();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets the value of the field represented by the given {@link VarHandle} on the specified
   * instance.
   *
   * @param <I> The type of the instance.
   * @param <T> The type of the field value.
   * @param fieldHandle The {@code VarHandle} representing the field.
   * @param instance The instance on which to set the field value.
   * @param value The value to set.
   */
  public static <I, T> void set(VarHandle fieldHandle, I instance, T value) {
    fieldHandle.set(instance, value);
  }

  /**
   * Sets the value of the static field represented by the given {@link VarHandle}.
   *
   * @param <T> The type of the field value.
   * @param fieldHandle The {@code VarHandle} representing the static field.
   * @param value The value to set.
   */
  public static <T> void setStatic(VarHandle fieldHandle, T value) {
    fieldHandle.set(value);
  }

  /**
   * Creates a getter function for the field represented by the given {@link VarHandle}.
   *
   * @param <T> The type of the field value.
   * @param fieldHandle The {@code VarHandle} representing the field.
   * @return A function that takes an instance and returns the field value.
   */
  public static <I, T> Function<I, T> getterOf(VarHandle fieldHandle) {
    return (instance) -> (T) fieldHandle.get(instance);
  }

  /**
   * Creates a getter function for the static field represented by the given {@link VarHandle}.
   *
   * @param <T> The type of the field value.
   * @param fieldHandle The {@code VarHandle} representing the static field.
   * @return A supplier that returns the static field value.
   */
  public static <T> Supplier<T> getterOfStatic(VarHandle fieldHandle) {
    return () -> (T) fieldHandle.get();
  }

  /**
   * Creates a getter function for the method represented by the given {@link MethodHandle}.
   *
   * <p>The referenced method must be an instance method that takes no parameters and returns a
   * value.
   *
   * @param <T> The type of the method return value.
   * @param methodHandle The {@code MethodHandle} representing the method.
   * @return A function that takes an instance and returns the method return value.
   */
  public static <I, T> Function<I, T> getterOf(MethodHandle methodHandle) {
    return (instance) -> {
      try {
        return (T) methodHandle.invoke(instance);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Creates a getter function for the static method represented by the given {@link MethodHandle}.
   *
   * <p>The referenced method must be a static method that takes no parameters and returns a value.
   *
   * @param <T> The type of the method return value.
   * @param methodHandle The {@code MethodHandle} representing the static method.
   * @return A supplier that returns the static method return value.
   */
  public static <T> Supplier<T> getterOfStatic(MethodHandle methodHandle) {
    return () -> {
      try {
        return (T) methodHandle.invoke();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Creates a setter function for the field represented by the given {@link VarHandle}.
   *
   * @param <T> The type of the field value.
   * @param fieldHandle The {@code VarHandle} representing the field.
   * @return A function that takes an instance and a value, and sets the field value.
   */
  public static <I, T> BiConsumer<I, T> setterOf(VarHandle fieldHandle) {
    return (instance, value) -> fieldHandle.set(instance, value);
  }

  /**
   * Creates a setter function for the static field represented by the given {@link VarHandle}.
   *
   * @param <T> The type of the field value.
   * @param fieldHandle The {@code VarHandle} representing the static field.
   * @return A function that takes a value and sets the static field value.
   */
  public static <T> Consumer<T> setterOfStatic(VarHandle fieldHandle) {
    return (value) -> fieldHandle.set(value);
  }

  /**
   * Creates a getter function for an enum field represented by the given {@link VarHandle}.
   *
   * @param <E> The type of the enum.
   * @param fieldHandle The {@code VarHandle} representing the enum field.
   * @param enumType The class of the enum type.
   * @return A function that takes an instance and returns the enum value.
   */
  public static <E extends Enum<E>, I> Function<I, E> getterOfEnum(
      VarHandle fieldHandle, Class<E> enumType) {
    return (instance) -> (E) fieldHandle.get(instance);
  }

  /**
   * Creates a getter function for a static enum field represented by the given {@link VarHandle}.
   *
   * @param <E> The type of the enum.
   * @param fieldHandle The {@code VarHandle} representing the static enum field.
   * @param enumType The class of the enum type.
   * @return A supplier that returns the enum value.
   */
  public static <E extends Enum<E>> Supplier<E> getterOfStaticEnum(
      VarHandle fieldHandle, Class<E> enumType) {
    return () -> (E) fieldHandle.get();
  }

  /**
   * Creates a getter function for an enum method represented by the given {@link MethodHandle}.
   *
   * <p>The referenced method must be an instance method that takes no parameters and returns a
   * value of the specified enum type, {@code E}.
   *
   * @param <E> The type of the enum.
   * @param methodHandle The {@code MethodHandle} representing the enum method.
   * @param enumType The class of the enum type.
   * @return A function that takes an instance and returns the enum value.
   */
  public static <E extends Enum<E>, I> Function<I, E> getterOfEnum(
      MethodHandle methodHandle, Class<E> enumType) {
    return (instance) -> {
      try {
        return (E) methodHandle.invoke(instance);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Creates a getter function for a static enum method represented by the given {@link
   * MethodHandle}.
   *
   * <p>The referenced method must be a static method that takes no parameters and returns a value
   * of the specified enum type, {@code E}.
   *
   * @param <E> The type of enum.
   * @param methodHandle The {@code MethodHandle} representing the static enum method.
   * @param enumType The class of the enum type.
   * @return A supplier that returns the enum value.
   */
  public static <E extends Enum<E>> Supplier<E> getterOfStaticEnum(
      MethodHandle methodHandle, Class<E> enumType) {
    return () -> {
      try {
        return (E) methodHandle.invoke();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Creates a setter function for an enum field represented by the given {@link VarHandle}.
   *
   * @param <E> The type of the enum.
   * @param fieldHandle The {@code VarHandle} representing the enum field.
   * @param enumType The class of the enum type.
   * @return A function that takes an instance and a value, and sets the enum field value.
   */
  public static <E extends Enum<E>, I> BiConsumer<I, E> setterOfEnum(
      VarHandle fieldHandle, Class<E> enumType) {
    return (instance, value) -> fieldHandle.set(instance, value);
  }

  /**
   * Creates a setter function for a static enum field represented by the given {@link VarHandle}.
   *
   * @param <E> The type of the enum.
   * @param fieldHandle The {@code VarHandle} representing the static enum field.
   * @param enumType The class of the enum type.
   * @return A function that takes a value and sets the static enum field value.
   */
  public static <E extends Enum<E>> Consumer<E> setterOfStaticEnum(
      VarHandle fieldHandle, Class<E> enumType) {
    return (value) -> fieldHandle.set(value);
  }

  /**
   * Creates a getter function for a boolean field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the boolean field.
   * @return A function that takes an instance and returns the boolean field value.
   */
  public static <I> ToBooleanFunction<I> getterOfBoolean(VarHandle fieldHandle) {
    return (instance) -> (boolean) fieldHandle.get(instance);
  }

  /**
   * Creates a getter function for a static boolean field represented by the given {@link
   * VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the static boolean field.
   * @return A function that returns the static boolean field value.
   */
  public static BooleanSupplier getterOfStaticBoolean(VarHandle fieldHandle) {
    return () -> (boolean) fieldHandle.get();
  }

  /**
   * Creates a getter function for a boolean method represented by the given {@link MethodHandle}.
   *
   * <p>The referenced method must be an instance method that takes no parameters and returns a
   * {@code boolean} value.
   *
   * @param methodHandle The {@code MethodHandle} representing the boolean method.
   * @return A function that takes an instance and returns the boolean method value.
   */
  public static <I> ToBooleanFunction<I> getterOfBoolean(MethodHandle methodHandle) {
    return (instance) -> {
      try {
        return (boolean) methodHandle.invoke(instance);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Creates a getter function for a static boolean method represented by the given {@link
   * MethodHandle}.
   *
   * <p>The referenced method must be a static method that takes no parameters and returns a {@code
   * boolean} value.
   *
   * @param methodHandle The {@code MethodHandle} representing the static boolean method.
   * @return A function that returns the static boolean method value.
   */
  public static BooleanSupplier getterOfStaticBoolean(MethodHandle methodHandle) {
    return () -> {
      try {
        return (boolean) methodHandle.invoke();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Creates a setter function for a boolean field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the boolean field.
   * @return A function that takes an instance and a value, and sets the boolean field value.
   */
  public static <I> ObjBooleanConsumer<I> setterOfBoolean(VarHandle fieldHandle) {
    return (instance, value) -> fieldHandle.set(instance, value);
  }

  /**
   * Creates a setter function for a static boolean field represented by the given {@link
   * VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the static boolean field.
   * @return A function that takes a value and sets the static boolean field value.
   */
  public static BooleanConsumer setterOfStaticBoolean(VarHandle fieldHandle) {
    return (value) -> fieldHandle.set(value);
  }

  /**
   * Creates a getter function for a float field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the float field.
   * @return A function that takes an instance and returns the float field value.
   */
  public static <I> ToFloatFunction<I> getterOfFloat(VarHandle fieldHandle) {
    return (instance) -> (float) fieldHandle.get(instance);
  }

  /**
   * Creates a getter function for a static float field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the static float field.
   * @return A function that returns the static float field value.
   */
  public static FloatSupplier getterOfStaticFloat(VarHandle fieldHandle) {
    return () -> (float) fieldHandle.get();
  }

  /**
   * Creates a getter function for a float method represented by the given {@link MethodHandle}.
   *
   * <p>The referenced method must be an instance method that takes no parameters and returns a
   * {@code float} value.
   *
   * @param methodHandle The {@code MethodHandle} representing the float method.
   * @return A function that takes an instance and returns the float method value.
   */
  public static <I> ToFloatFunction<I> getterOfFloat(MethodHandle methodHandle) {
    return (instance) -> {
      try {
        return (float) methodHandle.invoke(instance);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Creates a getter function for a static float method represented by the given {@link
   * MethodHandle}.
   *
   * <p>The referenced method must be a static method that takes no parameters and returns a {@code
   * float} value.
   *
   * @param methodHandle The {@code MethodHandle} representing the static float method.
   * @return A function that returns the static float method value.
   */
  public static FloatSupplier getterOfStaticFloat(MethodHandle methodHandle) {
    return () -> {
      try {
        return (float) methodHandle.invoke();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Creates a setter function for a float field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the float field.
   * @return A function that takes an instance and a value, and sets the float field value.
   */
  public static <I> ObjFloatConsumer<I> setterOfFloat(VarHandle fieldHandle) {
    return (instance, value) -> fieldHandle.set(instance, value);
  }

  /**
   * Creates a setter function for a static float field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the static float field.
   * @return A function that takes a value and sets the static float field value.
   */
  public static FloatConsumer setterOfStaticFloat(VarHandle fieldHandle) {
    return (value) -> fieldHandle.set(value);
  }

  /**
   * Creates a getter function for a double field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the double field.
   * @return A function that takes an instance and returns the double field value.
   */
  public static <I> ToDoubleFunction<I> getterOfDouble(VarHandle fieldHandle) {
    return (instance) -> (double) fieldHandle.get(instance);
  }

  /**
   * Creates a getter function for a static double field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the static double field.
   * @return A function that returns the static double field value.
   */
  public static DoubleSupplier getterOfStaticDouble(VarHandle fieldHandle) {
    return () -> (double) fieldHandle.get();
  }

  /**
   * Creates a getter function for a double method represented by the given {@link MethodHandle}.
   *
   * <p>The referenced method must be an instance method that takes no parameters and returns a
   * {@code double} value.
   *
   * @param methodHandle The {@code MethodHandle} representing the double method.
   * @return A function that takes an instance and returns the double method value.
   */
  public static <I> ToDoubleFunction<I> getterOfDouble(MethodHandle methodHandle) {
    return (instance) -> {
      try {
        return (double) methodHandle.invoke(instance);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Creates a getter function for a static double method represented by the given {@link
   * MethodHandle}.
   *
   * <p>The referenced method must be a static method that takes no parameters and returns a {@code
   * double} value.
   *
   * @param methodHandle The {@code MethodHandle} representing the static double method.
   * @return A function that returns the static double method value.
   */
  public static DoubleSupplier getterOfStaticDouble(MethodHandle methodHandle) {
    return () -> {
      try {
        return (double) methodHandle.invoke();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Creates a setter function for a double field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the double field.
   * @return A function that takes an instance and a value, and sets the double field value.
   */
  public static <I> ObjDoubleConsumer<I> setterOfDouble(VarHandle fieldHandle) {
    return (instance, value) -> fieldHandle.set(instance, value);
  }

  /**
   * Creates a setter function for a static double field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the static double field.
   * @return A function that takes a value and sets the static double field value.
   */
  public static DoubleConsumer setterOfStaticDouble(VarHandle fieldHandle) {
    return (value) -> fieldHandle.set(value);
  }

  /**
   * Creates a getter function for an integer field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the integer field.
   * @return A function that takes an instance and returns the integer field value.
   */
  public static <I> ToLongFunction<I> getterOfInteger(VarHandle fieldHandle) {
    return (instance) -> (long) fieldHandle.get(instance);
  }

  /**
   * Creates a getter function for a static integer field represented by the given {@link
   * VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the static integer field.
   * @return A function that returns the static integer field value.
   */
  public static LongSupplier getterOfStaticInteger(VarHandle fieldHandle) {
    return () -> (long) fieldHandle.get();
  }

  /**
   * Creates a getter function for an integer method represented by the given {@link MethodHandle}.
   *
   * <p>The referenced method must be an instance method that takes no parameters and returns a
   * {@code int} or {@code long} value.
   *
   * @param methodHandle The {@code MethodHandle} representing the integer method.
   * @return A function that takes an instance and returns the integer method value.
   */
  public static <I> ToLongFunction<I> getterOfInteger(MethodHandle methodHandle) {
    return (instance) -> {
      try {
        return (long) methodHandle.invoke(instance);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Creates a getter function for a static integer method represented by the given {@link
   * MethodHandle}.
   *
   * <p>The referenced method must be a static method that takes no parameters and returns a {@code
   * int} or {@code long} value.
   *
   * @param methodHandle The {@code MethodHandle} representing the static integer method.
   * @return A function that returns the static integer method value.
   */
  public static LongSupplier getterOfStaticInteger(MethodHandle methodHandle) {
    return () -> {
      try {
        return (long) methodHandle.invoke();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  /**
   * Creates a setter function for an integer field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the integer field.
   * @return A function that takes an instance and a value, and sets the integer field value.
   */
  public static <I> ObjLongConsumer<I> setterOfInteger(VarHandle fieldHandle) {
    return (obj, value) -> fieldHandle.set(obj, value);
  }

  /**
   * Creates a setter function for a static integer field represented by the given {@link
   * VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the static integer field.
   * @return A function that takes a value and sets the static integer field value.
   */
  public static LongConsumer setterOfStaticInteger(VarHandle fieldHandle) {
    return (value) -> fieldHandle.set(value);
  }

  /**
   * Returns an array of parameter types for the given arguments, unboxing wrapper types to their
   * corresponding primitive types.
   *
   * @param args The arguments to determine parameter types for.
   * @return An array of parameter types.
   */
  public static Class<?>[] getParameterTypes(Object... args) {
    return Arrays.stream(args)
        .map(Object::getClass)
        .map(ReflectionUtil::unbox)
        .toArray(Class<?>[]::new);
  }

  /**
   * Converts an array of parameter types to a comma-separated string of their fully-qualified
   * names.
   *
   * @param parameterTypes The array of parameter types.
   * @return A comma-separated string of parameter type fully-qualified names.
   */
  public static String toArgumentTypeList(Class<?>[] parameterTypes) {
    return String.join(
        ", ", Arrays.stream(parameterTypes).map(Class::getName).toArray(String[]::new));
  }

  /**
   * Converts an array of arguments to a comma-separated string of their fully-qualified type names,
   * unboxing primitive wrapper types.
   *
   * @param args The array of arguments.
   * @return A comma-separated string of argument fully-qualified type names.
   */
  public static String toArgumentTypeList(Object... args) {
    return String.join(
        ", ",
        Arrays.stream(args)
            .map(Object::getClass)
            .map(ReflectionUtil::unbox)
            .map(Class::getName)
            .toArray(String[]::new));
  }

  private ReflectionUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}
