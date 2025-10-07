import React from "react";

function Input({
  placeholder = "Enter...",
  className = "",
  type = "text",
  ...props
}) {
  return (
    <input
      type={type}
      placeholder={placeholder}
      className={`p-2 border rounded focus:outline-none focus:ring-2 focus:ring-[var(--orange)] ${className}`}
      {...props}
    />
  );
}

export default Input;
