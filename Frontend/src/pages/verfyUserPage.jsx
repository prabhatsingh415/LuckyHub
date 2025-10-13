import React from "react";
import { useVerifyUserQuery } from "../Redux/slices/apiSlice";
import Loader from "../pages/Loader";
import { useSearchParams } from "react-router-dom";
function verfyUserPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  const { data, isLoading, isError, error, isSuccess } =
    useVerifyUserQuery(token);

  if (isLoading) return <Loader />;
  if (isError)
    return (
      <div className="text-red-500">
        {error?.data?.message || "Failed to verify user"}
      </div>
    );
  if (isSuccess) return <div className="text-green-500">{data?.message}</div>;
}

export default verfyUserPage;
