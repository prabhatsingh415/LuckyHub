import { configureStore } from "@reduxjs/toolkit";
import themeSlice from "./slices/themeSlice";
import { apiSlice } from "./slices/apiSlice";

const store = configureStore({
  reducer: {
    theme: themeSlice,
    [apiSlice.reducerPath]: apiSlice.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(apiSlice.middleware),
});

export default store;
