library Warfarin_NSAIDs_CDS version '1.0'

using FHIR version '4.0.1'
include FHIRHelpers version '4.0.1' called FHIRHelpers

// include PDDICDSCommon version '1.0' called Common

codesystem "RXNORM": 'http://www.nlm.nih.gov/research/umls/rxnorm'

valueset "Topical Diclofenac": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-topicaldiclofenac'
valueset "Warfarins": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-warfarin'
valueset "NSAIDs": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-NSAIDS'
valueset "PPIs and Misoprostols": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-PPIS'
valueset "History of GI Bleeds": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-Hx-UGIB-snomed'
valueset "Systemic Corticosteroids": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-SCS'
valueset "Aldosterone Antagonists": 'http://hl7.org/fhir/ig/PDDI-CDS/ValueSet/valueset-AAS'

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
//  if condition is not null
//    then ToString(condition.assertedDate.value)
//  else 'NA'

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

define function ToString(value FHIR.Coding): value.display.value
define function ToString(value Code): value.display

/*
**
** Warfarin NSAID CDS Recommendation
**
*/

define "Inclusion Criteria":
  if "Is context medication topical diclofenac"
    then "Is warfarin in prefetch"
  else (
    "Is context medication systemic NSAID"
      and "Is warfarin in prefetch"
  )


define "Is context medication topical diclofenac":
  exists ("Topical Diclofenac Prescription")

define "Is not context medication topical diclofenac":
  not exists ("Topical Diclofenac Prescription")

define "Topical Diclofenac Prescription":
  [MedicationRequest: "Topical Diclofenac"] MR
    where MR.extension[0].value.value='contextPrescriptions'
    return MR.medication.coding[0]
//  ContextPrescriptions P
//    where ToCode(P.medication.coding[0]) in "Topical Diclofenac"
//    return P.medication.coding[0]


define "Is warfarin in prefetch":
  exists ("Warfarin Rx")

define "Warfarin Rx":
  flatten (
    (
      [MedicationRequest: "Warfarins"] MR
        where MR.authoredOn.value in Interval[Today() - 100 days, null]
        return MR.medication.coding[0]
    )
    union (
      [MedicationAdministration: "Warfarins"] MA
        where MA.effective.value in Interval[Today() - 100 days, null]
        return MA.medication.coding[0]
    )
    union (
      [MedicationDispense: "Warfarins"] MD
        where MD.whenHandedOver.value in Interval[Today() - 100 days, null]
        return MD.medication.coding[0]
    )
    union (
      [MedicationStatement: "Warfarins"] MS
        where MS.effective.value in Interval[Today() - 100 days, null]
        return MS.medication.coding[0]
    )
  )

define "Is context medication systemic NSAID":
  exists ("NSAID Prescription")

define "NSAID Prescription":
  [MedicationRequest: "NSAIDs"] MR
    where MR.extension[0].value.value='contextPrescriptions'
    return MR.medication.coding[0]
//  ContextPrescriptions P
//    where ToCode(P.medication.coding[0]) in "NSAIDs"
//    return P.medication.coding[0]




// Card 1: Base
define "Get Base Summary":
  'ワルファリン ('
    + GetDrugNames("Warfarin Rx")
    + ') と NSAID ('
    + GetDrugNames("NSAID Prescription")
    + ') の間の潜在的な薬物間相互作用.'

define "Get Base Indicator":
  if "Is context medication topical diclofenac"
    then 'info'
  else 'warning'


define "Acetaminophen 325 MG": Code '313782' from "RXNORM" display 'Acetaminophen 325 MG Oral Tablet'
define "Acetaminophen 500 MG": Code '198440' from "RXNORM" display 'Acetaminophen 500 MG Oral Tablet'

define "Get Base Label 1":
  'NSAID ('
  + GetDrugNames("NSAID Prescription")
  + ') を APAP ('
  + ToString("Acetaminophen 325 MG")
  + ') の代用とする.'

define "Get Base Label 2":
  'NSAID ('
  + GetDrugNames("NSAID Prescription")
  + ') with APAP ('
  + ToString("Acetaminophen 500 MG")
  + ') の代用とする.'



// Card 2: PPI or misoprostol
define "Get Card 2 Indicator":
  if "Taking PPI or misoprostol"
    then 'info'
  else 'critical'

define "Get Card 2 Summary":
  if "Taking PPI or misoprostol"
    then '患者はプロトンポンプ阻害剤 ('
      + GetDrugNames("PPIs and Misoprostols Rx")
      + ') を服用している.'
  else '患者はプロトンポンプ阻害剤またはミソプロストールを服用していない.'

