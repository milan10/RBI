//import com.sap.gateway.ip.core.customdev.util.Message;

import groovy.util.slurpersupport.GPathResult;
import groovy.util.slurpersupport.NodeChildren
import groovy.xml.MarkupBuilder;

enum TimeLine {
    PAST, CURRENT, FUTURE
}

enum ContractStatus {
    ContractStatus, ContractStatusNew, ContractStatusNewFrom
}

def static parseString(GPathResult result) {
    if (result.size() == 1)
        return result.toString()
    else
        return ""
}

def static parseDate(GPathResult result) {
    if (result.size() == 1 && result.toString().length() == 23)
        return Date.parse("yyyy-MM-dd'T'HH:mm:ss.sss", result.toString())
    else
        return Date.parse("yyyy-MM-dd", "1970-01-01")
}

def static parseDateMax(GPathResult result) {
    if (result.size() == 1 && result.toString().length() == 23)
        return Date.parse("yyyy-MM-dd'T'HH:mm:ss.sss", result.toString())
    else
        return Date.parse("yyyy-MM-dd", "9999-12-31")
}

def static getBooleanResult(GPathResult result, String field, StringBuilder sb) {
    if (result.size() == 1)
        return Boolean.parseBoolean(result.toString())

    sb.append(field + ": has more input elements" + "\n")
    return ""
}

def static controlLength(String value, int length) {
    if (value.length() < length)
        return value
    else
        return value.substring(0, length)
}

def static getIns(GPathResult customerString4, boolean isContingentWorker, GPathResult vendor, StringBuilder sb) {
    def length = 10
    String result

    if (!isContingentWorker)
        result = getStringResult(customerString4, "getIns_1", 999, sb)
    else
        result = getStringResult(vendor, "getIns_2", 999, sb)

    return controlLength(result, length)
}

/* def static getEmployeeShortCode(GPathResult empJob, StringBuilder sb) {
    String empShortCode = ""
    def tomorrow = new Date().next()
    empJob.userNav.User.externalCodeOfcust_EmpShortCodeNav.children().each() { it ->
        if (i == 0) {
            mdfSystemEffectiveEndDate = getDateResult(it.mdfSystemEffectiveEndDate)
            if (tomorrow < mdfSystemEffectiveEndDate)
                empShortCode = getStringResult(it.cust_EmpShortCode, 999)
        } else {
            esd = getDateResult(it.effectiveStartDate)
            eed = getDateResult(it.effectiveEndDate)
            if (esd != null && eed != null)
                if (esd < tomorrow && tomorrow < eed)
                    empShortCode = getStringResult(it.cust_EmpShortCode, 999)

        }
        i++
    }
    return empShortCode
} */

def static getTitb(cust_Signature_Authorization, String company, StringBuilder sb) {
    if (cust_Signature_Authorization.size() < 1) {
        sb.append("getTitb: has no input elements" + "\n")
        return ""
    }
    def today = new Date()

    def items = cust_Signature_Authorization.cust_SignatureAuthorization.cust_Signature_Authorization_Details.findAll {
        it -> ((parseDate(it.cust_validfrom) <= today) && (it.cust_LegalEntity.text().trim() == company.trim()) && (today <= parseDateMax(it.cust_AuthorityLimitedTo)))
    }

    if (items.size() > 1) {
        sb.append("getTitb: has more input elements" + "\n")
        return controlLength(items[items.size() - 1].cust_TypeOfAuthority.text(), 50)
    }
    if (items.size() == 1)
        return controlLength(items[items.size() - 1].cust_TypeOfAuthority.text(), 50)

    sb.append("getTitb: has no active elements" + "\n")
    return ""
}

def static getFutureKennung(GPathResult externalCodeOfcust_EmpShortCodeNav, StringBuilder sb) {
    if (externalCodeOfcust_EmpShortCodeNav.size() < 1) {
        sb.append("getKennung: has no active elements\n")
        return ""
    }

    def today = new Date()
    def cust_EmpShortCode = externalCodeOfcust_EmpShortCodeNav.cust_EmpShortCode.findAll {
        it -> (today < parseDate(it.effectiveStartDate) && parseDate(it.effectiveStartDate) < parseDateMax(it.mdfSystemEffectiveEndDate))
    }

    if (cust_EmpShortCode.size() > 1) {
        sb.append("getKennung: has more active elements\n")
        return controlLength(cust_EmpShortCode[cust_EmpShortCode.size() - 1].cust_EmpShortCode.text(), 10)
    }
    if (cust_EmpShortCode.size() == 1)
        return controlLength(cust_EmpShortCode[cust_EmpShortCode.size() - 1].cust_EmpShortCode.text(), 10)

    sb.append("getKennung: has no active elements\n")
    return ""
}

