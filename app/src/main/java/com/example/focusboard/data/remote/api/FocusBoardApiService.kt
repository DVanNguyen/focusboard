package com.example.focusboard.data.remote.api

import com.example.focusboard.data.remote.dto.ApiResponse
import com.example.focusboard.data.remote.dto.AuthResponse
import com.example.focusboard.data.remote.dto.BlockDto
import com.example.focusboard.data.remote.dto.BlockRequest
import com.example.focusboard.data.remote.dto.LoginRequest
import com.example.focusboard.data.remote.dto.PageDto
import com.example.focusboard.data.remote.dto.PageRequest
import com.example.focusboard.data.remote.dto.ReorderBlocksRequest
import com.example.focusboard.data.remote.dto.RegisterRequest
import com.example.focusboard.data.remote.dto.UserDto
import com.example.focusboard.data.remote.dto.WorkspaceDto
import com.example.focusboard.data.remote.dto.WorkspaceRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FocusBoardApiService {
    // ── Auth ──────────────────────────────────────────
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("auth/me")
    suspend fun getMe(): Response<ApiResponse<UserDto>>

    // ── Workspaces ───────────────────────────────────
    @GET("workspaces")
    suspend fun getWorkspaces(): Response<ApiResponse<List<WorkspaceDto>>>

    @POST("workspaces")
    suspend fun createWorkspace(@Body body: WorkspaceRequest): Response<ApiResponse<WorkspaceDto>>

    @PUT("workspaces/{id}")
    suspend fun updateWorkspace(
        @Path("id") id: String,
        @Body body: WorkspaceRequest,
    ): Response<ApiResponse<WorkspaceDto>>

    @DELETE("workspaces/{id}")
    suspend fun deleteWorkspace(@Path("id") id: String): Response<Unit>

    // ── Pages ────────────────────────────────────────
    @GET("workspaces/{wsId}/pages")
    suspend fun getPages(@Path("wsId") workspaceId: String): Response<ApiResponse<List<PageDto>>>

    @POST("workspaces/{wsId}/pages")
    suspend fun createPage(
        @Path("wsId") workspaceId: String,
        @Body body: PageRequest,
    ): Response<ApiResponse<PageDto>>

    @PUT("pages/{id}")
    suspend fun updatePage(
        @Path("id") id: String,
        @Body body: PageRequest,
    ): Response<ApiResponse<PageDto>>

    @DELETE("pages/{id}")
    suspend fun deletePage(@Path("id") id: String): Response<Unit>

    // ── Blocks ───────────────────────────────────────
    @GET("pages/{pageId}/blocks")
    suspend fun getBlocks(@Path("pageId") pageId: String): Response<ApiResponse<List<BlockDto>>>

    @POST("pages/{pageId}/blocks")
    suspend fun createBlock(
        @Path("pageId") pageId: String,
        @Body body: BlockRequest,
    ): Response<ApiResponse<BlockDto>>

    @PUT("blocks/{id}")
    suspend fun updateBlock(
        @Path("id") id: String,
        @Body body: BlockRequest,
    ): Response<ApiResponse<BlockDto>>

    @DELETE("blocks/{id}")
    suspend fun deleteBlock(@Path("id") id: String): Response<Unit>

    @PUT("pages/{pageId}/blocks/reorder")
    suspend fun reorderBlocks(
        @Path("pageId") pageId: String,
        @Body body: ReorderBlocksRequest,
    ): Response<Unit>
}
