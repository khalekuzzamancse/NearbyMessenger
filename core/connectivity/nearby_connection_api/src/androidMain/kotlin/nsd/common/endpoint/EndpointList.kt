package nsd.common.endpoint

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * - Store the list of endpoint as observable
 * - expose some manipulation method
 */

internal class EndpointList{
    private val _devices = MutableStateFlow<Set<EndPointInfo>>(emptySet())
     val endpoints=_devices.asStateFlow()
     fun add(endPointInfo: EndPointInfo) =_devices.update { devices -> devices + endPointInfo }
     fun remove(endpointId: String) = _devices.update { devices ->
            val temp = devices.toMutableSet()
            temp.removeIf { it.id == endpointId }
            temp
    }

     fun updateStatus(endpointId: String, newState: EndPointStatus)= _devices.update { devices ->
            devices.map {
                if (it.id == endpointId) it.copy(
                    status = newState
                ) else it
            }.toSet()
        }
     fun doesEndPointExits(endpointId: String) = _devices.value.find { it.id == endpointId } != null

}