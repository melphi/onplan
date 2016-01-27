namespace java com.onplan.client
namespace js com.onplan.client

include "domain.thrift"

service VirtualMachineServiceRemote {
    domain.VirtualMachineInfo getVirtualMachineInfo()
}
