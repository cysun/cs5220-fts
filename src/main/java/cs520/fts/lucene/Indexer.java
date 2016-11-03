package cs520.fts.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

    String fileDir = "files";

    String indexDir = "indexes";

    IndexWriter indexWriter;

    public Indexer() throws Exception
    {
        // The directory where the index will be located.
        Directory dir = FSDirectory.open( new File( indexDir ) );

        // Configuration for an index writer:
        // . use the latest standard analyzer
        // . create new index (i.e. remove any existing index)
        Analyzer analyzer = new StandardAnalyzer( Version.LUCENE_34 );
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
            Version.LUCENE_31,
            analyzer );
        indexWriterConfig.setOpenMode( OpenMode.CREATE );

        // Create an index writer.
        indexWriter = new IndexWriter( dir, indexWriterConfig );
    }

    public void buildIndex() throws Exception
    {
        int filesIndexed = 0;

        File dir = new File( fileDir );
        for( File file : dir.listFiles() )
        {
            // We only index *.txt files.
            if( !file.getName().endsWith( ".txt" ) ) continue;

            // Create a Document and add it to the index. We assume the
            // first line of the file is the title, and the rest of it
            // is content.
            BufferedReader br = new BufferedReader( new FileReader( file ) );

            Document document = new Document();
            String title = br.readLine();
            document.add( new Field(
                "title",
                title,
                Field.Store.YES,
                Field.Index.ANALYZED ) );
            document.add( new Field( "content", br ) );
            indexWriter.addDocument( document );

            br.close();
            ++filesIndexed;
        }

        System.out.println( filesIndexed + " files indexed." );
    }

    public void close() throws Exception
    {
        indexWriter.close();
    }

    public static void main( String[] args ) throws Exception
    {
        Indexer indexer = new Indexer();
        indexer.buildIndex();
        indexer.close();
    }

}
