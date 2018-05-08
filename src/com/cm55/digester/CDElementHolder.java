package com.cm55.digester;

import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.digester.*;
import org.xml.sax.*;


/**
 * 一つの要素クラス({@link CDElement}クラスのサブクラス）をラップする。
 * Digesterには「ルール」を与えなければならないのだが、{@link CDElement}は{@link CDElementType}
 * によってアノテーションされた静的な情報としてXMLの構成を保持している。
 * これらのXML構成をDigesterのルールに変換するのがこのクラス。
 */
public class CDElementHolder {
  
  /** ラップする要素クラス */
  private final Class<? extends CDElement>clazz;
  
  /** 要素アノテーション */
  private final CDElementType elemType;
  
  /** 子要素タグ名・要素ホルダマップ */
  private final Map<String, CDElementHolder>children = new HashMap<>();

  /** この要素に関してDigesterに与えるルールリスト */
  private final List<Rule>rules = new ArrayList<>();
  
  /**
   * 要素ホルダを作成する
   * @param clazz 要素クラス
   * @param isTop トップ要素であることを示す
   */
  CDElementHolder(Digester digester, Class<? extends CDElement>clazz, boolean isTop) {

    this.clazz = clazz;
    
    // このCDElementクラスに指定されているCDElementTypeアノテーションを取得
    elemType = clazz.getAnnotation(CDElementType.class);
    if (elemType == null) {
      throw new CDElementException("No CDElement annotation for " + clazz);
    }
    
    // オブジェクト生成ルールを作成
    rules.add(createObjectCreateRule());
    
    // プロパティ設定ルールを作成する
    rules.add(createSetPropertiesRule());
        
    if (!isTop) {
      rules.add(new SetNextRule("addChild"));
    }
    
    // 全ルールにDigesterを設定する
    for (Rule rule: rules) {
      rule.setDigester(digester);
    }
  }

  /**
   * オブジェクト生成ルールを作成する
   * @return
   */
  private ObjectCreateRule createObjectCreateRule() {
    return new ObjectCreateRule(clazz) {
      public void begin(Attributes attributes) throws Exception {

        // Identify the name of the class to instantiate
        String realClassName = className;
        if (attributeName != null) {
            String value = attributes.getValue(attributeName);
            if (value != null) {
                realClassName = value;
            }
        }


        // Instantiate the new object and push it on the context stack
        Class<?> clazz = digester.getClassLoader().loadClass(realClassName);
        Object instance;
        try {
          instance = clazz.newInstance();
        } catch (Exception ex) {
          System.err.println("" + clazz);
          ex.printStackTrace();
          throw ex;
        }
        digester.push(instance);

      }
    };
  }
  
  /** プロパティ設定ルールを作成する */
  private SetPropertiesRule createSetPropertiesRule() {
    List<String>extNames = new ArrayList<>();
    List<String>intNames = new ArrayList<>();
    extToInt(extNames, intNames);
    
    // SetPropertiesRuleオブジェクトを作成
    return new SetPropertiesRule(extNames.toArray(new String[0]), intNames.toArray(new String[0])) {
        {
          this.setIgnoreMissingProperty(false);
        }
    };   
  }
  
  void extToInt(List<String>extNames, List<String>intNames) {
    Set<String>extSet = new HashSet<>();
    
    // メソッドを処理
    for (Method method: clazz.getMethods()) {
         
      // @CDMethodアノテーションの継いているメソッドだけを調査対象とする
      CDMethod anno = method.getAnnotation(CDMethod.class);
      if (anno == null) continue;
      
      // このメソッドを定義するクラスがpublicでないとBeanUtilsにてアクセスできない。
      Class<?> declaringClass = method.getDeclaringClass();
      if ((declaringClass.getModifiers() & Modifier.PUBLIC) == 0)
        throw new RuntimeException("" + declaringClass.getName() + " must be public");

      /* CDElementの下位クラスとした
      // このメソッドを定義するクラスが@Beanでないと名称変更されてしまう。
      Bean bean = declaringClass.getAnnotation(Bean.class);
      if (bean == null) throw new InternalError("class " + declaringClass + " is not a @Bean");
      */
      
      // メソッド名はset*の形で引数が一つでなければいけない。
      String methodName = method.getName();
      if (methodName.length() <= 3 || 
          !methodName.startsWith("set") ||
          method.getParameterTypes().length != 1) {
        throw new RuntimeException("Invalid method " + methodName);
      }

      // 内部名を作成
      String intName = 
        Character.toLowerCase(methodName.charAt(3)) +  methodName.substring(4);
      
      // 外部名を取得する。空文字列の場合は内部名称と同一になる。      
      String extName = anno.extName();
      if (extName.equals("")) extName = intName;
      
      // 外部名が登録済みでないか？
      if (extSet.contains(extName)) {
        throw new RuntimeException("Duplicate definition of attribute " + extName);
      }
            
      // マップに設定
      extNames.add(extName);
      intNames.add(intName);
    }
  }
  
  /** XMLタグ名を取得する */
  String tagName() {
    return elemType.tagName();
  }
  
  /** {@link Rule}リストを取得する */
  List<Rule> getRules() {
    return rules;
  }
  
  /** 子要素クラスの配列を取得する */
  Class<? extends CDElement>[]getChildren() {
    return elemType.children();
  }
  
  /** 子要素のホルダを追加する */
  void addChildHolder(CDElementHolder holder) {  
    children.put(holder.tagName(), holder);
  }
  
  /** 指定されたタグ名の子要素のホルダがあれば取得する。なければnullを返す */
  CDElementHolder getChildHolder(String tagName) {
    return children.get(tagName);
  }
    
  /** デバッグ用文字列化 */
  @Override
  public String toString() {
    return tagName();
  }

}