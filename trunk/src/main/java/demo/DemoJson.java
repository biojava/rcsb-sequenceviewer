package demo;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.rcsb.sequence.biojavadao.BioJavaPubMedFactory;
import org.rcsb.sequence.biojavadao.BioJavaResidueInfoFactory;
import org.rcsb.sequence.biojavadao.BioJavaSequenceCollectionFactory;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.core.PubMedProvider;
import org.rcsb.sequence.core.ResidueProvider;
import org.rcsb.sequence.core.SequenceCollectionFactory;
import org.rcsb.sequence.core.SequenceCollectionProvider;
import org.rcsb.sequence.model.ResidueInfoFactory;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.model.SequenceCollection;
import org.rcsb.sequence.view.html.ChainView;
import org.rcsb.sequence.view.html.PageView;
import org.rcsb.sequence.view.html.ViewParameters;

public class DemoJson {

	public static void main(String[] args){

		String pdbId = "1cdg";
		String chainId = "A";

		DemoJson demo = new DemoJson();
		demo.initBioJavaView();
		System.out.println("got JSON:");
		System.out.println(demo.viewMultiLine(pdbId, chainId));
	}

	public String viewMultiLine(String pdbId, String chainId) {

		SequenceCollection coll = SequenceCollectionProvider.get(pdbId);

		Sequence s = coll.getChainByPDBID(chainId);
		s.ensureAnnotated();

		ViewParameters params = new ViewParameters();
		Collection<AnnotationName > annos = params.getAnnotations();
		
		List<AnnotationName> newAnnos = new ArrayList<AnnotationName>();
		for ( AnnotationName anno : annos){
			newAnnos.add(anno);
		}
		
		newAnnos.add(AnnotationRegistry.getAnnotationByName("SITE record"));
		params.setAnnotations(newAnnos);
		
		PageView pv = new PageView(0, params);



		//		params.setDesiredSequenceRns(ResidueNumberScheme.ATOM);


		// register the anntation mapper for th emulti line view

		//		Annotation2MultiLineDrawer a2h = new Annotation2MultiLineDrawer();


		ChainView view = new ChainView(s, params);

		pv.addChain(view.getChain());
		return pv.getAnnotationJson();
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
