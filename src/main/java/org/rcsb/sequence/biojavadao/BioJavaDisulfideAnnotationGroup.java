package org.rcsb.sequence.biojavadao;

import static org.rcsb.sequence.conf.AnnotationClassification.strdom;
import static org.rcsb.sequence.model.ResidueNumberScheme.SEQRES;


import org.biojava.nbio.structure.Bond;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.ResidueNumber;
import org.biojava.nbio.structure.io.SSBondImpl;
import org.rcsb.sequence.annotations.DisulphideValue;
import org.rcsb.sequence.conf.AnnotationClassification;
import org.rcsb.sequence.conf.AnnotationName;
import org.rcsb.sequence.conf.AnnotationRegistry;
import org.rcsb.sequence.core.AbstractAnnotationGroup;
import org.rcsb.sequence.core.DisulfideAnnotationGroup;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.model.SequenceCollection;
import org.rcsb.sequence.util.AnnotationConstants;

import java.util.List;

public class BioJavaDisulfideAnnotationGroup
        extends AbstractAnnotationGroup<ResidueId> implements DisulfideAnnotationGroup {

    public static final String annotationName = AnnotationConstants.disulphide;
    /**
     *
     */
    private static final long serialVersionUID = -4850280651624309824L;
    BioJavaChainProxy proxy;


    public BioJavaDisulfideAnnotationGroup(BioJavaChainProxy chain, AnnotationClassification ac, AnnotationName name) {

        super(ac, name, SEQRES, chain);
        this.proxy = chain;
    }

    public BioJavaDisulfideAnnotationGroup(Sequence sequence) {
        super(strdom, AnnotationRegistry.getAnnotationByName(annotationName), SEQRES, sequence);
    }

    @Override
    protected void constructAnnotationsImpl() throws Exception {


        // this class can only deal with SSbonds on the same chain...
        // the view can't draw lines across images...

        ResidueId rid1, rid2;
        Chain bj = proxy.getBJChain();

        List<Bond> ssbonds = bj.getStructure().getSSBonds();
        for (Bond bond : ssbonds) {

            String chainID1 = bond.getAtomA().getGroup().getChain().getChainID();
            String chainID2 = bond.getAtomB().getGroup().getChain().getChainID();

            if (chainID1.equals(bj.getChainID()) || chainID2.equals(bj.getChainID())) {

                // have to add 1 since the internal coord sys is starting at 1

                ResidueNumber res1 = bond.getAtomA().getGroup().getResidueNumber();
                ResidueNumber res2 = bond.getAtomB().getGroup().getResidueNumber();

                //int seqId1 = proxy.getSeqPosition(Integer.toString(res1.getSeqNum()), Character.toString(res1.getInsCode())) + 1;
                //int seqId2 = proxy.getSeqPosition(Integer.toString(res2.getSeqNum()), Character.toString(res2.getInsCode())) + 1;

                int seqId1 = res1.getSeqNum()+1;
                int seqId2 = res2.getSeqNum()+1;

                rid1 = getResidueId(null, chainID1, seqId1);
                rid2 = getResidueId(null, chainID2, seqId2);

                maybeAddAnnotation(rid1, rid2, -99f);
                maybeAddAnnotation(rid2, rid1, -99f);

            }
        }
    }

    /*
     * the residue might be on a different chain
     * AP: in that case we now return NULL since the maybeAddAnnotation method only considers same chain disulfid bonds...
     */
    public ResidueId getResidueId(SequenceCollection sequenceCollection, String chainId, Integer seqId) {
        Sequence c;
        if (!chain.getChainId().equals(chainId)) {
            //c = sequenceCollection.getChain(chainId);
            return null;
        } else {
            c = chain;
        }
        //	      if(c.getStatus() == ChainStatus.instantiated) c.ensureResiduesInstantiated();
        return c.getResidueId(SEQRES, seqId);
    }

    public void maybeAddAnnotation(ResidueId annotated, ResidueId connected,
                                   Float distance) {
        if (connected == null)
            return;

        if (annotated != null && annotated.getChain() == chain && !annotatesResidue(annotated)) {

            addAnnotation(new DisulphideValue(annotated, connected, distance), annotated);
        }

    }

}
