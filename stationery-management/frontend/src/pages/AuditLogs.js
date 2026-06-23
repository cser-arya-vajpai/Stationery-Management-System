import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import { getAuditLogs } from '../utils/auditLogger';

const AuditLogs = () => {
  const [logs, setLogs] = useState([]);
  const [activeTab, setActiveTab] = useState('STUDENT'); // STUDENT or ADMIN

  useEffect(() => {
    // Load logs on mount
    setLogs(getAuditLogs());
  }, []);

  const filteredLogs = logs.filter(log => log.userRole === activeTab);

  return (
    <div>
      <Navbar />
      <div className="page-container">
        <div className="welcome-banner" style={{ background: 'linear-gradient(135deg, #fce7f3 0%, #fbcfe8 100%)', padding: '24px 30px' }}>
          <h1>System Audit Logs</h1>
          <p>Trace and monitor all actions performed by students and administrators across microservices</p>
        </div>

        <div className="audit-tabs">
          <button 
            className={`audit-tab ${activeTab === 'STUDENT' ? 'active' : ''}`}
            onClick={() => setActiveTab('STUDENT')}
          >
            🎓 Student Logs
          </button>
          <button 
            className={`audit-tab ${activeTab === 'ADMIN' ? 'active' : ''}`}
            onClick={() => setActiveTab('ADMIN')}
          >
            🛡️ Admin Logs
          </button>
        </div>

        {filteredLogs.length === 0 ? (
          <div className="empty-state" style={{ background: 'var(--panel-bg)', borderRadius: '16px', border: '1px solid var(--panel-border)', padding: '50px' }}>
            <span className="empty-state-icon">📝</span>
            <p style={{ fontWeight: '600', fontSize: '16px', color: 'var(--text-secondary)' }}>No logs recorded for this category yet.</p>
          </div>
        ) : (
          <div className="audit-log-list">
            {filteredLogs.map((log) => (
              <div key={log.id} className="audit-log-item">
                <div className="audit-log-main">
                  <span className="audit-log-action">{log.action}</span>
                  <span className="audit-log-details">{log.details}</span>
                </div>
                <div className="audit-log-meta">
                  <span className="audit-log-user">{log.userEmail}</span>
                  <span>{new Date(log.timestamp).toLocaleString()}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default AuditLogs;