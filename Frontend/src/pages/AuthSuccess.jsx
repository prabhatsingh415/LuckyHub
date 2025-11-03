import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function AuthSuccess() {
  const navigate = useNavigate();

  useEffect(() => {
    localStorage.setItem("isSignIn", "true");
    window.dispatchEvent(new Event("storage"));

    // pehle navigate karo
    navigate("/home", { replace: true });

    // fir reload karao thoda delay deke (React ko navigate complete karne ka time mile)
    setTimeout(() => {
      window.location.reload();
    }, 500); // half second delay is enough
  }, [navigate]);

  return (
    <div className="flex justify-center items-center h-screen text-xl">
      Signing you in...
    </div>
  );
}
