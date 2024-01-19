library Digoxin_Cyclosporine_CDS version '1.0'

using FHIR version '4.0.1'
include FHIRHelpers version '4.0.1' called FHIRHelpers

// include PDDICDSCommon version ''1.0'' called Common

valueset "Digoxin": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-digoxin'
valueset "Cyclosporine": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-cyclosporine'
valueset "Aldosterone Antagonists": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-AAS'
valueset "Loop Diuretics": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-LOOPDIURETIC'
valueset "Digoxin LOINC": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-digoxin-LOINC'
valueset "Potassium LOINC": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-potassium-LOINC'
valueset "Magnesium LOINC": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-magnesium-LOINC'
valueset "Calcium LOINC": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-calcium-LOINC'
valueset "Renal LOINC": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-renal-LOINC'

//parameter ContextPrescriptions List<MedicationRequest>

context Patient

// NOTE: These functions have been moved here from the Commmon library to boost performance
define function GetConditionName(condition Condition):
  if condition is not null
    then condition.code.coding[0].display.value
  else 'NA'

define function GetAssertedDate(condition Condition):
  if condition is not null
    then ToString(condition.recordedDate.value)
  else 'NA'

define function GetLevelAndDate(observation Observation):
  if observation is not null
    then ToString(ToQuantity(observation.value as Quantity))
      + ' and '
      + ToString(observation.effective.value)
  else 'NA'

define function GetMedicationNames(medications List<MedicationRequest>):
  medications M
    return ToCode(M.medication.coding[0]).display

define function GetDrugNames(codings List<FHIR.Coding>):
  Combine(
    flatten (
      codings coding
          return ToCode(coding).display
    ), ', ')

define function ToCode(coding FHIR.Coding):
  System.Code {
    code: coding.code.value,
    system: coding.system.value,
    version: coding.version.value,
    display: coding.display.value
  }

define function ToQuantity(quantity FHIR.Quantity):
  System.Quantity { value: quantity.value.value, unit: quantity.unit.value }

define function ToQuantityFromObservation(observation FHIR.Observation):
  if observation.value is not null and observation.value is FHIR.Quantity
    then System.Quantity { value: observation.value.value.value, unit: observation.value.unit.value }
  else System.Quantity { value: 0, unit: '1' }

define function ToString(value FHIR.Coding): value.display.value
define function ToString(value Code): value.display

/*
**
** Digoxin Cyclosporine CDS Recommendation
**
*/

define "Inclusion Criteria":
  (
    "Is Context medication cyclosporine"
      and "Is digoxin in prefetch"
  )
  or (
    "Is Context medication digoxin"
      and "Is cyclosporine in prefetch"
  )

define "Is Context medication cyclosporine":
  exists ("Cyclosporine Prescription")

define "Cyclosporine Prescription":
  [MedicationRequest: "Cyclosporine"] MR
    where MR.extension[0].value.value='contextPrescriptions'
    return MR.medication.coding[0]
//  ContextPrescriptions P
//    where ToCode(P.medication.coding[0]) in "Cyclosporine"
//    return P.medication.coding[0]

define "Is digoxin in prefetch":
  exists ("Digoxin Rx")

define "Digoxin Rx":
  flatten (
    (
      [MedicationRequest: "Digoxin"] MR
        where MR.authoredOn.value in Interval[Today() - 100 days, null]
        return MR.medication.coding[0]
    )
    union (
      [MedicationAdministration: "Digoxin"] MA
        where MA.effective.value in Interval[Today() - 100 days, null]
        return MA.medication.coding[0]
    )
    union (
      [MedicationDispense: "Digoxin"] MD
        where MD.whenHandedOver.value in Interval[Today() - 100 days, null]
        return MD.medication.coding[0]
    )
    union (
      [MedicationStatement: "Digoxin"] MS
        where MS.effective.value in Interval[Today() - 100 days, null]
        return MS.medication.coding[0]
    )
  )


define "Is Context medication digoxin":
  exists ("Digoxin Prescription")

