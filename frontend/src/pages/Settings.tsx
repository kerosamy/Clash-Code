import { useEffect, useState } from "react";

export default function Settings() {
  const [output, setOutput] = useState<string>("");

  const API_BASE = "http://localhost:8080/test";

  const getToken = () => localStorage.getItem("token");
    console.log(localStorage.getItem("token"));
  // --- Test Functions ---

  const callPublicEndpoint = async () => {
    try {
      const res = await axios.get(`${API_BASE}/public`);
      setOutput(`Public: ${res.data}`);
    } catch (err) {
      setOutput("Public endpoint error.");
      console.error(err);
    }
  };

  const callSecureEndpoint = async () => {
    try {
        const token = getToken();
      
      // FIX: Stop the request if there is no token
      if (!token) {
        setOutput("No token found. Please login first.");
        return;
      }      const res = await axios.get(`${API_BASE}/secure`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setOutput(`Secure: ${res.data}`);
    } catch (err) {
      setOutput("Secure endpoint failed (invalid or missing token).");
      console.error(err);
    }
  };

  const callSecureData = async () => {
    try {
      const token = getToken();
      
      // FIX: Stop the request if there is no token
      if (!token) {
        setOutput("No token found. Please login first.");
        return;
      }
      const res = await axios.get(`${API_BASE}/secure-data`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setOutput(`Secure Data: ${JSON.stringify(res.data)}`);
    } catch (err) {
      setOutput("Secure-data failed.");
      console.error(err);
    }
  };

  const callWithInvalidToken = async () => {
    try {
      const token = getToken();
      const fakeToken = token?.slice(0, -5) + "12345"; // corrupt last 5 chars

      const res = await axios.get(`${API_BASE}/secure`, {
        headers: { Authorization: `Bearer ${fakeToken}` },
      });

      setOutput(`Invalid Token Response: ${res.data}`);
    } catch (err) {
      setOutput("Invalid token rejected (expected).");
      console.error(err);
    }
  };

  useEffect(() => {
    // Optional: test automatically when page loads
    console.log("Settings page mounted.");
  }, []);

  return (
    <div className="text-white p-5">
      <h1 className="text-2xl mb-4">Settings Page (JWT Test Panel)</h1>

      <div className="space-y-3">
        <button
          onClick={callPublicEndpoint}
          className="bg-blue-600 px-4 py-2 rounded"
        >
          Test Public Endpoint
        </button>

        <button
          onClick={callSecureEndpoint}
          className="bg-green-600 px-4 py-2 rounded"
        >
          Test Secure Endpoint (Requires Valid Token)
        </button>

        <button
          onClick={callSecureData}
          className="bg-purple-600 px-4 py-2 rounded"
        >
          Test Secure Data (JSON Output)
        </button>

        <button
          onClick={callWithInvalidToken}
          className="bg-red-600 px-4 py-2 rounded"
        >
          Test With Invalid Token
        </button>
      </div>

      {/* Output display */}
      <div className="mt-6 p-4 bg-gray-800 rounded">
        <h2 className="text-lg mb-2 font-semibold">Output:</h2>
        <pre className="whitespace-pre-wrap">{output}</pre>
      </div>
    </div>
  );
}
