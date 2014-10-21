package com.sdl.webapp.common.api.mapping.annotations;

import com.sdl.webapp.common.api.mapping.Vocabularies;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SemanticDefaults {

    String vocab() default Vocabularies.SDL_CORE;

    String prefix() default "";

    boolean mapAllProperties() default true;
}
