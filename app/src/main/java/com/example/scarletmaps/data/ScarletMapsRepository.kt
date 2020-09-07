package com.example.scarletmaps.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.scarletmaps.data.local.DataUtils
import com.example.scarletmaps.data.models.NetworkStatus
import com.example.scarletmaps.data.models.Vehicle
import com.example.scarletmaps.data.models.arrival.Arrival
import com.example.scarletmaps.data.models.arrival.ArrivalDao
import com.example.scarletmaps.data.models.building.Building
import com.example.scarletmaps.data.models.building.BuildingDao
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.data.models.route.RouteDao
import com.example.scarletmaps.data.models.segment.Segment
import com.example.scarletmaps.data.models.segment.SegmentDao
import com.example.scarletmaps.data.models.stop.Stop
import com.example.scarletmaps.data.models.stop.StopDao
import com.example.scarletmaps.data.remote.ScarletMapsService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ScarletMapsRepository @Inject constructor(
    private val scarletmapsService: ScarletMapsService,
    private val dataUtils: DataUtils,
    private val routeDao: RouteDao,
    private val stopDao: StopDao,
    private val arrivalDao: ArrivalDao,
    private val buildingDao: BuildingDao,
    private val segmentDao: SegmentDao
) {

    private val routeListLoaded = MutableLiveData(true)
    private val stopListLoaded = MutableLiveData(true)
    private val routeArrivalsStatus: HashMap<Int, MutableLiveData<NetworkStatus>> = HashMap()

    init {
        validateBuildingList()
        validateStopList()
        validateRouteList()
    }

    fun getBuildingList(): LiveData<List<Building>> {
        validateBuildingList()
        return buildingDao.all()
    }

    fun getBuildingListImmediate(): List<Building> {
        return buildingDao.allImmediate()
    }


    fun getStop(id: Int): LiveData<Stop> {
        return stopDao.load(id)
    }

    private fun validateBuildingList() {
        if (dataUtils.shouldRefreshBuildingList()) {
            scarletmapsService.buildings().enqueue(object : Callback<List<Building>> {
                override fun onFailure(call: Call<List<Building>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<List<Building>>,
                    response: Response<List<Building>>
                ) {
                    val buildings = response.body()
                    if (buildings != null) {
                        for (building in response.body()!!) {
                            buildingDao.save(building)
                        }
                        dataUtils.setBuildingListUpdateTime()
                    }
                    Log.d("ADAMSKI", "building size: ${buildingDao.allImmediate().size}")
                }
            })
        }
    }

    fun getRouteSegments(route: Route) : LiveData<List<Segment>> {
        validateSegmentList()
        return segmentDao.getSelected(route.segments)
    }

    fun getRouteSegmentsImmediate(route: Route) : List<Segment> {
        validateSegmentList()
        return segmentDao.getSelectedImmediate(route.segments)
    }


    private fun validateSegmentList() {
        if (dataUtils.shouldRefreshSegmentList()) {
            scarletmapsService.segments().enqueue(object : Callback<List<Segment>> {
                override fun onResponse(call: Call<List<Segment>>, response: Response<List<Segment>>) {
                    val segments = response.body()
                    if (segments != null) {
                        for (segment in response.body()!!) {
                            segmentDao.save(segment)
                        }
                        dataUtils.setSegmentListUpdateTime()
                    }
                }

                override fun onFailure(call: Call<List<Segment>>, t: Throwable) {
                    Log.d("RETROFIT", t.message.toString())
                }
            })
        }
    }

    fun getRouteStops(route: Route) : LiveData<List<Stop>> {
        return stopDao.getSelected(route.stops)
    }

    fun getRouteList(): LiveData<List<Route>> {
        validateRouteList()
        return routeDao.getAll()
    }

    fun getRouteListImmediate(): ArrayList<Route> {
        return ArrayList(routeDao.getAllImmediate())
    }

    fun getRouteListStatus(): LiveData<Boolean> {
        return routeListLoaded
    }

    fun getRoute(id: Int): LiveData<Route> {
        return routeDao.getObservable(id)
    }

    fun getRouteImmediate(id: Int): Route {
        return routeDao.get(id)
    }

    fun getArrivalPair(route: Int, stop: Int): Arrival? {
        return arrivalDao.getPair(route, stop)
    }

    fun refreshRouteArrivals(id: Int) {
        if (!routeArrivalsStatus.containsKey(id)) {
            routeArrivalsStatus[id] = MutableLiveData(NetworkStatus.SUCCESS)
        }
        routeArrivalsStatus[id] = MutableLiveData(NetworkStatus.LOADING)
        scarletmapsService.routeArrivals(id).enqueue(object : Callback<List<Arrival>> {
            override fun onFailure(call: Call<List<Arrival>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Arrival>>, response: Response<List<Arrival>>) {
                val arrivals = response.body()
                if (arrivals != null) {
                    for (arrival in response.body()!!) {
                        arrivalDao.save(arrival)
                    }
                }
                routeArrivalsStatus[id] = MutableLiveData(NetworkStatus.SUCCESS)
            }
        })
    }

    fun refreshStopArrivals(id: Int) {
        scarletmapsService.stopArrivals(id).enqueue(object : Callback<List<Arrival>> {
            override fun onFailure(call: Call<List<Arrival>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Arrival>>, response: Response<List<Arrival>>) {
                val arrivals = response.body()
                if (arrivals != null) {
                    for (arrival in response.body()!!) {
                        arrivalDao.save(arrival)
                        Log.d("ADAMSKI", "SAVING ${arrival.route_id}, ${arrival.stop_id}")
                    }
                }
            }
        })
    }

    fun getRouteVehicles(id: Int, callback: Callback<List<Vehicle>>) {
        scarletmapsService.vehicles(id).enqueue(callback)
    }

    fun getRouteArrivals(id: Int): LiveData<List<Arrival>> {
        val route = routeDao.get(id)
        return arrivalDao.getSelected(route.id, route.stops)
    }

    fun getStopArrivals(id: Int): LiveData<List<Arrival>> {
        val stop = stopDao.loadImmediate(id)
        return arrivalDao.getStopRoutes(stop.id, stop.routes)
    }

    fun getRouteArrivalsStatus(route: Route): MutableLiveData<NetworkStatus>? {
        if (!routeArrivalsStatus.containsKey(route.id)) {
            routeArrivalsStatus[route.id] = MutableLiveData(NetworkStatus.SUCCESS)
        }

        return routeArrivalsStatus[route.id]
    }

    fun getRouteStops(id: Int): ArrayList<Stop> {
        val route = routeDao.get(id)
        return ArrayList(stopDao.getSelectedImmediate(route.stops))
    }

    fun getStopRoutes(id: Int): LiveData<List<Route>> {
        val stop = stopDao.loadImmediate(id)
        return routeDao.getSelected(stop.routes)
    }

    private fun validateRouteList() {
        if (dataUtils.shouldRefreshRouteList()) {
            routeListLoaded.value = false

            scarletmapsService.getRoutes().enqueue(object : Callback<List<Route>> {
                override fun onResponse(call: Call<List<Route>>, response: Response<List<Route>>) {
                    val routes = response.body()
                    if (routes != null) {
                        for (route in response.body()!!) {
                            routeDao.save(route)
                        }
                        dataUtils.setRouteListUpdateTime()
                    }
                    routeListLoaded.value = true
                }

                override fun onFailure(call: Call<List<Route>>, t: Throwable) {
                    routeListLoaded.value = true
                    Log.d("RETROFIT", t.message.toString())
                }
            })
        }
    }

    fun getStopList(): LiveData<List<Stop>> {
        validateStopList()
        return stopDao.getAll()
    }

    fun getStopListImmediate(): List<Stop> {
        validateStopList()
        return stopDao.getAllImmediate()
    }

    fun getStopListStatus(): LiveData<Boolean> {
        return stopListLoaded
    }

    private fun validateStopList() {
        if (dataUtils.shouldRefreshStopList()) {
            stopListLoaded.value = false

            scarletmapsService.getStops().enqueue(object : Callback<List<Stop>> {
                override fun onResponse(call: Call<List<Stop>>, response: Response<List<Stop>>) {
                    val body = response.body()
                    if (body != null) {
                        for (stop in body) {
                            stopDao.save(stop)
                        }

                        dataUtils.setStopListUpdateTime()
                        stopListLoaded.value = true
                    }
                }

                override fun onFailure(call: Call<List<Stop>>, t: Throwable) {
                    stopListLoaded.value = true
                    Log.d("RETROFIT", t.message.toString())
                }
            })
        }
    }
}

