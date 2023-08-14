package com.tomastu.iceweather


import com.google.gson.annotations.SerializedName

data class WeatherData(
    @SerializedName("api_status")
    val apiStatus: String, // active
    @SerializedName("api_version")
    val apiVersion: String, // v2.5
    @SerializedName("lang")
    val lang: String, // zh_CN
    @SerializedName("location")
    val location: List<Double>,
    @SerializedName("result")
    val result: Result,
    @SerializedName("server_time")
    val serverTime: Int, // 1690191411
    @SerializedName("status")
    val status: String, // ok
    @SerializedName("timezone")
    val timezone: String, // Asia/Shanghai
    @SerializedName("tzshift")
    val tzshift: Int, // 28800
    @SerializedName("unit")
    val unit: String // metric
) {
    data class Result(
        @SerializedName("daily")
        val daily: Daily,
        @SerializedName("forecast_keypoint")
        val forecastKeypoint: String, // 最近的降雨带在南边114公里外呢
        @SerializedName("hourly")
        val hourly: Hourly,
        @SerializedName("minutely")
        val minutely: Minutely,
        @SerializedName("primary")
        val primary: Int, // 0
        @SerializedName("realtime")
        val realtime: Realtime
    ) {
        data class Daily(
            @SerializedName("air_quality")
            val airQuality: AirQuality,
            @SerializedName("astro")
            val astro: List<Astro>,
            @SerializedName("cloudrate")
            val cloudrate: List<Cloudrate>,
            @SerializedName("dswrf")
            val dswrf: List<Dswrf>,
            @SerializedName("humidity")
            val humidity: List<Humidity>,
            @SerializedName("life_index")
            val lifeIndex: LifeIndex,
            @SerializedName("precipitation")
            val precipitation: List<Precipitation>,
            @SerializedName("pressure")
            val pressure: List<Pressure>,
            @SerializedName("skycon")
            val skycon: List<Skycon>,
            @SerializedName("skycon_08h_20h")
            val skycon08h20h: List<Skycon08h20h>,
            @SerializedName("skycon_20h_32h")
            val skycon20h32h: List<Skycon20h32h>,
            @SerializedName("status")
            val status: String, // ok
            @SerializedName("temperature")
            val temperature: List<Temperature>,
            @SerializedName("visibility")
            val visibility: List<Visibility>,
            @SerializedName("wind")
            val wind: List<Wind>
        ) {
            data class AirQuality(
                @SerializedName("aqi")
                val aqi: List<Aqi>,
                @SerializedName("pm25")
                val pm25: List<Pm25>
            ) {
                data class Aqi(
                    @SerializedName("avg")
                    val avg: Avg,
                    @SerializedName("date")
                    val date: String, // 2023-07-24T00:00+08:00
                    @SerializedName("max")
                    val max: Max,
                    @SerializedName("min")
                    val min: Min
                ) {
                    data class Avg(
                        @SerializedName("chn")
                        val chn: Int, // 61
                        @SerializedName("usa")
                        val usa: Int // 65
                    )

                    data class Max(
                        @SerializedName("chn")
                        val chn: Int, // 75
                        @SerializedName("usa")
                        val usa: Int // 86
                    )

                    data class Min(
                        @SerializedName("chn")
                        val chn: Int, // 26
                        @SerializedName("usa")
                        val usa: Int // 57
                    )
                }

                data class Pm25(
                    @SerializedName("avg")
                    val avg: Int, // 18
                    @SerializedName("date")
                    val date: String, // 2023-07-24T00:00+08:00
                    @SerializedName("max")
                    val max: Int, // 29
                    @SerializedName("min")
                    val min: Int // 15
                )
            }

            data class Astro(
                @SerializedName("date")
                val date: String, // 2023-07-24T00:00+08:00
                @SerializedName("sunrise")
                val sunrise: Sunrise,
                @SerializedName("sunset")
                val sunset: Sunset
            ) {
                data class Sunrise(
                    @SerializedName("time")
                    val time: String // 05:05
                )

                data class Sunset(
                    @SerializedName("time")
                    val time: String // 18:55
                )
            }

            data class Cloudrate(
                @SerializedName("avg")
                val avg: Double, // 0.56
                @SerializedName("date")
                val date: String, // 2023-07-24T00:00+08:00
                @SerializedName("max")
                val max: Double, // 1.0
                @SerializedName("min")
                val min: Double // 0.0
            )

            data class Dswrf(
                @SerializedName("avg")
                val avg: Double, // 160.6
                @SerializedName("date")
                val date: String, // 2023-07-24T00:00+08:00
                @SerializedName("max")
                val max: Double, // 637.5
                @SerializedName("min")
                val min: Double // 0.0
            )

            data class Humidity(
                @SerializedName("avg")
                val avg: Double, // 0.87
                @SerializedName("date")
                val date: String, // 2023-07-24T00:00+08:00
                @SerializedName("max")
                val max: Double, // 0.95
                @SerializedName("min")
                val min: Double // 0.68
            )

            data class LifeIndex(
                @SerializedName("carWashing")
                val carWashing: List<CarWashing>,
                @SerializedName("coldRisk")
                val coldRisk: List<ColdRisk>,
                @SerializedName("comfort")
                val comfort: List<Comfort>,
                @SerializedName("dressing")
                val dressing: List<Dressing>,
                @SerializedName("ultraviolet")
                val ultraviolet: List<Ultraviolet>
            ) {
                data class CarWashing(
                    @SerializedName("date")
                    val date: String, // 2023-07-24T00:00+08:00
                    @SerializedName("desc")
                    val desc: String, // 适宜
                    @SerializedName("index")
                    val index: String // 1
                )

                data class ColdRisk(
                    @SerializedName("date")
                    val date: String, // 2023-07-24T00:00+08:00
                    @SerializedName("desc")
                    val desc: String, // 极易发
                    @SerializedName("index")
                    val index: String // 4
                )

                data class Comfort(
                    @SerializedName("date")
                    val date: String, // 2023-07-24T00:00+08:00
                    @SerializedName("desc")
                    val desc: String, // 闷热
                    @SerializedName("index")
                    val index: String // 0
                )

                data class Dressing(
                    @SerializedName("date")
                    val date: String, // 2023-07-24T00:00+08:00
                    @SerializedName("desc")
                    val desc: String, // 很热
                    @SerializedName("index")
                    val index: String // 2
                )

                data class Ultraviolet(
                    @SerializedName("date")
                    val date: String, // 2023-07-24T00:00+08:00
                    @SerializedName("desc")
                    val desc: String, // 最弱
                    @SerializedName("index")
                    val index: String // 1
                )
            }

            data class Precipitation(
                @SerializedName("avg")
                val avg: Double, // 0.0
                @SerializedName("date")
                val date: String, // 2023-07-24T00:00+08:00
                @SerializedName("max")
                val max: Double, // 2.9033
                @SerializedName("min")
                val min: Double // 0.0
            )

            data class Pressure(
                @SerializedName("avg")
                val avg: Double, // 100700.45
                @SerializedName("date")
                val date: String, // 2023-07-24T00:00+08:00
                @SerializedName("max")
                val max: Double, // 100720.0
                @SerializedName("min")
                val min: Double // 100503.4
            )

            data class Skycon(
                @SerializedName("date")
                val date: String, // 2023-07-24T00:00+08:00
                @SerializedName("value")
                val value: String // PARTLY_CLOUDY_DAY
            )

            data class Skycon08h20h(
                @SerializedName("date")
                val date: String, // 2023-07-24T00:00+08:00
                @SerializedName("value")
                val value: String // MODERATE_RAIN
            )

            data class Skycon20h32h(
                @SerializedName("date")
                val date: String, // 2023-07-24T00:00+08:00
                @SerializedName("value")
                val value: String // PARTLY_CLOUDY_NIGHT
            )

            data class Temperature(
                @SerializedName("avg")
                val avg: Double, // 27.87
                @SerializedName("date")
                val date: String, // 2023-07-24T00:00+08:00
                @SerializedName("max")
                val max: Double, // 34.25
                @SerializedName("min")
                val min: Double // 25.88
            )

            data class Visibility(
                @SerializedName("avg")
                val avg: Double, // 17.83
                @SerializedName("date")
                val date: String, // 2023-07-24T00:00+08:00
                @SerializedName("max")
                val max: Double, // 24.13
                @SerializedName("min")
                val min: Double // 6.25
            )

            data class Wind(
                @SerializedName("avg")
                val avg: Avg,
                @SerializedName("date")
                val date: String, // 2023-07-24T00:00+08:00
                @SerializedName("max")
                val max: Max,
                @SerializedName("min")
                val min: Min
            ) {
                data class Avg(
                    @SerializedName("direction")
                    val direction: Double, // 171.6
                    @SerializedName("speed")
                    val speed: Double // 10.17
                )

                data class Max(
                    @SerializedName("direction")
                    val direction: Double, // 151.3
                    @SerializedName("speed")
                    val speed: Double // 18.71
                )

                data class Min(
                    @SerializedName("direction")
                    val direction: Double, // 333.0
                    @SerializedName("speed")
                    val speed: Double // 1.8
                )
            }
        }

        data class Hourly(
            @SerializedName("air_quality")
            val airQuality: AirQuality,
            @SerializedName("cloudrate")
            val cloudrate: List<Cloudrate>,
            @SerializedName("description")
            val description: String, // 未来24小时多云
            @SerializedName("dswrf")
            val dswrf: List<Dswrf>,
            @SerializedName("humidity")
            val humidity: List<Humidity>,
            @SerializedName("precipitation")
            val precipitation: List<Precipitation>,
            @SerializedName("pressure")
            val pressure: List<Pressure>,
            @SerializedName("skycon")
            val skycon: List<Skycon>,
            @SerializedName("status")
            val status: String, // ok
            @SerializedName("temperature")
            val temperature: List<Temperature>,
            @SerializedName("visibility")
            val visibility: List<Visibility>,
            @SerializedName("wind")
            val wind: List<Wind>
        ) {
            data class AirQuality(
                @SerializedName("aqi")
                val aqi: List<Aqi>,
                @SerializedName("pm25")
                val pm25: List<Pm25>
            ) {
                data class Aqi(
                    @SerializedName("datetime")
                    val datetime: String, // 2023-07-24T17:00+08:00
                    @SerializedName("value")
                    val value: Value
                ) {
                    data class Value(
                        @SerializedName("chn")
                        val chn: Int, // 36
                        @SerializedName("usa")
                        val usa: Int // 57
                    )
                }

                data class Pm25(
                    @SerializedName("datetime")
                    val datetime: String, // 2023-07-24T17:00+08:00
                    @SerializedName("value")
                    val value: Int // 15
                )
            }

            data class Cloudrate(
                @SerializedName("datetime")
                val datetime: String, // 2023-07-24T17:00+08:00
                @SerializedName("value")
                val value: Double // 0.56
            )

            data class Dswrf(
                @SerializedName("datetime")
                val datetime: String, // 2023-07-24T17:00+08:00
                @SerializedName("value")
                val value: Double // 363.923
            )

            data class Humidity(
                @SerializedName("datetime")
                val datetime: String, // 2023-07-24T17:00+08:00
                @SerializedName("value")
                val value: Double // 0.71
            )

            data class Precipitation(
                @SerializedName("datetime")
                val datetime: String, // 2023-07-24T17:00+08:00
                @SerializedName("value")
                val value: Double // 0.0
            )

            data class Pressure(
                @SerializedName("datetime")
                val datetime: String, // 2023-07-24T17:00+08:00
                @SerializedName("value")
                val value: Double // 100651.571
            )

            data class Skycon(
                @SerializedName("datetime")
                val datetime: String, // 2023-07-24T17:00+08:00
                @SerializedName("value")
                val value: String // PARTLY_CLOUDY_DAY
            )

            data class Temperature(
                @SerializedName("datetime")
                val datetime: String, // 2023-07-24T17:00+08:00
                @SerializedName("value")
                val value: Double // 31.0
            )

            data class Visibility(
                @SerializedName("datetime")
                val datetime: String, // 2023-07-24T17:00+08:00
                @SerializedName("value")
                val value: Double // 19.39
            )

            data class Wind(
                @SerializedName("datetime")
                val datetime: String, // 2023-07-24T17:00+08:00
                @SerializedName("direction")
                val direction: Double, // 333.0
                @SerializedName("speed")
                val speed: Double // 1.8
            )
        }

        data class Minutely(
            @SerializedName("datasource")
            val datasource: String, // radar
            @SerializedName("description")
            val description: String, // 最近的降雨带在南边114公里外呢
            @SerializedName("precipitation")
            val precipitation: List<Double>,
            @SerializedName("precipitation_2h")
            val precipitation2h: List<Double>,
            @SerializedName("probability")
            val probability: List<Double>,
            @SerializedName("status")
            val status: String // ok
        )

        data class Realtime(
            @SerializedName("air_quality")
            val airQuality: AirQuality,
            @SerializedName("apparent_temperature")
            val apparentTemperature: Double, // 35.6
            @SerializedName("cloudrate")
            val cloudrate: Double, // 0.56
            @SerializedName("dswrf")
            val dswrf: Double, // 363.9
            @SerializedName("humidity")
            val humidity: Double, // 0.71
            @SerializedName("life_index")
            val lifeIndex: LifeIndex,
            @SerializedName("precipitation")
            val precipitation: Precipitation,
            @SerializedName("pressure")
            val pressure: Double, // 100651.57
            @SerializedName("skycon")
            val skycon: String, // PARTLY_CLOUDY_DAY
            @SerializedName("status")
            val status: String, // ok
            @SerializedName("temperature")
            val temperature: Double, // 31.0
            @SerializedName("visibility")
            val visibility: Double, // 19.39
            @SerializedName("wind")
            val wind: Wind
        ) {
            data class AirQuality(
                @SerializedName("aqi")
                val aqi: Aqi,
                @SerializedName("co")
                val co: Double, // 0.5
                @SerializedName("description")
                val description: Description,
                @SerializedName("no2")
                val no2: Int, // 9
                @SerializedName("o3")
                val o3: Int, // 108
                @SerializedName("pm10")
                val pm10: Int, // 36
                @SerializedName("pm25")
                val pm25: Int, // 15
                @SerializedName("so2")
                val so2: Int // 8
            ) {
                data class Aqi(
                    @SerializedName("chn")
                    val chn: Int, // 36
                    @SerializedName("usa")
                    val usa: Int // 57
                )

                data class Description(
                    @SerializedName("chn")
                    val chn: String, // 优
                    @SerializedName("usa")
                    val usa: String // 良
                )
            }

            data class LifeIndex(
                @SerializedName("comfort")
                val comfort: Comfort,
                @SerializedName("ultraviolet")
                val ultraviolet: Ultraviolet
            ) {
                data class Comfort(
                    @SerializedName("desc")
                    val desc: String, // 闷热
                    @SerializedName("index")
                    val index: Int // 0
                )

                data class Ultraviolet(
                    @SerializedName("desc")
                    val desc: String, // 很弱
                    @SerializedName("index")
                    val index: Double // 2.0
                )
            }

            data class Precipitation(
                @SerializedName("local")
                val local: Local,
                @SerializedName("nearest")
                val nearest: Nearest
            ) {
                data class Local(
                    @SerializedName("datasource")
                    val datasource: String, // radar
                    @SerializedName("intensity")
                    val intensity: Double, // 0.0
                    @SerializedName("status")
                    val status: String // ok
                )

                data class Nearest(
                    @SerializedName("distance")
                    val distance: Double, // 114.29
                    @SerializedName("intensity")
                    val intensity: Double, // 0.1875
                    @SerializedName("status")
                    val status: String // ok
                )
            }

            data class Wind(
                @SerializedName("direction")
                val direction: Double, // 333.0
                @SerializedName("speed")
                val speed: Double // 1.8
            )
        }
    }
}