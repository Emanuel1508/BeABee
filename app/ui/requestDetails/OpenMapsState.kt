package com.ibm.internship.beabee.ui.requestDetails

sealed class OpenMapsState {
    data object TryOpenMaps : OpenMapsState()
    data object OpenMaps : OpenMapsState()
    data object TryOpenMapInBrowserOrMarket : OpenMapsState()
    data object OpenMapInBrowserOrMarket : OpenMapsState()
    data object OpenMapsFailed : OpenMapsState()
}
