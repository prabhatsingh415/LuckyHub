import { useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";

export default function GoogleOAuthCallback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const code = searchParams.get("code");
  const redirectURL = import.meta.env.VITE_API_BASE_URL

  useEffect(() => {
    if (code) {
      // send the code to backend for token exchange
      fetch(`${redirectURL}?code=${code}`, {
        method: "GET",
      })
        .then((res) => res.json())
        .then((data) => {
          localStorage.setItem("accessToken", data.token); // backend JWT
          navigate("/dashboard"); // redirect after login
        })
        .catch((err) => console.error(err));
    }
  }, [code, navigate]);

  return <div>Logging you in...</div>;
}
