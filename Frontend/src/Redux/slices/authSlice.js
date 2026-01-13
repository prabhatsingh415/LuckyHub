import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  isAuthenticated: false,
  user: null,
  isCheckingAuth: true,
};

const authSlice = createSlice({
  name: "userAuth",
  initialState,
  reducers: {
    setAuth: (state, action) => {
      state.isAuthenticated = action.payload.isAuthenticated;
      state.user = action.payload.user;
      state.isCheckingAuth = false;
    },
    logout: (state) => {
      state.isAuthenticated = false;
      state.user = null;
      state.isCheckingAuth = false;
    },
    setAuthenticating: (state, action) => {
      state.isCheckingAuth = action.payload;
    },
  },
});

export const { setAuth, logout, setAuthenticating } = authSlice.actions;
export default authSlice.reducer;