def static getKennung(GPathResult externalCodeOfcust_EmpShortCodeNav, StringBuilder sb, TimeLine timeLine) {
    if (externalCodeOfcust_EmpShortCodeNav.size() < 1) {
        sb.append("getKennung: has no active elements\n")
        return ""
    }

    def today = new Date()
    def cust_EmpShortCode = externalCodeOfcust_EmpShortCodeNav.cust_EmpShortCode.findAll {
        it -> (parseDate(it.effectiveStartDate) <= today && today <= parseDate(it.mdfSystemEffectiveEndDate))
    }

    if (cust_EmpShortCode.size() > 1) {
        sb.append("getKennung: has more active elements\n")
        return controlLength(cust_EmpShortCode[cust_EmpShortCode.size() - 1].cust_EmpShortCode.text(), 10)
    }
    if (cust_EmpShortCode.size() == 1)
        return controlLength(cust_EmpShortCode[cust_EmpShortCode.size() - 1].cust_EmpShortCode.text(), 10)

    sb.append("getKennung: has no active elements\n")
    return ""
}

def static getCurrentKennung(GPathResult externalCodeOfcust_EmpShortCodeNav, StringBuilder sb) {
    if (externalCodeOfcust_EmpShortCodeNav.size() < 1) {
        sb.append("getKennung: has no active elements\n")
        return ""
    }

    def today = new Date()
    def cust_EmpShortCode = externalCodeOfcust_EmpShortCodeNav.cust_EmpShortCode.findAll {
        it -> (parseDate(it.effectiveStartDate) <= today && today <= parseDate(it.mdfSystemEffectiveEndDate))
    }

    if (cust_EmpShortCode.size() > 1) {
        sb.append("getKennung: has more active elements\n")
        return controlLength(cust_EmpShortCode[cust_EmpShortCode.size() - 1].cust_EmpShortCode.text(), 10)
    }
    if (cust_EmpShortCode.size() == 1)
        return controlLength(cust_EmpShortCode[cust_EmpShortCode.size() - 1].cust_EmpShortCode.text(), 10)

    sb.append("getKennung: has no active elements\n")
    return ""
}

def static getPastKennung(GPathResult externalCodeOfcust_EmpShortCodeNav, StringBuilder sb) {
    if (externalCodeOfcust_EmpShortCodeNav.size() < 1) {
        sb.append("getKennung: has no active elements\n")
        return ""
    }

    def today = new Date()
    def cust_EmpShortCode = externalCodeOfcust_EmpShortCodeNav.cust_EmpShortCode.findAll {
        it -> (parseDate(it.mdfSystemEffectiveEndDate) <= today)
    }

    if (cust_EmpShortCode.size() > 1) {
        sb.append("getKennung: has more active elements\n")
        return controlLength(cust_EmpShortCode[cust_EmpShortCode.size() - 1].cust_EmpShortCode.text(), 10)
    }
    if (cust_EmpShortCode.size() == 1)
        return controlLength(cust_EmpShortCode[cust_EmpShortCode.size() - 1].cust_EmpShortCode.text(), 10)

    sb.append("getKennung: has no active elements\n")
    return ""
}

def getEmpTimesFuture(GPathResult empJob, StringBuilder sb) {
    empTimesFuture = empJob.userNav.User.userIdOfEmployeeTimeNav.EmployeeTime.findAll {
        it -> (it.approvalStatus.text() == "APPROVED" && today < getDateResult(it.startDate, "startDate", sb))
    }
    if (empTimesFuture.size() == 0)
        return null
    if (empTimesFuture.size() == 1)
        return empTimesFuture

    earliestStartDate = Date.parse("yyyy-MM-dd", "9999-12-31")
    empTimesFuture.eachWithIndex { item, i ->
        startDate = parseDate(item.startDate)
        if (startDate < earliestStartDate) {
            earliestStartDate = startDate
            index = i
        }
    }

    return empTimesFuture[index]
}

