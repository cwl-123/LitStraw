package com.micerlabs.LitStraw.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static void main(String[] args) {
        readFileToString("materialLib/txt/人口评估/01/A Model to Estimate the Population Contributing to the Wastewater Using Samples Collected on Census Day.txt");
    }

    /**
     * 函数名：getFile
     * 作用：使用递归，输出指定文件夹内的所有文件
     * 参数：path：文件夹路径
     * 前置空格缩进，显示文件层次结构
     */
    public static List<String> getFile(String path) {
        // 获得指定文件对象
        File file = new File(path);
        // 获得该文件夹内的所有文件
        File[] array = file.listFiles();
        ArrayList<String> filenames = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            if (array[i].isFile()) { //如果是文件
//                System.out.println(array[i].getName());
                filenames.add(array[i].getName());
            }
        }
        return filenames;
    }


    /**
     * 批量修改文件夹下，文件后缀名称
     *
     * @param LibPath
     * @param from
     * @param to
     */
    public static void modifySuffixName(String LibPath, String from, String to) {
        File f = new File(LibPath);
        File[] fs = f.listFiles();
        for (File subFile : fs) {
            // 如果文件是文件夹则递归调用批量更改文件后缀名的函数
            if (subFile.isDirectory()) {
                modifySuffixName(subFile.getPath(), from, to);
            } else {
                String name = subFile.getName();
                if (name.endsWith(from)) {
                    subFile.renameTo(new File(subFile.getParent() + "/" + name.substring(0, name.indexOf(from)) + to));
                }
            }
        }
    }

    /**
     * @param path
     * @param fileName
     */
    public static void createFile(String path, String fileName) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(path + "/" + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件和目录（文件夹）
     *
     * @author Jia
     */
    public static void deleteDir(String path) {
        // 为传进来的路径参数创建一个文件对象
        File file = new File(path);
        // 如果目标路径是一个文件，那么直接调用delete方法删除即可
        // file.delete();
        // 如果是一个目录，那么必须把该目录下的所有文件和子目录全部删除，才能删除该目标目录，这里要用到递归函数
        // 创建一个files数组，用来存放目标目录下所有的文件和目录的file对象
        File[] files = new File[50];
        // 将目标目录下所有的file对象存入files数组中
        files = file.listFiles();
        // 循环遍历files数组
        for (File temp : files) {
            // 判断该temp对象是否为文件对象
            if (temp.isFile()) {
                temp.delete();
            }
            // 判断该temp对象是否为目录对象
            if (temp.isDirectory()) {
                // 将该temp目录的路径给delete方法（自己），达到递归的目的
                deleteDir(temp.getAbsolutePath());
                // 确保该temp目录下已被清空后，删除该temp目录
                temp.delete();
            }
        }
    }

    /**
     * 把Txt内容读取为String
     *
     * @param path
     */
    public static String readFileToString(String path) {
        StringBuffer str = null;
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(path));
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String tempString = null;
            str = new StringBuffer();
            while ((tempString = br.readLine()) != null) {
                str.append(tempString + "\r\n");
            }
//            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(str);
    }
}

