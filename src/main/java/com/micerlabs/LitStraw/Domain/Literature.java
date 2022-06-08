package com.micerlabs.LitStraw.Domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * com.micerlabs.LitStraw.Domain 通用论文格式 Literature
 *
 */
@Data
@Document(collection="literature")
public class Literature implements Serializable {
    /**
     * 论文常见字段，属性可以为空（说明无值）
     */
    @Id
    //主键
    private String id;
    @Indexed(unique = true)
    // 论文标题
    private String title;
    // 论文摘要  caption == Abstract
    private Section summary;
    // 论文关键词  TODO:暂时设置为String 后期需要再处理成List<String>
    private String keywordList;
    // 论文引言 caption == Introduction
    private Section introduction;
    // 论文正文
    private List<Section> mainBody = new ArrayList<>();
    // 论文附录 caption == Appendix
    private Section appendix;
    // 论文参考文献 caption == References
    private Section citationList;
    // 论文表格 浮动pattern
    private List<Section> floatSectionList = new ArrayList<>();



    @Override
    public String toString() {
        return "Literature{" +
                "title=" + title +
                ", summary=" + summary +
                ", keywordList='" + keywordList + '\'' +
                ", introduction=" + introduction +
                ", mainBody=" + mainBody +
                ", appendix=" + appendix +
                ", citationList=" + citationList ;
    }
}