def processEmpTimesFuture(empJob, sb) {
    def contractStatus = [:]

    //EDC:
    find = empJob.findAll { it ->
        (it.timeType.text() == "27" && getBooleanResult(it.cust_KeepAccess, "cust_KeepAccess", sb))
    }
    if (find.size() > 0) {
        contractStatus.put(ContractStatus.ContractStatusNew, "EDC")
        contractStatus.put(ContractStatus.ContractStatusNewFrom, find[0].startDate.text().substring(0, 10))
        return contractStatus
    }

    //EDU:
    find = empJob.findAll {
        it -> (it.timeType.text() == "27" && !getBooleanResult(it.cust_KeepAccess, "cust_KeepAccess", sb))
    }
    if (find.size() > 0) {
        contractStatus.put(ContractStatus.ContractStatusNew, "EDU")
        contractStatus.put(ContractStatus.ContractStatusNewFrom, find[0].startDate.text().substring(0, 10))
        return contractStatus
    }
    find = empJob.findAll {
        it -> (it.timeType.text() == "27" && !getBooleanResult(it.cust_KeepAccess, "cust_KeepAccess", sb))
    }
    if (find.size() > 0) {
        contractStatus.put(ContractStatus.ContractStatusNew, "EDU")
        contractStatus.put(ContractStatus.ContractStatusNewFrom, find[0].startDate.text().substring(0, 10))
        return contractStatus
    }

    //IAC:
    find = empJob.findAll {
        it -> ((it.timeType.text() == "6" || it.timeType.text() == "13" || it.timeType.text() == "14" || it.timeType.text() == "15" || it.timeType.text() == "16" || it.timeType.text() == "18" || it.timeType.text() == "45" || it.timeType.text() == "74") && getBooleanResult(it.cust_KeepAccess, "cust_KeepAccess", sb))
    }
    if (find.size() > 0) {
        contractStatus.put(ContractStatus.ContractStatusNew, "IAC")
        contractStatus.put(ContractStatus.ContractStatusNewFrom, find[0].startDate.text().substring(0, 10))
        return contractStatus
    }

    //IAM:
    find = empJob.findAll {
        it -> ((it.timeType.text() == "6" || it.timeType.text() == "13" || it.timeType.text() == "14" || it.timeType.text() == "15" || it.timeType.text() == "16" || it.timeType.text() == "18" || it.timeType.text() == "45" || it.timeType.text() == "74") && !getBooleanResult(it.cust_KeepAccess, "cust_KeepAccess", sb))
    }
    if (find.size() > 0) {
        contractStatus.put(ContractStatus.ContractStatusNew, "IAM")
        contractStatus.put(ContractStatus.ContractStatusNewFrom, find[0].startDate.text().substring(0, 10))
        return contractStatus
    }

    find = empJob.findAll {
        it -> (it.timeType.text() == "19")
    }
    if (find.size() > 0) {
        contractStatus.put(ContractStatus.ContractStatusNew, "IAM")
        contractStatus.put(ContractStatus.ContractStatusNewFrom, find[0].startDate.text().substring(0, 10))
        return contractStatus
    }
}

def getContractStatus(GPathResult empJob, StringBuilder sb) {
    def today = new Date()
    def cs_endDate = new Date()
    def contractStatus = [:]

    if (empJob.userId.text().length() < 1) {
        sb.append("getContractStatus: no EmpJob information\n")
        return contractStatus
    }

    def startDate = parseDate(empJob.employmentNav.EmpEmployment.startDate)
    def endDate = parseDateMax(empJob.employmentNav.EmpEmployment.endDate)

    if (today < startDate) {
        contractStatus.put(ContractStatus.ContractStatus, "NEW")
        sb.append("getContractStatus: startDate is in the future")
        return contractStatus
    }

    if (startDate <= today && endDate <= today) {
        contractStatus.put(ContractStatus.ContractStatus, "TRM")
        sb.append("getContractStatus: startDate and endDate are both in the past")
        return contractStatus
    }

    empStatus = getStringResult(empJob.emplStatusNav.PicklistOption.externalCode, "empStatus", 999, sb)
    reportingCategory = getStringResult(empJob.customString15Nav.PicklistOption.externalCode, "reportingCategory", 999, sb)

    empTimesFuture = getEmpTimesFuture(empJob, sb)

    if (endDate != Date.parse("yyyy-MM-dd", "9999-12-31")) {
        contractStatus.put(ContractStatus.ContractStatusNew, "TRM")
        contractStatus.put(ContractStatus.ContractStatusNewFrom, (endDate + 1).format("yyyy-MM-dd").toString())
    }

    def empTimesCurrent = empJob.userNav.User.userIdOfEmployeeTimeNav.EmployeeTime.findAll {
        it -> (it.approvalStatus.text() == "APPROVED" && (getDateResult(it.startDate, "startDate", sb) <= today) && (today <= getDateResult(it.endDate, "endDate", sb)))
    }

    //EDC:
    find = empTimesCurrent.findAll { it ->
        (it.timeType.text() == "27" && getBooleanResult(it.cust_KeepAccess, "cust_KeepAccess", sb))
    }
    if (find.size() > 0) {
        contractStatus.put(ContractStatus.ContractStatus, "EDC")
        cs_endDate = parseDateMax(find[0].endDate)
    }

    //EDU:
    find = empTimesCurrent.findAll {
        it -> (it.timeType.text() == "27" && !getBooleanResult(it.cust_KeepAccess, "cust_KeepAccess", sb))
    }
    if (find.size() > 0) {
        contractStatus.put(ContractStatus.ContractStatus, "EDU")
        cs_endDate = parseDateMax(find[0].endDate)
    }
    find = empTimesCurrent.findAll {
        it -> (it.timeType.text() == "27" && !getBooleanResult(it.cust_KeepAccess, "cust_KeepAccess", sb))
    }
    if (find.size() > 0) {
        contractStatus.put(ContractStatus.ContractStatus, "EDU")
        cs_endDate = parseDateMax(find[0].endDate)
    }

    //IAC:
    find = empTimesCurrent.findAll {
        it -> ((it.timeType.text() == "6" || it.timeType.text() == "13" || it.timeType.text() == "14" || it.timeType.text() == "15" || it.timeType.text() == "16" || it.timeType.text() == "18" || it.timeType.text() == "45" || it.timeType.text() == "74") && getBooleanResult(it.cust_KeepAccess, "cust_KeepAccess", sb))
    }
    if (find.size() > 0) {
        contractStatus.put(ContractStatus.ContractStatus, "IAC")
        cs_endDate = parseDateMax(find[0].endDate)
    }

    //IAM:
    find = empTimesCurrent.findAll {
        it -> ((it.timeType.text() == "6" || it.timeType.text() == "13" || it.timeType.text() == "14" || it.timeType.text() == "15" || it.timeType.text() == "16" || it.timeType.text() == "18" || it.timeType.text() == "45" || it.timeType.text() == "74") && !getBooleanResult(it.cust_KeepAccess, "cust_KeepAccess", sb))
    }
    if (find.size() > 0) {
        contractStatus.put(ContractStatus.ContractStatus, "IAM")
        cs_endDate = parseDateMax(find[0].endDate)
    }

    find = empTimesCurrent.findAll {
        it -> (it.timeType.text() == "19")
    }
    if (find.size() > 0) {
        contractStatus.put(ContractStatus.ContractStatus, "IAM")
        cs_endDate = parseDateMax(find[0].endDate)
    }

    if (contractStatus.get(ContractStatus.ContractStatus) != null && endDate == Date.parse("yyyy-MM-dd", "9999-12-31")) {
        contractStatus.put(ContractStatus.ContractStatusNew, "ACT")
        contractStatus.put(ContractStatus.ContractStatusNewFrom, (cs_endDate + 1).format("yyyy-MM-dd").toString())
        return contractStatus
    } else if (contractStatus.get(ContractStatus.ContractStatus) != null) {
        contractStatus.put(ContractStatus.ContractStatusNew, "TRM")
        contractStatus.put(ContractStatus.ContractStatusNewFrom, (cs_endDate + 1).format("yyyy-MM-dd").toString())
        return contractStatus
    }

    //ACT:
    if (empStatus == "A" && !(reportingCategory == "3" || reportingCategory == "12")) {
        contractStatus.put(ContractStatus.ContractStatus, "ACT")
    }

    //EXC:
    if (reportingCategory == "3" && (empStatus == "A" || empStatus == "D")) {
        contractStatus.put(ContractStatus.ContractStatus, "EXC")
    }

    //EXP:
    if (reportingCategory == "12" && (empStatus == "A" || empStatus == "D")) {
        contractStatus.put(ContractStatus.ContractStatus, "EXP")
    }
    if (contractStatus.get(ContractStatus.ContractStatus) == null)
        sb.append("getContractStatus: no contractStatus selected")

    return contractStatus
}

