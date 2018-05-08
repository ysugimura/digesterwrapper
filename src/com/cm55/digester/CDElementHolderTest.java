package com.cm55.digester;

import static org.junit.Assert.*;

import org.apache.commons.digester.*;
import org.junit.*;
import org.mockito.*;

public class CDElementHolderTest {

  @Mock Digester digester;
  
  @Before public void setup() {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void test() {

    
    CDElementHolder holder = new CDElementHolder(digester,  Top.class, true);
    
    // タグ名の確認
    assertEquals("top", holder.tagName());
    
    // 子要素クラスの確認
    assertArrayEquals(new Class[] {
       Child1.class,
       Child2.class,
      }, holder.getChildren()
    );
    
    // ルールの確認。外側からは確認が難しい。
    org.apache.commons.digester.Rule[]rules = holder.getRules().toArray(new org.apache.commons.digester.Rule[0]);
    for (org.apache.commons.digester.Rule rule: rules) {
      System.out.println("" + rule);
    }
    assertEquals(2, rules.length);
    ObjectCreateRule createRule = (ObjectCreateRule)rules[0];
    SetPropertiesRule propertiesRule = (SetPropertiesRule)rules[1];
    
  }
    
  @CDElementType(tagName="top", children={Child1.class, Child2.class})
  public static class Top extends AbstractCDElement {
    int value;
    @CDMethod
    public void setValue(int value) {
      this.value = value;
    }
    public void check() {}
  }
  public static class Child1 extends AbstractCDElement {
    public void check() {}
  }
  public static class Child2 extends AbstractCDElement {
    public void check() {}
  }
  
}
