import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useDashboardAPIQuery } from "../Redux/slices/apiSlice";
import { setAuth, logout, setAuthenticating } from "../Redux/slices/authSlice";
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
      skip: isAuthenticated,
    }
  );

  useEffect(() => {
    if (isSuccess && data) {
      dispatch(setAuth({ isAuthenticated: true, user: data }));
    }

    if (isError) {
      dispatch(logout());
    }

    if (isAuthenticated) {
      dispatch(setAuthenticating(false));
    }
  }, [isSuccess, isError, data, dispatch, isAuthenticated]);

  if (isCheckingAuth && isLoading) {
    return <Loader />;
  }

  return children;
};

export default AuthInitializer;
