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
import com.nrg948.util.function.ToBooleanFunction;
import com.nrg948.util.function.ToFloatFunction;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.ObjDoubleConsumer;
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
   * @param <T> The type of the field value.
   * @param fieldHandle The {@code VarHandle} representing the field.
   * @param instance The instance from which to get the field value.
   * @return The value of the field.
   */
  public static <T> T get(VarHandle fieldHandle, Object instance) {
    return (T) fieldHandle.get(instance);
  }

  /**
   * Gets the value returned by the method referenced by the given {@link MethodHandle} from the
   * specified instance.
   *
   * <p>The referenced method must be an instance method that takes no parameters and returns a
   * value.
   *
   * @param <T> The type of the method return value.
   * @param methodHandle The {@code MethodHandle} representing the method.
   * @param instance The instance from which to invoke the method.
   * @return The value returned by the method.
   */
  public static <T> T get(MethodHandle methodHandle, Object instance) {
    try {
      return (T) methodHandle.invoke(instance);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets the value of the field represented by the given {@link VarHandle} on the specified
   * instance.
   *
   * @param <T> The type of the field value.
   * @param fieldHandle The {@code VarHandle} representing the field.
   * @param instance The instance on which to set the field value.
   * @param value The value to set.
   */
  public static <T> void set(VarHandle fieldHandle, Object instance, T value) {
    fieldHandle.set(instance, value);
  }

  /**
   * Creates a getter function for the field represented by the given {@link VarHandle}.
   *
   * @param <T> The type of the field value.
   * @param fieldHandle The {@code VarHandle} representing the field.
   * @return A function that takes an instance and returns the field value.
   */
  public static <T> Function<Object, T> getterOf(VarHandle fieldHandle) {
    return (instance) -> (T) fieldHandle.get(instance);
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
  public static <T> Function<Object, T> getterOf(MethodHandle methodHandle) {
    return (instance) -> {
      try {
        return (T) methodHandle.invoke(instance);
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
  public static <T> BiConsumer<Object, T> setterOf(VarHandle fieldHandle) {
    return (instance, value) -> fieldHandle.set(instance, value);
  }

  /**
   * Creates a getter function for an enum field represented by the given {@link VarHandle}.
   *
   * @param <E> The type of the enum.
   * @param fieldHandle The {@code VarHandle} representing the enum field.
   * @param enumType The class of the enum type.
   * @return A function that takes an instance and returns the enum value.
   */
  public static <E extends Enum<E>> Function<Object, E> getterOfEnum(
      VarHandle fieldHandle, Class<E> enumType) {
    return (instance) -> (E) fieldHandle.get(instance);
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
  public static <E extends Enum<E>> Function<Object, E> getterOfEnum(
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
   * Creates a setter function for an enum field represented by the given {@link VarHandle}.
   *
   * @param <E> The type of the enum.
   * @param fieldHandle The {@code VarHandle} representing the enum field.
   * @param enumType The class of the enum type.
   * @return A function that takes an instance and a value, and sets the enum field value.
   */
  public static <E extends Enum<E>> BiConsumer<Object, E> setterOfEnum(
      VarHandle fieldHandle, Class<E> enumType) {
    return (instance, value) -> fieldHandle.set(instance, value);
  }

  /**
   * Creates a getter function for a boolean field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the boolean field.
   * @return A function that takes an instance and returns the boolean field value.
   */
  public static ToBooleanFunction<Object> getterOfBoolean(VarHandle fieldHandle) {
    return (instance) -> (boolean) fieldHandle.get(instance);
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
  public static ToBooleanFunction<Object> getterOfBoolean(MethodHandle methodHandle) {
    return (instance) -> {
      try {
        return (boolean) methodHandle.invoke(instance);
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
  public static ObjBooleanConsumer<Object> setterOfBoolean(VarHandle fieldHandle) {
    return (instance, value) -> fieldHandle.set(instance, value);
  }

  /**
   * Creates a getter function for a float field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the float field.
   * @return A function that takes an instance and returns the float field value.
   */
  public static ToFloatFunction<Object> getterOfFloat(VarHandle fieldHandle) {
    return (instance) -> (float) fieldHandle.get(instance);
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
  public static ToFloatFunction<Object> getterOfFloat(MethodHandle methodHandle) {
    return (instance) -> {
      try {
        return (float) methodHandle.invoke(instance);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  public static BiConsumer<Object, Float> setterOfFloat(VarHandle fieldHandle) {
    return (instance, value) -> fieldHandle.set(instance, value);
  }

  /**
   * Creates a getter function for a double field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the double field.
   * @return A function that takes an instance and returns the double field value.
   */
  public static ToDoubleFunction<Object> getterOfDouble(VarHandle fieldHandle) {
    return (instance) -> (double) fieldHandle.get(instance);
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
  public static ToDoubleFunction<Object> getterOfDouble(MethodHandle methodHandle) {
    return (instance) -> {
      try {
        return (double) methodHandle.invoke(instance);
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
  public static ObjDoubleConsumer<Object> setterOfDouble(VarHandle fieldHandle) {
    return (instance, value) -> fieldHandle.set(instance, value);
  }

  /**
   * Creates a getter function for an integer field represented by the given {@link VarHandle}.
   *
   * @param fieldHandle The {@code VarHandle} representing the integer field.
   * @return A function that takes an instance and returns the integer field value.
   */
  public static ToLongFunction<Object> getterOfInteger(VarHandle fieldHandle) {
    return (instance) -> (long) fieldHandle.get(instance);
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
  public static ToLongFunction<Object> getterOfInteger(MethodHandle methodHandle) {
    return (instance) -> {
      try {
        return (long) methodHandle.invoke(instance);
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
  public static LongConsumer setterOfInteger(VarHandle fieldHandle, Object instance) {
    return (value) -> fieldHandle.set(instance, value);
  }

  private ReflectionUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}
