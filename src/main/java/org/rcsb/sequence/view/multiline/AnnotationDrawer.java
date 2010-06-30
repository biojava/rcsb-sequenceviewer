package org.rcsb.sequence.view.multiline;

import org.rcsb.sequence.conf.AnnotationName;

public interface AnnotationDrawer extends Drawer
{

   public abstract AnnotationName getAnnotation();
   public abstract boolean canDrawAnnotationsThatOverlap();

}
