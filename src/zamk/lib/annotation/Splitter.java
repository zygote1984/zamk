package zamk.lib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Splitter {
	//fully qualified name of the accepted type
	String inputType();
	String[] output() default {};
	String splitType() default "manual";
}
