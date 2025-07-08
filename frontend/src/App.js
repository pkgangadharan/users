import React from 'react';
import { BrowserRouter, Routes, Route, useSearchParams } from 'react-router-dom';
import UserList from './components/UserList';
import ErrorPage from './components/ErrorPage';

function TenantRouter() {
  const [params] = useSearchParams();
  const tenant = params.get('tenant');

  const isValidTenant = (id) => /^[a-zA-Z0-9]{3,10}$/.test(id);

  if (!tenant || !isValidTenant(tenant)) {
    return <ErrorPage message="Invalid or missing tenant ID in URL. Please use ?tenant=your_tenant" />;
  }

  return <UserList tenantId={tenant} />;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<TenantRouter />} />
      </Routes>
    </BrowserRouter>
  );
}
/*
http://localhost:3000/?tenant=tenant1
http://yourdomain.com/?tenant=xyz
http://yourdomain.com/?tenant=tenant123
*/