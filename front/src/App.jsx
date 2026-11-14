import { useCallback, useEffect, useState } from 'react'
import './App.css'

const API = '/api'
const STATUSES = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED']
const PRIORITIES = ['LOW', 'MEDIUM', 'HIGH', 'URGENT']

async function request(path, options = {}) {
  const token = localStorage.getItem('helpdesk-token')
  const response = await fetch(`${API}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  })
  if (!response.ok) {
    const body = await response.json().catch(() => ({}))
    throw new Error(body.message || `Request failed (${response.status})`)
  }
  return response.status === 204 ? null : response.json()
}

function formatDate(value) {
  return value ? new Date(value).toLocaleString() : '—'
}

function App() {
  const [session, setSession] = useState(() => {
    const saved = localStorage.getItem('helpdesk-session')
    return saved ? JSON.parse(saved) : null
  })
  const [authMode, setAuthMode] = useState('login')
  const [authForm, setAuthForm] = useState({ name: '', email: '', password: '' })
  const [tickets, setTickets] = useState([])
  const [selectedId, setSelectedId] = useState(null)
  const [selectedTicket, setSelectedTicket] = useState(null)
  const [comments, setComments] = useState([])
  const [users, setUsers] = useState([])
  const [filters, setFilters] = useState({ status: '', priority: '' })
  const [showCreate, setShowCreate] = useState(false)
  const [showCreateAgent, setShowCreateAgent] = useState(false)
  const [agentForm, setAgentForm] = useState({ name: '', email: '', password: '' })
  const [newTicket, setNewTicket] = useState({ title: '', description: '', ticketPriority: 'MEDIUM' })
  const [comment, setComment] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const isStaff = session && ['ADMIN', 'SUPPORT_AGENT'].includes(session.role)
  const canCreateTicket = session && ['USER', 'ADMIN'].includes(session.role)

  const loadTickets = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const params = new URLSearchParams({ page: 0, size: 50, sortBy: 'createdAt', direction: 'desc' })
      if (filters.status) params.set('status', filters.status)
      if (filters.priority) params.set('priority', filters.priority)
      const page = await request(`${session.role === 'USER' ? '/tickets/my' : '/tickets'}?${params}`)
      setTickets(page.content || [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [session, filters.status, filters.priority])

  useEffect(() => {
    if (!session) return undefined
    const timer = window.setTimeout(loadTickets, 0)
    return () => window.clearTimeout(timer)
  }, [session, loadTickets])

  async function selectTicket(ticket) {
    setSelectedId(ticket.id)
    setSelectedTicket(ticket)
    setError('')
    try {
      const [fullTicket, ticketComments] = await Promise.all([
        request(`/tickets/${ticket.id}`),
        request(`/comments/tickets/${ticket.id}`),
      ])
      setSelectedTicket(fullTicket)
      setComments(ticketComments)
    } catch (err) {
      setError(err.message)
    }
  }

  async function submitAuth(event) {
    event.preventDefault()
    setLoading(true)
    setError('')
    try {
      const path = authMode === 'login' ? '/auth/login' : '/auth/register'
      const result = await request(path, { method: 'POST', body: JSON.stringify(authForm) })
      if (authMode === 'register') {
        setAuthMode('login')
        setError('Account created. Sign in to continue.')
      } else {
        localStorage.setItem('helpdesk-token', result.token)
        localStorage.setItem('helpdesk-session', JSON.stringify(result))
        setSession(result)
      }
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  function logout() {
    localStorage.removeItem('helpdesk-token')
    localStorage.removeItem('helpdesk-session')
    setSession(null)
    setSelectedTicket(null)
    setTickets([])
  }

  async function createTicket(event) {
    event.preventDefault()
    try {
      await request('/tickets', { method: 'POST', body: JSON.stringify(newTicket) })
      setNewTicket({ title: '', description: '', ticketPriority: 'MEDIUM' })
      setShowCreate(false)
      await loadTickets()
    } catch (err) {
      setError(err.message)
    }
  }

  async function updateTicket(field, value) {
    if (!selectedTicket) return
    try {
      const path = field === 'status'
        ? `/tickets/${selectedTicket.id}/status?ticketStatus=${value}`
        : `/tickets/${selectedTicket.id}/priority?priority=${value}`
      const updated = await request(path, { method: 'PUT' })
      setSelectedTicket(updated)
      setTickets((current) => current.map((ticket) => ticket.id === updated.id ? updated : ticket))
    } catch (err) {
      setError(err.message)
    }
  }

  async function addComment(event) {
    event.preventDefault()
    if (!comment.trim() || !selectedTicket) return
    try {
      await request(`/comments/tickets/${selectedTicket.id}`, {
        method: 'POST',
        body: JSON.stringify({ content: comment }),
      })
      setComment('')
      setComments(await request(`/comments/tickets/${selectedTicket.id}`))
    } catch (err) {
      setError(err.message)
    }
  }

  async function loadUsers() {
    try {
      setUsers(await request('/admin/agents'))
    } catch (err) {
      setError(err.message)
    }
  }

  async function createAgent(event) {
    event.preventDefault()
    try {
      await request('/admin/agents', {
        method: 'POST',
        body: JSON.stringify(agentForm),
      })
      setAgentForm({ name: '', email: '', password: '' })
      setShowCreateAgent(false)
      await loadUsers()
    } catch (err) {
      setError(err.message)
    }
  }

  async function assignTicket(agentId) {
    if (!selectedTicket || !agentId) return
    try {
      const updated = await request(`/tickets/${selectedTicket.id}/assign/${agentId}`, { method: 'PUT' })
      setSelectedTicket(updated)
      setTickets((current) => current.map((ticket) => ticket.id === updated.id ? updated : ticket))
    } catch (err) {
      setError(err.message)
    }
  }

  if (!session) {
    return (
      <main className="auth-page">
        <section className="auth-card">
          <div className="brand-mark">H</div>
          <p className="eyebrow">HELPDESK</p>
          <h1>{authMode === 'login' ? 'Welcome back' : 'Create your account'}</h1>
          <p className="muted">{authMode === 'login' ? 'Sign in to manage your support requests.' : 'Get help from your support team.'}</p>
          <form onSubmit={submitAuth} className="stack">
            {authMode === 'register' && (
              <label>Name<input required value={authForm.name} onChange={(event) => setAuthForm({ ...authForm, name: event.target.value })} /></label>
            )}
            <label>Email<input required type="email" value={authForm.email} onChange={(event) => setAuthForm({ ...authForm, email: event.target.value })} /></label>
            <label>Password<input required minLength="8" type="password" value={authForm.password} onChange={(event) => setAuthForm({ ...authForm, password: event.target.value })} /></label>
            {error && <p className="message">{error}</p>}
            <button className="primary" disabled={loading}>{loading ? 'Please wait…' : authMode === 'login' ? 'Sign in' : 'Register'}</button>
          </form>
          <button className="link-button" onClick={() => { setAuthMode(authMode === 'login' ? 'register' : 'login'); setError('') }}>
            {authMode === 'login' ? 'Need an account? Register' : 'Already registered? Sign in'}
          </button>
        </section>
      </main>
    )
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand"><span className="brand-mark small">H</span><strong>Helpdesk</strong></div>
        <div className="user-menu"><span>{session.name} <small>{session.role}</small></span><button className="secondary" onClick={logout}>Sign out</button></div>
      </header>
      <main className="dashboard">
        <div className="page-heading">
          <div><p className="eyebrow">WORKSPACE</p><h1>{isStaff ? 'All tickets' : 'My tickets'}</h1></div>
          {canCreateTicket && <button className="primary" onClick={() => setShowCreate(true)}>+ New ticket</button>}
        </div>
        {error && <p className="message">{error}</p>}
        <div className="filters">
          <select value={filters.status} onChange={(event) => setFilters({ ...filters, status: event.target.value })}><option value="">All statuses</option>{STATUSES.map((status) => <option key={status}>{status}</option>)}</select>
          <select value={filters.priority} onChange={(event) => setFilters({ ...filters, priority: event.target.value })}><option value="">All priorities</option>{PRIORITIES.map((priority) => <option key={priority}>{priority}</option>)}</select>
          {session.role === 'ADMIN' && <><button className="secondary" onClick={loadUsers}>Load agents</button><button className="secondary" onClick={() => setShowCreateAgent(true)}>+ Add agent</button></>}
        </div>
        {session.role === 'ADMIN' && users.length > 0 && <section className="agent-panel"><div className="panel-heading"><div><h3>Support agents</h3><p className="muted">Agents available for ticket assignment.</p></div><span className="agent-count">{users.length}</span></div><div className="agent-list">{users.map((user) => <div className="agent-row" key={user.id}><div><strong>{user.name}</strong><p>{user.email}</p></div><span className="badge agent-badge">{user.role}</span></div>)}</div></section>}
        <div className="workspace">
          <section className="ticket-list">
            {loading && <p className="muted empty">Loading tickets…</p>}
            {!loading && tickets.length === 0 && <p className="muted empty">No tickets found.</p>}
            {tickets.map((ticket) => (
              <button className={`ticket-row ${selectedId === ticket.id ? 'selected' : ''}`} key={ticket.id} onClick={() => selectTicket(ticket)}>
                <div><strong>#{ticket.id} {ticket.title}</strong><p>{ticket.createdByName || 'Unknown requester'} · {formatDate(ticket.createdAt)}</p></div>
                <div className="row-meta"><span className={`badge ${ticket.priority.toLowerCase()}`}>{ticket.priority}</span><span className="status">{ticket.ticketStatus.replace('_', ' ')}</span></div>
              </button>
            ))}
          </section>
          <section className="ticket-detail">
            {!selectedTicket ? <div className="empty detail-empty"><strong>Select a ticket</strong><span>Choose a ticket to view its details and conversation.</span></div> : (
              <>
                <div className="detail-header"><div><p className="eyebrow">TICKET #{selectedTicket.id}</p><h2>{selectedTicket.title}</h2><p className="muted">Created by {selectedTicket.createdByName} on {formatDate(selectedTicket.createdAt)}</p></div><span className={`badge ${selectedTicket.priority.toLowerCase()}`}>{selectedTicket.priority}</span></div>
                <p className="description">{selectedTicket.description}</p>
                <div className="controls">
                  <label>Status<select disabled={!isStaff} value={selectedTicket.ticketStatus} onChange={(event) => updateTicket('status', event.target.value)}>{STATUSES.map((status) => <option key={status}>{status}</option>)}</select></label>
                  <label>Priority<select disabled={!isStaff} value={selectedTicket.priority} onChange={(event) => updateTicket('priority', event.target.value)}>{PRIORITIES.map((priority) => <option key={priority}>{priority}</option>)}</select></label>
                  {session.role === 'ADMIN' && <label>Assign to<select value={selectedTicket.assignedToId || ''} onChange={(event) => assignTicket(event.target.value)}><option value="">Unassigned</option>{users.map((user) => <option key={user.id} value={user.id}>{user.name}</option>)}</select></label>}
                </div>
                <div className="conversation"><h3>Conversation</h3>{comments.length === 0 && <p className="muted">No comments yet.</p>}{comments.map((item) => <article className="comment" key={item.id}><strong>{item.userName}</strong><small>{formatDate(item.createdAt)}</small><p>{item.content}</p></article>)}</div>
                <form className="comment-form" onSubmit={addComment}><input value={comment} onChange={(event) => setComment(event.target.value)} placeholder="Write a comment…" maxLength="2000" /><button className="primary">Send</button></form>
              </>
            )}
          </section>
        </div>
      </main>
      {showCreate && <div className="modal-backdrop"><form className="modal stack" onSubmit={createTicket}><div className="modal-heading"><h2>New ticket</h2><button type="button" className="icon-button" onClick={() => setShowCreate(false)}>×</button></div><label>Title<input required value={newTicket.title} onChange={(event) => setNewTicket({ ...newTicket, title: event.target.value })} /></label><label>Description<textarea required rows="5" value={newTicket.description} onChange={(event) => setNewTicket({ ...newTicket, description: event.target.value })} /></label><label>Priority<select value={newTicket.ticketPriority} onChange={(event) => setNewTicket({ ...newTicket, ticketPriority: event.target.value })}>{PRIORITIES.map((priority) => <option key={priority}>{priority}</option>)}</select></label><button className="primary">Create ticket</button></form></div>}
      {showCreateAgent && <div className="modal-backdrop"><form className="modal stack" onSubmit={createAgent}><div className="modal-heading"><div><h2>Add support agent</h2><p className="muted">The account receives the SUPPORT_AGENT role automatically.</p></div><button type="button" className="icon-button" onClick={() => setShowCreateAgent(false)}>×</button></div><label>Name<input required value={agentForm.name} onChange={(event) => setAgentForm({ ...agentForm, name: event.target.value })} /></label><label>Email<input required type="email" value={agentForm.email} onChange={(event) => setAgentForm({ ...agentForm, email: event.target.value })} /></label><label>Temporary password<input required minLength="8" type="password" value={agentForm.password} onChange={(event) => setAgentForm({ ...agentForm, password: event.target.value })} /></label><button className="primary">Create agent</button></form></div>}
    </div>
  )
}

export default App
