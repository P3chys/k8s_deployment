import React, { useState, useEffect } from 'react';

function App() {
  const [externalData, setExternalData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const externalResponse = await fetch('/api/external/data');
        const externalResult = await externalResponse.json();
        setExternalData(externalResult);
        setError(null);
      } catch (err) {
        console.error('Error fetching data:', err);
        setError('Failed to fetch data from the external API: ' + err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div style={{ padding: '20px' }}>
        <h1>Kubernetes Demo App</h1>
        <p>Loading data from external API...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: '20px' }}>
        <h1>Kubernetes Demo App</h1>
        <p style={{ color: 'red' }}>{error}</p>
      </div>
    );
  }

  return (
    <div style={{ padding: '20px' }}>
      <h1>Kubernetes Demo App</h1>
      <div>
        <h2>External API Response:</h2>
        <pre style={{ 
          backgroundColor: '#f5f5f5', 
          padding: '10px', 
          borderRadius: '4px' 
        }}>
          {JSON.stringify(externalData, null, 2)}
        </pre>
      </div>
    </div>
  );
}

export default App;