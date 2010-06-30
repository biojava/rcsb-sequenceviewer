package org.rcsb.sequence.view.html;



public class LinkOut extends HtmlElement {

	 public LinkOut(String url, String altText) {
	      super("a");
	      
	      addAttribute("href", url);
	      addAttribute("target", "_blank");
	      
	      HtmlElement linkImg = (new HtmlElement("span"))
	             .addAttribute("class", "iconSet-main icon-external")
	             .addAttribute("title", altText);
	      
	      addChild(linkImg);
	   }
   
   
}
