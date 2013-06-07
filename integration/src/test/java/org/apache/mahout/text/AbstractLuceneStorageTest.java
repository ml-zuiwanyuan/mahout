package org.apache.mahout.text;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.mahout.common.Pair;
import org.apache.mahout.text.doc.MultipleFieldsDocument;
import org.apache.mahout.text.doc.NumericFieldDocument;
import org.apache.mahout.text.doc.SingleFieldDocument;
import org.apache.mahout.utils.MahoutTestCase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract test for working with Lucene storage.
 */
public abstract class AbstractLuceneStorageTest extends MahoutTestCase {

  protected Path indexPath1;
  protected Path indexPath2;
  protected List<SingleFieldDocument> docs = new ArrayList<SingleFieldDocument>();
  protected List<SingleFieldDocument> misshapenDocs = new ArrayList<SingleFieldDocument>();

  @Override
  public void setUp() throws Exception {
    super.setUp();
    indexPath1 = getTestTempDirPath("index1");
    indexPath2 = getTestTempDirPath("index2");
    for (int i = 0; i < 2000; i++) {
      docs.add(new SingleFieldDocument(String.valueOf(i), "This is test document " + i));
    }
    misshapenDocs.add(new SingleFieldDocument("", "This doc has an empty id"));
    misshapenDocs.add(new SingleFieldDocument("empty_value", ""));
  }

  protected void commitDocuments(Directory directory, Iterable<SingleFieldDocument> theDocs) throws IOException{
    IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(Version.LUCENE_43)));

    for (SingleFieldDocument singleFieldDocument : theDocs) {
      indexWriter.addDocument(singleFieldDocument.asLuceneDocument());
    }

    indexWriter.commit();
    indexWriter.close();
  }

  protected void commitDocuments(Directory directory, SingleFieldDocument... documents) throws IOException {
    commitDocuments(directory, Arrays.asList(documents));
  }

  protected void assertSimpleDocumentEquals(SingleFieldDocument expected, Pair<Text, Text> actual) {
    assertEquals(expected.getId(), actual.getFirst().toString());
    assertEquals(expected.getField(), actual.getSecond().toString());
  }

  protected void assertMultipleFieldsDocumentEquals(MultipleFieldsDocument expected, Pair<Text, Text> actual) {
    assertEquals(expected.getId(), actual.getFirst().toString());
    assertEquals(expected.getField() + " " + expected.getField1() + " " + expected.getField2(), actual.getSecond().toString());
  }

  protected void assertNumericFieldEquals(NumericFieldDocument expected, Pair<Text, Text> actual) {
    assertEquals(expected.getId(), actual.getFirst().toString());
    assertEquals(expected.getField() + " " + expected.getNumericField(), actual.getSecond().toString());
  }

  protected FSDirectory getDirectory(File indexPath) throws IOException {
    return FSDirectory.open(indexPath);
  }

  protected File getIndexPath1AsFile() {
    return new File(indexPath1.toUri().getPath());
  }

  protected Path getIndexPath1() {
    return indexPath1;
  }

  protected File getIndexPath2AsFile() {
    return new File(indexPath2.toUri().getPath());
  }

  protected Path getIndexPath2() {
    return indexPath2;
  }
}