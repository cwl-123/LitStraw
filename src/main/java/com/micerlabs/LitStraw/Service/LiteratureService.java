package com.micerlabs.LitStraw.Service;

import com.micerlabs.LitStraw.Domain.*;
import com.micerlabs.LitStraw.Extract.AdobeExtract;
import com.micerlabs.LitStraw.Extract.Extract;
import com.micerlabs.LitStraw.Extract.Init;
import com.micerlabs.LitStraw.Extract.PostProcess;
import com.micerlabs.LitStraw.Utils.FileUtil;
import com.micerlabs.LitStraw.Utils.ZipUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class LiteratureService {
    @Resource
    private MongoService mongoService;

    @Resource
    private RedisService redisService;

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

        // pdf2json
        task.updateProgress(20, "Pdf2json started.");
        redisService.saveTask(task);
        try {
            AdobeExtract.pdfExtract2Zip(context.getImportPdfFile(),
                    "output/" + context.getImportFileName() + ".zip");
            ZipUtil.unzip("output/" + context.getImportFileName() + ".zip", context.getJsonLib());
            FileUtil.deleteDir("output");
        } catch (Exception e) {
            task.setErrorMsg("Pdf2json failed!");
            redisService.saveTask(task);
        }

        task.updateProgress(50, "Text labeled started.");
        redisService.saveTask(task);

        // json转化为List<Text>
//        String jsonPath = "data/2/structuredData.json";
        List<Text> textList = Extract.extractTextOfAdobe(context.getImportJsonFile());

        // 统计字体
        Init.statis(textList, context);

        // 两次打标
        List<TextLabel> textLabels = Extract.text2label(textList, context);
        List<TextPattern> textPatternList = Extract.label2pattern(textLabels, context);
        List<TextPattern> filteredPatterns = PostProcess.textPatternsFilter(textPatternList, context);

        // PatternList转Literature
        task.updateProgress(80, "Convert to txt started.");
        redisService.saveTask(task);
        Literature literature = PostProcess.patternList2Literature(context, filteredPatterns);
        PostProcess.literature2Txt(literature, context.getTxtLib(), context.getTitle()+".txt");

        // 存储Literature对象
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

        // 任务结束，统计信息
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
     * 单一论文处理-用于批处理调用和测试
     *
     * @return
     */
    public static Literature singlePaperWithoutRecord() {
        AdobeExtract.pdfExtract2Zip("src/main/resources/3.pdf", "output/3.zip");
        try {
            ZipUtil.unzip("output/3.zip", "output");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Context context = Init.initContext(storeLib, pdfFileName);
        Context context = new Context();
        // 1.根据TextType聚合，doc读成List<Text>
        String jsonPath = "output/structuredData.json";
        List<Text> textList = Extract.extractTextOfAdobe(jsonPath);
        // 统计字体
        Init.statis(textList, context);
        List<TextLabel> textLabels = Extract.text2label(textList, context);
        List<TextPattern> textPatternList = Extract.label2pattern(textLabels, context);
        List<TextPattern> filteredPatterns = PostProcess.textPatternsFilter(textPatternList, context);
        // 5. PatternList转Literature
        Literature literature = PostProcess.patternList2Literature(context, filteredPatterns);
        PostProcess.literature2Txt(literature, "materialLib", "3.txt");
        return literature;
    }

    public static void main(String[] args) {
        Literature literature = singlePaperWithoutRecord();
        System.out.println(literature);
    }

//    /**
//     * 论文批处理 格式
//     * 次级文件夹
//     * @param storeLib 主文件夹
//     * @return
//     */
//    public List<Literature> multiPaperWithoutRecord(String storeLib) {
//        List<Literature> literatureList = new ArrayList<>();
//        FileUtil.modifySuffixName(storeLib + "/word", ".docx", ".doc");
//        List<String> fileNameList = FileUtil.getFile(storeLib + "/pdf");
//        for (String fileName : fileNameList) {
//            Literature literature = singlePaperWithoutRecord(storeLib, fileName);
//            literatureList.add(literature);
//        }
//        return literatureList;
//    }

}

