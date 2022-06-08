package com.micerlabs.LitStraw.Extract;

import com.micerlabs.LitStraw.Domain.Text;
import com.micerlabs.LitStraw.Domain.TextType;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JsonAnalysis {

    private static String paraRegex = "//Document/P(.*)";
    private static String HeadRegex = "//Document/H(.*)";
    private static String TitleRegex = "//Document/Title";
    private static String TableRegex = "//Document/Table(.*)";
    private static String TableTextRegex = "//Document/P\\[\\d+\\]/Sub(.*)";
    private static String FootnoteRegex = "//Document/Footnote(.*)";

    public static void main(String[] args) throws IOException {
        test1();
    }

    public static void test1() throws IOException, JSONException {
        File file = new File("output/structuredData.json");
        String content = FileUtils.readFileToString(file, "UTF-8");
        JSONObject jsObj = new JSONObject(content);
        JSONArray elements = jsObj.getJSONArray("elements");
        List<Text> textList = new ArrayList<>();
        for (Object element : elements) {
            JSONObject e = (JSONObject) element;
            double textSize = e.optDouble("TextSize", 0.0D);
            if (textSize != 0.0D) {
                System.out.println(e.getString("Path") +": "+ textSize);
                textList.add(convert2Text(e));
            }
        }
        System.out.println(textList.size());
    }

    // jsonObject 转 Text
    public static Text convert2Text(JSONObject jsonObject) {
        // 提取信息
        float textSize = jsonObject.getFloat("TextSize");
        String path = jsonObject.getString("Path");
        JSONArray jsonArray = jsonObject.getJSONArray("Bounds");
        int page = jsonObject.getInt("Page");
        String content = jsonObject.getString("Text");
        float[] bounds = new float[]{jsonArray.getFloat(0), jsonArray.getFloat(1), jsonArray.getFloat(2), jsonArray.getFloat(3)};
        JSONObject font = jsonObject.getJSONObject("Font");
        String fontName = font.getString("name");
        int fontWeight = font.getInt("weight");
        boolean italic = font.getBoolean("italic");

        // 填充信息
        Text text = new Text();
        text.setContent(content);
        text.setBounds(bounds);
        text.setPage(page);
        text.setPath(path);

        TextType textType = new TextType();
        textType.setFont(textSize);
        textType.setFontName(fontName);
        textType.setWeight(fontWeight);
        textType.setItalic(italic);
        text.setTextType(textType);
        return text;
    }



    public static void test() throws IOException, JSONException {
        File file = new File("data/3/structuredData.json");
        String content = FileUtils.readFileToString(file, "UTF-8");
        JSONObject jsObj = new JSONObject(content);
        JSONArray elements = jsObj.getJSONArray("elements");
        for (Object element : elements) {
            JSONObject e = (JSONObject) element;
            String path = e.getString("Path");
            double textSize = 0.0;
            try {
                textSize = e.getDouble("TextSize");
            } catch (JSONException ex) {
            }
            System.out.println(path + "  " + textSize);
            if (path.matches(paraRegex) || path.matches(HeadRegex)) {
                String text = e.getString("Text");
                appendToTxt("data/2/2.txt", text);
            }
        }
    }

    /**
     * 内容追加写入txt
     * 文件不存在会自动创建，但文件夹得存在，不然抛npe
     *
     * @param file
     * @param content
     */
    public static void appendToTxt(String file, String content) {

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(content + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

