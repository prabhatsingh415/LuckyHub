import { configureStore } from "@reduxjs/toolkit";
import themeSlice from "./slices/themeSlice";
import { apiSlice } from "./slices/apiSlice";
import loaderSlice from "./slices/loaderSlice";
import credentialSlice from "./slices/credentialSlice";
import authSlice from "./slices/authSlice";

const store = configureStore({
  reducer: {
    theme: themeSlice,
    [apiSlice.reducerPath]: apiSlice.reducer,
    loader: loaderSlice,
    auth: credentialSlice,
    userDetails: authSlice,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(apiSlice.middleware),
});

export default store;
