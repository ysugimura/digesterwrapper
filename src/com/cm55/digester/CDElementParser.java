package com.cm55.digester;

import java.io.*;
import java.util.*;

import org.apache.commons.digester.*;


/**
 * XMLパーサ
 * ReaderからXMLを読み込み、CDElementオブジェクトの木構造として返す。
 * @author ysugimura
 *
 */
public class CDElementParser {

  /** ダイジェスタ */
  private Digester digester = new Digester();

  /** トップ要素のタグ名・要素ホルダマップ */
  private Map<String, CDElementHolder>topElementMap = 
    new HashMap<String, CDElementHolder>();

  
  /**
   * 複数のルート要素クラスを指定して作成
   * @param rootClasses
   */
  @SuppressWarnings("unchecked")
  public CDElementParser(Class<? extends CDElement>...rootClasses) {
    
    // トップ要素マップを作成する
    createTopElementMap(rootClasses);

    // ダイジェスタにルールを設定する
    digester.setRules(new RulesBase() {
      @SuppressWarnings("rawtypes")
      public List match(String namespaceURI, String pattern) { 

        // CDElementHolderを取得
        CDElementHolder holder = getHolderFor(pattern);
        if (holder == null)
          throw new CDElementException("Element not found for " + pattern);

        // ルールリストを取得
        List ruleList = holder.getRules();
        if (ruleList.size() == 0)
          throw new CDElementException("Element not found for " + pattern);
        return ruleList;
      }
    });    
  }


  /** 
   * 要素パーサーを作成する。
   * トップ要素として認められるクラスを複数許す
   * @param digester ダイジェスタ
   * @param topClasses 複数のトップ要素クラス
   */
  private void createTopElementMap(@SuppressWarnings("unchecked") Class<? extends CDElement>...topClasses) {

    // 任意の要素クラス・要素ホルダのマップ
    final Map<Class<? extends CDElement>, CDElementHolder>elementHolderMap = 
      new HashMap<Class<? extends CDElement>, CDElementHolder>();

    // 各トップ要素のクラスについて処理する
    for (Class<? extends CDElement>topClass: topClasses) {

      // このトップ要素のホルダを作成する
      CDElementHolder topHolder = new Object() {

        /** 
         * 指定された要素クラスの要素ホルダを作成する。その際、その子要素の要素
         * ホルダも作成する。
         * 
         * @param top
         * @param clazz
         * @return
         */
        private CDElementHolder build(boolean top, Class<? extends CDElement>clazz) {       

          CDElementHolder holder = elementHolderMap.get(clazz);

          // 既に作成済み
          if (holder != null) return holder;

          // 作成してElementHolderMapに登録する
          holder = new CDElementHolder(digester, clazz, top);
          elementHolderMap.put(clazz, holder);

          // 子要素クラスについてもホルダを作成し、登録する
          for (Class<? extends CDElement>childClass: holder.getChildren()) {
            CDElementHolder childHolder = build(false, childClass);
            //holder.children.put(childHolder.tagName(), childHolder);
            holder.addChildHolder(childHolder);
          }

          return holder;

        }        
      }.build(true, topClass);

      // トップ要素マップに登録
      topElementMap.put(topHolder.tagName(), topHolder);      
    }      
  }
  
  /**
   * '/'で区切られたタグ名の配列から該当する要素のホルダを取得する。
   * 無い場合はnullを返す。
   * 
   * @param pattern '/'で区切られたタグ名配列
   * @return 要素ホルダ。該当するものが無い場合はnullを返す。
   */
  private CDElementHolder getHolderFor(String pattern) {

    // タグ名配列を取得
    String[]tagNames = pattern.split("/");

    // トップ要素マップにて最初の要素ホルダを取得
    CDElementHolder holder = topElementMap.get(tagNames[0]);
    if (holder == null) {
      return null;
    }

    // 引き続きタグ名があるなら、子要素ホルダを取得していく
    for (int i = 1; i < tagNames.length; i++) {
      CDElementHolder nextHolder = holder.getChildHolder(tagNames[i]);
      if (nextHolder == null) return null;
      holder = nextHolder;
    }

    return holder;
  }

  /** Readerから読み込んだXMLをパースしてCDElementオブジェクトの木構造を作成する */
  @SuppressWarnings("unchecked")
  public <T extends CDElement>T parse(Reader reader) {
    try {
      return (T)digester.parse(reader);
    } catch (Exception ex) {
      throw new CDElementException(ex);
    }
  }
  
  public <T extends CDElement>T parse(String text) {
    System.out.println("" + text);
    return parse(new StringReader(text));
  }
}
