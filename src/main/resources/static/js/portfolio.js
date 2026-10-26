/* ===== API DEMO ===== */

async function loadExperiences() {
    const output = document.getElementById('api-result');
    output.textContent = '// Loading...';
    try {
        const res = await fetch('/api/experience');
        const data = await res.json();
        output.textContent = JSON.stringify(data, null, 2);
    } catch (e) {
        output.textContent = '// Error: ' + e.message;
    }
}

async function searchByTech() {
    const tech = document.getElementById('techSearch').value.trim();
    if (!tech) { alert('Please enter a technology name'); return; }
    const output = document.getElementById('api-result');
    output.textContent = `// Searching for experiences with: "${tech}"...`;
    try {
        const res = await fetch(`/api/experience/search?tech=${encodeURIComponent(tech)}`);
        const data = await res.json();
        output.textContent = data.length
            ? JSON.stringify(data, null, 2)
            : `// No experiences found with technology: "${tech}"`;
    } catch (e) {
        output.textContent = '// Error: ' + e.message;
    }
}

/* ===== KAFKA DEMO ===== */

async function publishKafkaEvent() {
    const message = document.getElementById('kafkaMessage').value.trim();
    if (!message) { alert('Please enter a message'); return; }

    const statusEl = document.getElementById('kafka-status');
    statusEl.className = 'status-msg';
    statusEl.textContent = '';

    try {
        const res = await fetch('/api/events/publish', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message })
        });
        const data = await res.json();
        const result = data.result || '';

        if (result.includes('KAFKA_UNAVAILABLE') || result.includes('unavailable')) {
            statusEl.className = 'status-msg warning';
            statusEl.textContent = '⚠️ ' + result;
        } else {
            statusEl.className = 'status-msg success';
            statusEl.textContent = '✅ ' + result;
        }

        // Refresh event log after a short delay (consumer may need a moment)
        setTimeout(loadKafkaEvents, 800);
    } catch (e) {
        statusEl.className = 'status-msg error';
        statusEl.textContent = '❌ Error: ' + e.message;
    }
}

async function loadKafkaEvents() {
    const tbody = document.getElementById('kafka-event-body');
    try {
        const res = await fetch('/api/events');
        const events = await res.json();

        if (!events.length) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-row">No events yet. Publish a message above.</td></tr>';
            return;
        }

        tbody.innerHTML = events.map((e, i) => `
            <tr>
                <td>${e.id}</td>
                <td class="direction-${e.direction.toLowerCase()}">${e.direction}</td>
                <td>${escapeHtml(e.message)}</td>
                <td class="${e.status === 'OK' ? 'status-ok' : 'status-unavailable'}">${e.status}</td>
                <td>${e.timestamp}</td>
            </tr>
        `).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="5" class="empty-row">Error loading events: ${err.message}</td></tr>`;
    }
}

/* ===== ACTUATOR DEMO ===== */

async function loadActuatorHealth() {
    const output = document.getElementById('actuator-result');
    output.textContent = '// Loading health data...';
    try {
        const res = await fetch('/actuator/health');
        const data = await res.json();
        output.textContent = JSON.stringify(data, null, 2);
    } catch (e) {
        output.textContent = '// Error: ' + e.message;
    }
}

async function loadActuatorInfo() {
    const output = document.getElementById('actuator-result');
    output.textContent = '// Loading app info...';
    try {
        const res = await fetch('/actuator/info');
        const data = await res.json();
        output.textContent = JSON.stringify(data, null, 2);
    } catch (e) {
        output.textContent = '// Error: ' + e.message;
    }
}

/* ===== HELPERS ===== */

function escapeHtml(text) {
    const div = document.createElement('div');
    div.appendChild(document.createTextNode(String(text)));
    return div.innerHTML;
}

/* ===== INIT ===== */
document.addEventListener('DOMContentLoaded', () => {
    // Auto-load Kafka events on page load
    loadKafkaEvents();
});
