package com.wangYu.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class HelloLucene {
  /**
   * ��������
   */
	public void index(){	
		IndexWriter writer = null;
		try {
		    //1~ ����Directory
			//Directory directory = new RAMDirectory();//�������ڴ��е�
			Directory directory = FSDirectory.open(new File("D:/lucene/index01")); //������Ӳ����
			//2~ ����IndexWriter
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));		
			writer = new IndexWriter(directory,iwc);
			//3~ ����Document����
			Document doc = null;
			//4~ ΪDocument���Field
			File f = new File("D:/lucene/example");
			for(File file:f.listFiles()){
				doc = new Document();
				doc.add(new Field("content", new FileReader(file)));
				doc.add(new Field("filename",file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new Field("path",file.getAbsolutePath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
             //5~ IndexWriter����ĵ���������	
                writer.addDocument(doc);
			}		
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(null != writer)
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	/**
	 * ����
	 * @throws  
	 */
	public void searcher(){
		try {
			//1~ ����Directory
			Directory directory = FSDirectory.open(new File("D:/lucene/index01")); //������Ӳ����
			//2~ ����IndexReader
			IndexReader reader = IndexReader.open(directory);
			//3~ ����IndexReader����IndexSearcher
			IndexSearcher searcher = new IndexSearcher(reader);
			//4~ ����������Query
			//����parser��ȷ��Ҫ���������ݣ��ڶ���������ʾ��������
			QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
			//����query, ��ʾ������Ϊcontent�а���java���ĵ�
			Query query = parser.parse("java");
			//5~ ����searcher�������ҷ���TopDocs
			TopDocs tds = searcher.search(query, 10);
			//6~ ����TopDocs��ȡScoreDoc����
			ScoreDoc[] sds = tds.scoreDocs;
			for(ScoreDoc sd:sds){
				//7~ ����searcher��ScoreDoc�����ȡ�����Document����
				Document d = searcher.doc(sd.doc);
				//8~ ����Document�����ȡ��Ҫ��ֵ
				System.out.println(d.get("filename")+"["+d.get("path")+"]");
			}			
			//9~ �ر�reader
			reader.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
