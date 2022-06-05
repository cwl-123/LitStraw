package com.micerlabs.LitStraw.Utils;

import com.aspose.pdf.Document;
import com.aspose.pdf.SaveFormat;
import com.spire.doc.FileFormat;
import com.spire.pdf.PdfDocument;

import java.util.Collections;
import java.util.List;

/**
 * 利用spire和aspose提供的sdk完成pdf转doc！！
 */

// 有大量可优化的地方：因为贪图免费，做了很多傻事。非常多次磁盘IO..
public class WordUtils {
    public static void main(String[] args) {
        pdf2docForLargeFile("materialLib/1.pdf","materialLib/1.docx");
    }

    public static void pdf2docForLargeFile(String pdfPath, String wordPath) {
        splitPDF(pdfPath, "output/pdf");
        pdf2docForDir("output/pdf", "output/word");
        docMerge("output/word",wordPath);
        FileUtil.deleteDir("output");
    }


    /**
     * 拆分PDF
     *
     * @param inputPath  "materialLib/1.pdf"
     * @param outputPath 没有路径会自动创建路径 eg："output"
     */
    public static void splitPDF(String inputPath, String outputPath) {
        //加载PDF文档
        PdfDocument doc = new PdfDocument();
        doc.loadFromFile(inputPath);

        //拆分为多个PDF文档
        doc.split(outputPath + "/splitDocument-{0}.pdf", 0);
        doc.close();
    }


    /**
     * 整个文件夹里的所有pdf文件的转换为另一个文件夹里的所有doc文件
     *
     * @param pdfDirPath  默认"output/pdf"
     * @param wordDirPath 没有文件夹会自动创建文件夹 "output/word"
     */
    public static void pdf2docForDir(String pdfDirPath, String wordDirPath) {
        List<String> files = FileUtil.getFile(pdfDirPath);
        for (String file : files) {
            Document pdf = new Document(pdfDirPath + "/" + file);
            pdf.save(wordDirPath + "/" + RegexUtils.getPdfName(file) + ".docx", SaveFormat.DocX);
            pdf.close();
        }
    }

    /**
     * Doc 合并
     *
     * @param wordDirPath 默认"output/word"
     * @param aimedWordPath
     */
    public static void docMerge(String wordDirPath, String aimedWordPath) {
        List<String> files = FileUtil.getFile(wordDirPath);
        Collections.sort(files);
        com.spire.doc.Document finalDoc = new com.spire.doc.Document(wordDirPath + "/" + files.get(0));
        for (int i = 1; i < files.size(); i++) {
            finalDoc.insertTextFromFile(wordDirPath + "/" + files.get(i), FileFormat.Docx);
        }
        finalDoc.saveToFile(aimedWordPath, FileFormat.Docx);
        finalDoc.close();
    }

}
