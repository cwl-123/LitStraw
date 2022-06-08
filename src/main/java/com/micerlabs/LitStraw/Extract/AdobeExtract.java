package com.micerlabs.LitStraw.Extract;

import com.adobe.pdfservices.operation.ExecutionContext;
import com.adobe.pdfservices.operation.auth.Credentials;
import com.adobe.pdfservices.operation.exception.SdkException;
import com.adobe.pdfservices.operation.exception.ServiceApiException;
import com.adobe.pdfservices.operation.exception.ServiceUsageException;
import com.adobe.pdfservices.operation.io.FileRef;
import com.adobe.pdfservices.operation.pdfops.ExtractPDFOperation;
import com.adobe.pdfservices.operation.pdfops.options.extractpdf.ExtractElementType;
import com.adobe.pdfservices.operation.pdfops.options.extractpdf.ExtractPDFOptions;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Arrays;

public class AdobeExtract {

    @Value("${api-credentials}")
    private static String apiCredentials;


    public static void main(String[] args) {
        pdfExtract2Zip("src/main/resources/3.pdf","output/3.zip");
    }

    /**
     * @param pdfPath    eg:"src/main/resources/3.pdf"
     * @param outputPath eg:"output/3.zip"
     * @return
     */
    public static void pdfExtract2Zip(String pdfPath, String outputPath) {

        try {
            // 初始化，创建鉴权对象实例
            Credentials credentials = Credentials.serviceAccountCredentialsBuilder()
                    .fromFile("src/main/resources/pdfservices-api-credentials.json")
                    .build();
            ExecutionContext executionContext = ExecutionContext.create(credentials);
            ExtractPDFOperation extractPDFOperation = ExtractPDFOperation.createNew();

            // 塞入pdf
            FileRef source = FileRef.createFromLocalFile(pdfPath);
            extractPDFOperation.setInputFile(source);

            ExtractPDFOptions extractPDFOptions = ExtractPDFOptions.extractPdfOptionsBuilder()
                    .addElementsToExtract(Arrays.asList(ExtractElementType.TEXT))
                    .build();
            extractPDFOperation.setOptions(extractPDFOptions);

            // 执行
            FileRef result = extractPDFOperation.execute(executionContext);

            // 保存，没有文件夹output会自动创建
            result.saveAs(outputPath);
        } catch (ServiceApiException | IOException | SdkException | ServiceUsageException e) {
            e.printStackTrace();
        }
    }
}

