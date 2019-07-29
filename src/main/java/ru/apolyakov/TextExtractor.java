package ru.apolyakov;

import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDGraphicsState;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextExtractor extends PDFTextStripper {

    private ArrayList<Page> previousPages = new ArrayList<Page>();

    private Page currentPage = null;

    private int pageCount = 0;

    public TextExtractor() throws IOException {
        super();
    }

    @Override
    public void processStream(PDPage aPage, PDResources resources,
                              COSStream cosStream) throws IOException {
        currentPage = new Page(aPage.findCropBox(), ++pageCount);

        super.processStream(aPage, resources, cosStream);
        coalesceRows(currentPage);
        removeDuplicates(currentPage);

        previousPages.add(currentPage);
        currentPage = null;
    }

    protected void processTextPosition(TextPosition tp) {
        PDGraphicsState gs = getGraphicsState();
        currentPage.addText(TextRow.newFor(tp, gs));
    }

    private void coalesceRows(Page page) {
        for (Float f : page.getYPosWithText()) {
            List<TextRow> ts = page.getTextAtY(f);

            Collections.sort(ts);

            int i=0;
            while (i+1 < ts.size()) {
                TextRow first = ts.get(i);
                TextRow snd = ts.get(i+1);

                if (first.hasMatchingStyle(snd)) {
                    first.addAfter(snd);
                    page.removeText(snd);
                } else {
                    i++;
                }
            }
        }
    }

    // different x, y.
    private void removeDuplicates(Page page) {
        for (Float f : page.getYPosWithText()) {
            List<TextRow> ts = page.getTextAtY(f);

            Collections.sort(ts);

            int i=0;
            while (i+1 < ts.size()) {
                TextRow first = ts.get(i);
                TextRow snd = ts.get(i+1);

                if (first.getRun().equals(snd.getRun())
                        && first.getX() == snd.getX()) {
                    page.removeText(snd);
                } else {
                    i++;
                }
            }
        }
    }

    public List<Page> getPages() {
        return previousPages;
    }

    public String toString() {
        String s = "";
        for (Page page : previousPages) {
            s += "Page @ " + page.getClipBox().getUpperRightY()
                    + ", " + page.getClipBox().getLowerLeftX();
            for (TextRow t : page.getText()) {
                s += t.getRun() + " @ " + t.getX() + "," + t.getBaseline()
                        + " w " + t.getWidth()
                        + " : " + t.getBaseFontName()
                        + " "   + t.getPointSize() + "pt"
                        + " C " + t.getForegroundColor()
                        + "\n";
            }
        }
        return s;
    }

    public List<Appeal> toAppeals()
    {
        AppealParser parser = new AppealParser();
        return parser.parse(previousPages);
    }
}
