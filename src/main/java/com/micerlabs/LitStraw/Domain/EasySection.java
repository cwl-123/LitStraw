package com.micerlabs.LitStraw.Domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 正文的领域类 Section章节
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EasySection {

    public EasySection(String caption) {
        this.caption = caption;
    }

    public EasySection(String caption, String content) {
        this.caption = caption;
        this.content = content;
    }

    // 章节标题
    private String caption;
    // 内容
    private String content;
    //外标题 更上一级的Caption
    private String outCaption;

    @Override
    public String toString() {
        return "EasySection{" +
                "caption='" + caption + '\'' +
                ", content='" + content + '\'' +
                ", outCaption='" + outCaption + '\'' +
                '}';
    }


}
