package org.rcsb.sequence.view.html;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.rcsb.sequence.model.Annotation;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.util.ResourceManager;


public class ColorUtil {

	
	public static final Color pdbRED;
	public static final Color pdbRED2;	
	public static final Color pdbPURPLE;
	public static final Color pdbBLUE;
	public static final Color pdbBLUE2; 
	public static final Color pdbGREEN;
	public static final Color pdbGREEN2;
	public static final Color pdbYELLOW;
	
	
	static {
		ResourceManager rm = new ResourceManager("sequenceview");
		
		pdbRED    	= Color.decode(rm.getString("pdbRED"));		
		pdbRED2  	= Color.decode(rm.getString("pdbRED2"));		
		pdbPURPLE 	= Color.decode(rm.getString("pdbPURPLE"));		
		pdbBLUE 	= Color.decode(rm.getString("pdbBLUE"));		
		pdbBLUE2  	= Color.decode(rm.getString("pdbBLUE2"));
		pdbGREEN  	= Color.decode(rm.getString("pdbGREEN"));	
		pdbGREEN2 	= Color.decode(rm.getString("pdbGREEN2"));
		pdbYELLOW 	= Color.decode(rm.getString("pdbYELLOW"));
		
	}
	
	
	
	//TODO: domains of same type should have the same color
	
   public static final Color[] COLORS = new Color[] { pdbRED, pdbRED2, pdbPURPLE, pdbBLUE, pdbBLUE2, pdbGREEN, pdbGREEN2, pdbYELLOW }; 
   
   public static Map<Object, Color> colorMap  = new HashMap<Object, Color>();
   
   public static Color getArbitraryColor(int aNumber)
   {
      return COLORS[ Math.abs(aNumber) % COLORS.length ];
   }
   
   public static Color getArbitraryColor(Annotation<?> a)
   {
      return getArbitraryColor(a.getAnnotationValue().value());
   }
   
   public static Color getArbitraryColor(AnnotationValue<?> av)
   {
      return getArbitraryColor(av.value());
   }
   
   public static Color getArbitraryColor(Object o)
   {
	   
	   if ( colorMap.containsKey(o))
		   return colorMap.get(o);
	   
	   Color c = getArbitraryColor(o.hashCode());
	   
	   // WARNING DANGER OF MEMORY LEAK
	   // never build up static data structures like this...
	   // TODO: move this to serialized cache...
	   colorMap.put(o, c);
	   // to avoid this leak, we are cleaning the Map every 100 domains...
	   // clear map every 100 domains...
	   if ( colorMap.entrySet().size() > 100){
		   colorMap.clear();
	   }
	   //PdbLogger.warn(c.getRed() + " " + c.getGreen() + " " + c.getBlue());
	   return c;
   }
   
   public static void cleanCache(){
	   colorMap.clear();
   }
 
   public static String getArbitraryHexColor(Object o)
   {
      return getColorHex(getArbitraryColor(o));
   }
   
   public static String getColorHex(Color c)
   {
      return "#" + Integer.toHexString((c.getRGB() & 0xffffff) | 0x1000000).substring(1);
   }
   
}
