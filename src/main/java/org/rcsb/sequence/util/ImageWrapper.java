package org.rcsb.sequence.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

import javax.swing.JScrollPane;

import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.core.SequenceCollectionProvider;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.model.SequenceCollection;
import org.rcsb.sequence.view.html.ChainView;
import org.rcsb.sequence.view.html.ViewParameters;
import org.rcsb.sequence.view.multiline.SequenceImage;

import org.biojava.bio.structure.align.gui.JPrintPanel;
import org.biojava.bio.structure.align.gui.MenuCreator;

public class ImageWrapper {

	public static JFrame showSeq(String pdbId, String chainId) {
		
		
		BufferedImage img = viewMultiLine(pdbId,chainId);
		
		// alternative:
		//BufferedImage img = panel.viewOneLine(pdbId,chainId);
		
		
		// now wrap the bufferedImage:
		//JLabel icon = new JLabel(new ImageIcon(img));
		
		
		JPrintPanel panel = new JPrintPanel();
		panel.add(new JLabel(new ImageIcon(img)));
		JScrollPane scroll = new JScrollPane(panel);
		// display the Pane in a frame
		
		JFrame frame = new JFrame("Display image " + pdbId + "." + chainId);
		
		JMenuBar menu = MenuCreator.getAlignmentTextMenu(frame, panel, null);
		frame.setJMenuBar(menu);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(scroll);
		//scroll.setPreferredSize(new Dimension(600,600));
		frame.pack();
		frame.setVisible(true);
		return frame;
		
	}
	
	public static  BufferedImage viewMultiLine(String pdbId, String chainId) {

		SequenceCollection coll = SequenceCollectionProvider.get(pdbId);

		Sequence s = coll.getChainByPDBID(chainId);
		s.ensureAnnotated();
		
		ViewParameters params = new ViewParameters();
		
//		params.setDesiredSequenceRns(ResidueNumberScheme.ATOM);
		
	
		// register the anntation mapper for th emulti line view
		
//		Annotation2MultiLineDrawer a2h = new Annotation2MultiLineDrawer();
		
				
		
		Collection<AnnotationName > annos = params.getAnnotations();
		
		List<AnnotationName> newAnnos = new ArrayList<AnnotationName>();
		for ( AnnotationName anno : annos){
			newAnnos.add(anno);
		}
		
		newAnnos.add(AnnotationRegistry.getAnnotationByName("SITE record"));
		params.setAnnotations(newAnnos);
		
//		view.setAnnotationDrawMapper(a2h);
		
		ChainView view = new ChainView(s, params);
		SequenceImage image = view.getSequenceImage();

		BufferedImage buf = image.getBufferedImage();
		
//		try {
//			javax.imageio.ImageIO.write(buf, "png", 
//					new java.io.File("C:\\Documents and Settings\\gjj\\Desktop\\jj\\"+pdbId+chainId+".png"));
//		} catch (Exception e){}

		return buf;


	}


}
