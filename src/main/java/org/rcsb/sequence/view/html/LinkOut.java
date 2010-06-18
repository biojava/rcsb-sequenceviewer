package org.rcsb.sequence.view.html;



public class LinkOut extends HtmlElement {

	static String linkURL = "/pdb/skins/web20/default/images/icons/square_external_12.png";
	
   public LinkOut(String url, String altText) {
      super("a");
      
      addAttribute("href", url);
      addAttribute("target", "_blank");
      
      HtmlElement linkImg = (new HtmlElement("img"))
             .addAttribute("src",  linkURL)
             .addAttribute("alt", altText)
             .addAttribute("title", altText)
             .addAttribute("border", "0");
      
      addChild(linkImg);
   }
   
   public static void setLinkImg(String imgPath){
	   linkURL = imgPath;
   }
   
   
}
