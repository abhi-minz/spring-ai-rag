const API_BASE = 'http://localhost:8080';

export async function chatDoc({ sessionId, question, topK = 3 }) {
    const res = await fetch(`${API_BASE}/api/chat-doc`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId, question, topK }),
    });

    if (!res.ok) {
        const text = await res.text();
        throw new Error(`API error ${res.status}: ${text.slice(0, 200)}`);
    }

    return res.json();
}

export async function askDoc({ question, topK = 3 }) {
    const res = await fetch(`${API_BASE}/api/ask-doc`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ question, topK }),
    });
    if (!res.ok) throw new Error(`API error ${res.status}`);
    return res.json();
}

export async function uploadPdf(file, source, chunkSize = 800) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('source', source);
    formData.append('chunkSize', chunkSize);

    const res = await fetch('${API_BASE}/api/upload-pdf', {
        method: 'POST',
        body: formData
    });
    if (!res.ok) throw new Error(`Upload fail ${res.status}`);
    return res.json();
}

export async function askSimple(question) {
    const res = await fetch(`${API_BASE}/api/ask`, {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain' },
        body: question
    });
    if (!res.ok) throw new Error(`API erro ${res.status}`);
    const text = await res.text();
    return { answer: text };
}