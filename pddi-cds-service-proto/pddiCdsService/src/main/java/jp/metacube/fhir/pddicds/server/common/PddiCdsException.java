package jp.metacube.fhir.pddicds.server.common;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent;

/**
 * 本システムが出力する例外を定義
 *
 * @param statuscode ステータスコード
 * @param severity IssueSeverity
 * @param code IssueType
 * @param cause
 */
public class PddiCdsException extends Exception {
  private static final long serialVersionUID = 1L;
  private int statusCode = -1;
  private OperationOutcome oo = null;

  /**
   * @param statuscode
   * @param information
   * @param processing
   * @param detail
   */
  public PddiCdsException(int statuscode, IssueSeverity severity, IssueType code, String detail) {
    super(detail);
    this.statusCode = statuscode;
    oo = new OperationOutcome();
    OperationOutcomeIssueComponent issue = oo.addIssue();
    issue.setCode(code);
    issue.setSeverity(severity);
    CodeableConcept cc = new CodeableConcept();
    cc.setText(detail);
    issue.setDetails(cc);
  }

  /**
   * @param statuscode
   * @param severity
   * @param code
   * @param cause
   */
  public PddiCdsException(int statuscode, IssueSeverity severity, IssueType code, Throwable cause) {
    super(cause);
    this.statusCode = statuscode;
    oo = new OperationOutcome();
    OperationOutcomeIssueComponent issue = oo.addIssue();
    issue.setCode(code);
    issue.setSeverity(severity);
    CodeableConcept cc = new CodeableConcept();
    cc.setText(cause.toString());
    issue.setDetails(cc);
  }

  public int getStatusCode() {
    return this.statusCode;
  }

  public OperationOutcome getOperationOutcome() {
    return this.oo;
  }
}
