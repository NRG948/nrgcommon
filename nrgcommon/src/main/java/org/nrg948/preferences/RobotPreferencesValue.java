package org.nrg948.preferences;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotates a field containing a RobotPreferences value. */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RobotPreferencesValue {

}