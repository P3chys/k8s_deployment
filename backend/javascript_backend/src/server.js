import express from 'express';
import cors from 'cors';

const app = express();
const port = process.env.PORT || 3000;

app.use(express.json());
app.use(cors());

// Middleware to check if request is coming from the frontend
const validateOrigin = (req, res, next) => {
  const userAgent = req.headers['user-agent'];
  const isWebRequest = userAgent && (userAgent.includes('Mozilla') || userAgent.includes('Chrome') || userAgent.includes('Safari'));
  req.isWebRequest = isWebRequest;
  next();
};

// Internal API - only accessible via command line
app.get('/api/internal/data', (req, res) => {
  if (req.isWebRequest) {
    return res.status(403).json({
      error: 'Access denied. This API is only accessible via command line.'
    });
  }

  res.json({
    message: 'Internal API response',
    timestamp: new Date(),
    source: 'internal'
  });
});

// External API - only accessible via web browser
app.get('/api/external/data', validateOrigin, (req, res) => {
  if (!req.isWebRequest) {
    return res.status(403).json({
      error: 'Access denied. This API is only accessible via web browser.'
    });
  }

  res.json({
    message: 'External API response',
    timestamp: new Date(),
    source: 'external'
  });
});

app.listen(port, () => {
  console.log(`Backend server running on port ${port}`);
});