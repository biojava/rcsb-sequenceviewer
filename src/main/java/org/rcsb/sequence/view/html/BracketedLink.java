package org.rcsb.sequence.view.html;

public class BracketedLink extends HtmlElement {

   public BracketedLink(String url, String alt, String name)
   {
      super("a");
      addAttribute("href", url);
      addAttribute("alt", alt);
      appendToContent("[" + name + "]");
   }
   
}