def static getStringResult(GPathResult value, String field, int length, StringBuilder sb) {
    if (value.size() == 1)
        return controlLength(parseString(value), length)

    if (value.size() > 1)
        sb.append(field + ": has more input elements" + "\n")

    if (value.size() < 1)
        sb.append(field + ": has no input element" + "\n")

    return ""
}

def getDateResult(GPathResult value, String field, StringBuilder sb) {
    date_string = getStringResult(value, field, 999, sb)
    try {
        return Date.parse("yyyy-MM-dd'T'HH:mm:ss.sss", date_string)
    } catch (Exception e) {
        sb.append(field + ", with value: " + date_string + " could not be cast to date" + "\n")
        return ""
    }
}

def getDateResultToString(GPathResult value, String field, String outputFormat, StringBuilder sb) {
    if (value.size() > 1) {
        sb.append(field + ": has more input elements" + "\n")
        return ""
    }
    if (value.size() < 1) {
        sb.append(field + ": has no input element" + "\n")
        return ""
    }

    date_string = getStringResult(value, field, 999, sb)
    try {
        return Date.parse("yyyy-MM-dd'T'HH:mm:ss.sss", date_string).format(outputFormat).toString()
    } catch (Exception e) {
        sb.append(field + ", with value: " + date_string + " could not be cast to date" + "\n")
        return ""
    }
}

