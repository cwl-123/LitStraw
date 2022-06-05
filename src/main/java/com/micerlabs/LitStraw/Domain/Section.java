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
public class Section {

    public Section(TextPattern caption) {
        this.caption = caption;
    }

    public Section(TextPattern caption, TextPattern content) {
        this.caption = caption;
        this.content = content;
    }

    // 章节标题
    private TextPattern caption;
    // 内容
    private TextPattern content;
    //外标题 更上一级的Caption
    private TextPattern outCaption;
    // Note:连续Caption多存一级！但是一般不使用外级Caption


    @Override
    public String toString() {
        String outCaptionText = "", captionText = "", contentText = "";
        if (outCaption != null) {
            outCaptionText = outCaption.getText();
        }
        if (caption != null) {
            captionText = caption.getText();
        }
        if (content != null) {
            contentText = content.getText();
        }
        return "Section{" +
                "outCaption=" + outCaptionText +
                ", caption=" + captionText +
                ", content=" + contentText +
                '}';
    }
}
