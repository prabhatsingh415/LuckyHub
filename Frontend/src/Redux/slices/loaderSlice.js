import { createSlice } from "@reduxjs/toolkit";

const loaderSlice = createSlice({
  name: "loader",
  initialState: { loadingCount: 0 },
  reducers: {
    startLoading: (state) => {
      state.loadingCount += 1;
    },
    stopLoading: (state) => {
      state.loadingCount = Math.max(state.loadingCount - 1, 0);
    },
  },
});

export const { startLoading, stopLoading } = loaderSlice.actions;
export default loaderSlice.reducer;
