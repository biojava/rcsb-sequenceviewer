package org.rcsb.sequence.view.multiline;

import static org.rcsb.sequence.model.ResidueNumberScheme.ATOM;
import static org.rcsb.sequence.model.ResidueNumberScheme.DBREF;
import static org.rcsb.sequence.model.ResidueNumberScheme.SEQRES;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import org.biojava.nbio.protmod.structure.ModifiedCompound;
import org.rcsb.sequence.model.Annotation;
import org.rcsb.sequence.model.ResidueId;
import org.rcsb.sequence.model.ResidueNumberScheme;
import org.rcsb.sequence.model.Sequence;
import org.rcsb.sequence.util.ResourceManager;

public class SequenceDrawer extends AbstractDrawer<Object> {

    private final ResidueNumberScheme rns;


    protected ResourceManager resourceManager;
    Map<Character, Integer> charSize;
    // now colors are coming from config file
    private Color residueNoStructure;
    private Color residueWithStructure;
    private Color residueUniProtMismatch;
    private Color residueNonstandard;

    public SequenceDrawer(SequenceImageIF image, Sequence sequence, ResidueNumberScheme rns) {
        super(image, sequence);

        resourceManager = new ResourceManager("sequenceview");

        String col1 = resourceManager.getString("sequencedrawer.residueNoStructure");
        residueNonstandard = Color.decode(col1);

        String col2 = resourceManager.getString("sequencedrawer.residueWithStructure");
        residueWithStructure = Color.decode(col2);

        String col3 = resourceManager.getString("sequencedrawer.residueUniProtMismatch");
        residueUniProtMismatch = Color.decode(col3);

        String col4 = resourceManager.getString("sequencedrawer.residueNonstandard");
        residueNonstandard = Color.decode(col4);

        this.rns = rns;

        setImageHeight(image.getFontHeight());

        charSize = new HashMap<Character, Integer>();
    }


    public SequenceDrawer(SequenceImageIF image, Sequence sequence) {
        this(image, sequence, sequence.getDefaultResidueNumberScheme());


    }