define "Digoxin Prescription":
  [MedicationRequest: "Digoxin"] MR
    where MR.extension[0].value.value='contextPrescriptions'
    return MR.medication.coding[0]
//  ContextPrescriptions P
//    where ToCode(P.medication.coding[0]) in "Digoxin"
//    return P.medication.coding[0]


define "Is cyclosporine in prefetch":
  exists ("Cyclosporine Rx")

define "Cyclosporine Rx":
	flatten (
    (
      [MedicationRequest: "Cyclosporine"] MR
        where MR.authoredOn.value in Interval[Today() - 100 days, null]
        return MR.medication.coding[0]
    )
    union (
      [MedicationAdministration: "Cyclosporine"] MA
        where MA.effective.value in Interval[Today() - 100 days, null]
        return MA.medication.coding[0]
    )
    union (
      [MedicationDispense: "Cyclosporine"] MD
        where MD.whenHandedOver.value in Interval[Today() - 100 days, null]
        return MD.medication.coding[0]
    )
    union (
      [MedicationStatement: "Cyclosporine"] MS
        where MS.effective.value in Interval[Today() - 100 days, null]
        return MS.medication.coding[0]
    )
  )

// Card 1: Base
define "Get Base Summary":
  'ジゴキシン ('
    + (
      if "Is Context medication digoxin"
        then GetDrugNames("Digoxin Prescription")
      else GetDrugNames("Digoxin Rx")
    )
    + ') とシクロスポリン ('
    + (
      if "Is Context medication cyclosporine"
        then GetDrugNames("Cyclosporine Prescription")
      else GetDrugNames("Cyclosporine Rx")
    )
    + ') の間の潜在的な薬物間相互作用.'

define "Get Base Detail":
  'ジゴキシン毒性のリスクが増加する. リスクを評価し, 必要に応じて措置を講じます. ジゴキシンの毒性は深刻な可能性がある. 臨床結果には, 食欲不振, 吐き気, 嘔吐, 視覚的変化, 不整脈などが含まれる場合がある. この相互作用のメカニズムは, シクロスポリンによる P-糖タンパク質の阻害を介して媒介されるようである. P-糖タンパク質は, ジゴキシン流出の主要な輸送体である.'

define "Get Base Indicator":
  if "Is continuation order for digoxin"
    or "Is continuation order for cyclosporine"
    then if "Is there a normal digoxin level in prefetch"
        then if "Are diuretics or abnormal electrolyte or abnormal renal observations in prefetch"
            then 'warning'
          else 'info'
      else 'warning'
  else 'warning'


define "Is new order for digoxin":
  "Is Context medication digoxin"
      and not "Is digoxin in prefetch"

define "Is continuation order for digoxin":
  "Is Context medication digoxin"
      and "Is digoxin in prefetch"

define "Is new order for cyclosporine":
  "Is Context medication cyclosporine"
      and not "Is cyclosporine in prefetch"

define "Is continuation order for cyclosporine":
  "Is Context medication cyclosporine"
      and "Is cyclosporine in prefetch"



// Card 2
define "Get Card 2 Indicator":
  if "Is new order for cyclosporine"
    then if "Is there a normal digoxin level in prefetch"
      then 'warning'
    else 'critical'
  else if "Is there a normal digoxin level in prefetch"
      then 'info'
    else 'warning'


define "Get Card 2 Summary":
  if "Is there a normal digoxin level in prefetch"
    then '患者の30日以内のジゴキシンレベルが0.9 ng/mL (SI: 1.2 nmol/L) 未満である'
  else '患者には過去 30 日以内に記録上のジゴキシンレベルがない.'

