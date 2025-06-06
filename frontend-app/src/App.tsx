import React from 'react';
import { UserTable } from './components/UserTable/UserTable';
import styles from './App.module.css';

const App: React.FC = () => {
  return (
    <div className={styles.app}>
      <header className={styles.header}>
        <h1>User Management System</h1>
      </header>
      <main className={styles.main}>
        <UserTable />
      </main>
    </div>
  );
};

export default App; 