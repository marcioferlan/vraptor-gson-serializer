package br.com.caelum.vraptor.serialization.gson.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Json {

	String[] include() default {};
	String[] exclude() default {};
	Class<? extends JsonFields> fields() default DEFAULT.class;

	// Dummy class to make include() property optional
	static final class DEFAULT extends JsonFields {
		@Override public String[] includes() { return null; }
	}

}