define "Get Card 2 Detail":
  if "Is there a normal digoxin level in prefetch"
    then '(ジゴキシン: '
    + GetLevelAndDate("Normal Digoxin Observation")
    + '). 信頼できる血漿ジゴキシン濃度が正常範囲内である患者の場合、シクロスポリンの開始後に血漿濃度の上昇が予想されるのは合理的です。 開始後は注意深くモニタリングし、必要に応じてジゴキシンの用量を調整することが推奨される.'
  else 'シクロスポリンの投与を開始すると、ジゴキシンレベルが上昇すると予想される. 信頼できる血漿ジゴキシン濃度が正常範囲にない患者の場合は、リスクを上回る利益がある場合にのみ使用すること. 細心の注意と厳重な監視が必要.'


define "Need to reduce Digoxin dose":
  "Is Context medication cyclosporine"
    or not "Is there a normal digoxin level in prefetch"

define "Is there a normal digoxin level in prefetch":
  exists ("Normal Digoxin Observation")

define "Normal Digoxin Observation":
	Last (
    [Observation: "Digoxin LOINC"] O
      where O.effective.value in Interval[Today() - 30 days, null]
        and O.value is not null
        and O.value is FHIR.Quantity
        and ToQuantityFromObservation(O) < 0.9 'ng/mL'
      sort by effective.value
  )
//	Last (
//    [Observation: "Digoxin LOINC"] O
//      where O.effective.value in Interval[Today() - 30 days, null]
//        and ToQuantity(O.value) < 0.9 'ng/mL'
//      sort by effective.value
//  )


define "Are diuretics in prefetch":
  exists ("Aldosterone Antagonists Rx")
    or exists("Loop Diuretics Rx")

define "Aldosterone Antagonists Rx":
	flatten (
    (
      [MedicationRequest: "Aldosterone Antagonists"] MR
        where MR.authoredOn.value in Interval[Today() - 100 days, null]
        return MR.medication.coding[0]
    )
    union (
      [MedicationAdministration: "Aldosterone Antagonists"] MA
        where MA.effective.value in Interval[Today() - 100 days, null]
        return MA.medication.coding[0]
    )
    union (
      [MedicationDispense: "Aldosterone Antagonists"] MD
        where MD.whenHandedOver.value in Interval[Today() - 100 days, null]
        return MD.medication.coding[0]
    )
    union (
      [MedicationStatement: "Aldosterone Antagonists"] MS
        where MS.effective.value in Interval[Today() - 100 days, null]
        return MS.medication.coding[0]
    )
  )

define "Loop Diuretics Rx":
	flatten (
    (
      [MedicationRequest: "Loop Diuretics"] MR
        where MR.authoredOn.value in Interval[Today() - 100 days, null]
        return MR.medication.coding[0]
    )
    union (
      [MedicationAdministration: "Loop Diuretics"] MA
        where MA.effective.value in Interval[Today() - 100 days, null]
        return MA.medication.coding[0]
    )
    union (
      [MedicationDispense: "Loop Diuretics"] MD
        where MD.whenHandedOver.value in Interval[Today() - 100 days, null]
        return MD.medication.coding[0]
    )
    union (
      [MedicationStatement: "Loop Diuretics"] MS
        where MS.effective.value in Interval[Today() - 100 days, null]
        return MS.medication.coding[0]
    )
  )


// Card 3
define "Get Card 3 Indicator":
  if "Is there a normal electrolyte and renal observation in prefetch"
    then 'info'
  else 'warning'

define "Get Card 3 Summary":
  if "Is there a normal electrolyte and renal observation in prefetch"
    then '100 日以内に患者は電解質と血清クレアチニンのレベルを検査されており, カリウム保持性利尿薬やループ利尿薬を服用していない.'
  else '100 日以内に患者は電解質パネルを装着していない. 患者は血清クレアチニン値があり, カリウム保持性利尿薬やループ利尿薬を服用していない.'

