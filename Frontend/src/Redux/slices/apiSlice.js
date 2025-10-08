import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

export const apiSlice = createApi({
  reducerPath: "api",
  baseQuery: fetchBaseQuery({
    baseUrl: import.meta.env.VITE_API_BASE_URL,
  }),
  tagTypes: ["User"],
  endpoints: (builder) => ({
    signUp: builder.mutation({
      query: (data) => ({
        url: "/signup",
        method: "POST",
        body: data,
      }),
    }),
    signIn: builder.mutation({
      query: (data) => ({
        url: "/signin",
        method: "POST",
        body: data,
      }),
    }),
  }),
});

export const { useSignUpMutation, useSignInMutation } = apiSlice;
