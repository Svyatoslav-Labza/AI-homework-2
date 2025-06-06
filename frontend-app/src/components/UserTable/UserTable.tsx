import React, { useState, useCallback } from 'react';
import { User } from '../../types';
import { api } from '../../services/api';
import styles from './UserTable.module.css';

export const UserTable: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [isClosing, setIsClosing] = useState(false);

  React.useEffect(() => {
    const fetchUsers = async () => {
      try {
        const data = await api.getUsers();
        setUsers(data);
        setError(null);
      } catch (err) {
        setError('Failed to fetch users. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, []);

  const handleDelete = (e: React.MouseEvent, userId: number) => {
    e.stopPropagation();
    setUsers(users.filter(user => user.id !== userId));
  };

  const handleCloseModal = useCallback(() => {
    setIsClosing(true);
    setTimeout(() => {
      setSelectedUser(null);
      setIsClosing(false);
    }, 300); // Match this with the CSS animation duration
  }, []);

  if (loading) {
    return <div className={styles.loading}>Loading users...</div>;
  }

  if (error) {
    return <div className={styles.error}>{error}</div>;
  }

  return (
    <div className={styles.tableContainer}>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>NAME / EMAIL</th>
            <th>ADDRESS</th>
            <th>PHONE</th>
            <th>WEBSITE</th>
            <th>COMPANY</th>
            <th>ACTION</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id} onClick={() => setSelectedUser(user)} className={styles.tableRow}>
              <td className={styles.nameCell}>
                <div className={styles.name}>{user.name}</div>
                <div className={styles.email}>{user.email}</div>
              </td>
              <td>{user.address.city}, {user.address.street}</td>
              <td>{user.phone}</td>
              <td>
                <a 
                  href={`https://${user.website}`} 
                  className={styles.website} 
                  target="_blank" 
                  rel="noopener noreferrer"
                  onClick={(e) => e.stopPropagation()}
                >
                  {user.website}
                </a>
              </td>
              <td>{user.company.name}</td>
              <td>
                <button
                  className={styles.deleteButton}
                  onClick={(e) => handleDelete(e, user.id)}
                  aria-label="Delete user"
                >
                  ×
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {selectedUser && (
        <div 
          className={`${styles.modalOverlay} ${isClosing ? styles.closing : ''}`} 
          onClick={handleCloseModal}
        >
          <div 
            className={`${styles.modal} ${isClosing ? styles.closing : ''}`} 
            onClick={e => e.stopPropagation()}
          >
            <button 
              className={styles.modalClose} 
              onClick={handleCloseModal}
              aria-label="Close modal"
            >
              ×
            </button>
            
            <div className={styles.modalContent}>
              <h2 className={styles.modalName}>{selectedUser.name}</h2>
              <a href={`mailto:${selectedUser.email}`} className={styles.modalEmail}>
                {selectedUser.email}
              </a>

              <section className={styles.modalSection}>
                <h3>Address</h3>
                <p>{selectedUser.address.street}, Apt. {selectedUser.address.suite}</p>
                <p>{selectedUser.address.city}, {selectedUser.address.zipcode}</p>
                <a 
                  href={`https://maps.google.com/?q=${selectedUser.address.geo.lat},${selectedUser.address.geo.lng}`}
                  className={styles.mapLink}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <span role="img" aria-label="location">📍</span> View on map
                </a>
              </section>

              <section className={styles.modalSection}>
                <h3>Contact</h3>
                <p><strong>Phone:</strong> {selectedUser.phone}</p>
                <p>
                  <strong>Website:</strong>{' '}
                  <a 
                    href={`https://${selectedUser.website}`}
                    className={styles.website}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    {selectedUser.website}
                  </a>
                </p>
              </section>

              <section className={styles.modalSection}>
                <h3>Company</h3>
                <p><strong>Name:</strong> {selectedUser.company.name}</p>
                <p><strong>Catchphrase:</strong> {selectedUser.company.catchPhrase}</p>
                <p><strong>Business:</strong> {selectedUser.company.bs}</p>
              </section>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}; 