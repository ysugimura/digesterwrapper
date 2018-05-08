package com.cm55.digester;

import java.util.*;

public abstract class AbstractCDElement implements CDElement {
  
  /** 親要素 */
  protected CDElement parent;
  
  /** 子要素リスト */
  protected java.util.List<CDElement>children = new ArrayList<CDElement>();
  
  /** 子要素を追加する */
  public void addChild(CDElement _element) {
    AbstractCDElement element = (AbstractCDElement)_element;
    if (element.parent != null) throw new IllegalStateException("Already added child");
    element.parent = this;
    children.add(element);
  }
  
  public int childCount() {
    if (children == null) return 0;
    return children.size();
  }
  
  public CDElement getChild(int index) {
    return children.get(index);
  }
  
  public Iterable<CDElement>children() {
    return children;
  }
  
  /** タグ名を取得する */
  public String getTagName() {
    CDElementType elemType = (CDElementType)getClass().getAnnotation(CDElementType.class);    
    return elemType.tagName();
  }
  

  /** 子要素をすべてチェック */
  protected void checkChildren() {
    if (children == null) return;
    for (CDElement child: children)
      child.check();
  }
  
  /** 文字列化 */
  @Override
  public String toString() {
    return toString("");
  }
  
  public String toString(String indent) {
    Class<? extends CDElement> clazz = getClass();
    
    CDElementType elemType = (CDElementType)clazz.getAnnotation(CDElementType.class);        
    return clazz.getSimpleName() + ", tagName:" + elemType.tagName();    
  }
}
