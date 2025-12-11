import "./LogoLoader.css";

const LogoLoader = ({ loadingMessage = "Loading" }) => {
  return (
    <div className="bg-background flex flex-col flex-1 items-center justify-center">
      <div className="flex flex-col items-center">
        {/* Logo */}
        <div className="mb-5 fade-in-scale elegant-glow">
          <svg
            width="300"
            height="100"
            viewBox="0 0 207 57"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <defs>
              <linearGradient
                id="paint0_linear_2385_17"
                x1="63"
                y1="23"
                x2="63"
                y2="36"
                gradientUnits="userSpaceOnUse"
              >
                <stop stopColor="#4F5869" />
                <stop offset="1" stopColor="#9CADCF" />
              </linearGradient>
              <linearGradient
                id="paint1_linear_2385_17"
                x1="146.436"
                y1="6.76953"
                x2="146.436"
                y2="55.7852"
                gradientUnits="userSpaceOnUse"
              >
                <stop stopColor="#4F5869" />
                <stop offset="1" stopColor="#9CADCF" />
              </linearGradient>
              <linearGradient
                id="paint2_linear_2385_17"
                x1="185.5"
                y1="7"
                x2="185.5"
                y2="54"
                gradientUnits="userSpaceOnUse"
              >
                <stop stopColor="#4F5869" />
                <stop offset="1" stopColor="#9CADCF" />
              </linearGradient>

              {/* Shimmer gradient */}
              <linearGradient id="shimmerGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" stopColor="transparent">
                  <animate
                    attributeName="offset"
                    values="0;1"
                    dur="1s"
                    repeatCount="indefinite"
                  />
                </stop>
                <stop offset="50%" stopColor="rgba(255,255,255,0.4)">
                  <animate
                    attributeName="offset"
                    values="0.5;1.5"
                    dur="1s"
                    repeatCount="indefinite"
                  />
                </stop>
                <stop offset="100%" stopColor="transparent">
                  <animate
                    attributeName="offset"
                    values="1;2"
                    dur="1s"
                    repeatCount="indefinite"
                  />
                </stop>
              </linearGradient>
            </defs>

            {/* Left C - Bottom */}
            <g style={{ animation: "slideInLeft 0.6s cubic-bezier(0.34, 1.56, 0.64, 1)" }}>
              <path
                d="M23 38H36.3726C45.3808 55.132 64.6853 44.0007 66.7749 39.2433L75 46.3621C68.2692 58.9166 29.6603 64.9275 23 38Z"
                fill="#EC7438"
              />
            </g>

            {/* Left C - Top */}
            <g
              style={{
                animation:
                  "slideInLeft 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) 0.05s backwards",
              }}
            >
              <path
                d="M65.0421 18.2669L73 10.2199C61.7476 -3.91188 27.231 -6.56321 23 23H35.4625C40.755 6.26912 58.1143 10.2199 65.0421 18.2669Z"
                fill="#EC7438"
              />
            </g>

            {/* Right C - Bottom */}
            <g
              style={{
                animation:
                  "slideInLeft 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) 0.1s backwards",
              }}
            >
              <path
                d="M75 38H88.1154C96.9504 55.132 115.884 44.0007 117.933 39.2433L126 46.3621C119.399 58.9166 81.5322 64.9275 75 38Z"
                fill="#EC7438"
              />
            </g>

            {/* Right C - Top */}
            <g
              style={{
                animation:
                  "slideInLeft 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) 0.15s backwards",
              }}
            >
              <path
                d="M116.201 18.2669L124 10.2199C112.973 -3.91188 79.1463 -6.56321 75 23H87.2132C92.3999 6.26912 109.412 10.2199 116.201 18.2669Z"
                fill="#EC7438"
              />
            </g>

            {/* Sword with shimmer */}
            <g
              style={{
                animation:
                  "slideInLeft 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) 0.2s backwards",
              }}
            >
              <path
                d="M117.892 30.7718H25.6263C24.9629 30.7718 24.4251 30.234 24.4251 29.5706C24.4251 28.9073 24.9629 28.3695 25.6263 28.3695H116.894C117.514 28.3695 118.074 28.0017 118.32 27.4333C118.765 26.4069 118.013 25.2608 116.894 25.2608H10.6569C9.8725 25.2608 9.11925 25.568 8.55861 26.1167L7.58121 27.0733C6.37899 28.2499 6.37899 30.1848 7.58121 31.3614L8.55861 32.318C9.11925 32.8666 9.8725 33.1739 10.6569 33.1739H116.501C117.282 33.1739 117.915 33.8065 117.915 34.5869C117.915 35.3674 117.282 36 116.501 36H9.02642C8.31055 36 7.61827 35.744 7.07462 35.2783L2.77705 31.5965C1.34001 30.3653 1.38775 28.1275 2.87599 26.9588L7.10137 23.6406C7.62983 23.2256 8.28231 23 8.95424 23H119.429C122 23 123.379 26.0226 121.695 27.9652L120.159 29.737C119.589 30.3943 118.762 30.7718 117.892 30.7718Z"
                fill="url(#paint0_linear_2385_17)"
                stroke="url(#shimmerGradient)"
                strokeWidth="1"
              />
            </g>

            {/* First + with shimmer */}
            <g
              style={{
                animation:
                  "slideInRight 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) 0.25s backwards",
              }}
            >
              <path
                d="M150.864 24.7275H169.404L161.407 37.9365H150.864V55.7852L140.299 48.9658V37.7666C140.185 37.7945 140.065 37.8109 139.942 37.8096L126.44 37.6621C124.102 37.6363 122.691 35.0639 123.926 33.0781L128.237 26.1436C128.785 25.2631 129.748 24.7276 130.785 24.7275H140.06C140.141 24.7275 140.221 24.7359 140.299 24.75V13.042L150.864 6.76953V24.7275ZM140.299 27.3711C140.221 27.3852 140.141 27.3945 140.06 27.3945H132.355C131.339 27.3946 130.392 27.9091 129.839 28.7617L128.77 30.4102C127.466 32.4193 128.927 35.0698 131.321 35.042L139.941 34.9414C140.065 34.94 140.184 34.9545 140.299 34.9824V27.3711Z"
                fill="url(#paint1_linear_2385_17)"
                stroke="url(#shimmerGradient)"
                strokeWidth="1"
              />
            </g>

            {/* Second + with shimmer */}
            <g
              style={{
                animation:
                  "slideInRight 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) 0.3s backwards",
              }}
            >
              <path
                d="M191.283 25.9548H207L199.29 38.14H191.283V54L180.903 47.6173V38.14H164L172.106 25.3745H180.903V13.0927L191.283 7V25.9548Z"
                fill="url(#paint2_linear_2385_17)"
                stroke="url(#shimmerGradient)"
                strokeWidth="1"
              />
            </g>
          </svg>
        </div>

        {/* Loading message with bouncing dots */}
        <div className="flex items-center gap-1">
          <span className="text-orange text-3xl font-medium tracking-wide">
            {loadingMessage}
          </span>
          <div className="flex gap-1 ml-1">
            {[0, 0.1, 0.2].map((i) => (
              <span
                key={i}
                className="rounded-full text-3xl font-bold text-orange jump"
                style={{ animationDelay: `${i}s` }}
              >
              .
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default LogoLoader;
