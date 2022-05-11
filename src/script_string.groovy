enum ContractStatus2 {
    NEW, UPDATED, DELETED
}
def mymap = [:]
mymap.put(ContractStatus2.NEW, "Konecny")
println mymap.get(ContractStatus2.NEW)