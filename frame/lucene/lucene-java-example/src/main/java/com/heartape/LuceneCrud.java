package com.heartape;

import lombok.SneakyThrows;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.nio.file.Path;

public class LuceneCrud {

    @SneakyThrows
    public void insert() {
        String path = System.getProperty("user.dir");
        File file = new File(path + "\\data");
        Path dataPath = file.toPath();

        String[] contentArray = {"是这样的", "elasticsearch"};
        for (String content : contentArray) {
            Document document = new Document();
            NumericDocValuesField id = new NumericDocValuesField("id", 1);
            StringField name = new StringField("name", "jackson", Field.Store.YES);
            TextField introduce = new TextField("introduce", content, Field.Store.NO);
            document.add(id);
            document.add(name);
            document.add(introduce);

            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
            indexWriterConfig.setUseCompoundFile(false);
            indexWriterConfig.setCheckPendingFlushUpdate(true);
            try (FSDirectory directory = FSDirectory.open(dataPath)) {
                IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
                indexWriter.addDocument(document);
                indexWriter.close();
            }
        }
    }

    @SneakyThrows
    public void select() {
        Term term = new Term("name", "jackson");
        TermQuery termQuery = new TermQuery(term);

        String path = System.getProperty("user.dir");
        File file = new File(path + "\\data");
        Path dataPath = file.toPath();
        try (FSDirectory directory = FSDirectory.open(dataPath)) {
            try (DirectoryReader directoryReader = DirectoryReader.open(directory)) {
                IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
                TopDocs topDocs = indexSearcher.search(termQuery, 10);
                // 查询获得数据量
                long value = topDocs.totalHits.value;
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    // Document对象id
                    int doc = scoreDoc.doc;
                    // 查询文档
                    Document document = indexSearcher.doc(doc);
                }
            }
        }
    }

    public void selectExample() {
        Term term = new Term("name", "jackson");
        Term introduceTerm = new Term("introduce", "elasticsearch");
        TermQuery termQuery = new TermQuery(term);

        // 所有
        MatchAllDocsQuery matchAllDocsQuery = new MatchAllDocsQuery();

        TermRangeQuery termRangeQuery = new TermRangeQuery("name", new BytesRef("ja"), new BytesRef("son"), false, false);

        // 数字查询
        Query slowRangeQuery = NumericDocValuesField.newSlowRangeQuery("id", 1, 5);
        Query slowExactQuery = NumericDocValuesField.newSlowExactQuery("id", 1);

        Query rangeQuery = LongPoint.newRangeQuery("id", 1, 5);
        Query exactQuery = LongPoint.newExactQuery("id", 1);

        // FloatPoint.newExactQuery("id", 1);
        // DoublePoint.newExactQuery("id", 1);
        // IntPoint.newExactQuery("id", 1);

        // 前缀查询
        PrefixQuery prefixQuery = new PrefixQuery(term);

        // boolean查询， 可以添加多个条件
        BooleanClause booleanClause1 = new BooleanClause(rangeQuery, BooleanClause.Occur.MUST);
        BooleanClause booleanClause2 = new BooleanClause(exactQuery, BooleanClause.Occur.MUST_NOT);
        BooleanQuery booleanQuery = new BooleanQuery.Builder().add(booleanClause1).add(booleanClause2).build();

        // slop是各个词之间的位移偏差
        PhraseQuery phraseQuery = new PhraseQuery.Builder()
                .add(term, 0)
                .add(introduceTerm, 2)
                .setSlop(1)
                .build();

        // 使用通配符查询，*代表0个或多个字母，?代表0个或1个字母。
        Term wildcardTerm = new Term("name", "jack*");
        WildcardQuery wildcardQuery = new WildcardQuery(wildcardTerm);

        // 模糊查询
        FuzzyQuery fuzzyQuery = new FuzzyQuery(term);
    }

}