def parseEmpJob(actual, future, StringBuilder sb, TimeLine timeLine) {
    line = new Line()
    def today = new Date()
    int maxLength = 999

    boolean isContingetWorker = getBooleanResult(actual.employmentNav.EmpEmployment.isContingentWorker, "isContingentWorker", sb)
    if (isContingetWorker)
        sb.append("isContingetWorker\n")

    company = getStringResult(actual.company, "company", maxLength, sb)

    line.ins = getIns(actual.customString4, isContingetWorker, actual.userNav.User.userSysIdOfWorkOrderNav.WorkOrder[0].vendorNav.VendorInfo.vendorCode, sb)
    line.mit_nr = getStringResult(actual.employmentNav.EmpEmployment.personIdExternal, "mit_nr", 6, sb)
    line.org = getStringResult(actual.customString2, "org", 10, sb)
    line.org_ins_nr = getStringResult(actual.customString2Nav.FODepartment.cust_level1Nav.FODepartment.cust_company, "org_ins_nr", maxLength, sb)
    line.mit_zname = getStringResult(actual.userNav.User.lastName, "mit_zname", 50, sb).toUpperCase()
    line.mit_vname = getStringResult(actual.userNav.User.firstName, "mit_vname", 50, sb)
    line.mit_gebdat = getDateResultToString(actual.employmentNav.EmpEmployment.personNav.PerPerson.dateOfBirth, "mit_gebdat", "yyyy-MM-dd", sb)
    line.mit_geburtsort = getStringResult(actual.employmentNav.EmpEmployment.personNav.PerPerson.placeOfBirth, "mit_geburtsort", 100, sb)

    value = getTitb(actual.userNav.User.externalCodeOfcust_Signature_AuthorizationNav.cust_Signature_Authorization, company, sb)
    line.titb = value
    line.emp_sig_authorization = value

    titf = getStringResult(actual.positionNav.Position.cust_BoardLevel, "titf", maxLength, sb)
    if (titf.startsWith("B"))
        line.titf = titf
    else
        line.titf = ""

    def tita = actual.employmentNav.EmpEmployment.personNav.PerPerson.personalInfoNav.PerPersonal.titleNav.PicklistOption.picklistLabels.PicklistLabel.findAll { tita ->
        (tita.locale.text() == "en_US")
    }
    if (tita.size() > 1)
        sb.append("tita: has more active values" + "\n")

    line.tita = tita.label

    value = getKennung(actual.userNav.User.externalCodeOfcust_EmpShortCodeNav, sb, timeLine)
    if (value != null && value.length() > 3)
        line.mit_bz = value.substring(3)
    else
        line.mit_bz = ""

    if (value != null)
        line.mit_kennung = value

    line.mit_eintritt = getDateResultToString(actual.employmentNav.EmpEmployment.startDate, "mit_eintritt", "yyyy-MM-dd", sb)
    value = getStringResult(actual.employmentNav.EmpEmployment.endDate, "mit_austritt", maxLength, sb)
    if (value.length() == 23) {
        date_value = Date.parse("yyyy-MM-dd'T'HH:mm:ss.sss", value)
        if (date_value - today <= 60)
            line.mit_austritt = date_value.format('yyyy-MM-dd').toString()
    } else
        line.mit_austritt = ""

    line.mit_geheim = ""

    def empTimeMeternity = actual.userNav.User.userIdOfEmployeeTimeNav.EmployeeTime.findAll {
        mt ->
            (
                    mt.approvalStatus.text() == "APPROVED" && today <= parseDate(mt.startDate) + 1 && (mt.timeType.text() == "13" || mt.timeType.text() == "14" || mt.timeType.text() == "15" || mt.timeType.text() == "45")
            )
    }
    if (empTimeMeternity.size() > 1)
        sb.append("mit_begruhe / mit_endruhe: has more active values" + "\n")
    if (empTimeMeternity.size() < 1)
        sb.append("mit_begruhe / mit_endruhe: has no active values" + "\n")

    if (empTimeMeternity.size() == 1) {
        endTimeMat = getDateResult(empTimeMeternity.endDate, "endDate", sb)
        if (today < endTimeMat) {
            line.mit_begruhe = getDateResultToString(empTimeMeternity.startDate, "mit_begruhe", "yyyy-MM-dd", sb)
            line.mit_endruhe = getDateResultToString(empTimeMeternity.endDate, "mit_endruhe", "yyyy-MM-dd", sb)
        } else {
            line.mit_begruhe = ""
            line.mit_endruhe = ""
        }
    }

    line.mit_geschl = getStringResult(actual.employmentNav.EmpEmployment.personNav.PerPerson.personalInfoNav.PerPersonal.gender, "mit_geschl", 1, sb)

    if (!isContingetWorker)
        line.mit_art = getStringResult(actual.employeeClassNav.PicklistOption.externalCode, "mit_art1", 50, sb)
    else
        line.mit_art = getStringResult(actual.userNav.User.userSysIdOfWorkOrderNav.WorkOrder[0].workerType, "mit_art2", 50, sb)

    line.mit_brwart = ""
    line.mit_hiwart = ""
    line.mit_brkurs = ""
    line.mit_hikurs = ""
    line.source = "M"
    line.mit_host_kennung = ""
    line.mit_host_bez = ""

    telvz = getStringResult(actual.customString6Nav.PicklistOption.externalCode, "telvz", maxLength, sb)
    if (telvz.equalsIgnoreCase("Y"))
        telvz = "J"
    else
        telvz = "N"
    line.mit_telvz = telvz

    line.emp_phone_nr = ""
    line.emp_mobile = ""
    line.emp_room_nr = ""
    line.emp_phone_ext = ""
    line.emp_afa_dest = ""
    line.emp_fullname_notes = ""
    line.emp_fullname_notes_abbreviate = ""
    line.emp_mailserver_notes = ""
    line.emp_mailfile_notes = ""
    line.emp_fax = ""
    line.emp_email = ""

    if (!isContingetWorker)
        line.INST_Name1 = getStringResult(actual.customString4Nav.FOCompany.cust_INST_Name1, "INST_Name1", 100, sb)
    else
        line.INST_Name1 = getStringResult(actual.userNav.User.userSysIdOfWorkOrderNav.WorkOrder[0].vendorNav.VendorInfo.vendorName, "INST_Name1", 100, sb)


    if (!isContingetWorker)
        line.INST_Name2 = getStringResult(actual.customString4Nav.FOCompany.name_en_US, "INST_Name2", 50, sb)
    else
        line.INST_Name2 = getStringResult(actual.userNav.User.userSysIdOfWorkOrderNav.WorkOrder[0].vendorNav.VendorInfo.vendorName, "INST_Name2", 50, sb)

    if (!isContingetWorker) {
        value = getStringResult(actual.customString4Nav.FOCompany.name_en_US, "INST_Name3_1", 100, sb)
        if (value.length() > 50)
            value = value.substring(50, value.length())
        else
            value = ""
        line.INST_Name3 = value
    } else {
        value = getStringResult(actual.userNav.User.userSysIdOfWorkOrderNav.WorkOrder[0].vendorNav.VendorInfo.vendorName, "INST_Name3_2", 100, sb)
        if (value.length() > 50)
            value = value.substring(50, value.length())
        else
            value = ""
        line.INST_Name3 = value
    }

    line.Kantinenabrechnung = ""
    line.org_neu_ins = ""
    line.org_neu = ""
    line.org_neu_from = ""

    def contrastStatus = getContractStatus(actual, sb)
    line.contract_status = contrastStatus.get(ContractStatus.ContractStatus)
    line.contract_status_new = contrastStatus.get(ContractStatus.ContractStatusNew)
    line.contract_status_new_from = contrastStatus.get(ContractStatus.ContractStatusNewFrom)

    line.vaz_ssnr = getStringResult(actual.customString19, "vaz_ssnr", 10, sb)

    value = getStringResult(actual.costCenterNav.FOCostCenter.costcenterExternalObjectID, "costcenterExternalObjectID", maxLength, sb)
    if (value.length() > 5)
        line.costcenter_ins_nr = value.substring(0, 5)
    else
        line.costcenter_ins_nr = value

    if (value.length() > 6)
        line.costcenter_nr = value.substring(5).replaceFirst("^0*", "")
    else
        line.costcenter_nr = ""

    line.costcenter_dec = getStringResult(actual.costCenterNav.FOCostCenter.name_en_US, "costcenter_dec", 100, sb)
    line.bc = ""
    line.account_nr = ""
    line.bic = getStringResult(actual.userNav.User.externalCodeOfcust_bankinfoNav.cust_bankinfo.cust_BIC, "bic", 11, sb)
    line.iban = getStringResult(actual.userNav.User.externalCodeOfcust_bankinfoNav.cust_bankinfo.cust_IBAN, "iban", 34, sb)
    line.mit_art_new = ""
    line.mit_art_new_from = ""
    line.pers_adresse = ""
    line.pers_plz = ""
    line.pers_ort = ""
    line.emp_board_level = getStringResult(actual.positionNav.Position.cust_BoardLevel, "emp_board_level", maxLength, sb)

    def ct = actual.customString28Nav.PicklistOption.picklistLabels.PicklistLabel.findAll() {
        item -> (item.locale == "en_US")
    }
    if (ct.size() > 0)
        line.emp_corporate_title = getStringResult(ct.label, "emp_corporate_title", 60, sb)

    line.emp_job_title = getStringResult(actual.jobCodeNav.FOJobCode.name_en_US, "emp_job_title", 60, sb)

    emplStatus = getStringResult(actual.employmentNav.EmpEmployment.externalCode, "emplStatus", maxLength, sb)

    return line
}

