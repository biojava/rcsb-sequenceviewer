package org.rcsb.sequence.core;

import org.rcsb.sequence.model.SequenceCollection;

public class SequenceCollectionProvider {

    static SequenceCollectionFactory factory;


    private SequenceCollectionProvider() {
    }

    public static void setSequenceCollectionFactory(SequenceCollectionFactory fact) {
        factory = fact;
    }

    public static synchronized SequenceCollection get(String structureId) {
        return factory.get(structureId);
    }


}
