package jp.metacube.fhir.pddicds.server.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.mozilla.universalchardet.UniversalDetector;

/**
 * 文字コードを判定するクラス
 *
 * @author takaha
 */
public class FileCharDetecter {

  /** コンストラクタ */
  public FileCharDetecter() {}

  /**
   * ファイル内部で使用される文字コードを判定
   *
   * @param filePath ファイルのパス
   * @return 文字コード
   * @throws java.io.IOException
   */
  public String detectorFile(String filePath) throws java.io.IOException {
    FileInputStream fis = null;
    String encType = null;
    try {
      fis = new FileInputStream(filePath);
      encType = this.detectorInputStream(fis);
    } finally {
      if (fis != null) {
        fis.close();
      }
    }
    return encType;
  }

  /**
   * InputStreamで使用される文字コードを判定
   *
   * @param is InputStream
   * @return 文字コード
   * @throws IOException
   */
  public String detectorInputStream(InputStream is) throws IOException {
    byte[] buf = new byte[4096];

    // 文字コード判定ライブラリの実装
    UniversalDetector detector = new UniversalDetector(null);

    // 判定開始
    int nread;
    while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
      detector.handleData(buf, 0, nread);
    }

    // 判定終了
    detector.dataEnd();

    // 文字コード判定
    String encType = detector.getDetectedCharset();
    if (encType == null) {
      // 判定できない場合はデフォルト設定
      encType = "JISAutoDetect";
    }

    // 判定の初期化
    detector.reset();

    return encType;
  }
}
