package com.cm55.digester;

import static org.junit.Assert.*;

import org.junit.*;


public class CDElementParserTest {

  public static final String XML =
      "<top value='123'>\n" +  
        "<child1/><child2 setting='abc'></child2>" +
      "</top>";
  
  @Test
  @SuppressWarnings("unchecked")
  public void test() {
    CDElementParser parser = new CDElementParser(Top.class);
    Top top = (Top)parser.parse(XML);
    assertEquals(123, top.value);
    assertEquals(2, top.childCount());
    assertEquals("abc", ((Child2)top.getChild(1)).value);
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
  @CDElementType(tagName="child1")
  public static class Child1 extends AbstractCDElement {
    public void check() {}
  }

  @CDElementType(tagName="child2")
  public static class Child2 extends AbstractCDElement {
    String value;
    public void check() {}
    @CDMethod(extName="setting")
    public void setText(String value) {
      this.value = value;
    }
  }
}