define "Get Card 2 Detail":
  'プロトンポンプ阻害剤とミソプロストールは、NSAID とワルファリンを受けている患者における UGIB のリスクを軽減する可能性がある.'

define "Get Card 2 Label":
  if "Taking PPI or misoprostol"
    then 'リスクを評価し、必要に応じて措置を講じること.'
  else '症状改善がリスクを上回る場合にのみ使用すること.'


define "Taking PPI or misoprostol":
  exists ("PPIs and Misoprostols Rx")

define "PPIs and Misoprostols Rx":
	flatten (
    (
      [MedicationRequest: "PPIs and Misoprostols"] MR
        where MR.authoredOn.value in Interval[Today() - 100 days, null]
        return MR.medication.coding[0]
    )
    union (
      [MedicationAdministration: "PPIs and Misoprostols"] MA
        where MA.effective.value in Interval[Today() - 100 days, null]
        return MA.medication.coding[0]
    )
    union (
      [MedicationDispense: "PPIs and Misoprostols"] MD
        where MD.whenHandedOver.value in Interval[Today() - 100 days, null]
        return MD.medication.coding[0]
    )
    union (
      [MedicationStatement: "PPIs and Misoprostols"] MS
        where MS.effective.value in Interval[Today() - 100 days, null]
        return MS.medication.coding[0]
    )
  )



// Card 3: History of UGIB/peptic ulcer or > 65 years old
define "Get Card 3 Indicator":
  if "Age > 65 years or Hx UGIB"
    then 'warning'
  else 'info'

define "Get Card 3 Summary":
  if "Age > 65 years or Hx UGIB"
    then '患者は65歳より上、または上部消化管出血の病歴がある ("'
      + GetConditionName("GI Bleeds Condition")
      + '" および '
      + GetAssertedDate("GI Bleeds Condition")
      + ').'
  else '患者は65歳以下で、かつ上部消化管出血の病歴がない.'

define "Get Card 3 Detail":
  'UGIB または消化性潰瘍の病歴のある患者は、この相互作用により UGIB のリスクが増加する可能性がある. これらの相互作用により、高齢がUGIBの独立した危険因子となる度合は厳格には確立されていないが、一般にUGIBは年齢とともに増加することが知られている.'

define "Get Card 3 Label":
  if "Taking PPI or misoprostol"
    then 'リスクを評価し、必要に応じて措置を講じること.'
  else if "Age > 65 years or Hx UGIB"
    then '症状改善がリスクを上回る場合にのみ使用すること.'
  else 'リスクを評価し、必要に応じて措置を講じること.'


define "Age > 65 years or Hx UGIB":
  exists ("GI Bleeds Condition")
    or AgeInYears() > 65

define "GI Bleeds Condition":
  Last(
    [Condition: "History of GI Bleeds"] C
      sort by recordedDate.value
  )



// Card 4: Systemic Corticosteroids or Aldosterone Antagonist
define "Get Card 4 Indicator":
  if "Taking CS, MCRA, or NSAID"
    then 'warning'
  else 'info'

define "Get Card 4 Summary":
  if "Taking CS, MCRA, or NSAID" then
    if "Has Systemic Corticosteroids" and not "Has Aldosterone Antagonists" and not "Has NSAID" then
      '患者は全身性コルチコステロイド ('
      + GetDrugNames("Systemic Corticosteroids Rx")
      + ') を併用している.'
    else if not "Has Systemic Corticosteroids" and "Has Aldosterone Antagonists" and not "Has NSAID" then
      '患者はアルドステロン拮抗薬 ('
      + GetDrugNames("Aldosterone Antagonists Rx")
      + ') を併用している.'
    else if not "Has Systemic Corticosteroids" and not "Has Aldosterone Antagonists" and "Has NSAID" then
      '患者は高用量または複数の NSAID ('
      + GetDrugNames("NSAID Rx")
      + ') を併用している.'
    else if "Has Systemic Corticosteroids" and "Has Aldosterone Antagonists" and not "Has NSAID" then
      '患者は全身性コルチコステロイド ('
      + GetDrugNames("Systemic Corticosteroids Rx")
      + '), またはアルドステロン拮抗薬 ('
      + GetDrugNames("Aldosterone Antagonists Rx")
      + ') を併用している.'
    else if "Has Systemic Corticosteroids" and not "Has Aldosterone Antagonists" and "Has NSAID" then
      '患者は全身性コルチコステロイド ('
      + GetDrugNames("Systemic Corticosteroids Rx")
      + '), あるいは高用量または複数の NSAID ('
      + GetDrugNames("NSAID Rx")
      + ') を併用している.'
    else if not "Has Systemic Corticosteroids" and "Has Aldosterone Antagonists" and "Has NSAID" then
      '患者はアルドステロン拮抗薬 ('
      + GetDrugNames("Aldosterone Antagonists Rx")
      + '), あるいは高用量または複数の NSAID ('
      + GetDrugNames("NSAID Rx")
      + ') を併用している.'
    else 
      '患者は全身性コルチコステロイド ('
      + GetDrugNames("Systemic Corticosteroids Rx")
      + '), アルドステロン拮抗薬 ('
      + GetDrugNames("Aldosterone Antagonists Rx")
      + '), あるいは高用量または複数の NSAID ('
      + GetDrugNames("NSAID Rx")
      + ') を併用している.'
  else '患者は全身性コルチコステロイド, アルドステロン拮抗薬, あるいは, 高用量または複数の NSAIDを併用していない.'
