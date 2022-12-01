package com.heartape;

import lombok.SneakyThrows;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class LuceneCrud {

    @SneakyThrows
    public void insert(User user) {
        Path dataPath = path();

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexWriterConfig.setUseCompoundFile(false);
        indexWriterConfig.setCheckPendingFlushUpdate(true);
        try (FSDirectory directory = FSDirectory.open(dataPath)) {
            IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
            indexWriter.addDocument(create(user));
            indexWriter.close();
        }
    }

    private Path path(){
        String path = System.getProperty("user.dir");
        File file = new File(path + "\\directory\\user");
        return file.toPath();
    }

    private Document create(User user){
        Document document = new Document();
        // 主键可以考虑使用docID保证唯一性
        LongPoint id = new LongPoint("id", user.getId());
        // 可以发现LongPoint构造函数没有Store stored参数，所以需要添加StoredField，
        StoredField idStored = new StoredField("id", user.getId());
        // 当需要id进行搜索时创建StringField，因为数字无法搜索，但不会影响NumericDocValuesField排序
        StringField idString = new StringField("id", user.getId().toString(), Field.Store.NO);
        // 数字类型正排索引，用于排序
        NumericDocValuesField idSort = new NumericDocValuesField("id", user.getId());
        // 与NumericDocValuesField相似，可以存储多个值
        // SortedNumericDocValuesField idSort1 = new SortedNumericDocValuesField("id", user.getId());
        // SortedNumericDocValuesField idSort2 = new SortedNumericDocValuesField("id", user.getId());

        StringField name = new StringField("name", user.getName(), Field.Store.YES);
        TextField introduce = new TextField("introduce", user.getIntroduce(), Field.Store.YES);
        //不需要创建索引的就使用StoreField存储
        StoredField experience = new StoredField("experience", user.getExperience());

        document.add(id);
        document.add(idStored);
        document.add(idSort);
        document.add(idString);
        document.add(name);
        document.add(introduce);
        document.add(experience);
        return document;
    }

    @SneakyThrows
    public void insertList(List<User> userList) {
        Path dataPath = path();

        List<Document> documents = userList.stream().map(this::create).toList();

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexWriterConfig.setUseCompoundFile(false);
        indexWriterConfig.setCheckPendingFlushUpdate(true);
        try (FSDirectory directory = FSDirectory.open(dataPath)) {
            IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
            indexWriter.addDocuments(documents);
            indexWriter.close();
        }
    }

    @SneakyThrows
    public void select(Long id) {
        Path dataPath = path();

        Query exactQuery = LongPoint.newExactQuery("id", id);

        try (FSDirectory directory = FSDirectory.open(dataPath)) {
            try (DirectoryReader directoryReader = DirectoryReader.open(directory)) {
                IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
                TopDocs topDocs = indexSearcher.search(exactQuery, directoryReader.maxDoc());
                // 查询获得数据量
                long value = topDocs.totalHits.value;
                System.out.println("查询document总数量: " + value);
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    // Document对象id
                    int doc = scoreDoc.doc;
                    // 查询文档
                    Document document = indexSearcher.doc(doc);
                    System.out.println(document);
                }
            }
        }
    }

    @SneakyThrows
    public void selectByName(String name) {
        Term term = new Term("name", name);
        TermQuery termQuery = new TermQuery(term);

        Path dataPath = path();
        try (FSDirectory directory = FSDirectory.open(dataPath)) {
            try (DirectoryReader directoryReader = DirectoryReader.open(directory)) {
                IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
                TopDocs topDocs = indexSearcher.search(termQuery, directoryReader.maxDoc());
                // 查询获得数据量
                long value = topDocs.totalHits.value;
                System.out.println("查询document总数量: " + value);
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    // Document对象id
                    int doc = scoreDoc.doc;
                    System.out.println(doc);
                    // 查询文档
                    Document document = indexSearcher.doc(doc);
                    System.out.println(document);
                }
            }
        }
    }

    @SneakyThrows
    public void selectByNameSortById(String name, int number) {
        Term term = new Term("name", name);
        TermQuery termQuery = new TermQuery(term);

        Path dataPath = path();
        try (FSDirectory directory = FSDirectory.open(dataPath)) {
            try (DirectoryReader directoryReader = DirectoryReader.open(directory)) {
                IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
                SortField idSort = new SortField("id", SortField.Type.LONG, false);
                TopDocs topDocs = indexSearcher.search(termQuery, number, new Sort(idSort));
                // 查询获得数据量
                long value = topDocs.totalHits.value;
                System.out.println("查询document总数量: " + value);
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    // Document对象id
                    int doc = scoreDoc.doc;
                    // 查询文档
                    Document document = indexSearcher.doc(doc);
                    System.out.println(document);
                }
            }
        }
    }

    /**
     * 如果需要根据id更新的话，由于Term仅支持String，创建索引时就应该以StoredField类型。
     */
    @SneakyThrows
    public void updateById(User user) {
        Path dataPath = path();
        Term term = new Term("id", user.getId().toString());
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexWriterConfig.setUseCompoundFile(false);
        indexWriterConfig.setCheckPendingFlushUpdate(true);
        try (FSDirectory directory = FSDirectory.open(dataPath)) {
            IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
            indexWriter.updateDocument(term, create(user));
            indexWriter.close();
        }
    }

    @SneakyThrows
    public void updateByName(User user, String name) {
        Path dataPath = path();
        Term term = new Term("name", name);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexWriterConfig.setUseCompoundFile(false);
        indexWriterConfig.setCheckPendingFlushUpdate(true);
        try (FSDirectory directory = FSDirectory.open(dataPath)) {
            IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
            indexWriter.updateDocument(term, create(user));
            indexWriter.close();
        }
    }

    /**
     * select api演示
     */
    public void selectExample() {
        Term term = new Term("name", "jackson");

        // 不使用分析器
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
        Term introduceTerm = new Term("introduce", "elasticsearch");
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