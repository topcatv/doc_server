package org.topcat.docserver;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * 最近的项目中使用Itext将txt文件转换为PDF文件， 并且实现对文件的一些权限控制。 现实对pdf文件加
 * 密，添加水印等。
 */
public class PDFUtil {
    // txt原始文件的路径
    private static final String SRC_PDF_PATH = "/Users/topcat/Downloads/6-6平台改版.docx.pdf";
    // 生成的pdf文件路径
    private static final String WATERMARK_PDF_PATH = "/Users/topcat/Downloads/wm.pdf";
    // 所有者密码
    private static final String OWNER_PASSWORD = "12345678";
    // 证书密码
    private static final char[] CERT_PWD = "ren8866".toCharArray();
    //利用keytool生成数字证书
    //keytool -genkey -alias ctidcert -keystore ~/renturbo_pdf.keystore -storepass "ren8866" -keypass "ren8866" -keyalg "RSA" -dname "CN=www.renturbo.com,OU=人动力,O=武汉人动力,L=武汉,ST=湖北,C=中国"
    public static final String KEYSTORE = "/Users/topcat/renturbo_pdf.keystore";
    public static final String SIGN_DEST = "/Users/topcat/Downloads/6-6平台改版.docx.cert.pdf";
    private PrivateKey pk;
    Certificate[] chain;
    BouncyCastleProvider provider;

    public PDFUtil() {
        try {
            KeyStore ks = KeyStore.getInstance("jks");
            ks.load(new FileInputStream(KEYSTORE), CERT_PWD);
            String alias = (String) ks.aliases().nextElement();
            pk = (PrivateKey) ks.getKey(alias, CERT_PWD);
            chain = ks.getCertificateChain(alias);
            provider = new BouncyCastleProvider();
            Security.addProvider(provider);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在pdf文件中添加水印
     *
     * @param inputFile     原始文件
     * @param outputFile    水印输出文件
     * @param waterMarkName 水印名字
     */
    public void waterMark(String inputFile, String outputFile, String waterMarkName) {
        try {
            PdfReader reader = new PdfReader(inputFile);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));

            BaseFont base_cn = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            BaseFont base = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            int total = reader.getNumberOfPages() + 1;
            PdfContentByte content;

            for (int i = 1; i < total; i++) {
                Rectangle pageRectangle = reader.getPageSize(i);
                //计算水印x,y坐标
                float x = pageRectangle.getWidth() / 2;
                float y = pageRectangle.getHeight() / 2;

                content = stamper.getOverContent(i);//获得pdf最顶层
                content.saveState();
                // set transparency
                PdfGState gs = new PdfGState();
                gs.setFillOpacity(0.6f);//设置透明度为0.6
                content.setGState(gs);


                content.beginText();
                content.setColorFill(BaseColor.GRAY);
                content.setFontAndSize(base_cn, 40);
                content.showTextAligned(Element.ALIGN_CENTER, waterMarkName, x, y, 35);//水印文字成35度角倾斜
                content.endText();
                content.beginText();

                content.setColorFill(BaseColor.GRAY);

                content.setFontAndSize(base, 30);
                String seeAttached = "(see attached digital certificate)";
                content.showTextAligned(Element.ALIGN_CENTER, seeAttached, x, y - 42, 35);
                content.endText();

                content.restoreState();
            }
            stamper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sign(String src, String dest,
                     String reason, String location, String userPassWord) {
        try {
            PdfReader reader = new PdfReader(src, "12345678".getBytes());
            FileOutputStream fout = new FileOutputStream(dest);
            PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0');
            // 设置密码
            stp.setEncryption(userPassWord.getBytes(), OWNER_PASSWORD.getBytes(), PdfWriter.ALLOW_COPY, false);


            // Creating the appearance
            PdfSignatureAppearance appearance = stp.getSignatureAppearance();
            appearance.setReason(reason);
            appearance.setLocation(location);
            appearance.setVisibleSignature(new Rectangle(36, 748, 144, 780), 1, "sig");
            // Creating the signature
            ExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA512, provider.getName());
            ExternalDigest digest = new BouncyCastleDigest();
            MakeSignature.signDetached(appearance, digest, pks, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);
            stp.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, DocumentException {
        PDFUtil pdfUtil = new PDFUtil();

        pdfUtil.waterMark(SRC_PDF_PATH, WATERMARK_PDF_PATH, "水印文字");
        pdfUtil.sign(WATERMARK_PDF_PATH, SIGN_DEST, "created", "wh", "123");
    }
}