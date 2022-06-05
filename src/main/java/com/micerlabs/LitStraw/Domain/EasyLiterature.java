
package com.micerlabs.LitStraw.Domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * com.micerlabs.LitStraw.Domain 通用论文格式 Literature
 */
@Data
@Document(collection = "EasyLiterature")
public class EasyLiterature implements Serializable {
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
    private EasySection summary;
    // 论文引言 caption == Introduction
    private EasySection introduction;
    // 论文正文
    private List<EasySection> mainBody = new ArrayList<>();
    // 论文附录 caption == Appendix
    private EasySection appendix;
    // 论文表格 浮动pattern
    private List<EasySection> floatSectionList = new ArrayList<>();


    @Override
    public String toString() {
        return "Literature{" +
                "title=" + title +
                ", summary=" + summary +
                ", introduction=" + introduction +
                ", mainBody=" + mainBody +
                ", appendix=" + appendix;
    }
}

