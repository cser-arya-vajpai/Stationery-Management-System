//creates new audit log entries:
//Adds a new activity record to localStorage and keeps newest records at the top.
export const addAuditLog = (userEmail, userRole, action, details) => {
  const logs = JSON.parse(localStorage.getItem('audit_logs') || '[]');
  const newLog = {
    id: Date.now() + Math.random().toString(36).substring(2, 7),
    timestamp: new Date().toISOString(),
    userEmail,
    userRole,
    action: action.replace(/_/g, ' '),
    details
  };
  logs.unshift(newLog); // Newest logs first
  localStorage.setItem('audit_logs', JSON.stringify(logs));
};

export const getAuditLogs = () => {
  const logs = localStorage.getItem('audit_logs');
  if (!logs) {
    // Pre-seed baseline data for evaluations
    const defaultLogs = [
      {
        id: '1',
        timestamp: new Date(Date.now() - 1000 * 60 * 15).toISOString(), // 15 mins ago
        userEmail: 'student@college.edu',
        userRole: 'STUDENT',
        action: 'SUBMIT REQUEST',
        details: 'Requested 3 Reams of A4 Printing Paper'
      },
      {
        id: '2',
        timestamp: new Date(Date.now() - 1000 * 60 * 45).toISOString(), // 45 mins ago
        userEmail: 'admin@stationery.com',
        userRole: 'ADMIN',
        action: 'UPDATE ITEM',
        details: 'Updated available quantity of HB Pencil to 120'
      },
      {
        id: '3',
        timestamp: new Date(Date.now() - 1000 * 60 * 120).toISOString(), // 2 hours ago
        userEmail: 'student@college.edu',
        userRole: 'STUDENT',
        action: 'LOGIN',
        details: 'Logged in successfully'
      },
      {
        id: '4',
        timestamp: new Date(Date.now() - 1000 * 60 * 180).toISOString(), // 3 hours ago
        userEmail: 'admin@stationery.com',
        userRole: 'ADMIN',
        action: 'LOGIN',
        details: 'Logged in successfully'
      }
    ];
    localStorage.setItem('audit_logs', JSON.stringify(defaultLogs));
    return defaultLogs;
  }
  return JSON.parse(logs);
};