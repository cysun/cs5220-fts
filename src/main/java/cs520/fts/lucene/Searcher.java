package cs520.fts.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {

    String indexDir = "indexes";

    IndexSearcher indexSearcher;

    QueryParser queryParser;

    public Searcher() throws Exception
    {
        // The directory where the index is located.
        Directory dir = FSDirectory.open( new File( indexDir ) );

        // Create an index searcher.
        indexSearcher = new IndexSearcher( dir );

        // Create a query parser:
        // . use the latest standard analyzer
        // . the default search field is "content"
        Analyzer analyzer = new StandardAnalyzer( Version.LUCENE_34 );
        queryParser = new QueryParser( Version.LUCENE_34, "content", analyzer );
    }

    public void search( String s ) throws IOException, ParseException
    {
        // Parse the input string into a Lucene Query.
        Query query = queryParser.parse( s );

        // Search the index and return the top 100 results.
        TopDocs results = indexSearcher.search( query, 100 );

        System.out.println( results.totalHits + " file(s) found." );

        for( ScoreDoc scoreDoc : results.scoreDocs )
        {
            Document doc = indexSearcher.doc( scoreDoc.doc );
            System.out.println( scoreDoc.score + ", " + doc.get( "title" ) );
        }
    }

    public static void main( String[] args ) throws Exception
    {
        Searcher searcher = new Searcher();

        Scanner scanner = new Scanner( System.in );
        while( true )
        {
            System.out.print( "Please enter a query string: " );
            String s = scanner.nextLine();
            if( s.equals( "q" ) ) break;
            searcher.search( s );
        }
        scanner.close();

        System.out.println( "Bye." );
    }

}