    public ImageMapData getHtmlMapData() {
        if (mapData == null) {
            mapData = new ImageMapData("sequence" + hashCode(), getImageHeight()) {
                private static final long serialVersionUID = 1L;

                @Override
                public void populateImageMapData() {
                    final Sequence sequence = getSequence();
                    final SequenceImageIF image = getImage();
                    final int maxPos = image.getImageWidth();
                    final int resWid = image.getFontWidth();
                    final int imageWidthOffset = image.getImageWidthOffset();

                    ResidueNumberScheme rnsOfSeq = sequence.getFirstResidue().getResidueNumberScheme();
                    ResidueNumberScheme rnsMismatch = rnsOfSeq == DBREF ? SEQRES : DBREF;

                    // do map entry for key
                    StringBuilder description = new StringBuilder(255);
                    switch (rnsOfSeq) {
                        case SEQRES:
                            description.append("Sequence as provided by the SEQRES entries in the PDB file");
                            break;
                        case DBREF:
                            description.append("Sequence from external sequence database ")
                                    .append(sequence.getExternalDbName());
                            break;
                        case ATOM:
                        case _ARRAY_IDX:
                        default:
                            description.append("This sequence is intended for internal use only and should not be publically visible");
                    }

                    addImageMapDataEntry(new Entry(0, imageWidthOffset - 1, description.toString(), null));


                    // do map entries for each resiude
                    int startPx = imageWidthOffset, endPx = startPx + resWid;
                    Annotation<ModifiedCompound> crosslink = null;
                    for (ResidueId rid : sequence.getResidueIds()) {
                        if (endPx > maxPos) {

                            System.err.println("Sequence too long for image (shouldn't be possible as image was designed for this sequence): "
                                    + endPx + " > " + maxPos);

                            endPx = maxPos;
                            //  throw new RuntimeException("Sequence too long for image (shouldn't be possible as image was designed for this sequence): "
                            //       + endPx + " > " + maxPos);
                        }
                        description = new StringBuilder(255);

                        if (rid.getResidueInfo().isNonstandard()) {
                            description.append("Modified Residue: ");
                        }

                        formatResidue(description, rid);

                        if (rid.getResidueInfo().isNonstandard()) {
                            //System.out.println(rid + " " + rid.getResidueInfo());
                            description.append(' ').append(rid.getResidueInfo().getName()).append(' ').append(rid.getResidueInfo().getFormula());
                            String parent = rid.getResidueInfo().getParentMonId();
                            if ((parent != null) && (!parent.equals(""))) {
                                description.append(" (parent: ").append(parent).append(")");
                            }
                        }


                        if (rid.hasDbrefMismatch()) {
                            String banana = rnsMismatch == DBREF ? sequence.getExternalDbName() : "PDB";
                            description.append(" (mismatch to ")
                                    .append(rid.getEquivalentResidueId(rnsMismatch).getResidueInfo().getMonId())
                                    .append(" in ")
                                    .append(banana)
                                    .append(')');
                        }
                        if (!rid.hasStructuralData()) {
                            description.append(" (no structural data available)");
                        }

                        addImageMapDataEntry(new Entry(startPx, endPx, description.toString(), crosslink == null ? null : crosslink)); // for now
                        //addImageMapDataEntry(new Entry(startPx, endPx, "xxxxx", disulphide == null ? null : disulphide));
                        startPx += resWid;
                        endPx += resWid;
                    }
                }

                //===================================================================================================================================================================================

                private void formatResidue(StringBuilder sb, ResidueId rid) {
                    ResidueId altRid;
                    ResidueNumberScheme resRns = rid.getResidueNumberScheme();
                    if (resRns == SEQRES) {
                        if ((altRid = rid.getEquivalentResidueId(ATOM)) != null) {
                            formatResidue(sb, altRid);
                        } else {
                            sb.append(rid.getResidueInfo().getMonId());
                            sb.append(" -- no identifier from ATOM record");
                        }
                    } else if (resRns == ATOM || resRns == DBREF) {

                        sb.append(rid.getResidueInfo().getMonId())
                                .append(' ')
                                .append(rid.getSeqId());

                        if (rid.hasInsertionCode()) {
                            sb.append(rid.getInsertionCode());
                        }
                    }
                }
            };
        }
        return mapData;
    }


    @Override
    protected void drawData(Graphics2D g2, int yOffset) {
        final SequenceImageIF image = getImage();
        final int fontWidth = image.getFontWidth();
//      g2.drawString(sequence.getSequenceString(), 0, fontAscent);
        int xPos = image.getImageWidthOffset();

        FontMetrics fontMetrics = image.getFontMetrics();

        charSize.clear();


        for (ResidueId r : getSequence().getResidueIds()) {
            g2.setColor(r.hasStructuralData() ? residueWithStructure : residueNoStructure);
            Color c = g2.getColor();
            if (r.hasDbrefMismatch()) {
                g2.setColor(residueUniProtMismatch);
            }

            if (r.isNonStandard()) {
                g2.setColor(residueNonstandard);
            }


            // make sure all characters are centered...
            int centerPos = getCenterPos(r.getResidueInfo().getOneLetterCode(), fontMetrics, fontWidth);

            g2.drawString(String.valueOf(r.getResidueInfo().getOneLetterCode()), xPos + centerPos, yOffset + image.getFontAscent());
            xPos += fontWidth;
            g2.setColor(c);
        }
    }

    /**
     * make sure that all characters get centered correctly (particularly I !)
     *
     * @param fontWidth - the max space available to draw a character
     * @return x value to shift the character into the center of area where it should be drawn.
     */
    private int getCenterPos(Character ch, FontMetrics fontMetrics, int fontWidth) {

        Integer x = charSize.get(ch);
        if (x != null) {
            return x;
        }

        x = 0;

        int size = fontMetrics.charWidth(ch);
        int diff = fontWidth - size;
        x = (diff / 2);

        charSize.put(ch, x);

        return x;
    }


    @Override
    protected String getKey() {
        switch (rns) {
            case DBREF:
                return getSequence().getExternalDbName();
            case SEQRES:
                return "PDB";
            default:
                return "INTERNAL SEQ";
        }
    }

    protected ResidueNumberScheme getResidueNumberScheme() {
        return rns;
    }


}