class Line {
    String ins;
    String mit_nr;
    String seq_nr;
    String org;
    String org_ins_nr;
    String mit_zname;
    String mit_vname;
    String mit_gebdat;
    String mit_geburtsort;
    String titb;
    String titf;
    String tita;
    String mit_bz;
    String mit_kennung;
    String mit_eintritt;
    String mit_austritt;
    String mit_geheim;
    String mit_begruhe;
    String mit_endruhe;
    String mit_geschl;
    String mit_art;
    String mit_brwart;
    String mit_hiwart;
    String mit_brkurs;
    String mit_hikurs;
    String source;
    String mit_host_kennung;
    String mit_host_bez;
    String mit_telvz;
    String emp_phone_nr;
    String emp_mobile;
    String emp_room_nr;
    String emp_phone_ext;
    String emp_afa_dest;
    String emp_fullname_notes;
    String emp_fullname_notes_abbreviate;
    String emp_mailserver_notes;
    String emp_mailfile_notes;
    String emp_fax;
    String emp_email;
    String INST_Name1;
    String INST_Name2;
    String INST_Name3;
    String Kantinenabrechnung;
    String org_neu_ins;
    String org_neu;
    String org_neu_from;
    String contract_status;
    String contract_status_new;
    String contract_status_new_from;
    String vaz_ssnr;
    String costcenter_ins_nr;
    String costcenter_nr;
    String costcenter_dec;
    String bc;
    String account_nr;
    String bic;
    String iban;
    String mit_art_new;
    String mit_art_new_from;
    String pers_adresse;
    String pers_plz;
    String pers_ort
    String emp_board_level;
    String emp_sig_authorization;
    String emp_corporate_title;
    String emp_job_title;
}

//def Message processData(Message message) {

