import React from "react";
import { loader } from "..";
import Lottie from "lottie-react";

export default function Loader() {
  return (
    <div className="fixed inset-0 flex justify-center items-center z-50 bg-black/30 backdrop-blur-md">
      <div className="w-1/2 sm:w-1/3 md:w-1/4 lg:w-1/5">
        <Lottie
          animationData={loader}
          loop={true}
          style={{ width: "100%", height: "auto" }}
        />
      </div>
    </div>
  );
}
