package com.micerlabs.LitStraw.Domain;

import com.micerlabs.LitStraw.Config.Config;
import com.google.common.collect.Sets;
import lombok.Data;

import java.util.*;

@Data
public class Context {

    public Context() {
    }

    /**
     * Lib地址
     */
    // 待配置变量
    private String pdfLib = "materialLib/pdf/09/";
    private String wordLib = "materialLib/doc/09/pdf2doc/";
    private String txtLib = "materialLib/txt/09/pdf2doc/";

    private String pdfSuffix = ".pdf";
    //    private String wordSuffix = "-converted.docx";
    private String wordSuffix = ".doc";

    // 中间变量
    private String importDocxFile = "";
    private String importPdfFile = "";
    private String importFileName = "";


    /**
     * label打标所需参数
     */
    private TextType referenceTextType;
    private TextType mainBodyTextType;
    private Set<TextType> SecondaryBodyTextTypeSet = new HashSet<>();

    // 没书签时需要收集与Reference匹配的Caption的字体类型
    private Set<TextType> captionTextTypeSet = new HashSet<>();

    /**
     * 统计指标
     */
    // 整个docx文档的 TextType和对应的String.length
    private Map<TextType, Integer> totalStatisTextTypeMap = new HashMap<>();
    // 主要字体
    private Set<TextType> mainTextTypeSet = new HashSet<>();
    private int introductionIndex;
    private int abstractIndex;


    /**
     * 关键字
     */
    // 纪录pdf的书签信息 未来可做Caption
    private Set<String> bookMarkSet = new HashSet<>();
    private Set<String> formatBookMarkSet = new HashSet<>();
    // 纪录Caption关键词
    private Set<String> captionKeyWordsSet = Sets.newHashSet("Abstract", "Introduction", "Keywords", "References", "Appendix");

    /**
     * 其他
     */
    private String title;
    private TextType titleTextType;
    // 记录下过滤掉的Patterns Index
    private List<Integer> filteredPatternIndex = new ArrayList<>();
    private List<String> barPatternList = new ArrayList<>();
    // barPattern 带"-"字符串
    private Map<String, Integer> barPatternCountMap = new HashMap<>();
    private Config config = new Config();
}
