import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function AuthSuccess() {
  const navigate = useNavigate();
  const redirectEndpoint = localStorage.getItem("redirectEndpoint") || "/home";

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate(redirectEndpoint, { replace: true });
      localStorage.removeItem("redirectEndpoint");
    }, 2000);

    return () => clearTimeout(timer);
  }, [navigate, redirectEndpoint]);

  return (
    <div className="flex flex-col justify-center items-center h-screen bg-[#0a0a0a] text-white">
      <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-orange-500 mb-4"></div>
      <p className="text-xl font-medium animate-pulse">Signing you in...</p>
    </div>
  );
}
