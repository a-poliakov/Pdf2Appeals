package ru.apolyakov;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ImageExtractor {
    public List<Appeal> extract(List<Appeal> appeals, PDDocument document) throws IOException {
        // если прикреплять изображения не к чему
        if (appeals.isEmpty())
        {
            return appeals;
        }

        List<PDPage> list = document.getDocumentCatalog().getAllPages();
        Iterator<Appeal> appealIterator = appeals.iterator();
        Appeal currentAppeal = appealIterator.next();
        Appeal nextAppeal = appealIterator.hasNext() ? appealIterator.next() : null;

        for (int i = 0; i < list.size(); i++) {
            if (!isInRange(currentAppeal, nextAppeal, i))
            {
                currentAppeal = nextAppeal;
                nextAppeal = appealIterator.hasNext() ? appealIterator.next() : null;
            }

            PDPage page = list.get(i);
            PDResources pdResources = page.getResources();

            Map pageImages = pdResources.getImages();
            if (pageImages != null) {

                Iterator imageIter = pageImages.keySet().iterator();
                while (imageIter.hasNext()) {
                    String key = (String) imageIter.next();
                    PDXObjectImage pdxObjectImage = (PDXObjectImage) pageImages.get(key);
                    if ("tiff".equals(pdxObjectImage.getSuffix())) {
                        continue;
                    }
                    currentAppeal.getImages().add(pdxObjectImage);
                }
            }
        }
        return appeals;
    }

    public void test() throws IOException {
        BufferedImage image = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("${image.getSuffix()}");
        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);
        writer.write(null, new IIOImage(image, null, null), param);
    }

    private static boolean isInRange(Appeal currentAppeal, Appeal nextAppeal, int page)
    {
        return page + 1 >= currentAppeal.getStartPage() && (nextAppeal == null || page + 1 < nextAppeal.getStartPage());
    }

}