//  if "Taking CS, MCRA, or NSAID"
//    then 'Patient is concomitantly taking systemic corticosteroids ('
//      + GetDrugNames("Systemic Corticosteroids Rx")
//      + '), aldosterone antagonist ('
//      + GetDrugNames("Aldosterone Antagonists Rx")
//      + '), or high dose or multiple NSAIDs ('
//      + GetDrugNames("NSAID Rx")
//      + ').'
//  else 'Patient is not concomitantly taking systemic corticosteroids, aldosterone antagonist, or high dose or multiple NSAIDs.'

define "Get Card 4 Detail":
  'コルチコステロイドとアルドステロン拮抗薬はどちらも、NSAID を服用している患者の UGIB のリスクを大幅に増加させることが示されており、相対リスクはそれぞれ 12.8 と 11 であり、NSAID 単独の場合のリスクは 4.3 である (Masclee et al. Gastroenterology 2014; 147:784-92.)'

define "Get Card 4 Label":
  if "Taking PPI or misoprostol"
    then 'リスクを評価し、必要に応じて措置を講じること.'
  else if "Taking CS, MCRA, or NSAID"
    then '症状改善がリスクを上回る場合にのみ使用すること.'
  else 'リスクを評価し、必要に応じて措置を講じること.'


define "Taking CS, MCRA, or NSAID":
  "Has Systemic Corticosteroids"
    or "Has Aldosterone Antagonists"
    or "Has NSAID"
    
define "Has Systemic Corticosteroids":
  exists ("Systemic Corticosteroids Rx")

define "Systemic Corticosteroids Rx":
	flatten (
    (
      [MedicationRequest: "Systemic Corticosteroids"] MR
        where MR.authoredOn.value in Interval[Today() - 100 days, null]
        return MR.medication.coding[0]
    )
    union (
      [MedicationAdministration: "Systemic Corticosteroids"] MA
        where MA.effective.value in Interval[Today() - 100 days, null]
        return MA.medication.coding[0]
    )
    union (
      [MedicationDispense: "Systemic Corticosteroids"] MD
        where MD.whenHandedOver.value in Interval[Today() - 100 days, null]
        return MD.medication.coding[0]
    )
    union (
      [MedicationStatement: "Systemic Corticosteroids"] MS
        where MS.effective.value in Interval[Today() - 100 days, null]
        return MS.medication.coding[0]
    )
  )

define "Has Aldosterone Antagonists":
  exists ("Aldosterone Antagonists Rx")

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

define "Has NSAID":
  exists ("NSAID Rx")
    
define "NSAID Rx":
	flatten (
    (
      [MedicationRequest: "NSAIDs"] MR
        where MR.authoredOn.value in Interval[Today() - 100 days, null]
        return MR.medication.coding[0]
    )
    union (
      [MedicationAdministration: "NSAIDs"] MA
        where MA.effective.value in Interval[Today() - 100 days, null]
        return MA.medication.coding[0]
    )
    union (
      [MedicationDispense: "NSAIDs"] MD
        where MD.whenHandedOver.value in Interval[Today() - 100 days, null]
        return MD.medication.coding[0]
    )
    union (
      [MedicationStatement: "NSAIDs"] MS
        where MS.effective.value in Interval[Today() - 100 days, null]
        return MS.medication.coding[0]
    )
  )
