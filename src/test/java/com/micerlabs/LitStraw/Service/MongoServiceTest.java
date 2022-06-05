package com.micerlabs.LitStraw.Service;

import com.micerlabs.LitStraw.Domain.*;
import com.micerlabs.LitStraw.Extract.PostProcess;
import com.micerlabs.LitStraw.ExtractApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.micerlabs.LitStraw.Extract.PostProcess.appendToTxt;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExtractApplication.class)
public class MongoServiceTest {


    @Autowired
    private MongoService mongoService;

    @Autowired
    private LiteratureService literatureService;

    @Test
    public void testSaveLiterature() {
        Literature literature = new Literature();
        literature.setId("00002");
        literature.setTitle("justTest1");
        TextPattern anAbstract = new TextPattern(TextPatternTypeEnum.CAPTION, "Abstract", new TextType(), TextLabelTypeEnum.CAPTION);
        literature.setSummary(new Section(anAbstract));
        mongoService.saveLiterature(literature);
    }

    @Test
    public void testFindLiteratureByTitle() {
        Literature justTest = mongoService.findLiteratureByTitle("Alarming Situation of Spreading Enteric Viruses Through Sewage__Water in Dhaka City_ Molecular Epidemiological Evidences");
        System.out.println(justTest);
    }

    @Test
    public void testFindAllLiterature() {
        List<Literature> allLiterature = mongoService.findAllLiterature();
        System.out.println(allLiterature);
    }


    @Test
    public void testGetLiteratureIdByTitle() {
        System.out.println(mongoService.getLiteratureIdByTitle("justTest"));
    }

    @Test
    public void testDeleteLiterature() {
        mongoService.deleteLiteratureByTitle("justTest");
    }

    @Test
    public void testFindLiteratureBySample() {
        Literature literature = new Literature();
        literature.setId("00001");
        Literature literatureBySample = mongoService.findLiteratureBySample(Example.of(literature));
        System.out.println(literatureBySample);
    }


    @Test
    public void process() {
        List<Literature> allLiterature = literatureService.multiPaperWithoutRecord("materialLib/3");
        for (Literature literature : allLiterature) {
            PostProcess.literature2Txt(literature,"materialLib/3/txt",  literature.getTitle() + ".txt");
        }
    }

}


