package com.authrus.common.swing.ui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used to annotate a method that is to be invoked when
 * the value of a combo box changes.
 * 
 * @author Niall Gallagher
 * 
 * @see com.authrus.common.swing.ui.Controller
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComboBoxChange {
   String value();
}
