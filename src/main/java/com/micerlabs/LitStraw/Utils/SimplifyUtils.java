package com.micerlabs.LitStraw.Utils;

import com.micerlabs.LitStraw.Domain.EasyLiterature;
import com.micerlabs.LitStraw.Domain.EasySection;
import com.micerlabs.LitStraw.Domain.Literature;
import com.micerlabs.LitStraw.Domain.Section;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SimplifyUtils {


    public static EasyLiterature simplifyLiterature(Literature literature) {
        if (literature == null) {
            return null;
        }
        EasyLiterature easyLiterature = new EasyLiterature();

        if (literature.getTitle() != null) {
            easyLiterature.setTitle(literature.getTitle());
        }

        if (literature.getSummary() != null) {
            easyLiterature.setSummary(simplifySection(literature.getSummary()));
        }

        if (literature.getIntroduction() != null) {
            easyLiterature.setIntroduction(simplifySection(literature.getIntroduction()));
        }

        if (CollectionUtils.isNotEmpty(literature.getMainBody())) {
            easyLiterature.setMainBody(simplifyListSection(literature.getMainBody()));
        }

        if (literature.getAppendix() != null) {
            easyLiterature.setAppendix(simplifySection(literature.getAppendix()));
        }

        if (CollectionUtils.isNotEmpty(literature.getFloatSectionList())) {
            easyLiterature.setFloatSectionList(simplifyListSection(literature.getFloatSectionList()));
        }
        return easyLiterature;
    }

    public static EasySection simplifySection(Section section) {
        if (section == null) {
            return null;
        }
        EasySection easySection = new EasySection();
        if (section.getOutCaption() != null) {
            easySection.setOutCaption(section.getOutCaption().getTextContent());
        }
        if (section.getCaption() != null) {
            easySection.setCaption(section.getCaption().getTextContent());
        }
        if (section.getContent() != null) {
            easySection.setContent(section.getContent().getTextContent());
        }
        return easySection;
    }

    public static List<EasySection> simplifyListSection(List<Section> sectionList) {
        List<EasySection> easySectionList = new ArrayList<>();
        if (CollectionUtils.isEmpty(sectionList)) {
            return easySectionList;
        }
        for (Section section : sectionList) {
            easySectionList.add(simplifySection(section));
        }
        return easySectionList;
    }
}
