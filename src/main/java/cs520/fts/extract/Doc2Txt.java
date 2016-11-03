package cs520.fts.extract;

import java.io.File;
import java.io.IOException;

import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

public class Doc2Txt {

    public static void main( String args[] ) throws InvalidFormatException,
        IOException, OpenXML4JException, XmlException
    {
        // Locate the file
        File file = new File( "files", "sample2.docx" );

        // Create a TextExtractor
        POITextExtractor extractor = ExtractorFactory.createExtractor( file );

        // Extract text and store it in a String
        String text = extractor.getText();

        // Print out the text
        System.out.print( text );
    }

}
