import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  accessToken: null,
};

const credentialSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setCredentials: (state, action) => {
      state.accessToken = action.payload.accessToken;
    },
    logOut: (state) => {
      state.accessToken = null;
    },
  },
});

export const { setCredentials, logOut } = credentialSlice.actions;
export default credentialSlice.reducer;
