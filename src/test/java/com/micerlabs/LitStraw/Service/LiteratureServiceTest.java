package com.micerlabs.LitStraw.Service;

import com.micerlabs.LitStraw.ExtractApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExtractApplication.class)
public class LiteratureServiceTest {
    @Autowired
    private LiteratureService literatureService;


    @Test
    public void testSinglePaper()  {
        String pdfLib = "materialLib/pdf/09/";
        String wordLib = "materialLib/doc/09/pdf2doc/";
        String txtLib = "materialLib/txt/09/";
//        String pdfFileName = "Towards an efficient method for the extraction and analysis of cannabinoids in wastewater.pdf";
        String pdfFileName = "Tracking narcotics consumption at a Southwestern U.S. university campus by wastewater-based epidemiology.pdf";
//        literatureService.singlePaper(pdfLib, wordLib, pdfFileName, txtLib);
    }


//    @Test
//    public void testBatchPaper() {
//        RunTimeRecord runTimeRecord = literatureService.batchPaper("materialLib/pdf/饮食/01");
//        System.out.println(runTimeRecord);
//    }

}
