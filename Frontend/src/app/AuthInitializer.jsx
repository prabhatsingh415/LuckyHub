import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useDashboardAPIQuery } from "../Redux/slices/apiSlice";
import { setAuth, logout } from "../Redux/slices/authSlice";
import { Loader } from "../components/Common";
import { useSelector } from "react-redux";
const AuthInitializer = ({ children }) => {
  const dispatch = useDispatch();
  const { isCheckingAuth, isAuthenticated } = useSelector(
    (state) => state.userDetails
  );
  const token = useSelector((state) => state.auth.accessToken);

  const { data, isError, isSuccess, isLoading } = useDashboardAPIQuery(
    undefined,
    {
      skip: !isCheckingAuth || isAuthenticated || !token,
    }
  );

  useEffect(() => {
    if (isAuthenticated && isCheckingAuth) {
      dispatch(setAuthenticating(false));
      return;
    }

    if (isCheckingAuth) {
      if (isSuccess && data) {
        dispatch(setAuth({ isAuthenticated: true, user: data }));
      } else if (isError) {
        dispatch(logout());
      }
    }
  }, [isSuccess, isError, data, dispatch, isCheckingAuth, isAuthenticated]);

  if (isCheckingAuth && isLoading) {
    return <Loader />;
  }

  return children;
};
