package com.cm55.digester;


/**
 * Digesterにて読み込まれる一つのXML要素を表すクラス。
 * この要素のタグ名及び下位タグはCDElementTypeによってアノテーションされる。
 */
public interface CDElement {

  
  /** 子要素を追加する */
  public void addChild(CDElement element);
  
  /** 子要素の数を取得する */
  public int childCount();
  
  /** 指定インデックス位置の子要素を取得する */
  public CDElement getChild(int index) ;
  
  /** 子要素のイテレータ */
  public Iterable<CDElement>children();
  
  /** タグ名を取得する */
  public String getTagName();
  
  /** 要素オブジェクトがすべて構築された後でチェック */
  public void check();
  
  public String toString(String indent);
  
}
