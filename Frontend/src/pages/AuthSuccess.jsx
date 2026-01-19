import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function AuthSuccess() {
  const navigate = useNavigate();

  useEffect(() => {
    navigate("/home", { replace: true });
    setTimeout(() => {
      window.location.reload();
    }, 500);
  }, [navigate]);

  return (
    <div className="flex justify-center items-center h-screen text-xl">
      Signing you in...
    </div>
  );
}
