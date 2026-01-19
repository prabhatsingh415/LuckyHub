import { Navigate } from "react-router-dom";
import { useSelector } from "react-redux";
import Loader from "../pages/Loader";

const ProtectedRoute = ({ element }) => {
  const { isAuthenticated, isCheckingAuth } = useSelector(
    (state) => state.userDetails
  );

  if (isCheckingAuth) {
    return <Loader />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/signin" replace />;
  }

  return element;
};

export default ProtectedRoute;
