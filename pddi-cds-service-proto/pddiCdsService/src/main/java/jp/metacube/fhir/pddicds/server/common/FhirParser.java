package jp.metacube.fhir.pddicds.server.common;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;

/**
 * FHIRパーサーの定義
 *
 * @author takaha
 */
public class FhirParser {
  private FhirContext fc = null;
  private IParser xmlParser = null;
  private IParser jsonParser = null;
  private static FhirParser fp = new FhirParser();

  /** コンストラクタ */
  private FhirParser() {
    // FHIRR4のコンテキストを生成
    this.fc = FhirContext.forR4();

    // パーサーの定義
    // XMLパーサ
    this.xmlParser = this.fc.newXmlParser();
    // TODO エラーハンドラのモード指定
    if (true) {
      this.xmlParser.setParserErrorHandler(new StrictErrorHandler());
    }
    // JSONパーサ
    this.jsonParser = this.fc.newJsonParser();
    // TODO エラーハンドラのモード指定
    if (true) {
      this.jsonParser.setParserErrorHandler(new StrictErrorHandler());
    }
  }

  /**
   * FhirParserのインスタンスを返す
   *
   * @return FhirParserのインスタンス
   */
  public static FhirParser getInstance() {
    return fp;
  }

  /**
   * FHIRのXML形式パーサーを返す
   *
   * @return XML形式パーサー
   */
  public IParser getXMLParser() {
    return this.xmlParser;
  }

  /**
   * FHIRのJSON形式パーサーを返す
   *
   * @return JSON形式パーサー
   */
  public IParser getJSONParser() {
    return this.jsonParser;
  }
}
