import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useDashboardAPIQuery } from "../Redux/slices/apiSlice";
import { setAuth, logout } from "../Redux/slices/authSlice";
import Loader from "../pages/Loader";
import { useSelector } from "react-redux";

const AuthInitializer = ({ children }) => {
  const dispatch = useDispatch();
  const { isCheckingAuth } = useSelector((state) => state.userDetails);

  const { data, isError, isSuccess } = useDashboardAPIQuery(undefined, {
    skip: !isCheckingAuth,
  });

  useEffect(() => {
    if (isCheckingAuth) {
      if (isSuccess && data) {
        dispatch(setAuth({ isAuthenticated: true, user: data }));
      } else if (isError) {
        dispatch(logout());
      }
    }
  }, [isSuccess, isError, data, dispatch, isCheckingAuth]);

  if (isCheckingAuth) {
    return <Loader />;
  }

  return children;
};

export default AuthInitializer;
