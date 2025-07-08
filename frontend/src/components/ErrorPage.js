import React from 'react';

export default function ErrorPage({ message }) {
  return (
    <div style={{
      padding: '40px',
      textAlign: 'center',
      fontFamily: 'Arial, sans-serif'
    }}>
      <h1>⚠️ Error</h1>
      <p>{message || 'An unexpected error occurred.'}</p>
      <p>
        Please check your tenant URL or contact support.
      </p>
    </div>
  );
}
