function Button({ className = "", type = "Click", ...props }) {
  return (
    <button className={className} {...props}>
      {type}
    </button>
  );
}

export default Button;
