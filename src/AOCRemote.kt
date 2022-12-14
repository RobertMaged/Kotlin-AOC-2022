import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

object Constants {
    private val prop: HashMap<String, String> = hashMapOf()
    operator fun set(key: String, value: String) {
        prop[key] = value
    }

    //if service is not lazy initialized, consider nullable in return
    operator fun get(key: String): String {
        return prop.getValue(key)
    }
}

/**
 * This function calls a top level function called [initAocHeaders] which must be declared in same package.
 *
 * [initAocHeaders] is a ***Must Implemented*** function,
 *
 * just to put your mandatory Auth headers in normal key-value map which is syntactically wrapped in [Constants].
 *
 * @see Constants
 */
private fun initializeHeadersConstants() = initAocHeaders()

fun sendResult(day: String, part: Int, answer: String, year: Int = 2022) {
    val intDay = day.toInt()

    require(intDay in 1..25)
    require(part == 1 || part == 2)

    val response = AOCRemote.sendAnswer(intDay, part, answer, year).execute()

    println(parseAnswerResult(response.body() ?: response.message()))
}

private fun parseAnswerResult(response: String): String {
    val mainPart = response.substringAfter("<main>", "<main> not found while parsing")
        .substringBefore("</main>")


    //mainPart.replace(Regex("<a .*?</a>"), "")

    val formatted = mainPart.replace(regex = Regex("<.*?>"), "").trim()

    val summery = when {
        formatted.contains("That's the right answer", ignoreCase = true) -> "Success Answer."
        formatted.contains("gold star", ignoreCase = true) -> "Success Answer."
        formatted.contains("stuck", ignoreCase = true) -> "Wrong Answer."
        formatted.contains("that's not the right answer", ignoreCase = true) -> "Wrong Answer."
        else -> "Can't Read Response"
    }
    return summery + "\n\n" +
            formatted.replace("(,|\\.|!) ".toRegex(), "$1\n")
}


private val AOCRemote by lazy {
    val url = "https://adventofcode.com/"

    initializeHeadersConstants()

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val (year, _, day) = chain.request().url().pathSegments()
            val newRequest = chain.request().newBuilder()
                .header("referer", "https://adventofcode.com/$year/day/$day")
                .header("cookie", "session=${Constants["cookie-session"]}")
                .header("user-agent", Constants["user-agent"])
//                .header("sec-ch-ua", "${Constants["sec-ch-ua"]}")
                .build()

            chain.proceed(newRequest)
        }

        .build()

    Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(client)
        .build()
        .create(AOC::class.java)
}

private interface AOC {
    @Headers(
        """accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8""",
        """accept-language: en-US,en;q=0.9""",
        "cache-control: max-age=0",
        "content-length: 21",
        "content-type: application/x-www-form-urlencoded",
        "dnt: 1",
        "origin: https://adventofcode.com",
        "sec-ch-ua-mobile: ?0",
        """sec-ch-ua-platform: "Linux"""",
        "sec-fetch-dest: document",
        "sec-fetch-mode: navigate",
        "sec-fetch-site: same-origin",
        "sec-fetch-user: ?1",
        "sec-gpc: 1",
        "upgrade-insecure-requests: 1",
    )
    @FormUrlEncoded
    @POST("{year}/day/{day}/answer")
    fun sendAnswer(
        @Path("day") day: Int,
        @Field("level") level: Int,
        @Field("answer") answer: String,
        @Path("year") year: Int = 2022
    ): Call<String>
}