package annot;

/*
  Exam: Advance Programming
  Year: 2019/2020
  Author: Gaspare Ferraro
  Mail: ferraro@gaspa.re
  Mat: 520549
*/

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Encrypt {

}
