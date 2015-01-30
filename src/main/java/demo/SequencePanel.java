package demo;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.rcsb.sequence.biojavadao.BioJavaPubMedFactory;
import org.rcsb.sequence.biojavadao.BioJavaResidueInfoFactory;
import org.rcsb.sequence.biojavadao.BioJavaSequenceCollectionFactory;
import org.rcsb.sequence.core.PubMedProvider;
import org.rcsb.sequence.core.ResidueProvider;
import org.rcsb.sequence.core.SequenceCollectionFactory;
import org.rcsb.sequence.core.SequenceCollectionProvider;
import org.rcsb.sequence.model.ResidueInfoFactory;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.model.SequenceCollection;
import org.rcsb.sequence.util.ImageWrapper;
import org.rcsb.sequence.view.html.ViewParameters;
import org.rcsb.sequence.view.multiline.SequenceImage;
import org.rcsb.sequence.view.oneline.Annotation2SingleLineDrawer;
import org.rcsb.sequence.view.oneline.OneLineView;

public class SequencePanel {

    JPanel imgpanel;

    public SequencePanel() {
        initBioJavaView();
    }

    public static void main(String[] args) {


        //String pdbId = "2B7N"; // crosslink2

//		String pdbId = "1A6L"; // crosslink3,4
//		String pdbId = "1UIS"; // crosslink1
//		String pdbId = "3HN3"; // attachment
//		String pdbId = "3MVJ"; // mod. res.
//		String pdbId = "1SU6"; // crosslink6
//		String pdbId = "1CAD"; // crosslink4
//		String pdbId = "3NYH";
//		String chainId = "A";

        // missing in loader:
//		String pdbId="1ibb";


        // culprit: 1htr???
        String pdbId = "5GDS";
        String chainId = "H";

        //String pdbId3 = "3KOB"; //
        //String chainId3 = "B";

        // define where PDB files are stored...
        //System.setProperty(AbstractUserArgumentProcessor.PDB_DIR,"/tmp/");
        //System.setProperty(InputStreamProvider.CACHE_PROPERTY, "true");
        initBioJavaView();
        ImageWrapper.showSeq(pdbId, chainId);


//		showSeq(panel, pdbId2,chainId2);
//		showSeq(panel, pdbId3,chainId3);
    }

    /**
     * provide a default view using BioJava.. could be done using some proper configuration managment...
     */
    public static void initBioJavaView() {

        // first the Residue Provider
        ResidueInfoFactory refactory = new BioJavaResidueInfoFactory();
        ResidueProvider.setResidueInfoFactory(refactory);

        // next the SequenceCollection Provider
        SequenceCollectionFactory sfactory = new BioJavaSequenceCollectionFactory();
        SequenceCollectionProvider.setSequenceCollectionFactory(sfactory);

        BioJavaPubMedFactory pfactory = new BioJavaPubMedFactory();
        PubMedProvider.setPubMedFactory(pfactory);


    }

    public BufferedImage viewOneLine(String pdbId, String chainId) {

        SequenceCollection coll = SequenceCollectionProvider.get(pdbId);

        Sequence s = coll.getChainByPDBID(chainId);
        s.ensureAnnotated();

        ViewParameters params = new ViewParameters();

        //params.setAnnotations(AnnotationRegistry.getAllAnnotations());
        params.setDesiredTopRulerRns(ResidueNumberScheme.ATOM);
        params.setDesiredBottomRulerRns(ResidueNumberScheme.SEQRES);


        //ChainView view = new ChainView(s, params);
        params.setFontSize(1);
        OneLineView view = new OneLineView(s, params);
        view.setAnnotationDrawMapper(new Annotation2SingleLineDrawer());
        SequenceImage image = view.getSequenceImage();

        BufferedImage buf = image.getBufferedImage();

        return buf;


    }


}

class ShowImage extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = -6604403623026935415L;
    BufferedImage image;

    public ShowImage(BufferedImage img) {
        image = img;
    }

    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }


}
