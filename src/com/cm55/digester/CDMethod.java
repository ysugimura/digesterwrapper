package com.cm55.digester;

import java.lang.annotation.*;

/**
 * Javaビーンのアクセスメソッドを宣言する。
 * <p>
 * デフォルトでは、"set"を除き、最初を小文字化した名称になる。つまり、"setValue"の場合は"value"になるが、
 * extNameを指定することにより、異なる名称を指定することもできる。
 * </p>
 * <pre>
 * @CDMethod(extName="text")
 * public void setTextStr(String value) {
 *  ....
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CDMethod {
  String extName() default "";
}
