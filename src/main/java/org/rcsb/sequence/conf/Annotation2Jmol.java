package org.rcsb.sequence.conf;

import static org.rcsb.sequence.conf.AnnotationClassification.func;
import static org.rcsb.sequence.conf.AnnotationClassification.protmod;
import static org.rcsb.sequence.conf.AnnotationClassification.secstr;
import static org.rcsb.sequence.conf.AnnotationClassification.seqdom;
import static org.rcsb.sequence.conf.AnnotationClassification.strdom;
import static org.rcsb.sequence.conf.AnnotationClassification.structuralFeature;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.rcsb.sequence.model.Annotation;
import org.rcsb.sequence.model.AnnotationGroup;
import org.rcsb.sequence.model.AnnotationValue;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.view.html.AnnotationJsonObject;

public class Annotation2Jmol {

    private static final String DOMAIN_JMOL_SCRIPT_BUILDER = "DomainJmolScriptBuilder";

    private static final String RESIDUE_JMOL_SCRIPT_BUILDER = "ImportantResidueScriptBuilder";

    /**
     * this is the name of a Javascript method that will be called to highlight this annotation in Jmol
     */
    private static final String DEFAULT_JMOL_SCRIPT_BUILDER_FUNCTION = DOMAIN_JMOL_SCRIPT_BUILDER;


    static {
        Map<AnnotationClassification, Class<? extends AnnotationJsonObject>> a2jMap = new LinkedHashMap<AnnotationClassification, Class<? extends AnnotationJsonObject>>();

		/*
         * Map annotation classification to annotation JSON data creator class
		 */
        a2jMap.put(strdom, AnnotationJsonObject.class);
        a2jMap.put(seqdom, AnnotationJsonObject.class);
        a2jMap.put(func, AnnotationJsonObject.class);
        // enable this to map secstruc to jmol
        // warning: bulidng up the image map take a lot of time, so don;t enable this
        // until we have found a better solution to create the image map
        a2jMap.put(secstr, AnnotationJsonObject.class);
        a2jMap.put(protmod, AnnotationJsonObject.class);
        a2jMap.put(structuralFeature, AnnotationJsonObject.class);

        ANNOTATION_CLASSIFICATION_TO_JSON_DATA_MAP = Collections.unmodifiableMap(a2jMap);
    }


    static {
        Map<AnnotationClassification, String> a2jMap = new LinkedHashMap<AnnotationClassification, String>();

		/*
		 * Map annotation classification to the name of the function used to build the jmol script
		 */
        a2jMap.put(strdom, DEFAULT_JMOL_SCRIPT_BUILDER_FUNCTION);
        a2jMap.put(seqdom, DEFAULT_JMOL_SCRIPT_BUILDER_FUNCTION);
        a2jMap.put(func, RESIDUE_JMOL_SCRIPT_BUILDER);
        a2jMap.put(protmod, RESIDUE_JMOL_SCRIPT_BUILDER);
        a2jMap.put(structuralFeature, RESIDUE_JMOL_SCRIPT_BUILDER);
        // enable this to map secstruc to jmol
        a2jMap.put(secstr, DEFAULT_JMOL_SCRIPT_BUILDER_FUNCTION);

        ANNOTATION_CLASSIFICATION_TO_JMOL_SCRIPT_BUILDING_FUNCTION = Collections.unmodifiableMap(a2jMap);
    }
    private static final Map<AnnotationClassification, Class<? extends AnnotationJsonObject>> ANNOTATION_CLASSIFICATION_TO_JSON_DATA_MAP;
    private static final Map<AnnotationClassification, String> ANNOTATION_CLASSIFICATION_TO_JMOL_SCRIPT_BUILDING_FUNCTION;

    public static boolean hasJsonData(AnnotationName an) {
        return ANNOTATION_CLASSIFICATION_TO_JSON_DATA_MAP.containsKey(an.getClassification());
    }

    public static AnnotationJsonObject createAnnotationJsonObject(AnnotationName an, Collection<Sequence> sequences) {
        try {
            Class<? extends AnnotationJsonObject> cl = ANNOTATION_CLASSIFICATION_TO_JSON_DATA_MAP.get(an.getClassification());
            Constructor<? extends AnnotationJsonObject> c = cl.getConstructor(AnnotationName.class, Collection.class);
            return c.newInstance(an, sequences);
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate JSON data for AnnotationNam "
                    + an.getName() + " on chains " + sequences, e);
        }
    }

    public static String getJmolScriptBuilderFunction(AnnotationClassification ac) {
        String result = ANNOTATION_CLASSIFICATION_TO_JMOL_SCRIPT_BUILDING_FUNCTION.get(ac);
        return result == null ? DEFAULT_JMOL_SCRIPT_BUILDER_FUNCTION : result;
    }

    public static String getOnclick(Annotation<?> a) {
        if (a == null) return "";
        return getOnclick(a.getSequence().getChainId(), a.getName(), a.getAnnotationValue());
    }

    public static String getOnclick(AnnotationGroup<?> ag, AnnotationValue<?> av) {
        if (ag == null) return "";
        return getOnclick(ag.getSequence().getChainId(), ag.getName(), av);
    }

    public static String getOnclick(String chainId, AnnotationName an, AnnotationValue<?> av) {
        if (an == null || av == null || chainId == null) return "";
        return String.format("proxyHighlight('%s','%s','%s')", chainId, an.getName(), av.value());
    }

}
