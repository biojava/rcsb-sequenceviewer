package org.rcsb.sequence.view.html;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 * Really simple HTML assemling class
 * @author mulvaney
 *
 */
public class HtmlElement {

   protected final String name;
   protected final Map<String, String> attributes = new HashMap<String, String>();
   protected StringBuilder content;
   protected final Set<HtmlElement> children = new LinkedHashSet<HtmlElement>();
   
   public HtmlElement(String name)
   {
      this.name = name;
   }
   
   public String getHtml()
   {
      StringBuilder result = new StringBuilder();
      int depth = 0;
      
      assembleHtml(result, depth);
      
      return result.toString();
   }
   
   public String getEscapedHtml()
   {
      String result = null;
      try {
         result = URLEncoder.encode(getHtml(), "UTF-8");
      } catch (UnsupportedEncodingException e) {
         System.err.println("Couldn't generate escaped html. " + e.getMessage());
      }
      return result;
   }
   
   private static final int MAX_DEPTH = 20;
   
   private void assembleHtml(StringBuilder result, int depth)
   {
      if(depth >= MAX_DEPTH)
      {
         throw new RuntimeException("Depth of nested html is too large");
      }
      
      // prettify the output a little
      appendTabs(result, depth);
      
      result.append('<')
            .append(name);

      for( Map.Entry<String, String> attribute : attributes.entrySet() )
      {
         result.append(' ')
               .append(attribute.getKey())
               .append('=')
               .append('"')
               .append(attribute.getValue())
               .append('"');
      }
      
      if(children.size() == 0 && (content == null || content.length() == 0))
      {
         result.append(' ')
               .append('/')
               .append('>');
      }
      
      else
      {
         result.append('>');
         
         if(children.size() != 0) result.append('\n');
         
         for( HtmlElement e : children )
         {
            if(e != this) e.assembleHtml(result, depth + 1);
            
            // the above will end with a character return. we should tab in the appropriate amount
            appendTabs(result, depth);
         }
         
         if(content != null)
         {
            result.append(content);
         }
         
         result.append('<')
               .append('/')
               .append(name)
               .append('>');
      }
      result.append('\n');
   }
   
   private void appendTabs(final StringBuilder sb, final int numTabs)
   {
      for(int i = 0; i < numTabs; i++) sb.append('\t');
   }
   
   public void addChild(HtmlElement e)
   {
      if(e != null && e != this) {
         children.add(e);
      }
   }
   
   public void replaceContent(String someContent)
   {
      this.content = new StringBuilder(someContent);
   }
   
   public void replaceContent(StringBuilder someContent)
   {
      this.content = someContent;
   }
   
   public HtmlElement appendToContent(String someContent)
   {
      if(this.content == null)
      {
         this.content = new StringBuilder();
      }
      
      this.content.append(someContent);
      
      return this;
   }
   
   public HtmlElement appendToContent(StringBuilder someContent)
   {
      if(this.content == null)
      {
         this.content = someContent;
      }
      else
      {
         this.content.append(someContent);
      }
      return this;
   }
   
   public HtmlElement addAttribute(String attrName, String value)
   {
      if(attrName != null && attrName.length() > 0)
      {
         attributes.put(attrName, value);
      }
      return this;
   }
   
   @Override
   public String toString()
   {
      return getHtml();
   }
}
