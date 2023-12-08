package com.example.weatherapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 2b418a3fcfabb7eae9a1cceb43cd2496

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("jaipur")

        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityname : String){
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherInfo(cityname,"2b418a3fcfabb7eae9a1cceb43cd2496","metric")

        response.enqueue(object : Callback<weatherknow>{
            override fun onResponse(call: Call<weatherknow>, response: Response<weatherknow>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temparature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val max_temp = responseBody.main.temp_max
                    val min_temp = responseBody.main.temp_min
                    val windspeed = responseBody.wind.speed
                    val sunset = responseBody.sys.sunset.toLong()
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: " unknow"

                    binding.todaytemp.text = "$temparature ℃"
                    binding.sunnyweather.text = condition
                    binding.maxtemp.text = "Max-Temp : $max_temp ℃"
                    binding.mintemp.text = "Min-Temp : $min_temp ℃"
                    binding.humidity.text = "$humidity %"
                    binding.sunset.text = "${time(sunset)}"
                    binding.sunrice.text = "${time(sunrise)}"
                    binding.windspeed.text = "$windspeed m/s"
                    binding.sealevel.text = "$sealevel hPa"
                    binding.sunnycondition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityname.text ="$cityname"
//                    Log.d( "TAG", "onResponse: $temparature")

                    changeImageAccordingToWeatherCondition(condition)

                }
            }

            override fun onFailure(call: Call<weatherknow>, t: Throwable) {

            }
        })
    }
    private fun changeImageAccordingToWeatherCondition(condition: String) {

        when(condition){
            "Clouds", "Mist", "Overcast", "Fog", "Partly Clouds" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.clouds)
            }
            "Clear", "Sunny", "Clear Sky" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Drizzle", " Light Rain", "Snow", "Moderate Rain", "Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Blizzard", " Light Snow", "Moderate Snow", "Heavy Snow" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }


        }
        binding.lottieAnimationView.playAnimation()
    }

    fun time(timestamp: Long):String{

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun date():String{

        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun dayName(timestamp: Long): String{

        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

}




