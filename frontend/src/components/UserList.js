import React, { useEffect, useState } from 'react';
import axios from '../api';
import UserForm from './UserForm';

export default function UserList({ tenantId }) {
  const [users, setUsers] = useState([]);
  const [editUser, setEditUser] = useState(null);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);

  const fetchUsers = async () => {
    if (!tenantId) return;
    setLoading(true);
    try {
      const res = await axios.get('/api/users', {
        headers: { 'x-tenant-id': tenantId },
        params: { search }
      });
      setUsers(res.data.content || []);
    } catch (error) {
      console.error('Error fetching users:', error);
      setUsers([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [search]);

  const handleDelete = async (id) => {
    await axios.delete(`/api/users/${id}`, {
      headers: { 'x-tenant-id': tenantId }
    });
    fetchUsers();
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Users (Tenant: {tenantId})</h2>

      <div style={{ marginBottom: 10 }}>
        <input
          type="text"
          value={search}
          placeholder="Search by name or email..."
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      <UserForm tenantId={tenantId} refresh={fetchUsers} user={editUser} />

      {loading ? (
        <p>Loading users...</p>
      ) : users.length === 0 ? (
        <p>No users found.</p>
      ) : (
        <ul>
          {users.map((user) => (
            <li key={user.id}>
              {user.firstName} {user.lastName} - {user.email}
              <button onClick={() => setEditUser(user)}>Edit</button>
              <button onClick={() => handleDelete(user.id)}>Delete</button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
