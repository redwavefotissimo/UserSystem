package com.common.AbstractOrInterface;

public @interface ClassInfoAnnotation {
    String name();
    String description() default "";
}
