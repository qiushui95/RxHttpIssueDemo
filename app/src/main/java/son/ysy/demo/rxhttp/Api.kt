package son.ysy.demo.rxhttp

import retrofit2.http.GET

interface Api {

    @ResponseResult
    @GET("article/list/0/json")
    suspend fun getPageData(): PageData<IdInfo>

    @ResponseResult
    @PagingResult
    @GET("article/list/0/json")
    suspend fun getDirectData(): List<IdInfo>
}