//    def empJobs = new XmlSlurper().parse(message.getBody(java.io.Reader));
def empJobs = new XmlSlurper().parse(new File("input_script11.xml"))
def writer = new StringWriter()
def builder = new MarkupBuilder(writer)

def lines = new ArrayList<Line>()
StringBuilder sb = new StringBuilder()
def minUserId = 0
def maxUserId = 0
def TimeLine timeLine


empJobs.children().eachWithIndex { it, index ->
    if (index == 0)
        minUserId = it.UserId
    println it.UserId.text()
    sb.append("--------------------------------\n")
    sb.append("UserId: " + it.UserId + "\n")
    past = it.Past.EmpJob.size()
    current = it.Current.EmpJob.size()
    future = it.Future.EmpJob.size()
    sb.append("Past: " + past + " Current: " + current + " Future: " + future + "\n")
    validEmpJob = null

    if (past > 0 && current == 0 && future > 0) {
        timeLine = TimeLine.FUTURE
        validEmpJob = findPastAndFutureEmpJob(it.Past, it.Future, sb, timeLine)
    }

    if (past > 0 && current == 0 && future == 0) {
        validEmpJob = findLattestEmpJob(it.Past, sb)
        timeLine = TimeLine.PAST
    }

    if (current > 0) {
        validEmpJob = findActualEmpJob(it.Current, sb)
        timeLine = TimeLine.CURRENT
    }

    if (past == 0 && current == 0 && future > 0) {
        validEmpJob = findEarliestEmpJob(it.Future, sb)
        timeLine = TimeLine.FUTURE
    }

    if (validEmpJob != null) {
        line = parseEmpJob(validEmpJob, it.Future, sb, timeLine)
        lines.add(line)
    }

    maxUserId = it.UserId
}

builder.CILEmpExport {
    for (Line l : lines) {
        'Item' {
            'ins' l.ins
            'mit_nr' l.mit_nr
            'org' l.org
            'org_ins_nr' l.org_ins_nr
            'mit_zname' l.mit_zname
            'mit_vname' l.mit_vname
            'mit_gebdat' l.mit_gebdat
            'mit_geburtsort' l.mit_geburtsort
            'titb' l.titb
            'titf' l.titf
            'tita' l.tita
            'mit_bz' l.mit_bz
            'mit_kennung' l.mit_kennung
            'mit_eintritt' l.mit_eintritt
            'mit_austritt' l.mit_austritt
            'mit_geheim' l.mit_geheim
            'mit_begruhe' l.mit_begruhe
            'mit_endgruhe' l.mit_endruhe
            'mit_geschl' l.mit_geschl
            'mit_art' l.mit_art
            'mit_brwart' l.mit_brwart
            'mit_hiwart' l.mit_hiwart
            'mit_brkurs' l.mit_brkurs
            'mit_hikurs' l.mit_hikurs
            'source' l.source
            'mit_host_kennung' l.mit_host_kennung
            'mit_host_bez' l.mit_host_bez
            'mit_telvz' l.mit_telvz
            'emp_phone_nr' l.emp_phone_nr
            'emp_mobile' l.emp_mobile
            'emp_room_nr' l.emp_room_nr
            'emp_phone_ext' l.emp_phone_ext
            'emp_afa_dest' l.emp_afa_dest
            'emp_fullname_notes' l.emp_fullname_notes
            'emp_fullname_notes_abbreviate' l.emp_fullname_notes_abbreviate
            'emp_mailserver_notes' l.emp_mailserver_notes
            'emp_mailfile_notes' l.emp_mailfile_notes
            'emp_fax' l.emp_fax
            'emp_email' l.emp_email
            'INST-Name1' l.INST_Name1
            'INST-Name2' l.INST_Name2
            'INST-Name3' l.INST_Name3
            'Kantinenabrechnung' l.Kantinenabrechnung
            'org_neu_ins' l.org_neu_ins
            'org_neu' l.org_neu
            'org_neu_from' l.org_neu_from
            'contract_status' l.contract_status
            'contract_status_new' l.contract_status_new
            'contract_status_new_from' l.contract_status_new_from
            'vaz_ssnr' l.vaz_ssnr
            'costcenter_ins_nr' l.costcenter_ins_nr
            'costcenter_nr' l.costcenter_nr
            'costcenter_dec' l.costcenter_dec
            'bc' l.bc
            'account_nr' l.account_nr
            'BIC' l.bic
            'IBAN' l.iban
            'mit_art_new' l.mit_art_new
            'mit_art_new_from' l.mit_art_new_from
            'pers_adresse' l.pers_adresse
            'pers_plz' l.pers_plz
            'pers_ort' l.pers_ort
            'emp_board_level' l.emp_board_level
            'emp_sig_authorization' l.emp_sig_authorization
            'emp_corporate_title' l.emp_corporate_title
            'emp_job_title' l.emp_job_title
        }
    }
}

println sb.toString()
println writer.toString()
new File("output_script11_old.xml").delete()
new File("output_script11.xml").renameTo("output_script11_old.xml")
new FileWriter("output_script11.xml").with {
    write(writer.toString())
    flush()
}


