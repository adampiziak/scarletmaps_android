package com.example.scarletmaps.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.scarletmaps.data.local.DataUtils
import com.example.scarletmaps.data.models.NetworkStatus
import com.example.scarletmaps.data.models.arrival.Arrival
import com.example.scarletmaps.data.models.arrival.ArrivalDao
import com.example.scarletmaps.data.models.route.Route
import com.example.scarletmaps.data.models.route.RouteDao
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
    private val arrivalDao: ArrivalDao
) {

    private val routeListLoaded = MutableLiveData(true)
    private val stopListLoaded = MutableLiveData(true)
    private val routeArrivalsStatus: HashMap<Int, MutableLiveData<NetworkStatus>> = HashMap()

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

    fun refreshRouteArrivals(id: Int) {
        if (!routeArrivalsStatus.containsKey(id)) {
            routeArrivalsStatus[id] = MutableLiveData(NetworkStatus.SUCCESS)
        }
        routeArrivalsStatus[id] = MutableLiveData(NetworkStatus.LOADING)
        scarletmapsService.arrivals(id).enqueue(object : Callback<List<Arrival>> {
            override fun onFailure(call: Call<List<Arrival>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Arrival>>, response: Response<List<Arrival>>) {
                for (arrival in response.body()!!) {
                    arrivalDao.save(arrival)
                }
                routeArrivalsStatus[id] = MutableLiveData(NetworkStatus.SUCCESS)
            }

        })
    }

    fun getRouteArrivals(id: Int): LiveData<List<Arrival>> {
        val route = routeDao.get(id)
        return arrivalDao.getSelected(route.id, route.stops)
    }

    fun getRouteArrivalsStatus(route: Route): MutableLiveData<NetworkStatus>? {
        if (!routeArrivalsStatus.containsKey(route.id)) {
            routeArrivalsStatus[route.id] = MutableLiveData(NetworkStatus.SUCCESS)
        }

        return routeArrivalsStatus[route.id]
    }

    fun getRouteStops(id: Int): ArrayList<Stop> {
        val route = routeDao.get(id)
        return ArrayList(stopDao.getSelected(route.stops))
    }

    private fun validateRouteList() {
        if (dataUtils.shouldRefreshRouteList()) {
            routeListLoaded.value = false

            scarletmapsService.getRoutes().enqueue(object : Callback<List<Route>> {
                override fun onResponse(call: Call<List<Route>>, response: Response<List<Route>>) {
                    for (route in response.body()!!) {
                        routeDao.save(route)
                    }
                    dataUtils.setRouteListUpdateTime()
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

    fun getStopListStatus(): LiveData<Boolean> {
        return stopListLoaded
    }

    private fun validateStopList() {
        if (dataUtils.shouldRefreshStopList()) {
            stopListLoaded.value = false

            scarletmapsService.getStops().enqueue(object : Callback<List<Stop>> {
                override fun onResponse(call: Call<List<Stop>>, response: Response<List<Stop>>) {
                    for (stop in response.body()!!) {
                        stopDao.save(stop)
                    }

                    dataUtils.setStopListUpdateTime()
                    stopListLoaded.value = true
                }

                override fun onFailure(call: Call<List<Stop>>, t: Throwable) {
                    stopListLoaded.value = true
                    Log.d("RETROFIT", t.message.toString())
                }
            })
        }
    }
}

