package com.micerlabs.LitStraw.Service;

import com.micerlabs.LitStraw.Domain.*;
import com.micerlabs.LitStraw.Domain.StatisForPara;
import com.micerlabs.LitStraw.Extract.Extract;
import com.micerlabs.LitStraw.Extract.Init;
import com.micerlabs.LitStraw.Extract.PostProcess;
import com.micerlabs.LitStraw.Utils.FileUtil;
import com.micerlabs.LitStraw.Utils.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class LiteratureService {
    @Resource
    private MongoService mongoService;

    @Resource
    private RedisService redisService;

    // trick逻辑
    @Value("${config.wordFileSuffix}")
    private String wordFileSuffix;

    @Value("${config.storePath}")
    private String storePath;

//    /**
//     * 批处理一批论文
//     */
//    public RunTimeRecord batchPaper(String path) {
//        String pdfLib = path + "/";
//        String wordLib = pdfLib.replaceAll("pdf", "doc") + wordFileSuffix;
//        String txtLib = pdfLib.replaceAll("pdf", "txt");
//        return batchPaper(pdfLib, wordLib, txtLib);
//    }
//
//    public RunTimeRecord batchPaper(String pdfLib, String wordLib, String txtLib) {
//        RunTimeRecord runTimeRecord = new RunTimeRecord();
//        long start = System.currentTimeMillis();
//
//        // 先处理 word-AllTo-doc
//        String wordPath = wordLib.substring(0, wordLib.length() - 1);
////        System.out.println(wordPath);
//        FileUtils.modifySuffixName(wordPath, "-converted.docx", ".doc");
//        FileUtils.modifySuffixName(wordPath, ".docx", ".doc");
//
//        String pdfPath = pdfLib.substring(0, pdfLib.length() - 1);
//        List<String> filenames = FileUtils.getFile(new File(pdfPath).getAbsolutePath());
////        List<String> filenames = FileUtils.getFile(pdfPath);
//        for (String pdfFilename : filenames) {
//            singlePaper(pdfLib, wordLib, pdfFilename, txtLib);
//        }
//
//        runTimeRecord.setFileNum(filenames.size());
//        runTimeRecord.setCostTime(System.currentTimeMillis() - start);
//        return runTimeRecord;
//    }


    /**
     * 一次处理一篇论文
     */

    @Async
    public Task singlePaper(FileReq fileReq, Task task) {
        long start = System.currentTimeMillis();
        String pdfFileName = fileReq.getFile().getOriginalFilename();

        // 初始化Context
        // ToDo : fileReq.config init context
        task.updateProgress(10, "InitContext started.");
        redisService.saveTask(task);
        Context context = Init.initContext(task.getStorePath(), pdfFileName);

        // pdf2doc
        task.updateProgress(20, "Pdf2doc started.");
        redisService.saveTask(task);
        try {
            WordUtils.pdf2docForLargeFile(task.getStorePath() + "/pdf/" + pdfFileName, context.getImportDocxFile());
        } catch (Exception e) {
            task.setErrorMsg("pdf2doc failed!");
            redisService.saveTask(task);
        }

        task.updateProgress(50, "Text labeled started.");
        redisService.saveTask(task);
        // 初步分析doc
        List<StatisForPara> statisForParas = Init.preAnalyseForDoc(context);

        // 1.根据TextType聚合，doc读成List<Text>
        List<Text> textList = Extract.extractTextOfSpire(statisForParas, context);
        // 中途再统计
        Init.statis(textList, context);

        // 2. Text转LabeledText
        List<TextLabel> textLabelList = Extract.text2label(textList, context);

        // 3. LabeledText转TextPattern
        List<TextPattern> textPatternList = Extract.label2pattern(textLabelList, context);

        // 以TextPatternType聚类，看看各Pattern都包含怎样的信息
        Map<TextPatternTypeEnum, List<TextPattern>> patternTypeEnumListMap = PostProcess.separatePattern(textPatternList);

        // 4. 主要过滤掉Other类别的Pattern
        List<TextPattern> filteredPatterns = PostProcess.textPatternsFilter(textPatternList, context);

        // 5. PatternList转Literature
        Literature literature = PostProcess.patternList2Literature(context, filteredPatterns);

        // TODO:trick逻辑 目前title判断做的不够好
        literature.setTitle(context.getImportFileName());


        // 6. 处理成txt样式
        task.updateProgress(80, "Convert to txt started.");
        redisService.saveTask(task);
        PostProcess.literature2Txt(literature, context.getTxtLib(), context.getImportFileName() + ".txt");
        if (!literature.getTitle().isEmpty()) {
            try {
                literature.setId(task.getTaskId());
                mongoService.saveLiterature(literature);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            task.setErrorMsg("Paper Extract without title!");
        }
        long end = System.currentTimeMillis();
        task.setCostTime(end - start);
        task.setAvgTime((end - start) / task.getFileNum());
        task.setEndTime(new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss").format(new Date()));
        task.updateProgress(100, "All the task finished.");
        redisService.saveTask(task);
        System.out.println("task:" + task.getTaskId() + " finished！");
        return task;
    }

    /**
     * 单一论文处理-用于批处理调用
     *
     * @param storeLib
     * @param pdfFileName
     * @return
     */
    public Literature singlePaperWithoutRecord(String storeLib, String pdfFileName) {
        Context context = Init.initContext(storeLib, pdfFileName);
        // 初步分析doc
        List<StatisForPara> statisForParas = Init.preAnalyseForDoc(context);

        // 1.根据TextType聚合，doc读成List<Text>
        List<Text> textList = Extract.extractTextOfSpire(statisForParas, context);
        // 中途再统计
        Init.statis(textList, context);

        // 2. Text转LabeledText
        List<TextLabel> textLabelList = Extract.text2label(textList, context);

        // 3. LabeledText转TextPattern
        List<TextPattern> textPatternList = Extract.label2pattern(textLabelList, context);

        // 以TextPatternType聚类，看看各Pattern都包含怎样的信息
        Map<TextPatternTypeEnum, List<TextPattern>> patternTypeEnumListMap = PostProcess.separatePattern(textPatternList);

        // 4. 主要过滤掉Other类别的Pattern
        List<TextPattern> filteredPatterns = PostProcess.textPatternsFilter(textPatternList, context);

        // 5. PatternList转Literature
        Literature literature = PostProcess.patternList2Literature(context, filteredPatterns);

        // TODO:trick逻辑 目前title判断做的不够好
        literature.setTitle(context.getImportFileName());

        return literature;
    }

    /**
     * 论文批处理 格式
     * 次级文件夹
     * @param storeLib 主文件夹
     * @return
     */
    public List<Literature> multiPaperWithoutRecord(String storeLib) {
        List<Literature> literatureList = new ArrayList<>();
        FileUtil.modifySuffixName(storeLib + "/word", ".docx", ".doc");
        List<String> fileNameList = FileUtil.getFile(storeLib + "/pdf");
        for (String fileName : fileNameList) {
            Literature literature = singlePaperWithoutRecord(storeLib, fileName);
            literatureList.add(literature);
        }
        return literatureList;
    }

}

