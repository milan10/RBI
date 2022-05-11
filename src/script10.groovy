import groovy.util.XmlSlurper
import groovy.xml.XmlUtil

def empJobs = new XmlSlurper().parse(new File("input_script10.xml"));


//Remove based on reporting Category
empJobs.EmpJob.findAll { it ->
    !(it.customString15Nav.PicklistOption.externalCode.text() == "1" ||
            it.customString15Nav.PicklistOption.externalCode.text() == "3" ||
            it.customString15Nav.PicklistOption.externalCode.text() == "4" ||
            it.customString15Nav.PicklistOption.externalCode.text() == "6" ||
            it.customString15Nav.PicklistOption.externalCode.text() == "8" ||
            it.customString15Nav.PicklistOption.externalCode.text() == "10" ||
            it.customString15Nav.PicklistOption.externalCode.text() == "11")
}.replaceNode {}


//Remove based on employeeStatus
empJobs.EmpJob.findAll { it ->
    !(it.emplStatusNav.PicklistOption.externalCode.text() == "A" ||
            it.emplStatusNav.PicklistOption.externalCode.text() == "D" ||
            it.emplStatusNav.PicklistOption.externalCode.text() == "P" ||
            it.emplStatusNav.PicklistOption.externalCode.text() == "U" ||
            it.emplStatusNav.PicklistOption.externalCode.text() == "T" ||
            it.emplStatusNav.PicklistOption.externalCode.text() == "RNS" ||
            it.emplStatusNav.PicklistOption.externalCode.text() == "R" ||
            it.emplStatusNav.PicklistOption.externalCode.size() == 0
    )
}.replaceNode {}


//Remove based on employeeType
empJobs.EmpJob.findAll { it ->
    (it.employeeTypeNav.PicklistOption.externalCode.text() == "SYS_ONLY" ||
            it.customString15Nav.PicklistOption.externalCode.text() == "PEN"
    )
}.replaceNode {}


//Remove based on EmployeeShortCode
empJobs.EmpJob.findAll { it ->
    (it.userNav.User.externalCodeOfcust_EmpShortCodeNav.cust_EmpShortCode.size() == 0)
}.replaceNode {}

println XmlUtil.serialize(empJobs);