define "Get Card 3 Detail":
  if "Is there a normal electrolyte and renal observation in prefetch"
    then '(カリウム: '
    + GetLevelAndDate("Normal Potassium Observation")
    + ') (マグネシウム: '
    + GetLevelAndDate("Normal Magnesium Observation")
    + ') (カルシウム: '
    + GetLevelAndDate("Normal Calcium Observation")
    + ')'
  else ''
  + '(血清クレアチニン: '
  + GetLevelAndDate("Normal Renal Observation")
  + ') 低カリウム血症, 低マグネシウム血症, 高カルシウム血症は, ジゴキシンの毒性を増強する可能性がある. ジゴキシンの 50 ～ 70% は変化せずに尿中に排泄される. 腎機能の変化により, 血清濃度が上昇し, 毒性のリスクが増加する可能性があります.'

define "Are diuretics or abnormal electrolyte or abnormal renal observations in prefetch":
  "Are diuretics in prefetch"
    or not "Is there a normal electrolyte and renal observation in prefetch"

define "Is there a normal electrolyte and renal observation in prefetch":
  "Is there a normal electrolyte observation in prefetch"
    and "Is there a normal renal observation in prefetch"

define "Is there a normal electrolyte observation in prefetch":
  exists ("Normal Potassium Observation")
  and exists ("Normal Magnesium Observation")
  and exists ("Normal Calcium Observation")

define "Normal Potassium Observation":
  Last (
    [Observation: "Potassium LOINC"] O
      where O.effective.value in Interval[Today() - 365 days, null]
        and O.value is not null
        and O.value is FHIR.Quantity
        and ToQuantityFromObservation(O) > 3.5 'meq/L'
        and ToQuantityFromObservation(O) < 5.0 'meq/L'
      sort by effective.value
  )
//  Last (
//    [Observation: "Potassium LOINC"] O
//      where O.effective.value in Interval[Today() - 365 days, null]
//        and ToQuantity(O.value) > 3.5 'meq/L'
//        and ToQuantity(O.value) < 5.0 'meq/L'
//      sort by effective.value
//  )

define "Normal Magnesium Observation":
  Last (
    [Observation: "Magnesium LOINC"] O
      where O.effective.value in Interval[Today() - 365 days, null]
        and O.value is not null
        and O.value is FHIR.Quantity
        and ToQuantityFromObservation(O) > 0.7 'mmol/L'
        and ToQuantityFromObservation(O) < 1.1 'mmol/L'
      sort by effective.value
  )
//  Last (
//    [Observation: "Magnesium LOINC"] O
//      where O.effective.value in Interval[Today() - 365 days, null]
//        and ToQuantity(O.value) > 0.7 'mmol/L'
//        and ToQuantity(O.value) < 1.1 'mmol/L'
//      sort by effective.value
//  )

define "Normal Calcium Observation":
  Last (
    [Observation: "Calcium LOINC"] O
      where O.effective.value in Interval[Today() - 365 days, null]
        and O.value is not null
        and O.value is FHIR.Quantity
        and ToQuantityFromObservation(O) > 8.5 'mg/dL'
        and ToQuantityFromObservation(O) < 10.2 'mg/dL'
      sort by effective.value
  )
//  Last (
//    [Observation: "Calcium LOINC"] O
//      where O.effective.value in Interval[Today() - 365 days, null]
//        and ToQuantity(O.value) > 8.5 'mg/dL'
//        and ToQuantity(O.value) < 10.2 'mg/dL'
//      sort by effective.value
//  )

define "Is there a normal renal observation in prefetch":
  exists ("Normal Renal Observation")

define "Normal Renal Observation":
  Last (
    [Observation: "Renal LOINC"] O
      where O.effective.value in Interval[Today() - 365 days, null]
        and O.value is not null
        and O.value is FHIR.Quantity
        and ToQuantityFromObservation(O) > 0.6 'mg/dL'
        and ToQuantityFromObservation(O) < 1.2 'mg/dL'
      sort by effective.value
  )
//  Last (
//    [Observation: "Renal LOINC"] O
//      where O.effective.value in Interval[Today() - 365 days, null]
//        and ToQuantity(O.value) > 0.6 'mg/dL'
//        and ToQuantity(O.value) < 1.2 'mg/dL'
//      sort by effective.value
//  )