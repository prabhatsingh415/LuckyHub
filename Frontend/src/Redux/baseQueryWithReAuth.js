import { fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setCredentials, logOut } from "./slices/credentialSlice";
import { setLoading } from "./slices/loaderSlice";

const baseQuery = fetchBaseQuery({
  baseUrl: import.meta.env.VITE_API_BASE_URL,
  credentials: "include",
  prepareHeaders: (headers, { getState }) => {
    const token = getState().auth.accessToken;
    if (token) headers.set("Authorization", `Bearer ${token}`);
    return headers;
  },
});

export const baseQueryWithReauth = async (args, api, extraOptions) => {
  api.dispatch(setLoading(true));
  try {
    let result = await baseQuery(args, api, extraOptions);

    if (
      result?.error &&
      (result.error.status === 401 || result.error.status === 403)
    ) {
      const refreshResult = await baseQuery(
        {
          url: "/user/refresh-token",
          method: "POST",
          credentials: "include",
        },
        api,
        extraOptions
      );

      if (refreshResult?.data) {
        api.dispatch(
          setCredentials({ accessToken: refreshResult.data.accessToken })
        );
        result = await baseQuery(args, api, extraOptions);
      } else {
        api.dispatch(logOut());
        window.location.href = "/signin";
      }
    }

    return result;
  } finally {
    api.dispatch(setLoading(false));
  }
};
