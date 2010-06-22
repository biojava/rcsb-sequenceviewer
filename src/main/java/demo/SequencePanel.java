package demo;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.biojava.bio.structure.align.ce.AbstractUserArgumentProcessor;
import org.biojava.utils.io.InputStreamProvider;

import org.rcsb.sequence.biojavadao.BioJavaPubMedFactory;
import org.rcsb.sequence.biojavadao.BioJavaResidueInfoFactory;
import org.rcsb.sequence.biojavadao.BioJavaSequenceCollectionFactory;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.core.PubMedProvider;
import org.rcsb.sequence.core.ResidueProvider;
import org.rcsb.sequence.core.SequenceCollectionFactory;
import org.rcsb.sequence.core.SequenceCollectionProvider;
import org.rcsb.sequence.model.ResidueInfoFactory;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.model.SequenceCollection;
import org.rcsb.sequence.view.html.ChainView;
import org.rcsb.sequence.view.html.ViewParameters;
import org.rcsb.sequence.view.image.SequenceImage;

public class SequencePanel {

	public static void main(String[] args){

		String pdbId = "1a4w";
		String chainId = "H";

		// define where PDB files are stored...
		System.setProperty(AbstractUserArgumentProcessor.PDB_DIR,"/tmp/");
		 System.setProperty(InputStreamProvider.CACHE_PROPERTY, "true");
		 
		SequencePanel panel = new SequencePanel();		
		
		panel.initBioJavaView();


		BufferedImage img = panel.load(pdbId,chainId);

		JPanel imgpanel = new ShowImage(img);


		JFrame frame = new JFrame("Display image");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(imgpanel);
		frame.setSize(img.getWidth() , img.getHeight()+10);
		frame.setVisible(true);


	}

	private BufferedImage load(String pdbId, String chainId) {

		SequenceCollection coll = SequenceCollectionProvider.get(pdbId);

		Sequence s = coll.getChainByPDBID(chainId);
		s.ensureAnnotated();
		
		ViewParameters params = new ViewParameters();
		
		//params.setAnnotations(AnnotationRegistry.getAllAnnotations());
		//params.setDesiredTopRulerRns(ResidueNumberScheme.ATOM);
		//params.setDesiredBottomRulerRns(ResidueNumberScheme.SEQRES);
		
		System.out.println("supported annotations: " + params.getAnnotations().size());
		
		ChainView view = new ChainView(s, params);
		
		SequenceImage image = view.getSequenceImage();

		BufferedImage buf = image.getBufferedImage();

		return buf;


	}

	public SequencePanel(){

	}

	/** provide a default view using BioJava.. could be done using some proper configuration managment...
	 * 
	 */
	public void initBioJavaView(){

		// first the Residue Provider
		ResidueInfoFactory refactory = new BioJavaResidueInfoFactory();
		ResidueProvider.setResidueInfoFactory(refactory);

		// next the SequenceCollection Provider
		SequenceCollectionFactory sfactory = new BioJavaSequenceCollectionFactory();
		SequenceCollectionProvider.setSequenceCollectionFactory(sfactory);

		BioJavaPubMedFactory pfactory = new BioJavaPubMedFactory();
		PubMedProvider.setPubMedFactory(pfactory);
	}


}

class ShowImage extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6604403623026935415L;
	BufferedImage  image;
	public ShowImage(BufferedImage img) {
		image = img;
	}

	public void paint(Graphics g) {
		g.drawImage( image, 0, 0, null);
	}


}
