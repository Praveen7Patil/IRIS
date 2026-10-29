#!/usr/bin/env python3
"""
Simulates live UI traffic hitting the MolFunnel / QpiAI Opt server:
Triggers GET /api/health, OPTIONS & POST /api/qubo/dispatch, and HUBO solving.
"""

import time
import urllib.request
import urllib.error

base_url = "http://127.0.0.1:8000"

def send(path, method="GET", data=None):
    url = f"{base_url}{path}"
    req = urllib.request.Request(url, method=method)
    if data:
        req.data = data.encode("utf-8")
        req.add_header("Content-Type", "application/json")
    try:
        with urllib.request.urlopen(req) as r:
            return r.status
    except urllib.error.HTTPError as e:
        return e.code
    except Exception as e:
        return str(e)

print("Sending UI traffic to http://127.0.0.1:8000...")

for i in range(5):
    status = send("/api/health")
    print(f"[{i+1}/5] GET /api/health -> {status}")
    time.sleep(0.3)

print("Sending OPTIONS /api/qubo/dispatch...")
send("/api/qubo/dispatch", method="OPTIONS")
time.sleep(0.4)

print("Sending POST /api/qubo/dispatch...")
send("/api/qubo/dispatch", method="POST", data='{"action": "dispatch"}')
time.sleep(0.6)

for i in range(4):
    send("/api/health")
    time.sleep(0.2)

print("Sending POST /api/qubo/solve (Stage 2)...")
send("/api/qubo/solve", method="POST", data='{"k": 12}')
time.sleep(0.5)

print("Sending POST /api/hubo/solve (Stage 3)...")
send("/api/hubo/solve", method="POST", data='{"k": 3}')
time.sleep(0.5)

print("\nDone! Check your server terminal window to see the generated live logs.")
