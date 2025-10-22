import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { baseQueryWithReauth } from "../baseQueryWithReAuth";

export const apiSlice = createApi({
  reducerPath: "api",
  baseQuery: baseQueryWithReauth,
  tagTypes: ["User"],
  endpoints: (builder) => ({
    signUp: builder.mutation({
      query: (data) => ({
        url: "/user/signup",
        method: "POST",
        body: data,
      }),
    }),
    signIn: builder.mutation({
      query: (data) => ({
        url: "/user/login",
        method: "POST",
        body: data,
      }),
    }),
    verifyUser: builder.query({
      query: (token) => ({
        url: `/user/verifyRegistration?token=${token}`,
        method: "GET",
      }),
    }),
    refreshToken: builder.mutation({
      query: () => ({
        url: "/user/refresh-token",
        method: "POST",
        credentials: "include",
      }),
    }),
    forgotPassword: builder.mutation({
      query: (data) => ({
        url: "/user/forgot-password",
        method: "POST",
        body: data,
      }),
    }),
  }),
});

export const {
  useSignUpMutation,
  useSignInMutation,
  useVerifyUserQuery,
  useRefreshTokenMutation,
  useForgotPasswordMutation,
} = apiSlice;
