package com.cm55.digester;

import java.lang.annotation.*;

/**
 * Digester用XML要素クラスアノテーション
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CDElementType {
  
  /** 一つのCDElementクラスが担当するXMLタグの名称 */
  String tagName();
  
  /** 子要素として認められるCDElementクラスを列挙する */
  Class<? extends CDElement>[]children() default {};
}
