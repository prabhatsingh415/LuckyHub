import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useDashboardAPIQuery } from "../Redux/slices/apiSlice";
import { setAuth, logout } from "../Redux/slices/authSlice";
import Loader from "../pages/Loader";
import { useSelector } from "react-redux";

const AuthInitializer = ({ children }) => {
  const dispatch = useDispatch();
  const { isCheckingAuth } = useSelector((state) => state.userDetails);

  // API call sirf tab hogi jab isCheckingAuth true ho
  const { data, isLoading, isError, isSuccess } = useDashboardAPIQuery(
    undefined,
    { skip: !isCheckingAuth }
  );

  useEffect(() => {
    if (isCheckingAuth) {
      if (isSuccess && data) {
        dispatch(setAuth({ isAuthenticated: true, user: data }));
      } else if (isError) {
        // baseQueryWithReauth already logOut() dispatch kar raha hoga,
        // par yahan manually ensure kar lete hain ki check khatam ho gaya.
        dispatch(logout());
      }
    }
  }, [isSuccess, isError, data, dispatch, isCheckingAuth]);

  // JAB TAK CHECKING CHAL RAHI HAI, ROUTER RENDER NAHI HOGA
  if (isCheckingAuth) {
    return <Loader />;
  }

  return children;
};

export default AuthInitializer;
