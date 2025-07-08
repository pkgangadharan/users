import React, { useState, useEffect } from 'react';
import axios from '../api';

export default function UserForm({ tenantId, refresh, user }) {
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    dateOfBirth: ''
  });

  useEffect(() => {
    if (user) {
      setForm({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        email: user.email || '',
        dateOfBirth: user.dateOfBirth || ''
      });
    } else {
      setForm({
        firstName: '',
        lastName: '',
        email: '',
        dateOfBirth: ''
      });
    }
  }, [user]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const headers = { 'x-tenant-id': tenantId };

    try {
      if (user && user.id) {
        await axios.put(`/api/users/${user.id}`, form, { headers });
      } else {
        await axios.post('/api/users', form, { headers });
      }

      refresh();
      setForm({ firstName: '', lastName: '', email: '', dateOfBirth: '' });
    } catch (error) {
      console.error('Error saving user:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: 20 }}>
      <input
        name="firstName"
        value={form.firstName}
        onChange={handleChange}
        placeholder="First Name"
        required
      />
      <input
        name="lastName"
        value={form.lastName}
        onChange={handleChange}
        placeholder="Last Name"
        required
      />
      <input
        type="email"
        name="email"
        value={form.email}
        onChange={handleChange}
        placeholder="Email"
        required
      />
      <input
        type="date"
        name="dateOfBirth"
        value={form.dateOfBirth}
        onChange={handleChange}
        required
      />
      <button type="submit">{user ? 'Update' : 'Add'} User</button>
    </form>
  );
}
