package ru.apolyakov;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Представление одной страницы PDF документа
 */
public class Page {
    private static final ArrayList<TextRow> NOTHING = new ArrayList<TextRow>();

    private ArrayList<TextRow> textRows;
    private HashMap<Float, ArrayList<TextRow>> yPosMap;
    private PDRectangle clipBox;
    private int number;

    public Page(PDRectangle newClipBox, int newNumber) {
        textRows = new ArrayList<TextRow>();
        yPosMap = new HashMap<Float, ArrayList<TextRow>>();
        clipBox = newClipBox;
        number = newNumber;
    }

    public void addText(TextRow t) {
        textRows.add(t);

        Float yPos = new Float(t.getBaseline());
        if (yPosMap.containsKey(yPos)) {
            ArrayList<TextRow> l = yPosMap.get(yPos);
            l.add(t);
        } else {
            ArrayList<TextRow> l = new ArrayList<TextRow>();
            l.add(t);
            yPosMap.put(yPos, l);
        }
    }

    public void removeText(TextRow t) {
        if (textRows.contains(t)) {
            textRows.remove(t);
            yPosMap.get(t.getBaseline()).remove(t);
        }
    }

    public PDRectangle getClipBox() {
        return clipBox;
    }

    public List<TextRow> getText() {
        return textRows;
    }

    public int getNumber() {
        return number;
    }

    /**
     * Возвращает все строки, начинающиеся на заданном положении по y
     * @param y
     * @return
     */
    public List<TextRow> getTextAtY(float y) {
        Float fObj = new Float(y);
        if (yPosMap.containsKey(fObj)) {
            return yPosMap.get(fObj);
        }
        return NOTHING;
    }

    /**
     * @return возвращает все y-позиции строк с текстом.
     */
    public Set<Float> getYPosWithText() {
        return yPosMap.keySet();
    }
}
