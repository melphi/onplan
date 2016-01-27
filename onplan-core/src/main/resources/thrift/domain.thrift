namespace java com.onplan.client.domain
namespace js onplan.client.domain

struct VirtualMachineInfo {
    1: optional i32 availableProcessors
    2: optional i64 maxMemory
    3: optional i64 totalMemory
    4: optional i64 freeMemory
    5: optional i64 collectionsCount
    6: optional double averageCollectionTime
}
