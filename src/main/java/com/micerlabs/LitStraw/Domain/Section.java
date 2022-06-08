package com.micerlabs.LitStraw.Domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 正文的领域类 Section章节
 */
@Data
@NoArgsConstructor
public class Section {

    // 章节标题
    private TextPattern caption;
    // 内容
    private TextPattern content;
    // Note:连续Caption多存一级！但是一般不使用外级Caption
    // 外标题 更上一级的Caption
    private TextPattern outCaption;

    public Section(TextPattern caption, TextPattern content, TextPattern outCaption) {
        this.caption = caption;
        this.content = content;
        this.outCaption = outCaption;
    }

    public Section(TextPattern caption, TextPattern content) {
        this.caption = caption;
        this.content = content;
    }

    public Section(TextPattern caption) {
        this.caption = caption;
    }


    @Override
    public String toString() {
        String outCaptionText = "", captionText = "", contentText = "";
        if (outCaption != null) {
            outCaptionText = outCaption.getTextContent();
        }
        if (caption != null) {
            captionText = caption.getTextContent();
        }
        if (content != null) {
            contentText = content.getTextContent();
        }
        return "Section{" +
                "outCaption=" + outCaptionText +
                ", caption=" + captionText +
                ", content=" + contentText +
                '}';
    }
}
