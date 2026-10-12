import './Sidebar.css'
import { uploadPdf } from './../../services/api'
import { useState } from 'react';

function Sidebar({ topK, setTopK, useRag, setUseRag }) {
    
    const [uploading, setUploading] = useState(false);
    const [documents, setDocuments] = useState([{name:'attention-paper', chunks:21}])
    const [message, setMessage] = useState(null);

    async function handleFileChange(e) {
        const file = e.target.files?.[0];
      if (!file) return;

        e.target.value = '';

        const sourceName = file.name.replace(/\.pdf$/i, '');
        setUploading(true);
        setMessage(null);

        try {
            const result = await uploadPdf(file, sourceName, 800);
            setDocuments((prev) => [...prev.filter((d) => d.name !== sourceName), { name: sourceName, chunks: result.chunks },]);

            setMessage({ type: 'success', text: `Ingested ${result.pages} pages -> ${result.chunks} chunks`, });
        }
        catch (err) {
            setMessage({ type: 'error', text: err.message });
        }
        finally {
            setUploading(false);
        }
    }

    return (
    <aside className="sidebar">
      <div className="sidebar-section">
        <h3>Upload</h3>
        <label className={`btn-primary ${uploading ? 'disabled' : ''}`}>
          {uploading ? 'Uploading...' : 'Upload PDF'}
          <input
            type="file"
            accept="application/pdf"
            onChange={handleFileChange}
            disabled={uploading}
            style={{ display: 'none' }}
          />
        </label>
        {message && (
          <p className={`upload-message ${message.type}`}>{message.text}</p>
        )}
      </div>

      <div className="sidebar-section">
        <h3>Documents</h3>
        <ul className="doc-list">
          {documents.map((doc, i) => (
            <li key={i}>
              <span className="doc-name">{doc.name}</span>
              <span className="doc-meta">{doc.chunks} chunks</span>
            </li>
          ))}
        </ul>
      </div>

      <div className="sidebar-section">
        <h3>Settings</h3>
        <label className="field">
          <span>Top K: {topK}</span>
          <input
            type="range"
            min="1"
            max="10"
            value={topK}
            onChange={(e) => setTopK(Number(e.target.value))}
          />
        </label>
        <label className="field toggle">
          <input
            type="checkbox"
            checked={useRag}
            onChange={(e) => setUseRag(e.target.checked)}
          />
          <span>Use RAG</span>
        </label>
      </div>

      <div className="sidebar-footer">
        <button className="btn-secondary">Clear Chat</button>
      </div>
    </aside>
  );

}

export default Sidebar;