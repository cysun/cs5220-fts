package cs520.fts.extract;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class Pdf2Txt {

    public static void main( String args[] ) throws IOException
    {
        // Locate the file
        File file = new File( "files", "sample1.pdf" );

        // Load the file into a PDDocument object
        PDDocument document = PDDocument.load( file );

        // Create a PDFTextStripper
        PDFTextStripper stripper = new PDFTextStripper();

        // Extract text and store it in a String
        StringWriter sw = new StringWriter();
        stripper.writeText( document, sw );
        String text = sw.toString();

        // Print out the text
        System.out.print( text );
    }

}
