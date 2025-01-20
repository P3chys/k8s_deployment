import React, { useState } from 'react';

function App() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [authResult, setAuthResult] = useState(null);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
        const formData = new URLSearchParams();
        formData.append('username', username);
        formData.append('password', password);

        var url = "http://localhost:3001/api/machine/login";
        if(username.includes("physical")){
          url = "http://localhost:3000/api/physical/login";
        }


        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData,
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const makeAuthenticatedRequest = async (url, options = {}) => {
          const userType = localStorage.getItem('userType');
          const headers = {
              ...options.headers,
              'X-User-Type': userType,
          };
      
          return fetch(url, {
              ...options,
              headers,
          });
      };
        // Store the user type from the response
        const userType = response.headers.get('X-User-Type');
        if (userType) {
            localStorage.setItem('userType', userType);
        }

        const result = await response.json();
        setAuthResult(result);
        
        if (!result.authenticated) {
            setError('Authentication failed');
        }
    } catch (err) {
        console.error('Login error:', err);
        setError('Failed to login: ' + err.message);
    } finally {
        setLoading(false);
    }
    
  };

  

  const renderLoginForm = () => (
    <form onSubmit={handleLogin} className="login-form">
      <div className="form-group">
        <label htmlFor="username">Username:</label>
        <input
          type="text"
          id="username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          className="form-input"
          required
        />
      </div>
      <div className="form-group">
        <label htmlFor="password">Password:</label>
        <input
          type="password"
          id="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="form-input"
          required
        />
      </div>
      <button type="submit" disabled={loading} className="login-button">
        {loading ? 'Logging in...' : 'Login'}
      </button>
    </form>
  );

  const renderConnectionInfo = () => {
    if (!authResult || !authResult.authenticated) return null;

    const { userType, connectionSource } = authResult;
    let message = '';

    if (userType === 'physical' && connectionSource === 'web') {
      message = 'Physical user connected from web';
    } else if (userType === 'physical' && connectionSource === 'cmd') {
      message = 'Physical user connected from cmd';
    } else if (userType === 'machine' && connectionSource === 'web') {
      message = 'Machine user connected from web';
    } else if (userType === 'machine' && connectionSource === 'cmd') {
      message = 'Machine user connected from cmd';
    }

    return (
      <div className="connection-info">
        <h3>Connection Details</h3>
        <p className="connection-text">{message}</p>
      </div>
    );
  };

  const renderUserInfo = () => (
    <div className="user-info">
      <h2>User Details</h2>
      {renderConnectionInfo()}
      <div className="json-display">
        <h3>User Data:</h3>
        {authResult.user && (
          <div className="user-details">
            <p><strong>DN:</strong> {authResult.user.dn}</p>
            <p><strong>CN:</strong> {authResult.user.cn}</p>
            <p><strong>UID Number:</strong> {authResult.user.uidNumber}</p>
            <p><strong>GID Number:</strong> {authResult.user.gidNumber}</p>
            <p><strong>Home Directory:</strong> {authResult.user.homeDirectory}</p>
            <p><strong>Login Shell:</strong> {authResult.user.loginShell}</p>
          </div>
        )}
      </div>
      <button
        onClick={() => {
          setAuthResult(null);
          setUsername('');
          setPassword('');
        }}
        className="logout-button"
      >
        Logout
      </button>
    </div>
  );

  return (
    <div className="app-container">
      <h1>LDAP Authentication Demo</h1>
      {error && <div className="error-message">{error}</div>}
      {!authResult ? renderLoginForm() : renderUserInfo()}
      <style>{`
        .app-container {
          max-width: 600px;
          margin: 0 auto;
          padding: 20px;
          font-family: Arial, sans-serif;
        }

        h1 {
          color: #333;
          text-align: center;
          margin-bottom: 30px;
        }

        .login-form {
          display: flex;
          flex-direction: column;
          gap: 15px;
          margin-top: 20px;
          background: #f8f9fa;
          padding: 20px;
          border-radius: 8px;
          box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .form-group {
          display: flex;
          flex-direction: column;
          gap: 5px;
        }

        .form-group label {
          color: #555;
          font-weight: bold;
        }

        .form-input {
          padding: 10px;
          border: 1px solid #ddd;
          border-radius: 4px;
          font-size: 16px;
          transition: border-color 0.3s;
        }

        .form-input:focus {
          border-color: #007bff;
          outline: none;
          box-shadow: 0 0 0 2px rgba(0,123,255,0.25);
        }

        .login-button, .logout-button {
          padding: 12px 20px;
          background-color: #007bff;
          color: white;
          border: none;
          border-radius: 4px;
          cursor: pointer;
          font-size: 16px;
          transition: background-color 0.3s;
        }

        .login-button:hover, .logout-button:hover {
          background-color: #0056b3;
        }

        .login-button:disabled {
          background-color: #ccc;
          cursor: not-allowed;
        }

        .error-message {
          color: #dc3545;
          margin: 10px 0;
          padding: 10px;
          border: 1px solid #dc3545;
          border-radius: 4px;
          background-color: #f8d7da;
        }

        .user-info {
          margin-top: 20px;
          background: #f8f9fa;
          padding: 20px;
          border-radius: 8px;
          box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .connection-info {
          margin: 15px 0;
          padding: 15px;
          background-color: #e3f2fd;
          border-radius: 4px;
          border-left: 4px solid #1976d2;
        }

        .connection-text {
          font-size: 16px;
          color: #1976d2;
          margin: 0;
          font-weight: bold;
        }

        .json-display {
          background-color: #f8f9fa;
          padding: 15px;
          border-radius: 4px;
          margin: 15px 0;
        }

        .user-details {
          display: grid;
          gap: 10px;
        }

        .user-details p {
          margin: 0;
          padding: 8px;
          background-color: white;
          border-radius: 4px;
          box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        .user-details strong {
          color: #555;
          margin-right: 10px;
        }

        h2, h3 {
          color: #333;
          margin-top: 0;
        }
      `}</style>
    </div>
  );
}

export default App;