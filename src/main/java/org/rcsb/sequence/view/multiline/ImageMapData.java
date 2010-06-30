package org.rcsb.sequence.view.multiline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.json.JSONException;
import org.json.JSONObject;
import org.rcsb.sequence.model.Annotation;

public abstract class ImageMapData implements Serializable
{
   private static final long serialVersionUID = 1L;

   private final Collection<Entry> imageMapEntries = new ArrayList<Entry>();

   public final String mapName;
   private final int drawerHeight;
   private int yOffset = 0;

   public abstract void populateImageMapData();

   public ImageMapData(String mapName, int drawerHeight)
   {
      this.mapName = mapName;
      this.drawerHeight = drawerHeight;
      
      populateImageMapData();
   }

   public Collection<Entry> getImageMapDataEntries()
   {
      return Collections.unmodifiableCollection(imageMapEntries);
   }
   
   
   public int getImageHeightPx()
   {
      return this.drawerHeight;
   }

   protected void addAllImageMapDataEntries(Collection<Entry> col)
   {
      imageMapEntries.addAll(col);
   }

   protected void addImageMapDataEntry(Entry e)
   {
      imageMapEntries.add(e);
   }

   protected int getYOffset()
   {
      return yOffset;
   }

   protected void setYOffset(int offset)
   {
      yOffset = offset;
   }

   
   /** A class that provides the coordinates for a sub-region of an image map
    * 
    * 
    */
   public class Entry implements Serializable
   {

      private static final long serialVersionUID = 1L;

      protected final String annotationValue;

      protected final String title;

      protected final int x1;

      protected final int x2;

      protected final int y1;

      protected final int y2;

      public String getTitle()
      {
         return title;
      }

      public int getX1()
      {
         return x1;
      }

      public int getX2()
      {
         return x2;
      }

      public int getY1()
      {
         return y1 + getYOffset();
      }

      public int getY2()
      {
         return y2 + getYOffset();
      }

      public String getAnnotationValue()
      {
         return annotationValue;
      }

      public Entry(int x1, int x2, String title, Annotation<?> a)
      {
         this(x1, 0, x2, getImageHeightPx(), title, getValueString(a));
      }

      public Entry(int x1, int y1, int x2, int y2, String title, Annotation<?> a)
      {
         this(x1, y1, x2, y2, title, getValueString(a));
      }

      public Entry(int x1, int y1, int x2, int y2, String title, String annotationValue)
      {
         this.x1 = x1;
         this.x2 = x2;
         this.y1 = y1;
         this.y2 = y2;
         this.title = title;
         this.annotationValue = annotationValue;
      }

      private int hashCode = 23;

      @Override
      public int hashCode()
      {
         if (hashCode == 23)
         {
            final int PRIME = 31;
            hashCode = PRIME * hashCode + x1;
            hashCode = PRIME * hashCode + x2;
            hashCode = PRIME * hashCode + y1;
            hashCode = PRIME * hashCode + y2;
         }
         return hashCode;
      }

      @Override
      public boolean equals(Object obj)
      {
         if (this == obj) return true;
         if (obj == null) return false;
         if (getClass() != obj.getClass()) return false;
         if (hashCode() != obj.hashCode()) return false;
         final Entry other = (Entry) obj;
         if (x1 != other.x1) return false;
         if (y1 != other.y1) return false;
         System.err.println("Non-identical HtmlMapData.Entry objects found with same start co-ords: " + this + "; " + other);
         if (x2 != other.x2) return false;
         if (y2 != other.y2) return false;
         System.err.println("Non-identical HtmlMapData.Entry objects found to be equal: " + this + "; " + other);
         return true;
      }

      @Override
      public String toString()
      {
         StringBuilder result = new StringBuilder();

         result.append("Map element ").append('(').append(x1).append(',').append(y1 + yOffset).append(") to (").append(x2).append(',')
               .append(y2 + yOffset).append(") with title '").append(title).append('\'');

         return result.toString();
      }

      public JSONObject getJson()
      {
         JSONObject result = new JSONObject();
         try
         {
            result.put("x1", x1);
            result.put("y1", y1);
            result.put("x2", x2);
            result.put("y2", y2);
            result.put("t", title);
            result.put("av", annotationValue);
         }
         catch (JSONException e)
         {
            System.err.println("Couldn't generate json for " + this + " " +  e.getMessage());
            e.printStackTrace();
         }
         return result;
      }
   }

   public static final ImageMapData EMPTY_HTML_DATA_MAP = new ImageMapData("empty", 0)
   {
      private static final long serialVersionUID = 1L;

      @Override
      public void populateImageMapData()
      {
         // do nothing
      }
   };

   private static String getValueString(Annotation<?> a)
   {
      return a == null ? "n/a" : a.getAnnotationValue().value().toString();
   }
}
