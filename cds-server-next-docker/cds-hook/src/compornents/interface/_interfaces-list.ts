//増えた場合はここに追加
import ClassInterface from "./_interface_test"
import PatientGreeter from "../cds-services/patient-greeter"
import StaticPatientGreeter from "../cds-services/static-patient-greeter"
import WarfarinNsaidsCdsSelect from "../order-select/warfarin-nsaids-cds-select"
import WarfarinNsaidsCdsSign from "../order-sign/warfarin-nsaids-cds-sign"
import DigoxinCyclosporineCdsSelect from "../order-select/digoxin-cyclosporine-cds-select"
import DigoxinCyclosporineCdsSign from "../order-sign/digoxin-cyclosporine-cds-sign"
import ProcessTest from "../demo-hook/process-test"

const SelectList: any = {
    ClassInterface,
    PatientGreeter,
    StaticPatientGreeter,
    WarfarinNsaidsCdsSelect,
    WarfarinNsaidsCdsSign,
    DigoxinCyclosporineCdsSelect,
    DigoxinCyclosporineCdsSign,
    ProcessTest
}

export default SelectList