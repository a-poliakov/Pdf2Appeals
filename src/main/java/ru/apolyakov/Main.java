package ru.apolyakov;

import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.WrappedIOException;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.util.PDFText2HTML;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private String password = "";

    public static void main(String[] args) {
        File inputFile = new File("example.pdf");
        TextExtractor te = null;

        try {
            te = new Main().parsePdf(inputFile);
            List<Appeal> appeals = te.toAppeals();
            ImageExtractor ie = new ImageExtractor();
            ie.extract(appeals, PDDocument.load(inputFile));
            System.out.println(appeals);
        } catch (IOException e) {
            System.err.println("Couldn't read file '" + inputFile +"'.");
            System.exit(1);
        }
    }

    private TextExtractor parsePdf(File f) throws IOException {
        PDDocument doc = PDDocument.load(f);

        if(doc.isEncrypted()) {
            // Some documents are encrypted with the empty password. Try
            // to decrypt with this password, or the one passed in on the
            // command line (if any), and fail if we can't.
            try {
                doc.decrypt(password); // Defaults to the empty string.
            } catch (CryptographyException e) {
                throw new WrappedIOException("Can't decrypt document: ", e);
            }
        }

        PDDocumentCatalog docCat = doc.getDocumentCatalog();

        PDPageNode root = docCat.getPages();
        List pages = new ArrayList();
        root.getAllKids(pages);

        TextExtractor te = new TextExtractor();

        for (Object p : pages) {
            PDPage page = (PDPage) p;

            te.setShouldSeparateByBeads(false);
            te.processStream(page,
                    page.getResources(),
                    page.getContents().getStream());
        }

        doc.close();

        return te;
    }
}
