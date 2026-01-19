import { createApi } from "@reduxjs/toolkit/query/react";
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
      extraOptions: { refetchOnMountOrArgChange: true },
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
    savePassword: builder.mutation({
      query: ({ password, token }) => ({
        url: `/user/reset-password-confirm?token=${token}`,
        method: "POST",
        body: { newPassword: password },
      }),
    }),
    resendVerification: builder.query({
      query: (token) => ({
        url: `/user/resendToken?token=${token}`,
        method: "GET",
      }),
    }),
    dashboardAPI: builder.query({
      query: () => ({
        url: "/user/me",
        method: "GET",
        keepUnusedDataFor: 300, // keep cache for 5 minutes
        refetchOnFocus: false,
        refetchOnReconnect: false,
      }),
    }),
    History: builder.query({
      query: () => ({ url: "/giveaway/history", method: "GET" }),
      providesTags: ["Giveaway"],
    }),
    changeName: builder.mutation({
      query: (data) => ({
        url: "/user/updateName",
        method: "PUT",
        body: {
          firstName: data.firstName,
          lastName: data.lastName,
        },
      }),
    }),
    changeAvatar: builder.mutation({
      query: (formData) => ({
        url: "/user/updateAvatar",
        method: "PUT",
        body: formData,
      }),
    }),

    changePassword: builder.mutation({
      query: (data) => ({
        url: "/user/changePassword",
        method: "PUT",
        body: data,
      }),
    }),

    getLastPayment: builder.query({
      query: () => ({
        url: "/subscription/lastPayment",
        method: "GET",
      }),
      providesTags: ["Subscription"],
    }),

    getWinners: builder.mutation({
      query: (data) => ({
        url: "/giveaway/pick-a-winner",
        method: "POST",
        body: data,
      }),
      providesTags: ["Giveaway"],
    }),

    getSubscription: builder.query({
      query: (data) => ({
        url: "/subscription/getSubscription",
        method: "GET",
      }),
      providesTags: ["Subscription"],
    }),
  }),
});

export const {
  useSignUpMutation,
  useSignInMutation,
  useVerifyUserQuery,
  useRefreshTokenMutation,
  useForgotPasswordMutation,
  useSavePasswordMutation,
  useLazyResendVerificationQuery,
  useDashboardAPIQuery,
  useHistoryQuery,
  useChangeNameMutation,
  useChangeAvatarMutation,
  useChangePasswordMutation,
  useGetLastPaymentQuery,
  useGetWinnersMutation,
  useGetSubscriptionQuery,
} = apiSlice;