//    map = message.getProperties();
//    def counter = map.get("p_counter");
//    def messageLog = messageLogFactory.getMessageLog(message);
//    if(messageLog != null){
//        messageLog.addAttachmentAsString("Logging after mapping from: " + minUserId + " to: " + maxUserId, sb.toString(), "text/plain");
//    }

//    message.setBody(XmlUtil.serialize(writer.toString()));
//    return message;
//}

def findLattestEmpJob(NodeChildren empJobs, sb) {
    today = new Date()
    lattestEnd = Date.parse("yyyy-MM-dd", "9999-12-31")
    index = -1

    def findLimit = empJobs.EmpJob.findAll { item ->
        (today - parseDate(item.employmentNav.EmpEmployment.endDate) <= 60)
    }

    if (findLimit.size() == 0) {
        sb.append("findLattestEmpJob: does not return any value\n")
        return null
    }

    if (findLimit.size() == 1)
        return findLimit

    findLimit.eachWithIndex { item, int i ->
        endDate = parseDate(item.endDate)
        if (endDate < lattestEnd) {
            lattestEnd = endDate
            index = i
        }
    }
    sb.append("findLattestEmpJob: iterate over several active records, select index: " + index + "\n")
    return findLimit[index]
}

def findActualEmpJob(NodeChildren current, sb) {
    today = new Date()
    index = -1
    earliestStart = Date.parse("yyyy-MM-dd", "1970-01-01")

    def findLimit = current.EmpJob.findAll { item ->
        (parseDate(item.employmentNav.EmpEmployment.startDate) <= today && today <= parseDateMax(item.employmentNav.EmpEmployment.endDate) + 1 &&
                parseString(item.emplStatusNav.PicklistOption.externalCode) == "A" && parseDate(item.startDate) <= today && today <= parseDateMax(item.endDate) + 1)
    }

    if (findLimit.size() == 1) {
        sb.append("findActualEmpJob: One Active EmpJob found within timeperiod found\n")
        return findLimit
    }

    if (findLimit.size() > 1) {
        sb.append("findActualEmpJob: Several Active EmpJobs found, nothing selected\n")
        return null
    }

    findLimit = current.EmpJob.findAll { item ->
        (parseDate(item.employmentNav.EmpEmployment.startDate) <= today && today <= parseDateMax(item.employmentNav.EmpEmployment.endDate) + 1
                && parseDate(item.startDate) <= today && today <= parseDateMax(item.endDate) + 1
                && parseString(item.emplStatusNav.PicklistOption.externalCode) != "RNS")
    }

    if (findLimit.size() == 1) {
        sb.append("findActualEmpJob: No Active EmpJob found, one EmplStatus found\n")
        return findLimit;
    }

    if (findLimit.size() > 1) {
        findLimit.eachWithIndex { item, int i ->
            startDate = parseDate(item.startDate)
            if (earliestStart < startDate) {
                earliestStart = startDate
                index = i
            }
        }
        sb.append("findActualEmpJob: Several EmpJobs found, lattest startDate selected\n")
        return findLimit[index]
    }

    sb.append("findActualEmpJob: does not return any value\n")
    return null
}

def findEarliestEmpJob(NodeChildren empJobs, sb) {
    today = new Date()
    index = -1
    earliestStart = Date.parse("yyyy-MM-dd", "1970-01-01")

    def findLimit = empJobs.EmpJob.findAll { item ->
        (today - parseDate(item.employmentNav.EmpEmployment.startDate) <= 60)
    }

    if (findLimit.size() == 0) {
        sb.append("findEarliestEmpJob: does not return any value\n")
        return null
    }

    if (findLimit.size() == 1)
        return findLimit

    findLimit.eachWithIndex { item, int i ->
        startDate = parseDate(item.startDate)
        if (earliestStart < startDate) {
            earliestStart = startDate
            index = i
        }
    }
    sb.append("findEarliestEmpJob: iterate over several active records, select index: " + index + "\n")
    return findLimit[index]
}

def findPastAndFutureEmpJob(NodeChildren past, NodeChildren future, StringBuilder sb, TimeLine timeLine) {
    def today = new Date()
    lattest = findLattestEmpJob(past, sb)
    earliest = findEarliestEmpJob(future, sb)

    if (lattest != null)
        pastEndDate = parseDate(lattest.employmentNav.EmpEmployment.endDate)
    else
        pastEndDate = Date.parse("yyyy-MM-dd", "1970-01-01")

    if (earliest != null)
        futureStartDate = parseDate(earliest.employmentNav.EmpEmployment.startDate)
    else
        futureStartDate = Date.parse("yyyy-MM-dd", "9999-12-31")

    sb.append("findPastAndFutureEmpJob: pastEndDate: " + pastEndDate.format("yyyy-MM-dd") + " futureStartDate: " + futureStartDate.format("yyyy-MM-dd") + "\n")

    if ((futureStartDate - today) < (today - pastEndDate)) {
        sb.append("findPastAndFutureEmpJob: future selected")
        timeLine = TimeLine.FUTURE
        return earliest
    } else {
        sb.append("findPastAndFutureEmpJob: past selected")
        timeLine = TimeLine.PAST
        return lattest
    }
}