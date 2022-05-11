//import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.xml.XmlUtil
import java.util.HashMap;

//def Message processData(Message message) {
//    def body = message.getBody();
//    message.setBody(body + "Body is modified");

    //Properties
//    map = message.getProperties();
//    filtered_empJob = map.get("p_empJob");
//    lastMessage = map.get("p_exit") as boolean;

    lastMessage = false
    filtered_empJob = "  "
    def String lastM = "true"

    def payload = new XmlParser().parse(new File("input_script13.xml"));
    StringWriter stringWriter = new StringWriter()
    XmlNodePrinter nodePrinter = new XmlNodePrinter(new PrintWriter(stringWriter))
    def filtered = null


    if(!lastMessage) {
        int maxUserId = 0;
        def empJobs = payload.children().each { item ->
            userId = Integer.parseInt(item.employmentNav.EmpEmployment.personIdExternal.text())
            if (maxUserId < userId)
                maxUserId = userId
        }
        println maxUserId
        filtered = payload.EmpJob.findAll {
            it.employmentNav.EmpEmployment.personIdExternal.text() as int == maxUserId
        }
        println filtered.size()

        maxUserId.toString()

        StringBuilder sb = new StringBuilder();
        sb.append("<EmpJob>")
        for (item in filtered) {
            sb.append(XmlUtil.serialize(item).replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""))
        }
        sb.append("</EmpJob>")
        p_empJobTemp = sb.toString()
        payload.EmpJob
                .findAll { it.employmentNav.EmpEmployment.personIdExternal.text() as int == maxUserId }
                .each { payload.remove(it) }
        println payload.EmpJob.size()
    }

/*    if(filtered_empJob.length() > 0) {
        def filter = new XmlParser().parse(new File("input_script13_1.xml"));
        //payload.children().add(filter)
        payload.children().addAll(filtered)
    } */

    nodePrinter.setPreserveWhitespace(true)
    nodePrinter.print(payload)
    println stringWriter.toString()

//    message.setBody(stringWriter.toString());
//    return message;
//}